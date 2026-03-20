package com.vatoo.erick.shared

/**
 * Manual JSON serializer/deserializer for CustomLayout objects.
 * Uses no external dependencies — pure Kotlin string manipulation.
 */
object CustomLayoutSerializer {

    fun serializeAll(layouts: List<CustomLayout>): String {
        val sb = StringBuilder()
        sb.append("[")
        layouts.forEachIndexed { i, layout ->
            if (i > 0) sb.append(",")
            sb.append(serializeOne(layout))
        }
        sb.append("]")
        return sb.toString()
    }

    fun deserializeAll(json: String): List<CustomLayout> {
        val trimmed = json.trim()
        if (trimmed.isEmpty() || trimmed == "[]") return emptyList()

        val results = mutableListOf<CustomLayout>()
        // Split top-level array into individual objects
        val objects = splitTopLevelObjects(trimmed)
        for (obj in objects) {
            try {
                val layout = deserializeOne(obj)
                if (layout != null) results.add(layout)
            } catch (_: Exception) {
                // Skip malformed entries
            }
        }
        return results
    }

    private fun serializeOne(layout: CustomLayout): String {
        val sb = StringBuilder()
        sb.append("{")
        sb.append("\"id\":${escapeJson(layout.id)},")
        sb.append("\"name\":${escapeJson(layout.name)},")
        sb.append("\"normalChordMap\":${serializeChordMap(layout.normalChordMap)},")
        sb.append("\"shiftedChordMap\":${serializeChordMap(layout.shiftedChordMap)},")
        sb.append("\"singleSwipeNormalMap\":${serializeSwipeMap(layout.singleSwipeNormalMap)},")
        sb.append("\"singleSwipeShiftedMap\":${serializeSwipeMap(layout.singleSwipeShiftedMap)}")
        sb.append("}")
        return sb.toString()
    }

    private fun deserializeOne(json: String): CustomLayout? {
        val fields = parseObject(json)
        val id = unescapeJson(fields["id"] ?: return null)
        val name = unescapeJson(fields["name"] ?: return null)
        val normalChord = parseChordMap(fields["normalChordMap"] ?: return null)
        val shiftedChord = parseChordMap(fields["shiftedChordMap"] ?: return null)
        val singleNormal = parseSwipeMap(fields["singleSwipeNormalMap"] ?: "{}")
        val singleShifted = parseSwipeMap(fields["singleSwipeShiftedMap"] ?: "{}")
        return CustomLayout(id, name, normalChord, shiftedChord, singleNormal, singleShifted)
    }

    // --- Chord map: {"N":["a","b",...], "NE":[...], ...} ---

    private fun serializeChordMap(map: Map<Direction, List<String>>): String {
        val sb = StringBuilder()
        sb.append("{")
        var first = true
        for ((dir, chars) in map) {
            if (!first) sb.append(",")
            first = false
            sb.append("\"${dir.name}\":[")
            chars.forEachIndexed { i, c ->
                if (i > 0) sb.append(",")
                sb.append(escapeJson(c))
            }
            sb.append("]")
        }
        sb.append("}")
        return sb.toString()
    }

    private fun parseChordMap(json: String): Map<Direction, List<String>> {
        val result = mutableMapOf<Direction, List<String>>()
        val fields = parseObject(json)
        for ((key, value) in fields) {
            val dir = Direction.entries.firstOrNull { it.name == key } ?: continue
            result[dir] = parseStringArray(value)
        }
        return result
    }

    // --- Swipe map: {"N":"action:MOVE_HOME", "NE":"char:,", ...} ---

    private fun serializeSwipeMap(map: Map<Direction, SingleSwipeBinding>): String {
        val sb = StringBuilder()
        sb.append("{")
        var first = true
        for ((dir, binding) in map) {
            if (!first) sb.append(",")
            first = false
            sb.append("\"${dir.name}\":${escapeJson(binding.toSerializable())}")
        }
        sb.append("}")
        return sb.toString()
    }

    private fun parseSwipeMap(json: String): Map<Direction, SingleSwipeBinding> {
        val result = mutableMapOf<Direction, SingleSwipeBinding>()
        val fields = parseObject(json)
        for ((key, value) in fields) {
            val dir = Direction.entries.firstOrNull { it.name == key } ?: continue
            val binding = SingleSwipeBinding.fromSerializable(unescapeJson(value)) ?: continue
            result[dir] = binding
        }
        return result
    }

    // --- JSON helpers ---

    private fun escapeJson(s: String): String {
        val sb = StringBuilder("\"")
        for (c in s) {
            when (c) {
                '"' -> sb.append("\\\"")
                '\\' -> sb.append("\\\\")
                '\n' -> sb.append("\\n")
                '\r' -> sb.append("\\r")
                '\t' -> sb.append("\\t")
                else -> sb.append(c)
            }
        }
        sb.append("\"")
        return sb.toString()
    }

    private fun unescapeJson(s: String): String {
        var v = s.trim()
        if (v.startsWith("\"") && v.endsWith("\"")) {
            v = v.substring(1, v.length - 1)
        }
        return v.replace("\\\"", "\"")
            .replace("\\\\", "\\")
            .replace("\\n", "\n")
            .replace("\\r", "\r")
            .replace("\\t", "\t")
    }

    /**
     * Parse a JSON object string into a map of key -> raw value strings.
     * Handles nested objects/arrays by tracking brace/bracket depth.
     */
    private fun parseObject(json: String): Map<String, String> {
        val trimmed = json.trim()
        if (!trimmed.startsWith("{") || !trimmed.endsWith("}")) return emptyMap()
        val inner = trimmed.substring(1, trimmed.length - 1)

        val result = mutableMapOf<String, String>()
        var i = 0
        while (i < inner.length) {
            // Skip whitespace
            while (i < inner.length && inner[i].isWhitespace()) i++
            if (i >= inner.length) break

            // Parse key
            if (inner[i] != '"') { i++; continue }
            val keyEnd = findClosingQuote(inner, i)
            if (keyEnd == -1) break
            val key = inner.substring(i + 1, keyEnd)
            i = keyEnd + 1

            // Skip colon
            while (i < inner.length && (inner[i] == ':' || inner[i].isWhitespace())) i++

            // Parse value
            val valueStart = i
            i = skipValue(inner, i)
            val value = inner.substring(valueStart, i).trim()
            result[key] = value

            // Skip comma
            while (i < inner.length && (inner[i] == ',' || inner[i].isWhitespace())) i++
        }
        return result
    }

    private fun parseStringArray(json: String): List<String> {
        val trimmed = json.trim()
        if (!trimmed.startsWith("[") || !trimmed.endsWith("]")) return emptyList()
        val inner = trimmed.substring(1, trimmed.length - 1).trim()
        if (inner.isEmpty()) return emptyList()

        val result = mutableListOf<String>()
        var i = 0
        while (i < inner.length) {
            while (i < inner.length && inner[i].isWhitespace()) i++
            if (i >= inner.length) break
            if (inner[i] == '"') {
                val end = findClosingQuote(inner, i)
                if (end == -1) break
                result.add(unescapeJson(inner.substring(i, end + 1)))
                i = end + 1
            } else {
                i++
            }
            while (i < inner.length && (inner[i] == ',' || inner[i].isWhitespace())) i++
        }
        return result
    }

    private fun findClosingQuote(s: String, start: Int): Int {
        var i = start + 1
        while (i < s.length) {
            if (s[i] == '\\') { i += 2; continue }
            if (s[i] == '"') return i
            i++
        }
        return -1
    }

    private fun skipValue(s: String, start: Int): Int {
        if (start >= s.length) return start
        return when (s[start]) {
            '"' -> findClosingQuote(s, start) + 1
            '{' -> skipBraced(s, start, '{', '}')
            '[' -> skipBraced(s, start, '[', ']')
            else -> {
                // null, number, boolean
                var i = start
                while (i < s.length && s[i] != ',' && s[i] != '}' && s[i] != ']') i++
                i
            }
        }
    }

    private fun skipBraced(s: String, start: Int, open: Char, close: Char): Int {
        var depth = 0
        var inString = false
        var i = start
        while (i < s.length) {
            val c = s[i]
            if (inString) {
                if (c == '\\') { i += 2; continue }
                if (c == '"') inString = false
            } else {
                if (c == '"') inString = true
                else if (c == open) depth++
                else if (c == close) { depth--; if (depth == 0) return i + 1 }
            }
            i++
        }
        return i
    }

    /**
     * Split a JSON array "[{...},{...}]" into individual object strings.
     */
    private fun splitTopLevelObjects(json: String): List<String> {
        val trimmed = json.trim()
        if (!trimmed.startsWith("[") || !trimmed.endsWith("]")) return emptyList()
        val inner = trimmed.substring(1, trimmed.length - 1).trim()
        if (inner.isEmpty()) return emptyList()

        val results = mutableListOf<String>()
        var i = 0
        while (i < inner.length) {
            while (i < inner.length && inner[i].isWhitespace()) i++
            if (i >= inner.length) break
            if (inner[i] == '{') {
                val end = skipBraced(inner, i, '{', '}')
                results.add(inner.substring(i, end))
                i = end
            } else {
                i++
            }
        }
        return results
    }
}
