package com.vatoo.erick

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.core.view.WindowCompat
import com.vatoo.erick.ui.theme.ERICKTheme

class SettingsActivity : ComponentActivity() {
    private lateinit var preferencesManager: PreferencesManager
    private lateinit var layoutPreferences: LayoutPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WindowCompat.enableEdgeToEdge(window)
        preferencesManager = PreferencesManager(this)
        layoutPreferences = LayoutPreferences(this)

        setContent {
            val themeMode by preferencesManager.themeMode.collectAsState(initial = PreferencesManager.THEME_SYSTEM)
            ProvideAppLanguage(preferencesManager = preferencesManager) {
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
}

