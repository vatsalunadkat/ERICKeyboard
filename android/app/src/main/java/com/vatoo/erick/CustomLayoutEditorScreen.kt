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
    val tabs = listOf("Normal Chords", "Shifted Chords", "Single Swipe")

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

            ScrollableTabRow(selectedTabIndex = selectedTab) {
                tabs.forEachIndexed { index, title ->
                    Tab(
                        selected = selectedTab == index,
                        onClick = { selectedTab = index },
                        text = { Text(title) }
                    )
                }
            }

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
                                    "${rightLabels[i]} ($colorName)",
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