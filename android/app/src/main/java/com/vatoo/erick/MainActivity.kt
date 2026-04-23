package com.vatoo.erick

import android.content.Context
import android.os.Bundle
import android.provider.Settings
import android.view.inputmethod.InputMethodManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import com.vatoo.erick.ui.theme.ERICKTheme

class MainActivity : ComponentActivity() {
    private var isKeyboardEnabledState = mutableStateOf(false)
    private var isKeyboardCurrentState = mutableStateOf(false)
    private lateinit var preferencesManager: PreferencesManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        updateKeyboardStatus()
        preferencesManager = PreferencesManager(this)
        setContent {
            val themeMode by preferencesManager.themeMode.collectAsState(initial = PreferencesManager.THEME_SYSTEM)
            ERICKTheme(themeMode = themeMode) {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    MainScreen(
                        modifier = Modifier.padding(innerPadding),
                        isKeyboardEnabled = isKeyboardEnabledState,
                        isKeyboardCurrent = isKeyboardCurrentState
                    )
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        updateKeyboardStatus()
    }

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        if (hasFocus) {
            updateKeyboardStatus()
        }
    }

    private fun updateKeyboardStatus() {
        isKeyboardEnabledState.value = isKeyboardEnabled(this)
        isKeyboardCurrentState.value = isCurrentInputMethod(this)
    }
}

fun isKeyboardEnabled(context: Context): Boolean {
    val imeManager = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    val enabledIMEs = imeManager.enabledInputMethodList
    return enabledIMEs.any { it.packageName == context.packageName }
}

fun isCurrentInputMethod(context: Context): Boolean {
    val imeManager = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    val currentIme = Settings.Secure.getString(
        context.contentResolver,
        Settings.Secure.DEFAULT_INPUT_METHOD
    )
    return currentIme?.contains(context.packageName) == true
}
