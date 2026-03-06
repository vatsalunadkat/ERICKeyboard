package com.vatoo.erick

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class SettingsViewModel(app: Application) : AndroidViewModel(app) {
    private val prefs = LayoutPreferences(app.applicationContext)

    val selectedLayout: StateFlow<KeyboardLayout> =
        prefs.selectedLayout.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = KeyboardLayout.LOGICAL
        )

    fun selectLayout(layout: KeyboardLayout) {
        viewModelScope.launch {
            prefs.setLayout(layout)
        }
    }
}
