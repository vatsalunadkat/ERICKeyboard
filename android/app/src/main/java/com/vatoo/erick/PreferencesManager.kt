package com.vatoo.erick

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

class PreferencesManager(private val context: Context) {
    companion object {
        private val LAYOUT_TYPE_KEY = stringPreferencesKey("layout_type")
        private val DARK_THEME_KEY = booleanPreferencesKey("dark_theme")
        private val COLORBLIND_MODE_KEY = booleanPreferencesKey("colorblind_mode")
        private val LEFT_HANDED_MODE_KEY = booleanPreferencesKey("left_handed_mode")

        const val LAYOUT_LOGICAL = "logical"
        const val LAYOUT_EFFICIENCY = "efficiency"
    }

    val layoutType: Flow<String> = context.dataStore.data
        .map { preferences ->
            preferences[LAYOUT_TYPE_KEY] ?: LAYOUT_LOGICAL
        }

    val darkTheme: Flow<Boolean> = context.dataStore.data
        .map { preferences ->
            preferences[DARK_THEME_KEY] ?: false
        }

    val colorblindMode: Flow<Boolean> = context.dataStore.data
        .map { preferences ->
            preferences[COLORBLIND_MODE_KEY] ?: false
        }

    val leftHandedMode: Flow<Boolean> = context.dataStore.data
        .map { preferences ->
            preferences[LEFT_HANDED_MODE_KEY] ?: false
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

    suspend fun setColorblindMode(enabled: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[COLORBLIND_MODE_KEY] = enabled
        }
    }

    suspend fun setLeftHandedMode(enabled: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[LEFT_HANDED_MODE_KEY] = enabled
        }
    }
}
