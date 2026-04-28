package com.vatoo.erick.shared

data class KeyboardLanguageProfile(
    val displayName: String,
    val languageCode: String,
    val logical8Extras: List<String> = emptyList(),
    val symbols6Extras: List<String> = emptyList(),
    val defaultSuggestions: List<String> = emptyList(),
    val dictionaryWords: Map<String, Int> = emptyMap(),
    val bigrams: Map<String, List<Pair<String, Int>>> = emptyMap()
)

object KeyboardLanguageProfiles {
    private val logical8ExtraSlots = listOf(
        Direction.N to 5,
        Direction.N to 6,
        Direction.NE to 5,
        Direction.NE to 6,
        Direction.E to 5,
        Direction.E to 6,
        Direction.SE to 6,
        Direction.SE to 7,
        Direction.S to 0,
        Direction.S to 1,
        Direction.S to 7,
        Direction.SW to 0,
        Direction.SW to 1,
        Direction.SW to 2,
        Direction.W to 1,
        Direction.W to 2,
        Direction.W to 3,
        Direction.NW to 3,
        Direction.NW to 4
    )

    private val symbols6ExtraSlots = listOf(
        Direction.N to 0,
        Direction.N to 1,
        Direction.N to 2,
        Direction.N to 3,
        Direction.N to 4,
        Direction.N to 5,
        Direction.NE to 0,
        Direction.NE to 1,
        Direction.NE to 2,
        Direction.NE to 3,
        Direction.NE to 4,
        Direction.NE to 5,
        Direction.SE to 0,
        Direction.SE to 1,
        Direction.SE to 2,
        Direction.SE to 3,
        Direction.SE to 4,
        Direction.SE to 5
    )

    private val profiles = mapOf(
        KeyboardLanguage.ENGLISH to KeyboardLanguageProfile(
            displayName = "English",
            languageCode = "en"
        ),
        KeyboardLanguage.SPANISH to KeyboardLanguageProfile(
            displayName = "Spanish",
            languageCode = "es",
            logical8Extras = listOf("á", "é", "í", "ó", "ú", "ü", "ñ", "¿", "¡"),
            symbols6Extras = listOf("á", "é", "í", "ó", "ú", "ü", "ñ", "¿", "¡"),
            defaultSuggestions = listOf("Hola", "Gracias", "Quiero"),
            dictionaryWords = weightedDictionary(
                1000 to listOf("hola", "gracias", "yo", "tú", "usted", "que", "como", "por", "favor", "sí", "no"),
                600 to listOf("quiero", "tengo", "bien", "dónde", "cuándo", "mañana", "niño", "niña", "años", "está", "estás", "día")
            ),
            bigrams = mapOf(
                "yo" to listOf("soy" to 900, "quiero" to 800, "tengo" to 700),
                "por" to listOf("favor" to 1000),
                "gracias" to listOf("por" to 800),
                "buenos" to listOf("días" to 900),
                "cómo" to listOf("estás" to 900)
            )
        ),
        KeyboardLanguage.PORTUGUESE to KeyboardLanguageProfile(
            displayName = "Portuguese",
            languageCode = "pt",
            logical8Extras = listOf("á", "à", "â", "ã", "ç", "é", "ê", "í", "ó", "ô", "õ", "ú"),
            symbols6Extras = listOf("á", "à", "â", "ã", "ç", "é", "ê", "í", "ó", "ô", "õ", "ú"),
            defaultSuggestions = listOf("Olá", "Obrigado", "Quero"),
            dictionaryWords = weightedDictionary(
                1000 to listOf("olá", "obrigado", "eu", "você", "que", "como", "por", "favor", "sim", "não"),
                600 to listOf("quero", "tenho", "bem", "onde", "quando", "amanhã", "ação", "coração", "está", "dias", "gosto")
            ),
            bigrams = mapOf(
                "eu" to listOf("sou" to 900, "quero" to 850, "tenho" to 800),
                "por" to listOf("favor" to 1000),
                "bom" to listOf("dia" to 900),
                "boa" to listOf("noite" to 900),
                "como" to listOf("vai" to 700)
            )
        ),
        KeyboardLanguage.FRENCH to KeyboardLanguageProfile(
            displayName = "French",
            languageCode = "fr",
            logical8Extras = listOf("à", "â", "é", "è", "ê", "ë", "î", "ï", "ô", "ù", "û", "ü", "ç"),
            symbols6Extras = listOf("à", "â", "é", "è", "ê", "ë", "î", "ï", "ô", "ù", "û", "ü", "ç"),
            defaultSuggestions = listOf("Bonjour", "Merci", "Je"),
            dictionaryWords = weightedDictionary(
                1000 to listOf("bonjour", "merci", "je", "tu", "vous", "nous", "que", "comment", "oui", "non"),
                600 to listOf("où", "quand", "être", "avoir", "déjà", "très", "garçon", "ça", "avec", "pour", "bonjour", "soir")
            ),
            bigrams = mapOf(
                "je" to listOf("suis" to 900, "veux" to 800, "vais" to 700),
                "tu" to listOf("es" to 900),
                "merci" to listOf("beaucoup" to 850),
                "bon" to listOf("jour" to 650),
                "à" to listOf("bientôt" to 750)
            )
        ),
        KeyboardLanguage.GERMAN to KeyboardLanguageProfile(
            displayName = "German",
            languageCode = "de",
            logical8Extras = listOf("ä", "ö", "ü", "ß"),
            symbols6Extras = listOf("ä", "ö", "ü", "ß"),
            defaultSuggestions = listOf("Hallo", "Danke", "Ich"),
            dictionaryWords = weightedDictionary(
                1000 to listOf("hallo", "danke", "ich", "du", "wir", "sie", "ja", "nein", "bitte", "ist"),
                600 to listOf("guten", "tag", "schön", "groß", "straße", "über", "für", "nicht", "bin", "morgen", "abend")
            ),
            bigrams = mapOf(
                "ich" to listOf("bin" to 950, "habe" to 850),
                "du" to listOf("bist" to 900),
                "guten" to listOf("tag" to 1000, "morgen" to 900),
                "wie" to listOf("geht" to 850)
            )
        ),
        KeyboardLanguage.ITALIAN to KeyboardLanguageProfile(
            displayName = "Italian",
            languageCode = "it",
            logical8Extras = listOf("à", "è", "é", "ì", "ò", "ù"),
            symbols6Extras = listOf("à", "è", "é", "ì", "ò", "ù"),
            defaultSuggestions = listOf("Ciao", "Grazie", "Io"),
            dictionaryWords = weightedDictionary(
                1000 to listOf("ciao", "grazie", "io", "tu", "noi", "loro", "che", "come", "sì", "no"),
                600 to listOf("dove", "quando", "perché", "già", "più", "città", "sarà", "essere", "avere", "bene", "oggi")
            ),
            bigrams = mapOf(
                "io" to listOf("sono" to 950, "voglio" to 850),
                "come" to listOf("stai" to 900),
                "grazie" to listOf("mille" to 800),
                "buon" to listOf("giorno" to 900)
            )
        ),
        KeyboardLanguage.NORWEGIAN_BOKMAL to KeyboardLanguageProfile(
            displayName = "Norwegian Bokmal",
            languageCode = "nb",
            logical8Extras = listOf("æ", "ø", "å"),
            symbols6Extras = listOf("æ", "ø", "å"),
            defaultSuggestions = listOf("Hei", "Takk", "Jeg"),
            dictionaryWords = weightedDictionary(
                1000 to listOf("hei", "takk", "jeg", "du", "vi", "de", "ja", "nei", "hvor", "når"),
                600 to listOf("hvordan", "god", "morgen", "kveld", "språk", "år", "åpne", "gjøre", "blå", "vær")
            ),
            bigrams = mapOf(
                "jeg" to listOf("er" to 950, "har" to 850),
                "god" to listOf("morgen" to 900, "kveld" to 800),
                "takk" to listOf("for" to 750)
            )
        ),
        KeyboardLanguage.DANISH to KeyboardLanguageProfile(
            displayName = "Danish",
            languageCode = "da",
            logical8Extras = listOf("æ", "ø", "å"),
            symbols6Extras = listOf("æ", "ø", "å"),
            defaultSuggestions = listOf("Hej", "Tak", "Jeg"),
            dictionaryWords = weightedDictionary(
                1000 to listOf("hej", "tak", "jeg", "du", "vi", "de", "ja", "nej", "hvor", "når"),
                600 to listOf("hvordan", "god", "morgen", "aften", "smør", "år", "åbne", "gøre", "blå", "sprog")
            ),
            bigrams = mapOf(
                "jeg" to listOf("er" to 950, "har" to 850),
                "god" to listOf("morgen" to 900),
                "tak" to listOf("for" to 750)
            )
        ),
        KeyboardLanguage.SWEDISH to KeyboardLanguageProfile(
            displayName = "Swedish",
            languageCode = "sv",
            logical8Extras = listOf("å", "ä", "ö"),
            symbols6Extras = listOf("å", "ä", "ö"),
            defaultSuggestions = listOf("Hej", "Tack", "Jag"),
            dictionaryWords = weightedDictionary(
                1000 to listOf("hej", "tack", "jag", "du", "vi", "de", "ja", "nej", "var", "när"),
                600 to listOf("hur", "god", "morgon", "kväll", "språk", "år", "göra", "blå", "vän", "snäll")
            ),
            bigrams = mapOf(
                "jag" to listOf("är" to 950, "har" to 850),
                "god" to listOf("morgon" to 900),
                "tack" to listOf("så" to 700)
            )
        ),
        KeyboardLanguage.FINNISH to KeyboardLanguageProfile(
            displayName = "Finnish",
            languageCode = "fi",
            logical8Extras = listOf("ä", "ö"),
            symbols6Extras = listOf("ä", "ö"),
            defaultSuggestions = listOf("Hei", "Kiitos", "Minä"),
            dictionaryWords = weightedDictionary(
                1000 to listOf("hei", "kiitos", "minä", "sinä", "me", "he", "kyllä", "ei", "missä", "milloin"),
                600 to listOf("hyvää", "päivää", "yötä", "työ", "sää", "käsi", "tehdä", "olla", "ystävä", "kieli")
            ),
            bigrams = mapOf(
                "minä" to listOf("olen" to 950),
                "hyvää" to listOf("päivää" to 900),
                "kiitos" to listOf("paljon" to 800)
            )
        )
    )

    fun profile(language: KeyboardLanguage): KeyboardLanguageProfile = profiles.getValue(language)

    fun supportsEfficiencyLayout(language: KeyboardLanguage): Boolean {
        return language == KeyboardLanguage.ENGLISH
    }

    fun logical8NormalOverlay(language: KeyboardLanguage): Map<Direction, Map<Int, String>> {
        return buildOverlay(profile(language).logical8Extras, logical8ExtraSlots)
    }

    fun logical8ShiftedOverlay(language: KeyboardLanguage): Map<Direction, Map<Int, String>> {
        return buildOverlay(profile(language).logical8Extras.map(::uppercaseVariant), logical8ExtraSlots)
    }

    fun symbols6NormalOverlay(language: KeyboardLanguage): Map<Direction, Map<Int, String>> {
        return buildOverlay(profile(language).symbols6Extras, symbols6ExtraSlots)
    }

    fun symbols6ShiftedOverlay(language: KeyboardLanguage): Map<Direction, Map<Int, String>> {
        return buildOverlay(profile(language).symbols6Extras.map(::uppercaseVariant), symbols6ExtraSlots)
    }

    private fun buildOverlay(
        values: List<String>,
        slots: List<Pair<Direction, Int>>
    ): Map<Direction, Map<Int, String>> {
        if (values.isEmpty()) return emptyMap()

        val overlay = mutableMapOf<Direction, MutableMap<Int, String>>()
        values.forEachIndexed { index, value ->
            val (direction, slot) = slots[index]
            overlay.getOrPut(direction) { mutableMapOf() }[slot] = value
        }
        return overlay
    }

    private fun uppercaseVariant(value: String): String {
        return when (value) {
            "ß" -> "ẞ"
            else -> if (value.any { it.isLetter() }) value.uppercase() else value
        }
    }

    private fun weightedDictionary(vararg groups: Pair<Int, List<String>>): Map<String, Int> {
        return buildMap {
            groups.forEach { (frequency, words) ->
                words.forEach { word ->
                    put(word, frequency)
                }
            }
        }
    }
}