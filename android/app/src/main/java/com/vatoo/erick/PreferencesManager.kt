package com.vatoo.erick

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.vatoo.erick.shared.CustomLayoutStorage
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

class PreferencesManager(private val context: Context) {
    companion object {
        private val LAYOUT_TYPE_KEY = stringPreferencesKey("layout_type")
        private val DARK_THEME_KEY = booleanPreferencesKey("dark_theme")
        private val THEME_MODE_KEY = stringPreferencesKey("theme_mode")
        private val COLORBLIND_MODE_KEY = booleanPreferencesKey("colorblind_mode")
        private val COLOR_PALETTE_KEY = stringPreferencesKey("color_palette")
        private val LEFT_HANDED_MODE_KEY = booleanPreferencesKey("left_handed_mode")
        private val CUSTOM_LAYOUT_ID_KEY = stringPreferencesKey("custom_layout_id")
        private val CUSTOM_LAYOUTS_JSON_KEY = stringPreferencesKey("custom_layouts_json")
        private val FONT_PREFERENCE_KEY = stringPreferencesKey("font_preference")

        const val LAYOUT_LOGICAL = "logical"
        const val LAYOUT_EFFICIENCY = "efficiency"
        const val LAYOUT_CUSTOM = "custom"

        const val THEME_SYSTEM = "system"
        const val THEME_LIGHT = "light"
        const val THEME_DARK = "dark"

        const val FONT_SYSTEM = "system"
        const val FONT_VERDANA = "verdana"
        const val FONT_GEORGIA = "georgia"
        const val FONT_OPENDYSLEXIC = "opendyslexic"

        const val PALETTE_OKABE_ITO = "okabe_ito"
        const val PALETTE_DEUTERANOPIA = "deuteranopia"
        const val PALETTE_PROTANOPIA = "protanopia"
        const val PALETTE_TRITANOPIA = "tritanopia"
        const val PALETTE_PASTEL = "pastel"
    }

    val layoutType: Flow<String> = context.dataStore.data
        .map { preferences ->
            preferences[LAYOUT_TYPE_KEY] ?: LAYOUT_LOGICAL
        }

    val darkTheme: Flow<Boolean> = context.dataStore.data
        .map { preferences ->
            preferences[DARK_THEME_KEY] ?: false
        }

    val themeMode: Flow<String> = context.dataStore.data
        .map { preferences ->
            preferences[THEME_MODE_KEY] ?: THEME_SYSTEM
        }

    val colorblindMode: Flow<Boolean> = context.dataStore.data
        .map { preferences ->
            preferences[COLORBLIND_MODE_KEY] ?: false
        }

    val colorPalette: Flow<String> = context.dataStore.data
        .map { preferences ->
            preferences[COLOR_PALETTE_KEY] ?: PALETTE_OKABE_ITO
        }

    val leftHandedMode: Flow<Boolean> = context.dataStore.data
        .map { preferences ->
            preferences[LEFT_HANDED_MODE_KEY] ?: false
        }

    val customLayoutId: Flow<String> = context.dataStore.data
        .map { preferences ->
            preferences[CUSTOM_LAYOUT_ID_KEY] ?: ""
        }

    val fontPreference: Flow<String> = context.dataStore.data
        .map { preferences ->
            preferences[FONT_PREFERENCE_KEY] ?: FONT_SYSTEM
        }

    suspend fun setLayoutType(layoutType: String) {
        context.dataStore.edit { preferences ->
            preferences[LAYOUT_TYPE_KEY] = layoutType
        }
    }

    suspend fun setDarkTheme(enabled: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[DARK_THEME_KEY] = enabled
        }
    }

    suspend fun setThemeMode(mode: String) {
        context.dataStore.edit { preferences ->
            preferences[THEME_MODE_KEY] = mode
        }
    }

    suspend fun setColorblindMode(enabled: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[COLORBLIND_MODE_KEY] = enabled
        }
    }

    suspend fun setColorPalette(palette: String) {
        context.dataStore.edit { preferences ->
            preferences[COLOR_PALETTE_KEY] = palette
        }
    }

    suspend fun setLeftHandedMode(enabled: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[LEFT_HANDED_MODE_KEY] = enabled
        }
    }

    suspend fun setCustomLayoutId(id: String) {
        context.dataStore.edit { preferences ->
            preferences[CUSTOM_LAYOUT_ID_KEY] = id
        }
    }

    suspend fun setFontPreference(font: String) {
        context.dataStore.edit { preferences ->
            preferences[FONT_PREFERENCE_KEY] = font
        }
    }

    /** Returns a [CustomLayoutStorage] backed by this DataStore. */
    fun createCustomLayoutStorage(): CustomLayoutStorage {
        return AndroidCustomLayoutStorage(context)
    }
}

/**
 * Platform storage for custom layouts using SharedPreferences (synchronous I/O
 * since CustomLayoutStorage interface is synchronous for KMP compatibility).
 */
class AndroidCustomLayoutStorage(private val context: Context) : CustomLayoutStorage {
    private val prefs by lazy {
        context.getSharedPreferences("custom_layouts", Context.MODE_PRIVATE)
    }

    override fun loadAllLayoutsJson(): String {
        return prefs.getString("layouts_json", "") ?: ""
    }

    override fun saveAllLayoutsJson(json: String) {
        prefs.edit().putString("layouts_json", json).apply()
    }
}
