package com.vatoo.erick

import android.content.Context
import android.view.inputmethod.InputMethodManager
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.unit.dp
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import com.vatoo.erick.shared.ColorPaletteType
import com.vatoo.erick.shared.ColorPalettes
import com.vatoo.erick.shared.CustomLayout
import com.vatoo.erick.shared.CustomLayoutManager
import com.vatoo.erick.shared.Direction
import com.vatoo.erick.shared.InputAction
import com.vatoo.erick.shared.LayoutType
import com.vatoo.erick.shared.SingleSwipeBinding
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
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

    val scope = rememberCoroutineScope()

    // Custom layout manager
    val customLayoutManager = remember {
        CustomLayoutManager(preferencesManager.createCustomLayoutStorage()).also { it.loadAll() }
    }
    var customLayouts by remember { mutableStateOf(customLayoutManager.getAll()) }

    // Navigation state
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
            onLayoutsChanged = {
                customLayouts = customLayoutManager.getAll()
            },
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun MainSettingsContent(
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
    customLayoutId: String,
    customLayouts: List<CustomLayout>,
    scope: kotlinx.coroutines.CoroutineScope,
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
        // Track which section is expanded (null = all collapsed)
        var expandedSection by remember { mutableStateOf<String?>(null) }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Switch keyboard shortcut — always visible at top
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

            // Dial Mode Section
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
                    text = "Use 6 larger segments instead of 8. Larger targets improve accuracy but change the chord layout. Symbols are accessed via NW single-swipe.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(start = 8.dp, end = 8.dp, bottom = 8.dp)
                )
            }

            // Layout Section
            CollapsibleSection(
                title = "Keyboard Layout",
                expanded = expandedSection == "layout",
                onToggle = { expandedSection = if (expandedSection == "layout") null else "layout" }
            ) {
                LayoutRadioOption(
                    title = "Logical (A–Z)",
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

                // Custom layout radio options
                customLayouts.forEach { cl ->
                    LayoutRadioOption(
                        title = cl.name,
                        subtitle = "Custom layout",
                        selected = layoutType == PreferencesManager.LAYOUT_CUSTOM && customLayoutId == cl.id,
                        enabled = true,
                        onClick = {
                            scope.launch {
                                preferencesManager.setCustomLayoutId(cl.id)
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

            // Appearance Section
            CollapsibleSection(
                title = "Appearance",
                expanded = expandedSection == "appearance",
                onToggle = { expandedSection = if (expandedSection == "appearance") null else "appearance" }
            ) {
                // Theme: System / Light / Dark segmented control
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

                // Custom Font toggle
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
                        Triple(PreferencesManager.FONT_VERDANA, "Verdana", FontFamily(android.graphics.Typeface.create("sans-serif", android.graphics.Typeface.NORMAL))),
                        Triple(PreferencesManager.FONT_GEORGIA, "Georgia", FontFamily(android.graphics.Typeface.create("serif", android.graphics.Typeface.NORMAL))),
                        Triple(PreferencesManager.FONT_OPENDYSLEXIC, "OpenDyslexic", FontFamily(Font(R.font.opendyslexic_regular)))
                    )

                    fontOptions.forEach { (key, name, fontFamily) ->
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
                                fontFamily = fontFamily
                            )
                        }
                    }
                }

                HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

                // Custom Colors toggle
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

            // Accessibility Section
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
                        scope.launch {
                            preferencesManager.setLeftHandedMode(checked)
                        }
                    }
                )
            }

            // Feedback Section
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

            // Input Mode Section
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

            // Privacy & Security Section
            CollapsibleSection(
                title = "Privacy & Security",
                expanded = expandedSection == "privacy",
                onToggle = { expandedSection = if (expandedSection == "privacy") null else "privacy" }
            ) {
                Text(
                    text = "🔒 Your privacy is our priority. ERICKeyboard:",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = androidx.compose.ui.text.font.FontWeight.SemiBold,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                Text(
                    text = "✓ Does NOT collect any text you type\n" +
                            "✓ Does NOT store passwords or personal data\n" +
                            "✓ Does NOT transmit any data from your device\n" +
                            "✓ Only stores your keyboard preferences locally\n" +
                            "✓ Has no internet permissions\n" +
                            "✓ Is 100% open source for full transparency",
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
    }
}

@Composable
private fun CollapsibleSection(
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
fun LayoutRadioOption(
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
                    Modifier.background(
                        MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
                    )
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
                color = if (enabled) {
                    MaterialTheme.colorScheme.onSurface
                } else {
                    MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                }
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
fun SettingToggle(
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
            color = if (enabled) {
                MaterialTheme.colorScheme.onSurface
            } else {
                MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
            }
        )
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            enabled = enabled
        )
    }
}

@Composable
fun PaletteRadioOption(
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

private fun parseHexColor(hex: String): Color {
    val clean = hex.trimStart('#')
    val colorLong = clean.toLong(16)
    return Color(
        red = ((colorLong shr 16) and 0xFF) / 255f,
        green = ((colorLong shr 8) and 0xFF) / 255f,
        blue = (colorLong and 0xFF) / 255f
    )
}

// =====================================================================
// Custom Palette Components
// =====================================================================

@Composable
fun CustomPaletteRadioOption(
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
            hexList.forEachIndexed { i, hex ->
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .clip(RoundedCornerShape(4.dp))
                            .background(parseHexColor(hex))
                            .border(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.3f), RoundedCornerShape(4.dp))
                    )
                    Text(
                        text = "${i + 1}",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(top = 2.dp)
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomPaletteEditorScreen(
    initialColors: String,
    onSave: (String) -> Unit,
    onBack: () -> Unit
) {
    val directionLabels = listOf("N", "NE", "E", "SE", "S", "SW", "W", "NW")
    val initialHexList = initialColors.split(",").map { it.trim() }
    var colors by remember { mutableStateOf(initialHexList.toMutableList().also { while (it.size < 8) it.add("#CCCCCC") }) }
    var selectedIndex by remember { mutableIntStateOf(0) }

    // HSV state for the currently selected color
    var hue by remember { mutableFloatStateOf(0f) }
    var saturation by remember { mutableFloatStateOf(1f) }
    var brightness by remember { mutableFloatStateOf(1f) }

    // Hex text field
    var hexInput by remember { mutableStateOf(colors[0].trimStart('#')) }

    // RGB text fields
    var rInput by remember { mutableStateOf("") }
    var gInput by remember { mutableStateOf("") }
    var bInput by remember { mutableStateOf("") }

    // Update HSV + text fields when a new color slot is selected
    fun syncFromHex(hex: String) {
        val c = parseHexColor(hex)
        val hsv = FloatArray(3)
        android.graphics.Color.RGBToHSV(
            (c.red * 255).toInt(),
            (c.green * 255).toInt(),
            (c.blue * 255).toInt(),
            hsv
        )
        hue = hsv[0]
        saturation = hsv[1]
        brightness = hsv[2]
        hexInput = hex.trimStart('#')
        rInput = (c.red * 255).toInt().toString()
        gInput = (c.green * 255).toInt().toString()
        bInput = (c.blue * 255).toInt().toString()
    }

    fun hsvToHex(h: Float, s: Float, v: Float): String {
        val color = android.graphics.Color.HSVToColor(floatArrayOf(h, s, v))
        return String.format("#%02X%02X%02X",
            android.graphics.Color.red(color),
            android.graphics.Color.green(color),
            android.graphics.Color.blue(color)
        )
    }

    fun updateFromHSV() {
        val hex = hsvToHex(hue, saturation, brightness)
        val updatedList = colors.toMutableList()
        updatedList[selectedIndex] = hex
        colors = updatedList
        hexInput = hex.trimStart('#')
        val c = parseHexColor(hex)
        rInput = (c.red * 255).toInt().toString()
        gInput = (c.green * 255).toInt().toString()
        bInput = (c.blue * 255).toInt().toString()
    }

    // Initialize from the first color
    LaunchedEffect(Unit) { syncFromHex(colors[0]) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Custom Palette") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    TextButton(onClick = { onSave(colors.joinToString(",")) }) {
                        Text("Save")
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
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Color slots row
            Text("Tap a slot to edit its color:", style = MaterialTheme.typography.bodyMedium)

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                colors.forEachIndexed { index, hex ->
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.clickable {
                            selectedIndex = index
                            syncFromHex(colors[index])
                        }
                    ) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(RoundedCornerShape(6.dp))
                                .background(parseHexColor(hex))
                                .border(
                                    width = if (index == selectedIndex) 3.dp else 1.dp,
                                    color = if (index == selectedIndex) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline.copy(alpha = 0.3f),
                                    shape = RoundedCornerShape(6.dp)
                                )
                        )
                        Text(
                            text = directionLabels[index],
                            style = MaterialTheme.typography.labelSmall,
                            color = if (index == selectedIndex) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(top = 2.dp)
                        )
                    }
                }
            }

            // Current color preview
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(parseHexColor(colors[selectedIndex]))
                    .border(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.3f), RoundedCornerShape(8.dp))
            )

            // Hue slider
            Text("Hue", style = MaterialTheme.typography.labelMedium)
            Slider(
                value = hue,
                onValueChange = { hue = it; updateFromHSV() },
                valueRange = 0f..360f,
                modifier = Modifier.fillMaxWidth()
            )

            // Saturation slider
            Text("Saturation", style = MaterialTheme.typography.labelMedium)
            Slider(
                value = saturation,
                onValueChange = { saturation = it; updateFromHSV() },
                valueRange = 0f..1f,
                modifier = Modifier.fillMaxWidth()
            )

            // Brightness slider
            Text("Brightness", style = MaterialTheme.typography.labelMedium)
            Slider(
                value = brightness,
                onValueChange = { brightness = it; updateFromHSV() },
                valueRange = 0f..1f,
                modifier = Modifier.fillMaxWidth()
            )

            // Hex input
            OutlinedTextField(
                value = hexInput,
                onValueChange = { input ->
                    val filtered = input.filter { it in "0123456789ABCDEFabcdef" }.take(6)
                    hexInput = filtered
                    if (filtered.length == 6) {
                        val hex = "#$filtered"
                        val updatedList = colors.toMutableList()
                        updatedList[selectedIndex] = hex
                        colors = updatedList
                        syncFromHex(hex)
                    }
                },
                label = { Text("Hex Color") },
                prefix = { Text("#") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            // RGB inputs
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedTextField(
                    value = rInput,
                    onValueChange = { input ->
                        val v = input.filter { it.isDigit() }.take(3)
                        rInput = v
                        val r = v.toIntOrNull()?.coerceIn(0, 255) ?: return@OutlinedTextField
                        val g = gInput.toIntOrNull()?.coerceIn(0, 255) ?: return@OutlinedTextField
                        val b = bInput.toIntOrNull()?.coerceIn(0, 255) ?: return@OutlinedTextField
                        val hex = String.format("#%02X%02X%02X", r, g, b)
                        val updatedList = colors.toMutableList()
                        updatedList[selectedIndex] = hex
                        colors = updatedList
                        syncFromHex(hex)
                    },
                    label = { Text("R") },
                    singleLine = true,
                    modifier = Modifier.weight(1f),
                    keyboardOptions = KeyboardOptions(keyboardType = androidx.compose.ui.text.input.KeyboardType.Number)
                )
                OutlinedTextField(
                    value = gInput,
                    onValueChange = { input ->
                        val v = input.filter { it.isDigit() }.take(3)
                        gInput = v
                        val r = rInput.toIntOrNull()?.coerceIn(0, 255) ?: return@OutlinedTextField
                        val g = v.toIntOrNull()?.coerceIn(0, 255) ?: return@OutlinedTextField
                        val b = bInput.toIntOrNull()?.coerceIn(0, 255) ?: return@OutlinedTextField
                        val hex = String.format("#%02X%02X%02X", r, g, b)
                        val updatedList = colors.toMutableList()
                        updatedList[selectedIndex] = hex
                        colors = updatedList
                        syncFromHex(hex)
                    },
                    label = { Text("G") },
                    singleLine = true,
                    modifier = Modifier.weight(1f),
                    keyboardOptions = KeyboardOptions(keyboardType = androidx.compose.ui.text.input.KeyboardType.Number)
                )
                OutlinedTextField(
                    value = bInput,
                    onValueChange = { input ->
                        val v = input.filter { it.isDigit() }.take(3)
                        bInput = v
                        val r = rInput.toIntOrNull()?.coerceIn(0, 255) ?: return@OutlinedTextField
                        val g = gInput.toIntOrNull()?.coerceIn(0, 255) ?: return@OutlinedTextField
                        val b = v.toIntOrNull()?.coerceIn(0, 255) ?: return@OutlinedTextField
                        val hex = String.format("#%02X%02X%02X", r, g, b)
                        val updatedList = colors.toMutableList()
                        updatedList[selectedIndex] = hex
                        colors = updatedList
                        syncFromHex(hex)
                    },
                    label = { Text("B") },
                    singleLine = true,
                    modifier = Modifier.weight(1f),
                    keyboardOptions = KeyboardOptions(keyboardType = androidx.compose.ui.text.input.KeyboardType.Number)
                )
            }
        }
    }
}

// =====================================================================
// Custom Layout List Screen
// =====================================================================

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomLayoutListScreen(
    customLayoutManager: CustomLayoutManager,
    customLayouts: List<CustomLayout>,
    onLayoutsChanged: () -> Unit,
    onEditLayout: (CustomLayout) -> Unit,
    onBack: () -> Unit
) {
    var showCreateDialog by remember { mutableStateOf(false) }
    var showDuplicateDialog by remember { mutableStateOf(false) }
    var deleteTarget by remember { mutableStateOf<CustomLayout?>(null) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Custom Layouts") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        },
        floatingActionButton = {
            Column {
                SmallFloatingActionButton(
                    onClick = { showDuplicateDialog = true },
                    containerColor = MaterialTheme.colorScheme.secondaryContainer
                ) {
                    Icon(Icons.Default.ContentCopy, contentDescription = "Duplicate built-in")
                }
                Spacer(modifier = Modifier.height(12.dp))
                FloatingActionButton(onClick = { showCreateDialog = true }) {
                    Icon(Icons.Default.Add, contentDescription = "Create blank layout")
                }
            }
        }
    ) { paddingValues ->
        if (customLayouts.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    "No custom layouts yet.\nTap + to create one.",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                contentPadding = PaddingValues(vertical = 16.dp)
            ) {
                items(customLayouts, key = { it.id }) { layout ->
                    Card(modifier = Modifier.fillMaxWidth()) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { onEditLayout(layout) }
                                .padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(layout.name, style = MaterialTheme.typography.titleSmall)
                                Text(
                                    "${layout.normalChordMap.values.flatten().count { it.isNotBlank() }} characters mapped",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                            IconButton(onClick = { onEditLayout(layout) }) {
                                Icon(Icons.Default.Edit, contentDescription = "Edit")
                            }
                            IconButton(onClick = { deleteTarget = layout }) {
                                Icon(
                                    Icons.Default.Delete,
                                    contentDescription = "Delete",
                                    tint = MaterialTheme.colorScheme.error
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    // Create blank dialog
    if (showCreateDialog) {
        var name by remember { mutableStateOf("") }
        AlertDialog(
            onDismissRequest = { showCreateDialog = false },
            title = { Text("New Blank Layout") },
            text = {
                OutlinedTextField(
                    value = name,
                    onValueChange = { if (it.length <= 30) name = it },
                    label = { Text("Layout Name") },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Words)
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        val layout = customLayoutManager.createBlank(name)
                        customLayoutManager.save(layout)
                        onLayoutsChanged()
                        showCreateDialog = false
                        onEditLayout(layout)
                    },
                    enabled = name.isNotBlank()
                ) { Text("Create") }
            },
            dismissButton = {
                TextButton(onClick = { showCreateDialog = false }) { Text("Cancel") }
            }
        )
    }

    // Duplicate built-in dialog
    if (showDuplicateDialog) {
        var name by remember { mutableStateOf("") }
        var sourceLayout by remember { mutableStateOf(LayoutType.LOGICAL) }
        AlertDialog(
            onDismissRequest = { showDuplicateDialog = false },
            title = { Text("Duplicate Built-in Layout") },
            text = {
                Column {
                    OutlinedTextField(
                        value = name,
                        onValueChange = { if (it.length <= 30) name = it },
                        label = { Text("New Layout Name") },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Words)
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text("Source:", style = MaterialTheme.typography.labelMedium)
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        RadioButton(
                            selected = sourceLayout == LayoutType.LOGICAL,
                            onClick = { sourceLayout = LayoutType.LOGICAL }
                        )
                        Text("Logical (A–Z)")
                        Spacer(modifier = Modifier.width(16.dp))
                        RadioButton(
                            selected = sourceLayout == LayoutType.EFFICIENCY,
                            onClick = { sourceLayout = LayoutType.EFFICIENCY }
                        )
                        Text("Efficiency")
                    }
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        val layout = customLayoutManager.duplicateFromBuiltIn(sourceLayout, name)
                        customLayoutManager.save(layout)
                        onLayoutsChanged()
                        showDuplicateDialog = false
                        onEditLayout(layout)
                    },
                    enabled = name.isNotBlank()
                ) { Text("Duplicate") }
            },
            dismissButton = {
                TextButton(onClick = { showDuplicateDialog = false }) { Text("Cancel") }
            }
        )
    }

    // Delete confirmation
    deleteTarget?.let { layout ->
        AlertDialog(
            onDismissRequest = { deleteTarget = null },
            title = { Text("Delete Layout") },
            text = { Text("Delete \"${layout.name}\"? This cannot be undone.") },
            confirmButton = {
                TextButton(onClick = {
                    customLayoutManager.delete(layout.id)
                    onLayoutsChanged()
                    deleteTarget = null
                }) { Text("Delete", color = MaterialTheme.colorScheme.error) }
            },
            dismissButton = {
                TextButton(onClick = { deleteTarget = null }) { Text("Cancel") }
            }
        )
    }
}

// =====================================================================
// Custom Layout Editor Screen
// =====================================================================

private val ALL_DIRECTIONS = listOf(
    Direction.N, Direction.NE, Direction.E, Direction.SE,
    Direction.S, Direction.SW, Direction.W, Direction.NW
)

private val DIRECTION_LABELS = mapOf(
    Direction.N to "N (Up)",
    Direction.NE to "NE",
    Direction.E to "E (Right)",
    Direction.SE to "SE",
    Direction.S to "S (Down)",
    Direction.SW to "SW",
    Direction.W to "W (Left)",
    Direction.NW to "NW"
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomLayoutEditorScreen(
    layout: CustomLayout,
    colorblindMode: Boolean = false,
    colorPalette: String = "",
    onSave: (CustomLayout) -> Unit,
    onBack: () -> Unit
) {
    val paletteType = if (colorblindMode) {
        when (colorPalette) {
            PreferencesManager.PALETTE_DEUTERANOPIA -> ColorPaletteType.DEUTERANOPIA
            PreferencesManager.PALETTE_PROTANOPIA -> ColorPaletteType.PROTANOPIA
            PreferencesManager.PALETTE_TRITANOPIA -> ColorPaletteType.TRITANOPIA
            PreferencesManager.PALETTE_PASTEL -> ColorPaletteType.PASTEL
            else -> ColorPaletteType.OKABE_ITO
        }
    } else ColorPaletteType.DEFAULT
    val palette = ColorPalettes.getPalette(paletteType)
    // Mutable state for editing
    var name by remember { mutableStateOf(layout.name) }
    var selectedTab by remember { mutableIntStateOf(0) }
    val tabs = listOf("Normal Chords", "Shifted Chords", "Single Swipe")

    // Mutable chord maps: Direction -> MutableList<String>
    val normalChords = remember {
        mutableStateMapOf<Direction, List<String>>().apply {
            layout.normalChordMap.forEach { (d, chars) -> put(d, chars.toList()) }
        }
    }
    val shiftedChords = remember {
        mutableStateMapOf<Direction, List<String>>().apply {
            layout.shiftedChordMap.forEach { (d, chars) -> put(d, chars.toList()) }
        }
    }

    // Single-swipe maps
    val singleSwipeNormal = remember {
        mutableStateMapOf<Direction, SingleSwipeBinding>().apply {
            layout.singleSwipeNormalMap.forEach { (d, b) -> put(d, b) }
        }
    }
    val singleSwipeShifted = remember {
        mutableStateMapOf<Direction, SingleSwipeBinding>().apply {
            layout.singleSwipeShiftedMap.forEach { (d, b) -> put(d, b) }
        }
    }

    fun buildLayout(): CustomLayout = layout.copy(
        name = name.trim().ifEmpty { "Custom Layout" },
        normalChordMap = normalChords.toMap(),
        shiftedChordMap = shiftedChords.toMap(),
        singleSwipeNormalMap = singleSwipeNormal.toMap(),
        singleSwipeShiftedMap = singleSwipeShifted.toMap()
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Edit Layout") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    TextButton(onClick = { onSave(buildLayout()) }) {
                        Text("Save")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // Layout name
            OutlinedTextField(
                value = name,
                onValueChange = { if (it.length <= 30) name = it },
                label = { Text("Layout Name") },
                singleLine = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Words)
            )

            // Tabs
            ScrollableTabRow(selectedTabIndex = selectedTab) {
                tabs.forEachIndexed { index, title ->
                    Tab(
                        selected = selectedTab == index,
                        onClick = { selectedTab = index },
                        text = { Text(title) }
                    )
                }
            }

            // Tab content
            when (selectedTab) {
                0 -> ChordMapEditor(normalChords, "Normal", palette)
                1 -> ChordMapEditor(shiftedChords, "Shifted", palette)
                2 -> SingleSwipeEditor(singleSwipeNormal, singleSwipeShifted)
            }
        }
    }
}

@Composable
private fun ChordMapEditor(
    chords: MutableMap<Direction, List<String>>,
    label: String,
    palette: List<com.vatoo.erick.shared.ColorEntry>
) {
    var expandedDir by remember { mutableStateOf<Direction?>(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        Text(
            "$label chord map — tap a direction to edit its 8 character slots",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        ALL_DIRECTIONS.forEach { dir ->
            val chars = chords[dir] ?: listOf("", "", "", "", "", "", "", "")
            val isExpanded = expandedDir == dir
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp)
            ) {
                Column(
                    modifier = Modifier
                        .clickable { expandedDir = if (isExpanded) null else dir }
                        .padding(12.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            DIRECTION_LABELS[dir] ?: dir.name,
                            style = MaterialTheme.typography.titleSmall,
                            modifier = Modifier.weight(1f)
                        )
                        Text(
                            chars.filter { it.isNotBlank() }.joinToString(" "),
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    if (isExpanded) {
                        Spacer(modifier = Modifier.height(8.dp))
                        // 8 text fields for the 8 right-dial positions
                        val rightLabels = listOf("N", "NE", "E", "SE", "S", "SW", "W", "NW")
                        for (i in 0 until 8) {
                            val colorHex = palette.getOrNull(i)?.hex ?: "#FAFAFA"
                            val bgColor = Color(android.graphics.Color.parseColor(colorHex))
                            val colorName = palette.getOrNull(i)?.name ?: ""
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.padding(vertical = 2.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(14.dp)
                                        .background(bgColor, CircleShape)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    "${rightLabels[i]} ($colorName)",
                                    style = MaterialTheme.typography.labelMedium,
                                    modifier = Modifier.width(100.dp)
                                )
                                OutlinedTextField(
                                    value = chars.getOrElse(i) { "" },
                                    onValueChange = { newVal ->
                                        // Allow only single character or empty
                                        val filtered = newVal.take(1)
                                        val mutable = chars.toMutableList()
                                        mutable[i] = filtered
                                        chords[dir] = mutable
                                    },
                                    singleLine = true,
                                    modifier = Modifier.weight(1f)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun SingleSwipeEditor(
    normalMap: MutableMap<Direction, SingleSwipeBinding>,
    shiftedMap: MutableMap<Direction, SingleSwipeBinding>
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        Text(
            "Single-swipe actions (right dial only, no left dial)",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        Text("Normal Mode", style = MaterialTheme.typography.titleSmall, modifier = Modifier.padding(vertical = 8.dp))
        ALL_DIRECTIONS.forEach { dir ->
            SwipeBindingRow(dir, normalMap[dir], onChanged = { normalMap[dir] = it })
        }
        HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp))
        Text("Shifted Mode", style = MaterialTheme.typography.titleSmall, modifier = Modifier.padding(vertical = 8.dp))
        ALL_DIRECTIONS.forEach { dir ->
            SwipeBindingRow(dir, shiftedMap[dir], onChanged = { shiftedMap[dir] = it })
        }
    }
}

@Composable
private fun SwipeBindingRow(
    dir: Direction,
    binding: SingleSwipeBinding?,
    onChanged: (SingleSwipeBinding) -> Unit
) {
    var showPicker by remember { mutableStateOf(false) }

    val displayText = when (binding) {
        is SingleSwipeBinding.Character -> "\"${binding.char}\""
        is SingleSwipeBinding.Action -> binding.action.name
        null -> "(none)"
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { showPicker = true }
            .padding(vertical = 6.dp, horizontal = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            DIRECTION_LABELS[dir] ?: dir.name,
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.width(80.dp)
        )
        Text(displayText, style = MaterialTheme.typography.bodyMedium, modifier = Modifier.weight(1f))
    }

    if (showPicker) {
        SwipeBindingPickerDialog(
            current = binding,
            onDismiss = { showPicker = false },
            onSelected = {
                onChanged(it)
                showPicker = false
            }
        )
    }
}

@Composable
private fun SwipeBindingPickerDialog(
    current: SingleSwipeBinding?,
    onDismiss: () -> Unit,
    onSelected: (SingleSwipeBinding) -> Unit
) {
    var charInput by remember {
        mutableStateOf(
            if (current is SingleSwipeBinding.Character) current.char else ""
        )
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Set Binding") },
        text = {
            Column {
                Text("Type a character:", style = MaterialTheme.typography.labelMedium)
                OutlinedTextField(
                    value = charInput,
                    onValueChange = { charInput = it.take(1) },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(12.dp))
                Text("Or select an action:", style = MaterialTheme.typography.labelMedium)
                Spacer(modifier = Modifier.height(4.dp))
                val actions = listOf(
                    InputAction.SPACE, InputAction.ENTER, InputAction.BACKSPACE,
                    InputAction.TOGGLE_SHIFT, InputAction.TOGGLE_CAPS,
                    InputAction.MOVE_HOME, InputAction.DELETE_FORWARD
                )
                actions.forEach { action ->
                    TextButton(
                        onClick = { onSelected(SingleSwipeBinding.Action(action)) },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(action.name, modifier = Modifier.fillMaxWidth())
                    }
                }
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    if (charInput.isNotEmpty()) {
                        onSelected(SingleSwipeBinding.Character(charInput))
                    }
                },
                enabled = charInput.isNotEmpty()
            ) { Text("Set Character") }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
}
