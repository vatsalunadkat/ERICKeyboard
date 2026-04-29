package com.vatoo.erick

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.unit.dp
import com.vatoo.erick.shared.ColorPaletteType
import com.vatoo.erick.shared.ColorPalettes
import com.vatoo.erick.shared.CustomLayout
import com.vatoo.erick.shared.Direction
import com.vatoo.erick.shared.InputAction
import com.vatoo.erick.shared.SingleSwipeBinding

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
    val appLanguage = LocalAppLanguageKey.current
    fun t(english: String): String = erickText(appLanguage, english)
    val paletteType = if (colorblindMode) {
        when (colorPalette) {
            PreferencesManager.PALETTE_DEUTERANOPIA -> ColorPaletteType.DEUTERANOPIA
            PreferencesManager.PALETTE_PROTANOPIA -> ColorPaletteType.PROTANOPIA
            PreferencesManager.PALETTE_TRITANOPIA -> ColorPaletteType.TRITANOPIA
            PreferencesManager.PALETTE_PASTEL -> ColorPaletteType.PASTEL
            else -> ColorPaletteType.OKABE_ITO
        }
    } else {
        ColorPaletteType.DEFAULT
    }
    val palette = ColorPalettes.getPalette(paletteType)
    var name by remember { mutableStateOf(layout.name) }
    var selectedTab by remember { mutableIntStateOf(0) }
    val tabs = listOf("Normal", "Shifted", "Swipe")

    val normalChords = remember {
        mutableStateMapOf<Direction, List<String>>().apply {
            layout.normalChordMap.forEach { (direction, chars) -> put(direction, chars.toList()) }
        }
    }
    val shiftedChords = remember {
        mutableStateMapOf<Direction, List<String>>().apply {
            layout.shiftedChordMap.forEach { (direction, chars) -> put(direction, chars.toList()) }
        }
    }
    val singleSwipeNormal = remember {
        mutableStateMapOf<Direction, SingleSwipeBinding>().apply {
            layout.singleSwipeNormalMap.forEach { (direction, binding) -> put(direction, binding) }
        }
    }
    val singleSwipeShifted = remember {
        mutableStateMapOf<Direction, SingleSwipeBinding>().apply {
            layout.singleSwipeShiftedMap.forEach { (direction, binding) -> put(direction, binding) }
        }
    }

    fun buildLayout(): CustomLayout = layout.copy(
        name = name.trim().ifEmpty { t("Custom Layout") },
        normalChordMap = normalChords.toMap(),
        shiftedChordMap = shiftedChords.toMap(),
        singleSwipeNormalMap = singleSwipeNormal.toMap(),
        singleSwipeShiftedMap = singleSwipeShifted.toMap()
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(t("Edit Layout")) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = t("Back"))
                    }
                },
                actions = {
                    TextButton(onClick = { onSave(buildLayout()) }) {
                        Text(t("Save"))
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
            OutlinedTextField(
                value = name,
                onValueChange = { if (it.length <= 30) name = it },
                label = { Text(t("Layout Name")) },
                singleLine = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Words)
            )

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 4.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = androidx.compose.foundation.layout.Arrangement.spacedBy(6.dp)) {
                    Text(t("Edit one layer at a time"), style = MaterialTheme.typography.titleSmall)
                    Text(
                        text = when (selectedTab) {
                            0 -> t("Normal is the everyday map. Start here first.")
                            1 -> t("Shifted is only for shifted typing. Change it after the normal layer feels right.")
                            else -> t("Swipe actions are optional utility shortcuts for the right dial alone.")
                        },
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            ScrollableTabRow(selectedTabIndex = selectedTab) {
                tabs.forEachIndexed { index, title ->
                    Tab(
                        selected = selectedTab == index,
                        onClick = { selectedTab = index },
                        text = { Text(t(title)) }
                    )
                }
            }

            when (selectedTab) {
                0 -> ChordMapEditor(normalChords, "Normal", palette, appLanguage)
                1 -> ChordMapEditor(shiftedChords, "Shifted", palette, appLanguage)
                2 -> SingleSwipeEditor(singleSwipeNormal, singleSwipeShifted, appLanguage)
            }
        }
    }
}

@Composable
private fun ChordMapEditor(
    chords: MutableMap<Direction, List<String>>,
    label: String,
    palette: List<com.vatoo.erick.shared.ColorEntry>,
    appLanguage: String
) {
    var expandedDir by remember { mutableStateOf<Direction?>(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        Text(
            "${erickText(appLanguage, label)} ${erickText(appLanguage, "map. Open one direction at a time and fill only the slots you want to change.")}",
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
                            directionLabel(dir, appLanguage),
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
                        val rightLabels = listOf("N", "NE", "E", "SE", "S", "SW", "W", "NW")
                        for (i in 0 until 8) {
                            val colorHex = palette.getOrNull(i)?.hex ?: "#FAFAFA"
                            val bgColor = Color(android.graphics.Color.parseColor(colorHex))
                            val colorName = palette.getOrNull(i)?.name ?: ""
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.padding(vertical = 2.dp)
                            ) {
                                androidx.compose.foundation.layout.Box(
                                    modifier = Modifier
                                        .size(14.dp)
                                        .background(bgColor, CircleShape)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    "${rightLabels[i]} (${erickText(appLanguage, colorName)})",
                                    style = MaterialTheme.typography.labelMedium,
                                    modifier = Modifier.width(100.dp)
                                )
                                OutlinedTextField(
                                    value = chars.getOrElse(i) { "" },
                                    onValueChange = { newVal ->
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
    shiftedMap: MutableMap<Direction, SingleSwipeBinding>,
    appLanguage: String
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        Text(
            erickText(appLanguage, "Optional right-dial shortcuts. Leave a direction empty if you do not want a swipe action there."),
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        Text(erickText(appLanguage, "Normal Mode"), style = MaterialTheme.typography.titleSmall, modifier = Modifier.padding(vertical = 8.dp))
        ALL_DIRECTIONS.forEach { dir ->
            SwipeBindingRow(dir, normalMap[dir], appLanguage, onChanged = { normalMap[dir] = it })
        }
        HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp))
        Text(erickText(appLanguage, "Shifted Mode"), style = MaterialTheme.typography.titleSmall, modifier = Modifier.padding(vertical = 8.dp))
        ALL_DIRECTIONS.forEach { dir ->
            SwipeBindingRow(dir, shiftedMap[dir], appLanguage, onChanged = { shiftedMap[dir] = it })
        }
    }
}

@Composable
private fun SwipeBindingRow(
    dir: Direction,
    binding: SingleSwipeBinding?,
    appLanguage: String,
    onChanged: (SingleSwipeBinding) -> Unit
) {
    var showPicker by remember { mutableStateOf(false) }

    val displayText = singleSwipeBindingDisplayText(binding, appLanguage)

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { showPicker = true }
            .padding(vertical = 6.dp, horizontal = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            directionLabel(dir, appLanguage),
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.width(80.dp)
        )
        Text(displayText, style = MaterialTheme.typography.bodyMedium, modifier = Modifier.weight(1f))
    }

    if (showPicker) {
        SwipeBindingPickerDialog(
            current = binding,
            appLanguage = appLanguage,
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
    appLanguage: String,
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
        title = { Text(erickText(appLanguage, "Set Binding")) },
        text = {
            Column {
                Text(erickText(appLanguage, "Type a character:"), style = MaterialTheme.typography.labelMedium)
                OutlinedTextField(
                    value = charInput,
                    onValueChange = { charInput = it.take(1) },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(12.dp))
                Text(erickText(appLanguage, "Or select an action:"), style = MaterialTheme.typography.labelMedium)
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
                        Text(inputActionDisplayName(action, appLanguage), modifier = Modifier.fillMaxWidth())
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
            ) { Text(erickText(appLanguage, "Set Character")) }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text(erickText(appLanguage, "Cancel")) }
        }
    )
}

private fun directionLabel(direction: Direction, languageKey: String): String = when (direction) {
    Direction.N -> "N (${erickText(languageKey, "Up")})"
    Direction.E -> "E (${erickText(languageKey, "Right")})"
    Direction.S -> "S (${erickText(languageKey, "Down")})"
    Direction.W -> "W (${erickText(languageKey, "Left")})"
    else -> DIRECTION_LABELS[direction] ?: direction.name
}

private fun singleSwipeBindingDisplayText(binding: SingleSwipeBinding?, languageKey: String): String = when (binding) {
    is SingleSwipeBinding.Character -> "\"${binding.char}\""
    is SingleSwipeBinding.Action -> inputActionDisplayName(binding.action, languageKey)
    null -> erickText(languageKey, "None")
}

private fun inputActionDisplayName(action: InputAction, languageKey: String): String = when (action) {
    InputAction.SPACE -> erickText(languageKey, "Space")
    InputAction.ENTER -> erickText(languageKey, "Enter")
    InputAction.BACKSPACE -> erickText(languageKey, "Backspace")
    InputAction.DELETE_FORWARD -> erickText(languageKey, "Delete Forward")
    InputAction.DELETE_WORD -> erickText(languageKey, "Delete Word")
    InputAction.TOGGLE_SHIFT -> erickText(languageKey, "Toggle Shift")
    InputAction.TOGGLE_CAPS -> erickText(languageKey, "Toggle Caps")
    InputAction.TOGGLE_SYMBOLS -> erickText(languageKey, "Toggle Symbols")
    InputAction.MOVE_HOME -> erickText(languageKey, "Move Home")
    InputAction.MOVE_END -> erickText(languageKey, "Move End")
    InputAction.DPAD_UP -> erickText(languageKey, "Move Up")
    InputAction.DPAD_DOWN -> erickText(languageKey, "Move Down")
    InputAction.DPAD_LEFT -> erickText(languageKey, "Move Left")
    InputAction.DPAD_RIGHT -> erickText(languageKey, "Move Right")
    InputAction.PAGE_UP -> erickText(languageKey, "Page Up")
    InputAction.PAGE_DOWN -> erickText(languageKey, "Page Down")
    InputAction.TAB -> erickText(languageKey, "Tab")
}