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
    fun parseHexColor(hex: String): Color {
    customPaletteColors: String,
    leftHandedMode: Boolean,
    hapticFeedback: Boolean,
    typingSounds: Boolean,
    inputMode: String,
    sixSectionDial: Boolean,
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
