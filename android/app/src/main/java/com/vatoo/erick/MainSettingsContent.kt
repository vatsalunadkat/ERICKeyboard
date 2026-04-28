package com.vatoo.erick

import android.content.Context
import android.content.Intent
import android.view.inputmethod.InputMethodManager
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Slider
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import com.vatoo.erick.shared.ColorPaletteType
import com.vatoo.erick.shared.ColorPalettes
import com.vatoo.erick.shared.CustomLayout
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun MainSettingsContent(
    preferencesManager: PreferencesManager,
    layoutType: String,
    darkTheme: Boolean,
    themeMode: String,
    fontPreference: String,
    keyboardLanguage: String,
    colorblindMode: Boolean,
    colorPalette: String,
    customPaletteColors: String,
    leftHandedMode: Boolean,
    hapticFeedback: Boolean,
    typingSounds: Boolean,
    inputMode: String,
    predictionDomain: String,
    sixSectionDial: Boolean,
    controllerDeadZone: Float,
    controllerYAxisInverted: Boolean,
    customLayoutId: String,
    customLayouts: List<CustomLayout>,
    scope: CoroutineScope,
    onClose: () -> Unit,
    onManageCustomLayouts: () -> Unit,
    onEditCustomLayout: (CustomLayout) -> Unit,
    onEditCustomPalette: () -> Unit
) {
    var showSetupWizard by remember { mutableStateOf(false) }

    if (showSetupWizard) {
        SetupWizardDialog(
            currentColorPalette = colorPalette,
            onDismiss = { showSetupWizard = false },
            onApply = { recommendation ->
                scope.launch {
                    applySetupWizardRecommendation(preferencesManager, recommendation)
                }
                showSetupWizard = false
            }
        )
    }

    val layoutSummary = when {
        layoutType == PreferencesManager.LAYOUT_CUSTOM -> {
            customLayouts.firstOrNull { it.id == customLayoutId }?.name ?: "Custom layout"
        }
        layoutType == PreferencesManager.LAYOUT_EFFICIENCY -> "Efficiency"
        else -> "Logical (A-Z)"
    }
    val languageSummary = keyboardLanguageDisplayName(keyboardLanguage)
    val appearanceSummary = buildList {
        add(
            when (themeMode) {
                PreferencesManager.THEME_LIGHT -> "Light"
                PreferencesManager.THEME_DARK -> "Dark"
                else -> "System theme"
            }
        )
        if (fontPreference != PreferencesManager.FONT_SYSTEM) {
            add(
                when (fontPreference) {
                    PreferencesManager.FONT_GEORGIA -> "Georgia"
                    PreferencesManager.FONT_OPENDYSLEXIC -> "OpenDyslexic"
                    else -> "Verdana"
                }
            )
        }
        if (colorPalette == PreferencesManager.PALETTE_PASTEL) add("Pastel colors")
        if (colorPalette == PreferencesManager.PALETTE_CUSTOM) add("Custom colors")
    }.joinToString(" • ")
    val accessibilitySummary = buildList {
        if (colorblindMode) add("Colorblind palette on")
        if (leftHandedMode) add("Left-handed mode on")
        if (isEmpty()) add("Standard setup")
    }.joinToString(" • ")
    val feedbackSummary = buildList {
        if (hapticFeedback) add("Haptics on")
        if (typingSounds) add("Sounds on")
        if (isEmpty()) add("Feedback off")
    }.joinToString(" • ")
    val inputModeSummary = when (inputMode) {
        PreferencesManager.INPUT_MODE_CONFIRM -> "Steady Type"
        PreferencesManager.INPUT_MODE_ASSISTED -> "One-Handed"
        else -> "Quick Type"
    }
    val predictionSummary = predictionDomainDisplayName(predictionDomain)

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Keyboard Settings") },
                navigationIcon = {
                    IconButton(onClick = onClose) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                    navigationIconContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        }
    ) { paddingValues ->
        var expandedSection by remember { mutableStateOf<String?>(null) }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            val context = LocalContext.current
            SectionOverviewCard(
                title = "Start with the essentials",
                summary = "Most people only need Dial Mode, Input Mode, and Accessibility. The rest is optional customization.",
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedButton(
                onClick = {
                    val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                    imm.showInputMethodPicker()
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Switch Keyboard")
            }

            OutlinedButton(
                onClick = { showSetupWizard = true },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Setup Wizard")
            }

            CollapsibleSection(
                title = "Dial Mode",
                summary = if (sixSectionDial) "6-section dial on" else "8-section dial on",
                expanded = expandedSection == "dial_mode",
                onToggle = { expandedSection = if (expandedSection == "dial_mode") null else "dial_mode" }
            ) {
                SettingToggle(
                    title = "6-Section Dial Mode",
                    checked = sixSectionDial,
                    enabled = true,
                    onCheckedChange = { checked ->
                        scope.launch { preferencesManager.setSixSectionDial(checked) }
                    }
                )
                Text(
                    text = "Use 6 larger segments instead of 8. Larger targets improve accuracy but change the chord layout. Symbols are accessed via N single-swipe.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(start = 8.dp, end = 8.dp, bottom = 8.dp)
                )
            }

            CollapsibleSection(
                title = "Language",
                summary = languageSummary,
                expanded = expandedSection == "language",
                onToggle = { expandedSection = if (expandedSection == "language") null else "language" }
            ) {
                Text(
                    text = "Languages are currently logical-first. English keeps the dedicated efficiency layout, while the other supported languages use language-aware logical maps and symbol overlays.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                keyboardLanguageOptions.forEach { option ->
                    LayoutRadioOption(
                        title = option.label,
                        subtitle = option.subtitle,
                        selected = keyboardLanguage == option.value,
                        enabled = true,
                        onClick = { scope.launch { preferencesManager.setKeyboardLanguage(option.value) } }
                    )
                }
            }

            CollapsibleSection(
                title = "Keyboard Layout",
                summary = layoutSummary,
                expanded = expandedSection == "layout",
                onToggle = { expandedSection = if (expandedSection == "layout") null else "layout" }
            ) {
                if (keyboardLanguage != PreferencesManager.LANGUAGE_ENGLISH) {
                    Text(
                        text = "Non-English languages currently fall back to the language-aware logical layout even if Efficiency stays selected.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                }

                LayoutRadioOption(
                    title = "Logical (A-Z)",
                    subtitle = null,
                    selected = layoutType == PreferencesManager.LAYOUT_LOGICAL,
                    enabled = true,
                    onClick = {
                        scope.launch {
                            preferencesManager.setLayoutType(PreferencesManager.LAYOUT_LOGICAL)
                        }
                    }
                )

                LayoutRadioOption(
                    title = "Efficiency",
                    subtitle = "Optimized for English letter frequency",
                    selected = layoutType == PreferencesManager.LAYOUT_EFFICIENCY,
                    enabled = true,
                    onClick = {
                        scope.launch {
                            preferencesManager.setLayoutType(PreferencesManager.LAYOUT_EFFICIENCY)
                        }
                    }
                )

                customLayouts.forEach { customLayout ->
                    LayoutRadioOption(
                        title = customLayout.name,
                        subtitle = "Custom layout",
                        selected = layoutType == PreferencesManager.LAYOUT_CUSTOM && customLayoutId == customLayout.id,
                        enabled = true,
                        onClick = {
                            scope.launch {
                                preferencesManager.setCustomLayoutId(customLayout.id)
                                preferencesManager.setLayoutType(PreferencesManager.LAYOUT_CUSTOM)
                            }
                        }
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedButton(
                    onClick = onManageCustomLayouts,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(Icons.Default.Edit, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Manage Custom Layouts")
                }
            }

            CollapsibleSection(
                title = "Appearance",
                summary = appearanceSummary.ifBlank { "System theme" },
                expanded = expandedSection == "appearance",
                onToggle = { expandedSection = if (expandedSection == "appearance") null else "appearance" }
            ) {
                Text(
                    text = "Theme",
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier.padding(bottom = 4.dp)
                )

                SingleChoiceSegmentedButtonRow(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    val themeOptions = listOf(
                        PreferencesManager.THEME_SYSTEM to "System",
                        PreferencesManager.THEME_LIGHT to "Light",
                        PreferencesManager.THEME_DARK to "Dark"
                    )
                    themeOptions.forEachIndexed { index, (key, label) ->
                        SegmentedButton(
                            selected = themeMode == key,
                            onClick = { scope.launch { preferencesManager.setThemeMode(key) } },
                            shape = SegmentedButtonDefaults.itemShape(index = index, count = themeOptions.size)
                        ) {
                            Text(label)
                        }
                    }
                }

                HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

                val isCustomFont = fontPreference != PreferencesManager.FONT_SYSTEM
                SettingToggle(
                    title = "Custom Font",
                    checked = isCustomFont,
                    enabled = true,
                    onCheckedChange = { checked ->
                        scope.launch {
                            if (!checked) {
                                preferencesManager.setFontPreference(PreferencesManager.FONT_SYSTEM)
                            } else {
                                preferencesManager.setFontPreference(PreferencesManager.FONT_VERDANA)
                            }
                        }
                    }
                )

                if (isCustomFont) {
                    val fontOptions = listOf(
                        Triple(
                            PreferencesManager.FONT_VERDANA,
                            "Verdana",
                            FontFamily(android.graphics.Typeface.create("sans-serif", android.graphics.Typeface.NORMAL))
                        ),
                        Triple(
                            PreferencesManager.FONT_GEORGIA,
                            "Georgia",
                            FontFamily(android.graphics.Typeface.create("serif", android.graphics.Typeface.NORMAL))
                        ),
                        Triple(
                            PreferencesManager.FONT_OPENDYSLEXIC,
                            "OpenDyslexic",
                            FontFamily(Font(R.font.opendyslexic_regular))
                        )
                    )

                    fontOptions.forEach { (key, name, family) ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    scope.launch { preferencesManager.setFontPreference(key) }
                                }
                                .padding(vertical = 6.dp, horizontal = 8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            RadioButton(
                                selected = fontPreference == key,
                                onClick = { scope.launch { preferencesManager.setFontPreference(key) } }
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = name,
                                style = MaterialTheme.typography.bodyMedium,
                                fontFamily = family
                            )
                        }
                    }
                }

                HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

                val isCustomColors = colorPalette == PreferencesManager.PALETTE_PASTEL || colorPalette == PreferencesManager.PALETTE_CUSTOM
                SettingToggle(
                    title = "Custom Colors",
                    checked = isCustomColors,
                    enabled = true,
                    onCheckedChange = { checked ->
                        scope.launch {
                            if (checked) {
                                preferencesManager.setColorPalette(PreferencesManager.PALETTE_PASTEL)
                                if (colorblindMode) preferencesManager.setColorblindMode(false)
                            } else {
                                preferencesManager.setColorPalette(PreferencesManager.PALETTE_OKABE_ITO)
                            }
                        }
                    }
                )

                if (isCustomColors) {
                    PaletteRadioOption(
                        title = "Pastel",
                        subtitle = "Softer colors that are easier on the eyes",
                        paletteType = ColorPaletteType.PASTEL,
                        selected = colorPalette == PreferencesManager.PALETTE_PASTEL,
                        onClick = {
                            scope.launch {
                                preferencesManager.setColorPalette(PreferencesManager.PALETTE_PASTEL)
                            }
                        }
                    )

                    CustomPaletteRadioOption(
                        customPaletteColors = customPaletteColors,
                        selected = colorPalette == PreferencesManager.PALETTE_CUSTOM,
                        onSelect = {
                            scope.launch {
                                preferencesManager.setColorPalette(PreferencesManager.PALETTE_CUSTOM)
                            }
                        },
                        onEditColors = onEditCustomPalette
                    )
                }
            }

            CollapsibleSection(
                title = "Accessibility",
                summary = accessibilitySummary,
                expanded = expandedSection == "accessibility",
                onToggle = { expandedSection = if (expandedSection == "accessibility") null else "accessibility" }
            ) {
                SettingToggle(
                    title = "Enable Colorblind Mode",
                    checked = colorblindMode,
                    enabled = true,
                    onCheckedChange = { checked ->
                        scope.launch {
                            preferencesManager.setColorblindMode(checked)
                            if (checked && (colorPalette == PreferencesManager.PALETTE_PASTEL || colorPalette == PreferencesManager.PALETTE_CUSTOM)) {
                                preferencesManager.setColorPalette(PreferencesManager.PALETTE_OKABE_ITO)
                            }
                        }
                    }
                )

                if (colorblindMode) {
                    Text(
                        text = "Select the palette that works best for your type of color vision.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(start = 8.dp, end = 8.dp, bottom = 8.dp)
                    )

                    PaletteRadioOption(
                        title = "Okabe-Ito (Universal)",
                        subtitle = "Recommended for all types of color vision deficiency",
                        paletteType = ColorPaletteType.OKABE_ITO,
                        selected = colorPalette == PreferencesManager.PALETTE_OKABE_ITO,
                        onClick = {
                            scope.launch { preferencesManager.setColorPalette(PreferencesManager.PALETTE_OKABE_ITO) }
                        }
                    )

                    PaletteRadioOption(
                        title = "Deuteranopia (Green-blind)",
                        subtitle = "Optimized for green-blind users",
                        paletteType = ColorPaletteType.DEUTERANOPIA,
                        selected = colorPalette == PreferencesManager.PALETTE_DEUTERANOPIA,
                        onClick = {
                            scope.launch { preferencesManager.setColorPalette(PreferencesManager.PALETTE_DEUTERANOPIA) }
                        }
                    )

                    PaletteRadioOption(
                        title = "Protanopia (Red-blind)",
                        subtitle = "Optimized for red-blind users",
                        paletteType = ColorPaletteType.PROTANOPIA,
                        selected = colorPalette == PreferencesManager.PALETTE_PROTANOPIA,
                        onClick = {
                            scope.launch { preferencesManager.setColorPalette(PreferencesManager.PALETTE_PROTANOPIA) }
                        }
                    )

                    PaletteRadioOption(
                        title = "Tritanopia (Blue-blind)",
                        subtitle = "Optimized for blue-blind users",
                        paletteType = ColorPaletteType.TRITANOPIA,
                        selected = colorPalette == PreferencesManager.PALETTE_TRITANOPIA,
                        onClick = {
                            scope.launch { preferencesManager.setColorPalette(PreferencesManager.PALETTE_TRITANOPIA) }
                        }
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                SettingToggle(
                    title = "Left-Handed Mode",
                    checked = leftHandedMode,
                    enabled = true,
                    onCheckedChange = { checked ->
                        scope.launch { preferencesManager.setLeftHandedMode(checked) }
                    }
                )
            }

            CollapsibleSection(
                title = "Feedback",
                summary = feedbackSummary,
                expanded = expandedSection == "feedback",
                onToggle = { expandedSection = if (expandedSection == "feedback") null else "feedback" }
            ) {
                SettingToggle(
                    title = "Haptic Feedback",
                    checked = hapticFeedback,
                    enabled = true,
                    onCheckedChange = { checked ->
                        scope.launch { preferencesManager.setHapticFeedback(checked) }
                    }
                )
                if (hapticFeedback) {
                    Text(
                        text = "Strong vibration for utility keys, light for letters.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(start = 8.dp, end = 8.dp, bottom = 8.dp)
                    )
                }

                SettingToggle(
                    title = "Typing Sounds",
                    checked = typingSounds,
                    enabled = true,
                    onCheckedChange = { checked ->
                        scope.launch { preferencesManager.setTypingSounds(checked) }
                    }
                )
            }

            CollapsibleSection(
                title = "Input Mode",
                summary = inputModeSummary,
                expanded = expandedSection == "input_mode",
                onToggle = { expandedSection = if (expandedSection == "input_mode") null else "input_mode" }
            ) {
                Text(
                    text = "Choose how chords are triggered when using the dials.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                LayoutRadioOption(
                    title = "Quick Type",
                    subtitle = "Type at full speed. Characters appear as soon as you release either dial.",
                    selected = inputMode == PreferencesManager.INPUT_MODE_INSTANT,
                    enabled = true,
                    onClick = { scope.launch { preferencesManager.setInputMode(PreferencesManager.INPUT_MODE_INSTANT) } }
                )
                LayoutRadioOption(
                    title = "Steady Type",
                    subtitle = "Take your time. Characters appear only after both dials return to center.",
                    selected = inputMode == PreferencesManager.INPUT_MODE_CONFIRM,
                    enabled = true,
                    onClick = { scope.launch { preferencesManager.setInputMode(PreferencesManager.INPUT_MODE_CONFIRM) } }
                )
                LayoutRadioOption(
                    title = "One-Handed",
                    subtitle = "Type with one hand. Lock a direction on the left dial, then swipe the right dial to type.",
                    selected = inputMode == PreferencesManager.INPUT_MODE_ASSISTED,
                    enabled = true,
                    onClick = { scope.launch { preferencesManager.setInputMode(PreferencesManager.INPUT_MODE_ASSISTED) } }
                )
            }

            CollapsibleSection(
                title = "Prediction",
                summary = predictionSummary,
                expanded = expandedSection == "prediction",
                onToggle = { expandedSection = if (expandedSection == "prediction") null else "prediction" }
            ) {
                Text(
                    text = "Predictions stay on-device. Choose a domain pack if you want ERICK to favor a particular vocabulary family.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                LayoutRadioOption(
                    title = "General",
                    subtitle = "Balanced everyday English suggestions.",
                    selected = predictionDomain == PreferencesManager.PREDICTION_DOMAIN_GENERAL,
                    enabled = true,
                    onClick = { scope.launch { preferencesManager.setPredictionDomain(PreferencesManager.PREDICTION_DOMAIN_GENERAL) } }
                )
                LayoutRadioOption(
                    title = "Conversation",
                    subtitle = "Favor quick texting and casual chat vocabulary.",
                    selected = predictionDomain == PreferencesManager.PREDICTION_DOMAIN_CONVERSATION,
                    enabled = true,
                    onClick = { scope.launch { preferencesManager.setPredictionDomain(PreferencesManager.PREDICTION_DOMAIN_CONVERSATION) } }
                )
                LayoutRadioOption(
                    title = "Productivity",
                    subtitle = "Favor work, planning, and follow-up vocabulary.",
                    selected = predictionDomain == PreferencesManager.PREDICTION_DOMAIN_PRODUCTIVITY,
                    enabled = true,
                    onClick = { scope.launch { preferencesManager.setPredictionDomain(PreferencesManager.PREDICTION_DOMAIN_PRODUCTIVITY) } }
                )
                LayoutRadioOption(
                    title = "Accessibility",
                    subtitle = "Favor supportive and assistive-communication vocabulary.",
                    selected = predictionDomain == PreferencesManager.PREDICTION_DOMAIN_ACCESSIBILITY,
                    enabled = true,
                    onClick = { scope.launch { preferencesManager.setPredictionDomain(PreferencesManager.PREDICTION_DOMAIN_ACCESSIBILITY) } }
                )
                LayoutRadioOption(
                    title = "Gaming",
                    subtitle = "Favor game, party, match, and controller-related terms.",
                    selected = predictionDomain == PreferencesManager.PREDICTION_DOMAIN_GAMING,
                    enabled = true,
                    onClick = { scope.launch { preferencesManager.setPredictionDomain(PreferencesManager.PREDICTION_DOMAIN_GAMING) } }
                )
            }

            CollapsibleSection(
                title = "Controller",
                summary = "Dead zone ${(controllerDeadZone * 100).roundToInt()}%${if (controllerYAxisInverted) " • Y inverted" else ""}",
                expanded = expandedSection == "controller",
                onToggle = { expandedSection = if (expandedSection == "controller") null else "controller" }
            ) {
                Text(
                    text = "Use diagnostics to tune controller dead zone, Y-axis inversion, and one-handed behavior with the same shared controller logic the IME uses.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                Text(
                    text = "Dead zone: ${(controllerDeadZone * 100).roundToInt()}%",
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(start = 8.dp, end = 8.dp, bottom = 4.dp)
                )

                Slider(
                    value = controllerDeadZone,
                    onValueChange = { value ->
                        scope.launch { preferencesManager.setControllerDeadZone(value) }
                    },
                    valueRange = 0f..0.6f,
                    modifier = Modifier.padding(horizontal = 8.dp)
                )

                SettingToggle(
                    title = "Invert Controller Y-Axis",
                    checked = controllerYAxisInverted,
                    enabled = true,
                    onCheckedChange = { checked ->
                        scope.launch { preferencesManager.setControllerYAxisInverted(checked) }
                    }
                )

                OutlinedButton(
                    onClick = {
                        context.startActivity(Intent(context, ControllerDiagnosticsActivity::class.java))
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Open Controller Diagnostics")
                }
            }

            CollapsibleSection(
                title = "Privacy & Security",
                summary = "No typing data leaves your device",
                expanded = expandedSection == "privacy",
                onToggle = { expandedSection = if (expandedSection == "privacy") null else "privacy" }
            ) {
                Text(
                    text = "Your privacy is our priority. ERICK:",
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                Text(
                    text = "Does not collect any text you type\n" +
                        "Does not store passwords or personal data\n" +
                        "Does not transmit any data from your device\n" +
                        "Only stores your keyboard preferences locally\n" +
                        "Has no internet permissions\n" +
                        "Is source available for transparency",
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
    }
}

@Composable
private fun SetupWizardDialog(
    currentColorPalette: String,
    onDismiss: () -> Unit,
    onApply: (SetupWizardRecommendation) -> Unit
) {
    var hardware by remember { mutableStateOf(SetupWizardHardware.TOUCH) }
    var targetPreference by remember { mutableStateOf(SetupWizardTargetPreference.LARGER_TARGETS) }
    var typingPreference by remember { mutableStateOf(SetupWizardTypingPreference.FASTEST) }
    var handPreference by remember { mutableStateOf(SetupWizardHandPreference.RIGHT) }
    var accessibilityPreference by remember { mutableStateOf(SetupWizardAccessibilityPreference.STANDARD) }

    val recommendation = buildSetupWizardRecommendation(
        hardware = hardware,
        targetPreference = targetPreference,
        typingPreference = typingPreference,
        handPreference = handPreference,
        accessibilityPreference = accessibilityPreference,
        currentColorPalette = currentColorPalette
    )

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Setup Wizard") },
        text = {
            Column(
                modifier = Modifier.verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    "Answer a few questions and ERICK will apply a recommended starting bundle. You can still adjust every setting manually later.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                SetupWizardQuestion(
                    title = "Main typing setup",
                    options = listOf(
                        SetupWizardOption("Touch first", hardware == SetupWizardHardware.TOUCH) {
                            hardware = SetupWizardHardware.TOUCH
                        },
                        SetupWizardOption("Controller first", hardware == SetupWizardHardware.CONTROLLER) {
                            hardware = SetupWizardHardware.CONTROLLER
                        },
                        SetupWizardOption("Both", hardware == SetupWizardHardware.BOTH) {
                            hardware = SetupWizardHardware.BOTH
                        }
                    )
                )

                SetupWizardQuestion(
                    title = "Dial preference",
                    options = listOf(
                        SetupWizardOption("Larger targets", targetPreference == SetupWizardTargetPreference.LARGER_TARGETS) {
                            targetPreference = SetupWizardTargetPreference.LARGER_TARGETS
                        },
                        SetupWizardOption("Full 8-direction layout", targetPreference == SetupWizardTargetPreference.FULL_EIGHT) {
                            targetPreference = SetupWizardTargetPreference.FULL_EIGHT
                        }
                    )
                )

                SetupWizardQuestion(
                    title = "Typing style",
                    options = listOf(
                        SetupWizardOption("Fastest path", typingPreference == SetupWizardTypingPreference.FASTEST) {
                            typingPreference = SetupWizardTypingPreference.FASTEST
                        },
                        SetupWizardOption("Steadier confirmation", typingPreference == SetupWizardTypingPreference.STEADIEST) {
                            typingPreference = SetupWizardTypingPreference.STEADIEST
                        },
                        SetupWizardOption("One-handed", typingPreference == SetupWizardTypingPreference.ONE_HANDED) {
                            typingPreference = SetupWizardTypingPreference.ONE_HANDED
                        }
                    )
                )

                SetupWizardQuestion(
                    title = "Handedness",
                    options = listOf(
                        SetupWizardOption("Right-handed", handPreference == SetupWizardHandPreference.RIGHT) {
                            handPreference = SetupWizardHandPreference.RIGHT
                        },
                        SetupWizardOption("Left-handed", handPreference == SetupWizardHandPreference.LEFT) {
                            handPreference = SetupWizardHandPreference.LEFT
                        }
                    )
                )

                SetupWizardQuestion(
                    title = "Accessibility default",
                    options = listOf(
                        SetupWizardOption("Standard", accessibilityPreference == SetupWizardAccessibilityPreference.STANDARD) {
                            accessibilityPreference = SetupWizardAccessibilityPreference.STANDARD
                        },
                        SetupWizardOption("Colorblind-safe palette", accessibilityPreference == SetupWizardAccessibilityPreference.COLORBLIND_SAFE) {
                            accessibilityPreference = SetupWizardAccessibilityPreference.COLORBLIND_SAFE
                        }
                    )
                )

                Card(colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)) {
                    Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        Text("Recommended bundle", style = MaterialTheme.typography.titleSmall)
                        recommendation.summaryLines.forEach { line ->
                            Text(line, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = { onApply(recommendation) }) {
                Text("Apply")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

@Composable
private fun SetupWizardQuestion(
    title: String,
    options: List<SetupWizardOption>
) {
    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        Text(title, style = MaterialTheme.typography.titleSmall)
        options.forEach { option ->
            LayoutRadioOption(
                title = option.title,
                subtitle = null,
                selected = option.selected,
                enabled = true,
                onClick = option.onSelect
            )
        }
    }
}

private data class SetupWizardOption(
    val title: String,
    val selected: Boolean,
    val onSelect: () -> Unit
)

private enum class SetupWizardHardware {
    TOUCH,
    CONTROLLER,
    BOTH
}

private enum class SetupWizardTargetPreference {
    LARGER_TARGETS,
    FULL_EIGHT
}

private enum class SetupWizardTypingPreference {
    FASTEST,
    STEADIEST,
    ONE_HANDED
}

private enum class SetupWizardHandPreference {
    RIGHT,
    LEFT
}

private enum class SetupWizardAccessibilityPreference {
    STANDARD,
    COLORBLIND_SAFE
}

private data class SetupWizardRecommendation(
    val sixSectionDial: Boolean,
    val inputMode: String,
    val leftHandedMode: Boolean,
    val colorblindMode: Boolean,
    val colorPalette: String,
    val controllerDeadZone: Float,
    val controllerYAxisInverted: Boolean,
    val summaryLines: List<String>
)

private fun buildSetupWizardRecommendation(
    hardware: SetupWizardHardware,
    targetPreference: SetupWizardTargetPreference,
    typingPreference: SetupWizardTypingPreference,
    handPreference: SetupWizardHandPreference,
    accessibilityPreference: SetupWizardAccessibilityPreference,
    currentColorPalette: String
): SetupWizardRecommendation {
    val sixSectionDial = when {
        hardware == SetupWizardHardware.CONTROLLER -> false
        typingPreference == SetupWizardTypingPreference.ONE_HANDED -> true
        targetPreference == SetupWizardTargetPreference.LARGER_TARGETS -> true
        else -> false
    }
    val inputMode = when (typingPreference) {
        SetupWizardTypingPreference.STEADIEST -> PreferencesManager.INPUT_MODE_CONFIRM
        SetupWizardTypingPreference.ONE_HANDED -> PreferencesManager.INPUT_MODE_ASSISTED
        SetupWizardTypingPreference.FASTEST -> PreferencesManager.INPUT_MODE_INSTANT
    }
    val leftHandedMode = handPreference == SetupWizardHandPreference.LEFT
    val colorblindMode = accessibilityPreference == SetupWizardAccessibilityPreference.COLORBLIND_SAFE
    val colorPalette = when {
        colorblindMode -> PreferencesManager.PALETTE_OKABE_ITO
        currentColorPalette == PreferencesManager.PALETTE_CUSTOM -> PreferencesManager.PALETTE_CUSTOM
        currentColorPalette == PreferencesManager.PALETTE_PASTEL -> PreferencesManager.PALETTE_PASTEL
        else -> PreferencesManager.PALETTE_OKABE_ITO
    }

    val summaryLines = buildList {
        add(if (sixSectionDial) "6-section dial" else "8-section dial")
        add(
            when (inputMode) {
                PreferencesManager.INPUT_MODE_CONFIRM -> "Steady Type"
                PreferencesManager.INPUT_MODE_ASSISTED -> "One-Handed"
                else -> "Quick Type"
            }
        )
        add(if (leftHandedMode) "Left-handed mode on" else "Right-handed default")
        add(if (colorblindMode) "Colorblind-safe palette enabled" else "Standard accessibility palette")
        if (hardware == SetupWizardHardware.CONTROLLER || hardware == SetupWizardHardware.BOTH) {
            add("Controller defaults reset for a clean starting point")
        }
    }

    return SetupWizardRecommendation(
        sixSectionDial = sixSectionDial,
        inputMode = inputMode,
        leftHandedMode = leftHandedMode,
        colorblindMode = colorblindMode,
        colorPalette = colorPalette,
        controllerDeadZone = PreferencesManager.DEFAULT_CONTROLLER_DEAD_ZONE,
        controllerYAxisInverted = false,
        summaryLines = summaryLines
    )
}

private suspend fun applySetupWizardRecommendation(
    preferencesManager: PreferencesManager,
    recommendation: SetupWizardRecommendation
) {
    preferencesManager.setSixSectionDial(recommendation.sixSectionDial)
    preferencesManager.setInputMode(recommendation.inputMode)
    preferencesManager.setLeftHandedMode(recommendation.leftHandedMode)
    preferencesManager.setColorblindMode(recommendation.colorblindMode)
    preferencesManager.setColorPalette(recommendation.colorPalette)
    preferencesManager.setControllerDeadZone(recommendation.controllerDeadZone)
    preferencesManager.setControllerYAxisInverted(recommendation.controllerYAxisInverted)
}

private fun predictionDomainDisplayName(predictionDomain: String): String = when (predictionDomain) {
    PreferencesManager.PREDICTION_DOMAIN_CONVERSATION -> "Conversation"
    PreferencesManager.PREDICTION_DOMAIN_PRODUCTIVITY -> "Productivity"
    PreferencesManager.PREDICTION_DOMAIN_ACCESSIBILITY -> "Accessibility"
    PreferencesManager.PREDICTION_DOMAIN_GAMING -> "Gaming"
    else -> "General"
}

private data class KeyboardLanguageOption(
    val value: String,
    val label: String,
    val subtitle: String
)

private val keyboardLanguageOptions = listOf(
    KeyboardLanguageOption(PreferencesManager.LANGUAGE_ENGLISH, "English", "Full logical and efficiency support."),
    KeyboardLanguageOption(PreferencesManager.LANGUAGE_SPANISH, "Spanish", "Includes accented vowels, ü, ñ, and inverted punctuation."),
    KeyboardLanguageOption(PreferencesManager.LANGUAGE_PORTUGUESE, "Portuguese", "Includes accented vowels, tilde vowels, and ç."),
    KeyboardLanguageOption(PreferencesManager.LANGUAGE_FRENCH, "French", "Includes accents, cedilla, and apostrophe-heavy prediction data."),
    KeyboardLanguageOption(PreferencesManager.LANGUAGE_GERMAN, "German", "Includes umlauts and ß."),
    KeyboardLanguageOption(PreferencesManager.LANGUAGE_ITALIAN, "Italian", "Includes accented vowels and Italian prediction data."),
    KeyboardLanguageOption(PreferencesManager.LANGUAGE_NORWEGIAN_BOKMAL, "Norwegian Bokmal", "Scandinavian profile with æ, ø, and å."),
    KeyboardLanguageOption(PreferencesManager.LANGUAGE_DANISH, "Danish", "Scandinavian profile with æ, ø, and å."),
    KeyboardLanguageOption(PreferencesManager.LANGUAGE_SWEDISH, "Swedish", "Scandinavian profile with å, ä, and ö."),
    KeyboardLanguageOption(PreferencesManager.LANGUAGE_FINNISH, "Finnish", "Includes ä and ö with Finnish prediction data.")
)

private fun keyboardLanguageDisplayName(keyboardLanguage: String): String = when (keyboardLanguage) {
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

@Composable
internal fun CollapsibleSection(
    title: String,
    summary: String,
    expanded: Boolean,
    onToggle: () -> Unit,
    content: @Composable ColumnScope.() -> Unit
) {
    val rotationAngle by animateFloatAsState(
        targetValue = if (expanded) 180f else 0f,
        label = "chevron"
    )

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainerLow
        )
    ) {
        Column {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onToggle() }
                    .padding(horizontal = 16.dp, vertical = 14.dp),
                verticalAlignment = Alignment.Top
            ) {
                Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text(
                        text = title,
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = summary,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                Icon(
                    imageVector = Icons.Default.ExpandMore,
                    contentDescription = if (expanded) "Collapse" else "Expand",
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.rotate(rotationAngle)
                )
            }

            AnimatedVisibility(visible = expanded) {
                Column(
                    modifier = Modifier.padding(start = 16.dp, end = 16.dp, bottom = 16.dp),
                    content = content
                )
            }
        }
    }
}

@Composable
private fun SectionOverviewCard(
    title: String,
    summary: String,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.tertiaryContainer)
    ) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Text(title, style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.onTertiaryContainer)
            Text(summary, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onTertiaryContainer)
        }
    }
}

@Composable
internal fun LayoutRadioOption(
    title: String,
    subtitle: String?,
    selected: Boolean,
    enabled: Boolean,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(enabled = enabled) { onClick() }
            .padding(vertical = 12.dp)
            .then(
                if (!enabled) {
                    Modifier.background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f))
                } else {
                    Modifier
                }
            )
            .padding(horizontal = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        RadioButton(
            selected = selected,
            onClick = onClick,
            enabled = enabled
        )
        Spacer(modifier = Modifier.width(8.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyLarge,
                color = if (enabled) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
            )
            if (subtitle != null) {
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                )
            }
        }
    }
}

@Composable
internal fun SettingToggle(
    title: String,
    checked: Boolean,
    enabled: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.weight(1f),
            color = if (enabled) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
        )
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            enabled = enabled
        )
    }
}

@Composable
internal fun PaletteRadioOption(
    title: String,
    subtitle: String,
    paletteType: ColorPaletteType,
    selected: Boolean,
    onClick: () -> Unit
) {
    val palette = ColorPalettes.getPalette(paletteType)

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(vertical = 8.dp, horizontal = 8.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            RadioButton(selected = selected, onClick = onClick)
            Spacer(modifier = Modifier.width(8.dp))
            Column {
                Text(text = title, style = MaterialTheme.typography.bodyLarge)
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                )
            }
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState())
                .padding(start = 48.dp, top = 4.dp),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            palette.forEach { entry ->
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    val bgColor = parseHexColor(entry.hex)
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .clip(RoundedCornerShape(4.dp))
                            .background(bgColor)
                            .border(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.3f), RoundedCornerShape(4.dp))
                    )
                    Text(
                        text = entry.name,
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(top = 2.dp)
                    )
                }
            }
        }
    }
}

internal fun parseHexColor(hex: String): Color {
    return try {
        Color(android.graphics.Color.parseColor(hex))
    } catch (_: Exception) {
        Color.Gray
    }
}

@Composable
internal fun CustomPaletteRadioOption(
    customPaletteColors: String,
    selected: Boolean,
    onSelect: () -> Unit,
    onEditColors: () -> Unit
) {
    val hexList = customPaletteColors.split(",").map { it.trim() }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onSelect() }
            .padding(vertical = 8.dp, horizontal = 8.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            RadioButton(selected = selected, onClick = onSelect)
            Spacer(modifier = Modifier.width(8.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(text = "Create Your Own", style = MaterialTheme.typography.bodyLarge)
                Text(
                    text = "Pick your own 8 colors",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                )
            }
            TextButton(onClick = onEditColors) {
                Text("Edit Colors")
            }
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState())
                .padding(start = 48.dp, top = 4.dp),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            hexList.forEachIndexed { index, hex ->
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .clip(RoundedCornerShape(4.dp))
                            .background(parseHexColor(hex))
                            .border(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.3f), RoundedCornerShape(4.dp))
                    )
                    Text(
                        text = "${index + 1}",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(top = 2.dp)
                    )
                }
            }
        }
    }
}