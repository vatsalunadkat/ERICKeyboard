package com.vatoo.erick

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.staticCompositionLocalOf
import com.vatoo.erick.shared.ErickAppTranslations
import com.vatoo.erick.shared.KeyboardLanguage
import com.vatoo.erick.shared.KeyboardLanguageProfiles

val LocalAppLanguageKey = staticCompositionLocalOf { PreferencesManager.LANGUAGE_ENGLISH }
val LocalAppLocaleTag = staticCompositionLocalOf { KeyboardLanguageProfiles.localeTag(KeyboardLanguage.ENGLISH) }

@Composable
fun ProvideAppLanguage(
    preferencesManager: PreferencesManager,
    content: @Composable () -> Unit
) {
    val languageKey by preferencesManager.keyboardLanguage.collectAsState(initial = PreferencesManager.LANGUAGE_ENGLISH)
    val localeTag = remember(languageKey) {
        KeyboardLanguageProfiles.localeTag(languageKey.toKeyboardLanguage())
    }

    CompositionLocalProvider(
        LocalAppLanguageKey provides languageKey,
        LocalAppLocaleTag provides localeTag,
        content = content
    )
}

fun String.toKeyboardLanguage(): KeyboardLanguage = when (this) {
    PreferencesManager.LANGUAGE_SPANISH -> KeyboardLanguage.SPANISH
    PreferencesManager.LANGUAGE_PORTUGUESE -> KeyboardLanguage.PORTUGUESE
    PreferencesManager.LANGUAGE_FRENCH -> KeyboardLanguage.FRENCH
    PreferencesManager.LANGUAGE_GERMAN -> KeyboardLanguage.GERMAN
    PreferencesManager.LANGUAGE_ITALIAN -> KeyboardLanguage.ITALIAN
    PreferencesManager.LANGUAGE_NORWEGIAN_BOKMAL -> KeyboardLanguage.NORWEGIAN_BOKMAL
    PreferencesManager.LANGUAGE_DANISH -> KeyboardLanguage.DANISH
    PreferencesManager.LANGUAGE_SWEDISH -> KeyboardLanguage.SWEDISH
    PreferencesManager.LANGUAGE_FINNISH -> KeyboardLanguage.FINNISH
    else -> KeyboardLanguage.ENGLISH
}

fun erickText(languageKey: String, english: String): String {
    return ErickAppTranslations.text(languageKey.toKeyboardLanguage(), english)
}

fun englishLanguageDisplayName(languageKey: String): String = when (languageKey) {
    PreferencesManager.LANGUAGE_SPANISH -> "Spanish"
    PreferencesManager.LANGUAGE_PORTUGUESE -> "Portuguese"
    PreferencesManager.LANGUAGE_FRENCH -> "French"
    PreferencesManager.LANGUAGE_GERMAN -> "German"
    PreferencesManager.LANGUAGE_ITALIAN -> "Italian"
    PreferencesManager.LANGUAGE_NORWEGIAN_BOKMAL -> "Norwegian Bokmal"
    PreferencesManager.LANGUAGE_DANISH -> "Danish"
    PreferencesManager.LANGUAGE_SWEDISH -> "Swedish"
    PreferencesManager.LANGUAGE_FINNISH -> "Finnish"
    else -> "English"
}