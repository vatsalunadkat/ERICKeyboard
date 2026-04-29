package com.vatoo.erick.shared

object PredictionProfileBundle {
    private const val HEADER = "[prediction-profiles]"
    private const val VERSION = 1

    fun serialize(profiles: Map<KeyboardLanguage, String>): String {
        if (profiles.isEmpty()) return ""

        return buildString {
            appendLine(HEADER)
            appendLine("version\t$VERSION")
            profiles.entries
                .filter { it.value.isNotBlank() }
                .sortedBy { it.key.name }
                .forEach { (language, serializedProfile) ->
                    append("profile\t")
                    append(language.name)
                    append('\t')
                    appendLine(escape(serializedProfile))
                }
        }
    }

    fun deserialize(serialized: String): Map<KeyboardLanguage, String> {
        if (serialized.isBlank()) return emptyMap()

        val lines = serialized.lineSequence().toList()
        if (lines.firstOrNull() != HEADER) {
            return mapOf(KeyboardLanguage.ENGLISH to serialized)
        }

        return buildMap {
            lines.drop(1).forEach { rawLine ->
                val line = rawLine.trim()
                if (line.isEmpty() || line.startsWith("version\t")) return@forEach
                val parts = line.split('\t', limit = 3)
                if (parts.size < 3 || parts[0] != "profile") return@forEach
                val language = runCatching { KeyboardLanguage.valueOf(parts[1]) }.getOrNull() ?: return@forEach
                put(language, unescape(parts[2]))
            }
        }
    }

    private fun escape(value: String): String {
        return value
            .replace("\\", "\\\\")
            .replace("\t", "\\t")
            .replace("\n", "\\n")
    }

    private fun unescape(value: String): String {
        val result = StringBuilder()
        var index = 0
        while (index < value.length) {
            val current = value[index]
            if (current == '\\' && index + 1 < value.length) {
                when (value[index + 1]) {
                    'n' -> {
                        result.append('\n')
                        index += 2
                        continue
                    }
                    't' -> {
                        result.append('\t')
                        index += 2
                        continue
                    }
                    '\\' -> {
                        result.append('\\')
                        index += 2
                        continue
                    }
                }
            }
            result.append(current)
            index += 1
        }
        return result.toString()
    }
}