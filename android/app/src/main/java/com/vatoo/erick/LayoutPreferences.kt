package com.vatoo.erick


import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.layoutDataStore by preferencesDataStore(name = "keyboard_settings")

enum class KeyboardLayout {
    LOGICAL,
    EFFICIENCY
}

class LayoutPreferences(private val context: Context) {

    private val KEY_LAYOUT = stringPreferencesKey("keyboard_layout")

    // Read the saved layout (default = LOGICAL)
    val selectedLayout: Flow<KeyboardLayout> =
        context.layoutDataStore.data.map { prefs ->
            when (prefs[KEY_LAYOUT]) {
                KeyboardLayout.EFFICIENCY.name -> KeyboardLayout.EFFICIENCY
                else -> KeyboardLayout.LOGICAL
            }
        }

    // Save the layout
    suspend fun setLayout(layout: KeyboardLayout) {
        context.layoutDataStore.edit { prefs ->
            prefs[KEY_LAYOUT] = layout.name
        }
    }
}
