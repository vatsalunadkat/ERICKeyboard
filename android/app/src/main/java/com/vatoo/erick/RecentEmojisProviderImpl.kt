package com.vatoo.erick

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import org.json.JSONArray

class RecentEmojisProviderImpl(
    private val preferencesManager: PreferencesManager,
    private val coroutineScope: CoroutineScope,
) {
    companion object {
        private const val MAX_RECENTS = 32
    }

    private val _recentEmojis = MutableStateFlow<List<String>>(emptyList())
    val recentEmojis: StateFlow<List<String>> = _recentEmojis.asStateFlow()

    init {
        preferencesManager.recentEmojis
            .onEach { serialized ->
                _recentEmojis.value = decode(serialized)
            }
            .launchIn(coroutineScope)
    }

    fun recordRecent(text: String): List<String> {
        if (text.isBlank()) {
            return _recentEmojis.value
        }

        val updated = buildList {
            add(text)
            _recentEmojis.value.forEach { item ->
                if (item != text && size < MAX_RECENTS) {
                    add(item)
                }
            }
        }

        _recentEmojis.value = updated
        coroutineScope.launch {
            preferencesManager.setRecentEmojis(encode(updated))
        }
        return updated
    }

    private fun decode(serialized: String): List<String> {
        if (serialized.isBlank()) {
            return emptyList()
        }

        return try {
            val array = JSONArray(serialized)
            buildList {
                for (index in 0 until array.length()) {
                    val item = array.optString(index)
                    if (item.isNotBlank() && size < MAX_RECENTS) {
                        add(item)
                    }
                }
            }
        } catch (_: Exception) {
            emptyList()
        }
    }

    private fun encode(items: List<String>): String {
        val array = JSONArray()
        items.take(MAX_RECENTS).forEach(array::put)
        return array.toString()
    }
}