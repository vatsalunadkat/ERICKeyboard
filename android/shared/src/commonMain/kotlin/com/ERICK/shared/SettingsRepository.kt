package com.ERICK.shared

import kotlinx.coroutines.flow.Flow

interface SettingsRepository {
    val layoutType: Flow<String>
    val darkTheme: Flow<Boolean>
    val colorblindMode: Flow<Boolean>
    val leftHandedMode: Flow<Boolean>

    fun setLayoutType(layoutType: String)
    fun setDarkTheme(enabled: Boolean)
    fun setColorblindMode(enabled: Boolean)
    fun setLeftHandedMode(enabled: Boolean)
}
