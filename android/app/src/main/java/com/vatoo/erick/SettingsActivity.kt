package com.vatoo.erick

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.vatoo.erick.ui.theme.ERICKTheme

class SettingsActivity : ComponentActivity() {
    private lateinit var preferencesManager: PreferencesManager
    private lateinit var layoutPreferences: LayoutPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        preferencesManager = PreferencesManager(this)
        layoutPreferences = LayoutPreferences(this)

        setContent {
            ERICKTheme {
                SettingsScreen(
                    preferencesManager = preferencesManager,
                    layoutPreferences = layoutPreferences,
                    onClose = { finish() }
                )
            }
        }
    }
}

