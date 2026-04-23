package com.vatoo.erick

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.floatPreferencesKey
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.core.stringSetPreferencesKey
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
        private val CUSTOM_PALETTE_COLORS_KEY = stringPreferencesKey("custom_palette_colors")
        private val HAPTIC_FEEDBACK_KEY = booleanPreferencesKey("haptic_feedback")
        private val TYPING_SOUNDS_KEY = booleanPreferencesKey("typing_sounds")
        private val INPUT_MODE_KEY = stringPreferencesKey("input_mode")
        private val SIX_SECTION_DIAL_KEY = booleanPreferencesKey("six_section_dial")
        private val CONTROLLER_DEAD_ZONE_KEY = floatPreferencesKey("controller_dead_zone")
        private val CONTROLLER_Y_AXIS_INVERTED_KEY = booleanPreferencesKey("controller_y_axis_inverted")
        private val ONBOARDING_COMPLETED_KEY = booleanPreferencesKey("onboarding_completed")
        private val ONBOARDING_DISMISSED_KEY = booleanPreferencesKey("onboarding_dismissed")
        private val ONBOARDING_STEP_KEY = intPreferencesKey("onboarding_step")
        private val PRACTICE_ATTEMPTED_LESSONS_KEY = stringSetPreferencesKey("practice_attempted_lessons")
        private val PRACTICE_COMPLETED_LESSONS_KEY = stringSetPreferencesKey("practice_completed_lessons")

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
        const val PALETTE_CUSTOM = "custom"

        const val DEFAULT_CUSTOM_COLORS = "#E60012,#F39800,#FFF100,#009944,#0068B7,#1D2088,#920783,#000000"

        const val INPUT_MODE_INSTANT = "instant"
        const val INPUT_MODE_CONFIRM = "confirm"
        const val INPUT_MODE_ASSISTED = "assisted"

        const val DEFAULT_CONTROLLER_DEAD_ZONE = 0.25f
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

    val customPaletteColors: Flow<String> = context.dataStore.data
        .map { preferences ->
            preferences[CUSTOM_PALETTE_COLORS_KEY] ?: DEFAULT_CUSTOM_COLORS
        }

    val hapticFeedback: Flow<Boolean> = context.dataStore.data
        .map { preferences ->
            preferences[HAPTIC_FEEDBACK_KEY] ?: false
        }

    val typingSounds: Flow<Boolean> = context.dataStore.data
        .map { preferences ->
            preferences[TYPING_SOUNDS_KEY] ?: false
        }

    val inputMode: Flow<String> = context.dataStore.data
        .map { preferences ->
            preferences[INPUT_MODE_KEY] ?: INPUT_MODE_INSTANT
        }

    val sixSectionDial: Flow<Boolean> = context.dataStore.data
        .map { preferences ->
            preferences[SIX_SECTION_DIAL_KEY] ?: false
        }

    val controllerDeadZone: Flow<Float> = context.dataStore.data
        .map { preferences ->
            preferences[CONTROLLER_DEAD_ZONE_KEY] ?: DEFAULT_CONTROLLER_DEAD_ZONE
        }

    val controllerYAxisInverted: Flow<Boolean> = context.dataStore.data
        .map { preferences ->
            preferences[CONTROLLER_Y_AXIS_INVERTED_KEY] ?: false
        }

    val onboardingCompleted: Flow<Boolean> = context.dataStore.data
        .map { preferences ->
            preferences[ONBOARDING_COMPLETED_KEY] ?: false
        }

    val onboardingDismissed: Flow<Boolean> = context.dataStore.data
        .map { preferences ->
            preferences[ONBOARDING_DISMISSED_KEY] ?: false
        }

    val onboardingStep: Flow<Int> = context.dataStore.data
        .map { preferences ->
            preferences[ONBOARDING_STEP_KEY] ?: 0
        }

    val practiceAttemptedLessons: Flow<Set<String>> = context.dataStore.data
        .map { preferences ->
            preferences[PRACTICE_ATTEMPTED_LESSONS_KEY] ?: emptySet()
        }

    val practiceCompletedLessons: Flow<Set<String>> = context.dataStore.data
        .map { preferences ->
            preferences[PRACTICE_COMPLETED_LESSONS_KEY] ?: emptySet()
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

    suspend fun setCustomPaletteColors(colors: String) {
        context.dataStore.edit { preferences ->
            preferences[CUSTOM_PALETTE_COLORS_KEY] = colors
        }
    }

    suspend fun setHapticFeedback(enabled: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[HAPTIC_FEEDBACK_KEY] = enabled
        }
    }

    suspend fun setTypingSounds(enabled: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[TYPING_SOUNDS_KEY] = enabled
        }
    }

    suspend fun setInputMode(mode: String) {
        context.dataStore.edit { preferences ->
            preferences[INPUT_MODE_KEY] = mode
        }
    }

    suspend fun setSixSectionDial(enabled: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[SIX_SECTION_DIAL_KEY] = enabled
        }
    }

    suspend fun setControllerDeadZone(deadZone: Float) {
        context.dataStore.edit { preferences ->
            preferences[CONTROLLER_DEAD_ZONE_KEY] = deadZone.coerceIn(0f, 1f)
        }
    }

    suspend fun setControllerYAxisInverted(enabled: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[CONTROLLER_Y_AXIS_INVERTED_KEY] = enabled
        }
    }

    suspend fun setOnboardingCompleted(completed: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[ONBOARDING_COMPLETED_KEY] = completed
            if (completed) {
                preferences[ONBOARDING_DISMISSED_KEY] = false
                preferences[ONBOARDING_STEP_KEY] = 0
            }
        }
    }

    suspend fun setOnboardingDismissed(dismissed: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[ONBOARDING_DISMISSED_KEY] = dismissed
        }
    }

    suspend fun setOnboardingStep(step: Int) {
        context.dataStore.edit { preferences ->
            preferences[ONBOARDING_STEP_KEY] = step.coerceAtLeast(0)
        }
    }

    suspend fun markPracticeLessonAttempted(lessonId: String) {
        context.dataStore.edit { preferences ->
            val updated = preferences[PRACTICE_ATTEMPTED_LESSONS_KEY].orEmpty().toMutableSet()
            updated.add(lessonId)
            preferences[PRACTICE_ATTEMPTED_LESSONS_KEY] = updated
        }
    }

    suspend fun markPracticeLessonCompleted(lessonId: String) {
        context.dataStore.edit { preferences ->
            val attempted = preferences[PRACTICE_ATTEMPTED_LESSONS_KEY].orEmpty().toMutableSet()
            attempted.add(lessonId)
            preferences[PRACTICE_ATTEMPTED_LESSONS_KEY] = attempted

            val completed = preferences[PRACTICE_COMPLETED_LESSONS_KEY].orEmpty().toMutableSet()
            completed.add(lessonId)
            preferences[PRACTICE_COMPLETED_LESSONS_KEY] = completed
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
