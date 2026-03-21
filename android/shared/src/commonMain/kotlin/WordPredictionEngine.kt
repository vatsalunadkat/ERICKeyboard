package com.vatoo.erick.shared

/**
 * Trie-based word prediction engine that provides:
 * 1. Word completions based on a typed prefix
 * 2. Spelling corrections for common typos (edit distance)
 * 3. Frequency-weighted suggestions
 */
class WordPredictionEngine {

    private val root = TrieNode()
    private var wordCount = 0
    // Bigram map: previousWord -> list of (nextWord, frequency)
    private val bigrams = mutableMapOf<String, MutableList<Pair<String, Int>>>()
    // Default suggestions when no context is available
    private var defaultSuggestions = listOf("I", "The", "Hello")

    // ── Trie data structure ──

    private class TrieNode {
        val children = mutableMapOf<Char, TrieNode>()
        var isWord = false
        var frequency = 0
    }

    fun insert(word: String, frequency: Int = 1) {
        if (word.isBlank()) return
        val lower = word.lowercase().trim()
        var node = root
        for (ch in lower) {
            node = node.children.getOrPut(ch) { TrieNode() }
        }
        if (!node.isWord) wordCount++
        node.isWord = true
        node.frequency = maxOf(node.frequency, frequency)
    }

    fun insertBigram(word: String, nextWord: String, frequency: Int = 1) {
        val key = word.lowercase().trim()
        val value = nextWord.lowercase().trim()
        if (key.isBlank() || value.isBlank()) return
        val list = bigrams.getOrPut(key) { mutableListOf() }
        // Update existing or add new
        val existing = list.indexOfFirst { it.first == value }
        if (existing >= 0) {
            list[existing] = value to maxOf(list[existing].second, frequency)
        } else {
            list.add(value to frequency)
        }
    }

    fun contains(word: String): Boolean {
        val lower = word.lowercase().trim()
        var node = root
        for (ch in lower) {
            node = node.children[ch] ?: return false
        }
        return node.isWord
    }

    // ── Word completion ──

    /**
     * Returns up to [limit] word completions for the given [prefix],
     * sorted by frequency (descending).
     */
    fun getCompletions(prefix: String, limit: Int = 3): List<String> {
        if (prefix.isBlank()) return emptyList()
        val lower = prefix.lowercase().trim()

        // Navigate to prefix node
        var node = root
        for (ch in lower) {
            node = node.children[ch] ?: return emptyList()
        }

        // Collect all words under this prefix
        val results = mutableListOf<Pair<String, Int>>() // word -> frequency
        collectWords(node, StringBuilder(lower), results, limit * 4) // collect extra for sorting

        return results
            .sortedByDescending { it.second }
            .map { it.first }
            .take(limit)
    }

    private fun collectWords(
        node: TrieNode,
        current: StringBuilder,
        results: MutableList<Pair<String, Int>>,
        maxCollect: Int
    ) {
        if (results.size >= maxCollect) return
        if (node.isWord) {
            results.add(current.toString() to node.frequency)
        }
        // Visit children in alphabetical order for deterministic results
        for ((ch, child) in node.children.entries.sortedBy { it.key }) {
            current.append(ch)
            collectWords(child, current, results, maxCollect)
            current.deleteAt(current.length - 1)
            if (results.size >= maxCollect) return
        }
    }

    // ── Autocorrect / spelling correction ──

    /**
     * Returns up to [limit] spelling corrections for [word] using
     * Levenshtein edit distance (max distance = [maxDistance]).
     * Only returns corrections that are different from the input word.
     */
    fun getCorrections(word: String, limit: Int = 3, maxDistance: Int = 2): List<String> {
        if (word.isBlank()) return emptyList()
        val lower = word.lowercase().trim()

        // If the word is already correct, no corrections needed
        if (contains(lower)) return emptyList()

        val candidates = mutableListOf<Pair<String, Int>>() // word -> frequency
        collectCorrectionCandidates(root, StringBuilder(), lower, maxDistance, candidates)

        return candidates
            .sortedWith(compareByDescending { it.second })
            .map { it.first }
            .filter { it != lower }
            .take(limit)
    }

    private fun collectCorrectionCandidates(
        node: TrieNode,
        current: StringBuilder,
        target: String,
        maxDistance: Int,
        results: MutableList<Pair<String, Int>>
    ) {
        if (node.isWord) {
            val dist = editDistance(current.toString(), target)
            if (dist <= maxDistance) {
                results.add(current.toString() to node.frequency)
            }
        }

        // Prune: if the current path is already too far from the target, stop
        val minPossibleDist = minEditDistanceBound(current.toString(), target)
        if (minPossibleDist > maxDistance) return

        for ((ch, child) in node.children) {
            current.append(ch)
            collectCorrectionCandidates(child, current, target, maxDistance, results)
            current.deleteAt(current.length - 1)
        }
    }

    // ── Suggestion API (unified completions + corrections + next-word) ──

    /**
     * Returns up to [limit] suggestions for the current word.
     * Prioritizes exact-prefix completions, then fills with corrections.
     * Works with any prefix length including single characters.
     */
    fun getSuggestions(currentWord: String, limit: Int = 3): List<String> {
        if (currentWord.isBlank()) return emptyList()
        val lower = currentWord.lowercase().trim()

        val completions = getCompletions(lower, limit)
        if (completions.size >= limit) return completions

        // Fill remaining slots with corrections (only for 2+ char words)
        if (lower.length >= 2) {
            val completionSet = completions.toSet()
            val corrections = getCorrections(lower, limit, maxDistance = 1)
                .filter { it !in completionSet && it != lower }
            val combined = (completions + corrections).take(limit)
            if (combined.size >= limit) return combined
        }

        return completions.take(limit)
    }

    /**
     * Returns up to [limit] next-word predictions based on the previous word.
     */
    fun getNextWordSuggestions(previousWord: String, limit: Int = 3): List<String> {
        if (previousWord.isBlank()) return getDefaultSuggestions(limit)
        val lower = previousWord.lowercase().trim()
        val nextWords = bigrams[lower] ?: return getDefaultSuggestions(limit)
        return nextWords
            .sortedByDescending { it.second }
            .map { it.first }
            .take(limit)
    }

    /**
     * Returns default suggestions for when no context is available
     * (e.g., start of input or after punctuation).
     */
    fun getDefaultSuggestions(limit: Int = 3): List<String> {
        return defaultSuggestions.take(limit)
    }

    // ── Edit distance utilities ──

    private fun editDistance(a: String, b: String): Int {
        val m = a.length
        val n = b.length
        // Use single-row DP for space efficiency
        var prev = IntArray(n + 1) { it }
        var curr = IntArray(n + 1)

        for (i in 1..m) {
            curr[0] = i
            for (j in 1..n) {
                val cost = if (a[i - 1] == b[j - 1]) 0 else 1
                curr[j] = minOf(
                    prev[j] + 1,      // deletion
                    curr[j - 1] + 1,  // insertion
                    prev[j - 1] + cost // substitution
                )
            }
            val temp = prev
            prev = curr
            curr = temp
        }
        return prev[n]
    }

    /**
     * Lower bound on edit distance between a partial prefix and
     * any completion of that prefix vs the target.
     * Used for pruning the trie search.
     */
    private fun minEditDistanceBound(prefix: String, target: String): Int {
        val m = prefix.length
        val n = target.length

        // If prefix is longer than target + maxDistance, prune
        if (m > n + 2) return m - n

        // Compute edit distance between prefix and target[0..m] truncated
        val truncTarget = if (m <= n) target.substring(0, m) else target
        return editDistance(prefix, truncTarget)
    }

    // ── Built-in dictionary ──

    companion object {
        /**
         * Creates a WordPredictionEngine pre-loaded with a common English dictionary.
         * Words are assigned frequency tiers so common words rank higher.
         */
        fun createWithDefaultDictionary(): WordPredictionEngine {
            val engine = WordPredictionEngine()
            loadDefaultDictionary(engine)
            return engine
        }

        private fun loadDefaultDictionary(engine: WordPredictionEngine) {
            // Tier 1: Ultra-common words (freq 1000)
            val tier1 = listOf(
                "the", "be", "to", "of", "and", "a", "in", "that", "have", "i",
                "it", "for", "not", "on", "with", "he", "as", "you", "do", "at",
                "this", "but", "his", "by", "from", "they", "we", "say", "her", "she",
                "or", "an", "will", "my", "one", "all", "would", "there", "their",
                "what", "so", "up", "out", "if", "about", "who", "get", "which", "go",
                "me", "when", "make", "can", "like", "time", "no", "just", "him",
                "know", "take", "people", "into", "year", "your", "good", "some",
                "could", "them", "see", "other", "than", "then", "now", "look",
                "only", "come", "its", "over", "think", "also", "back", "after",
                "use", "two", "how", "our", "work", "first", "well", "way", "even",
                "new", "want", "because", "any", "these", "give", "day", "most", "us",
                "is", "are", "was", "were", "been", "has", "had", "did", "does",
                "am", "being", "having", "doing", "going", "got", "getting",
                "very", "much", "more", "many", "too", "here", "where"
            )

            // Tier 2: Common words (freq 500)
            val tier2 = listOf(
                "great", "help", "through", "long", "right", "own", "still", "find",
                "while", "last", "might", "before", "old", "never", "world", "life",
                "same", "another", "should", "home", "big", "tell", "end", "does",
                "move", "try", "kind", "hand", "call", "keep", "turn", "between",
                "need", "every", "each", "made", "really", "already", "start",
                "always", "place", "off", "under", "three", "began", "show",
                "house", "both", "number", "part", "name", "being", "read", "run",
                "small", "set", "put", "always", "high", "live", "left", "down",
                "ask", "may", "change", "around", "little", "head", "thought",
                "still", "found", "few", "side", "next", "without", "stop",
                "open", "close", "best", "better", "ever", "enough", "line", "hear",
                "water", "food", "money", "group", "point", "city", "sure", "real",
                "word", "quite", "soon", "done", "family", "love", "school",
                "children", "country", "night", "away", "again", "room", "body",
                "morning", "friend", "book", "problem", "nothing", "face", "door",
                "play", "feel", "together", "until", "hard", "power", "story",
                "today", "important", "young", "different", "second", "woman",
                "early", "white", "girl", "man", "boy", "game", "eyes", "case",
                "state", "company", "service"
            )

            // Tier 3: Everyday words (freq 200)
            val tier3 = listOf(
                "happy", "beautiful", "believe", "please", "thank", "sorry",
                "hello", "goodbye", "tomorrow", "yesterday", "tonight",
                "together", "today", "always", "sometimes", "often", "usually",
                "never", "maybe", "probably", "possible", "actually", "already",
                "information", "business", "government", "question", "answer",
                "experience", "example", "because", "different", "important",
                "understand", "remember", "interest", "complete", "continue",
                "country", "between", "through", "against", "however", "although",
                "address", "animal", "weather", "music", "color", "picture",
                "computer", "phone", "email", "message", "number", "program",
                "system", "market", "health", "education", "language",
                "kitchen", "window", "garden", "office", "meeting", "project",
                "develop", "create", "build", "design", "write", "paper",
                "report", "heart", "light", "table", "watch", "walk",
                "travel", "smile", "laugh", "drive", "sleep", "dream",
                "eat", "drink", "break", "think", "learn", "teach",
                "speak", "listen", "follow", "happen", "include",
                "provide", "consider", "appear", "allow", "discuss",
                "receive", "expect", "suggest", "produce", "offer",
                "support", "require", "prepare", "accept", "decision",
                "simple", "strong", "clear", "certain", "special",
                "reason", "minute", "moment", "person", "result"
            )

            // Tier 4: Extended vocabulary (freq 100)
            val tier4 = listOf(
                "about", "above", "across", "after", "afternoon", "again",
                "almost", "along", "anything", "apply", "area",
                "attention", "available", "ball", "bank", "basis", "become",
                "before", "behind", "below", "beside", "blood", "blue",
                "board", "bottom", "bring", "brother", "building", "buy",
                "camera", "carry", "catch", "center", "chair",
                "character", "check", "church", "class", "clean", "clothes",
                "collect", "college", "common", "compare", "concern",
                "condition", "connect", "control", "corner", "cost",
                "cover", "cross", "current", "cut", "daily", "dance",
                "dark", "daughter", "dead", "deal", "deep", "degree",
                "describe", "detail", "determine", "dinner", "direct",
                "discover", "distance", "doctor", "draw", "dress",
                "drop", "during", "edge", "effect", "effort", "either",
                "energy", "enjoy", "enter", "environment", "equal",
                "especially", "evening", "event", "exactly", "explain",
                "express", "extra", "eye", "fail", "fall", "fast",
                "father", "fear", "figure", "fill", "final", "finger",
                "finish", "fire", "floor", "fly", "foot", "force",
                "foreign", "forget", "form", "forward", "four", "free",
                "front", "full", "fun", "future", "general", "glass",
                "goal", "gold", "green", "ground", "grow", "guess",
                "gun", "hair", "half", "hang", "happen", "hour",
                "husband", "idea", "imagine", "industry", "inside",
                "instead", "issue", "job", "join", "jump", "king",
                "knowledge", "land", "late", "law", "lay", "lead",
                "letter", "level", "lie", "list", "lose", "lot",
                "low", "machine", "main", "major", "matter", "mean",
                "measure", "media", "medical", "member", "memory",
                "mention", "middle", "might", "military", "million",
                "mind", "miss", "model", "modern", "month", "mother",
                "mountain", "mouth", "myself", "nation", "natural",
                "near", "necessary", "news", "nice", "north", "note",
                "notice", "oil", "once", "opportunity", "order",
                "outside", "page", "paint", "parent", "particular",
                "pass", "past", "patient", "pay", "peace", "perform",
                "period", "pick", "piece", "plan", "plant", "player",
                "please", "policy", "political", "poor", "popular",
                "position", "positive", "pressure", "pretty", "price",
                "private", "process", "professional", "protect", "prove",
                "public", "pull", "push", "raise", "range", "rate",
                "reach", "record", "red", "reduce", "region",
                "relate", "remain", "remove", "repeat", "replace",
                "represent", "research", "resource", "rest", "return",
                "reveal", "rich", "rise", "risk", "road", "rock",
                "role", "rule", "safe", "save", "scene", "season",
                "seat", "sell", "send", "senior", "sense", "serious",
                "serve", "seven", "share", "short", "shot", "sign",
                "significant", "similar", "since", "single", "sister",
                "sit", "situation", "six", "size", "skill", "social",
                "society", "soldier", "somebody", "someone", "song",
                "sort", "sound", "south", "space", "spend", "sport",
                "spring", "staff", "stage", "stand", "standard", "star",
                "stock", "store", "street", "student", "study", "style",
                "subject", "success", "summer", "surface", "table",
                "technology", "television", "ten", "test", "themselves",
                "theory", "thing", "thousand", "threat", "throw",
                "tie", "top", "total", "tough", "toward", "trade",
                "traditional", "training", "treat", "tree", "trial",
                "trip", "trouble", "true", "truth", "type", "unit",
                "value", "various", "view", "visit", "voice", "vote",
                "wait", "wall", "want", "war", "west", "whatever",
                "whole", "wide", "wife", "win", "wish", "wonder",
                "worker", "wrong", "yard", "yeah", "young"
            )

            for (w in tier1) engine.insert(w, tier1.size + 1 - tier1.indexOf(w) + 900)
            for (w in tier2) engine.insert(w, 500)
            for (w in tier3) engine.insert(w, 200)
            for (w in tier4) engine.insert(w, 100)

            // ── Bigram data (next-word predictions) ──
            loadBigrams(engine)

            // Default suggestions for start of input
            engine.defaultSuggestions = listOf("I", "The", "Hello")
        }

        private fun loadBigrams(engine: WordPredictionEngine) {
            val bigramData = mapOf(
                "i" to listOf("am" to 1000, "have" to 900, "was" to 800, "will" to 700, "can" to 600, "think" to 500, "know" to 450, "want" to 400, "like" to 350, "need" to 300),
                "you" to listOf("are" to 1000, "can" to 800, "have" to 700, "will" to 600, "know" to 500, "want" to 400, "need" to 350, "should" to 300),
                "he" to listOf("is" to 1000, "was" to 900, "will" to 700, "has" to 600, "would" to 500, "can" to 400, "said" to 350),
                "she" to listOf("is" to 1000, "was" to 900, "will" to 700, "has" to 600, "would" to 500, "can" to 400, "said" to 350),
                "it" to listOf("is" to 1000, "was" to 900, "will" to 700, "would" to 600, "can" to 500, "has" to 400),
                "we" to listOf("are" to 1000, "have" to 800, "can" to 700, "will" to 600, "need" to 500, "should" to 400, "were" to 350),
                "they" to listOf("are" to 1000, "have" to 800, "will" to 700, "can" to 600, "were" to 500, "would" to 400),
                "the" to listOf("best" to 800, "first" to 700, "same" to 600, "most" to 500, "other" to 450, "next" to 400, "new" to 350, "last" to 300, "world" to 250),
                "my" to listOf("name" to 1000, "own" to 600, "life" to 500, "family" to 400, "friend" to 350, "phone" to 300, "house" to 250, "self" to 200),
                "is" to listOf("a" to 900, "the" to 800, "not" to 700, "very" to 500, "that" to 400, "it" to 350, "good" to 300),
                "are" to listOf("you" to 900, "not" to 700, "the" to 600, "there" to 500, "we" to 400, "they" to 350),
                "was" to listOf("a" to 800, "the" to 700, "not" to 600, "very" to 500, "it" to 400, "going" to 350),
                "have" to listOf("a" to 900, "been" to 800, "to" to 700, "the" to 600, "not" to 500, "you" to 400),
                "has" to listOf("been" to 900, "a" to 700, "the" to 600, "not" to 500, "to" to 400),
                "do" to listOf("you" to 1000, "not" to 800, "it" to 700, "the" to 500, "this" to 400),
                "does" to listOf("not" to 900, "it" to 700, "the" to 500, "this" to 400),
                "did" to listOf("you" to 900, "not" to 800, "the" to 600, "it" to 500, "he" to 400),
                "will" to listOf("be" to 1000, "have" to 700, "not" to 600, "you" to 500, "the" to 400),
                "would" to listOf("be" to 900, "like" to 800, "have" to 700, "you" to 600, "not" to 500),
                "can" to listOf("you" to 900, "be" to 700, "i" to 600, "we" to 500, "the" to 400),
                "could" to listOf("be" to 800, "have" to 700, "you" to 600, "not" to 500),
                "should" to listOf("be" to 800, "have" to 700, "i" to 600, "we" to 500, "not" to 400),
                "what" to listOf("is" to 1000, "are" to 800, "do" to 700, "the" to 500, "about" to 400, "time" to 350),
                "how" to listOf("are" to 1000, "do" to 800, "is" to 700, "much" to 600, "many" to 500, "about" to 400),
                "there" to listOf("is" to 1000, "are" to 900, "was" to 700, "were" to 600),
                "that" to listOf("is" to 900, "was" to 700, "the" to 600, "it" to 500, "you" to 400),
                "this" to listOf("is" to 1000, "was" to 700, "the" to 500, "will" to 400),
                "not" to listOf("a" to 700, "the" to 600, "be" to 500, "have" to 400, "sure" to 350),
                "with" to listOf("the" to 800, "a" to 700, "you" to 600, "my" to 500, "his" to 400),
                "for" to listOf("the" to 800, "a" to 700, "you" to 600, "me" to 500, "your" to 400),
                "in" to listOf("the" to 900, "a" to 700, "my" to 500, "this" to 400, "your" to 350),
                "on" to listOf("the" to 900, "a" to 600, "my" to 500, "your" to 400, "this" to 350),
                "at" to listOf("the" to 800, "a" to 600, "home" to 500, "all" to 400, "least" to 350),
                "to" to listOf("the" to 900, "be" to 800, "do" to 700, "get" to 600, "go" to 500, "have" to 400, "make" to 350),
                "of" to listOf("the" to 1000, "a" to 700, "my" to 500, "this" to 400, "it" to 350),
                "and" to listOf("the" to 800, "i" to 700, "a" to 600, "it" to 500, "we" to 400),
                "but" to listOf("i" to 800, "the" to 700, "it" to 600, "he" to 500, "we" to 400),
                "or" to listOf("the" to 600, "a" to 500, "not" to 400, "you" to 350),
                "if" to listOf("you" to 900, "the" to 700, "i" to 600, "it" to 500, "we" to 400),
                "so" to listOf("i" to 700, "much" to 600, "that" to 500, "the" to 400, "many" to 350),
                "just" to listOf("a" to 700, "the" to 600, "like" to 500, "want" to 400, "need" to 350),
                "very" to listOf("much" to 800, "good" to 700, "well" to 600, "happy" to 500, "important" to 400),
                "good" to listOf("morning" to 1000, "night" to 800, "job" to 600, "luck" to 500, "to" to 400),
                "thank" to listOf("you" to 1000, "god" to 400),
                "thanks" to listOf("for" to 900, "a" to 400),
                "please" to listOf("let" to 700, "help" to 600, "be" to 500, "do" to 400),
                "name" to listOf("is" to 1000),
                "going" to listOf("to" to 1000),
                "want" to listOf("to" to 1000, "a" to 500, "the" to 400),
                "need" to listOf("to" to 900, "a" to 600, "the" to 500),
                "like" to listOf("to" to 800, "a" to 600, "the" to 500, "it" to 400, "this" to 350),
                "love" to listOf("you" to 1000, "it" to 600, "the" to 400, "to" to 350),
                "know" to listOf("that" to 700, "how" to 600, "what" to 500, "if" to 400, "the" to 350),
                "think" to listOf("that" to 700, "it" to 600, "about" to 500, "so" to 400, "the" to 350),
                "see" to listOf("you" to 800, "the" to 600, "it" to 500, "if" to 400),
                "come" to listOf("back" to 700, "to" to 600, "here" to 500, "in" to 400),
                "go" to listOf("to" to 900, "back" to 600, "home" to 500, "out" to 400),
                "get" to listOf("the" to 700, "a" to 600, "it" to 500, "to" to 400, "out" to 350),
                "take" to listOf("a" to 700, "the" to 600, "it" to 500, "care" to 400),
                "make" to listOf("a" to 700, "the" to 600, "it" to 500, "sure" to 400),
                "look" to listOf("at" to 800, "like" to 600, "for" to 500, "good" to 400),
                "no" to listOf("one" to 700, "problem" to 600, "thanks" to 500, "way" to 400),
                "yes" to listOf("i" to 700, "please" to 500, "it" to 400),
                "hello" to listOf("how" to 700, "my" to 500, "there" to 400),
                "hi" to listOf("how" to 700, "there" to 500, "my" to 400),
                "nice" to listOf("to" to 800, "job" to 500, "day" to 400),
                "happy" to listOf("birthday" to 800, "to" to 600, "new" to 400),
                "new" to listOf("year" to 700, "york" to 500, "to" to 400),
                "let" to listOf("me" to 900, "us" to 700, "the" to 500, "it" to 400),
                "all" to listOf("the" to 800, "of" to 600, "right" to 500, "about" to 400),
                "been" to listOf("a" to 700, "to" to 600, "the" to 500, "in" to 400)
            )

            for ((word, nextWords) in bigramData) {
                for ((nextWord, freq) in nextWords) {
                    engine.insertBigram(word, nextWord, freq)
                }
            }
        }
    }
}
