package com.vatoo.erick

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import com.vatoo.erick.shared.CustomLayout
import com.vatoo.erick.shared.CustomLayoutManager
import kotlinx.coroutines.launch

@Composable
fun SettingsScreen(
    preferencesManager: PreferencesManager,
    layoutPreferences: LayoutPreferences,
    onClose: () -> Unit
) {
    val layoutType by preferencesManager.layoutType.collectAsState(initial = PreferencesManager.LAYOUT_LOGICAL)
    val darkTheme by preferencesManager.darkTheme.collectAsState(initial = false)
    val themeMode by preferencesManager.themeMode.collectAsState(initial = PreferencesManager.THEME_SYSTEM)
    val colorblindMode by preferencesManager.colorblindMode.collectAsState(initial = false)
    val colorPalette by preferencesManager.colorPalette.collectAsState(initial = PreferencesManager.PALETTE_OKABE_ITO)
    val leftHandedMode by preferencesManager.leftHandedMode.collectAsState(initial = false)
    val customLayoutId by preferencesManager.customLayoutId.collectAsState(initial = "")
    val fontPreference by preferencesManager.fontPreference.collectAsState(initial = PreferencesManager.FONT_SYSTEM)
    val customPaletteColors by preferencesManager.customPaletteColors.collectAsState(initial = PreferencesManager.DEFAULT_CUSTOM_COLORS)
    val hapticFeedback by preferencesManager.hapticFeedback.collectAsState(initial = false)
    val typingSounds by preferencesManager.typingSounds.collectAsState(initial = false)
    val inputMode by preferencesManager.inputMode.collectAsState(initial = PreferencesManager.INPUT_MODE_INSTANT)
    val sixSectionDial by preferencesManager.sixSectionDial.collectAsState(initial = false)
    val controllerDeadZone by preferencesManager.controllerDeadZone.collectAsState(initial = PreferencesManager.DEFAULT_CONTROLLER_DEAD_ZONE)
    val controllerYAxisInverted by preferencesManager.controllerYAxisInverted.collectAsState(initial = false)

    val scope = rememberCoroutineScope()

    val customLayoutManager = remember {
        CustomLayoutManager(preferencesManager.createCustomLayoutStorage()).also { it.loadAll() }
    }
    var customLayouts by remember { mutableStateOf(customLayoutManager.getAll()) }

    var currentScreen by remember { mutableStateOf<SettingsNav>(SettingsNav.Main) }

    when (val nav = currentScreen) {
        is SettingsNav.Main -> MainSettingsContent(
            preferencesManager = preferencesManager,
            layoutType = layoutType,
            darkTheme = darkTheme,
            themeMode = themeMode,
            fontPreference = fontPreference,
            colorblindMode = colorblindMode,
            colorPalette = colorPalette,
            customPaletteColors = customPaletteColors,
            leftHandedMode = leftHandedMode,
            hapticFeedback = hapticFeedback,
            typingSounds = typingSounds,
            inputMode = inputMode,
            sixSectionDial = sixSectionDial,
            controllerDeadZone = controllerDeadZone,
            controllerYAxisInverted = controllerYAxisInverted,
            customLayoutId = customLayoutId,
            customLayouts = customLayouts,
            scope = scope,
            onClose = onClose,
            onManageCustomLayouts = { currentScreen = SettingsNav.CustomLayoutList },
            onEditCustomLayout = { layout -> currentScreen = SettingsNav.CustomLayoutEditor(layout) },
            onEditCustomPalette = { currentScreen = SettingsNav.CustomPaletteEditor }
        )
        is SettingsNav.CustomLayoutList -> CustomLayoutListScreen(
            customLayoutManager = customLayoutManager,
            customLayouts = customLayouts,
            onLayoutsChanged = { customLayouts = customLayoutManager.getAll() },
            onEditLayout = { layout -> currentScreen = SettingsNav.CustomLayoutEditor(layout) },
            onBack = { currentScreen = SettingsNav.Main }
        )
        is SettingsNav.CustomLayoutEditor -> CustomLayoutEditorScreen(
            layout = nav.layout,
            colorblindMode = colorblindMode,
            colorPalette = colorPalette,
            onSave = { updated ->
                customLayoutManager.save(updated)
                customLayouts = customLayoutManager.getAll()
                currentScreen = SettingsNav.CustomLayoutList
            },
            onBack = { currentScreen = SettingsNav.CustomLayoutList }
        )
        is SettingsNav.CustomPaletteEditor -> CustomPaletteEditorScreen(
            initialColors = customPaletteColors,
            onSave = { colorsStr ->
                scope.launch { preferencesManager.setCustomPaletteColors(colorsStr) }
                currentScreen = SettingsNav.Main
            },
            onBack = { currentScreen = SettingsNav.Main }
        )
    }
}

sealed class SettingsNav {
    data object Main : SettingsNav()
    data object CustomLayoutList : SettingsNav()
    data class CustomLayoutEditor(val layout: CustomLayout) : SettingsNav()
    data object CustomPaletteEditor : SettingsNav()
}