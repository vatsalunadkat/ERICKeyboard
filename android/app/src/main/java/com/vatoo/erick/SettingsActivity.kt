package com.vatoo.erick

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.vatoo.erick.ui.theme.ERICKTheme

class SettingsActivity : ComponentActivity() {
    private lateinit var preferencesManager: PreferencesManager
    private lateinit var layoutPreferences: LayoutPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        preferencesManager = PreferencesManager(this)
        layoutPreferences = LayoutPreferences(this)

        setContent {
            val themeMode by preferencesManager.themeMode.collectAsState(initial = PreferencesManager.THEME_SYSTEM)
            ERICKTheme(themeMode = themeMode) {
                SettingsScreen(
                    preferencesManager = preferencesManager,
                    layoutPreferences = layoutPreferences,
                    onClose = { finish() }
                )
            }
        }
    }
}

