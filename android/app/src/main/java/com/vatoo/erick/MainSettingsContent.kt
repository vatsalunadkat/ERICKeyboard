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
    colorblindMode: Boolean,
    colorPalette: String,
    customPaletteColors: String,
    leftHandedMode: Boolean,
    hapticFeedback: Boolean,
    typingSounds: Boolean,
    inputMode: String,
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
            OutlinedButton(
                onClick = {
                    val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                    imm.showInputMethodPicker()
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Switch Keyboard")
            }

            CollapsibleSection(
                title = "Dial Mode",
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
                title = "Keyboard Layout",
                expanded = expandedSection == "layout",
                onToggle = { expandedSection = if (expandedSection == "layout") null else "layout" }
            ) {
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
                title = "Dial Mode",
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
                title = "Input Mode",
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
                title = "Controller",
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
internal fun CollapsibleSection(
    title: String,
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
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.weight(1f)
                )
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