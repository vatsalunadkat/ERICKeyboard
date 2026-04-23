package com.vatoo.erick

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp

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

    var hue by remember { mutableFloatStateOf(0f) }
    var saturation by remember { mutableFloatStateOf(1f) }
    var brightness by remember { mutableFloatStateOf(1f) }

    var hexInput by remember { mutableStateOf(colors[0].trimStart('#')) }

    var rInput by remember { mutableStateOf("") }
    var gInput by remember { mutableStateOf("") }
    var bInput by remember { mutableStateOf("") }
    var showAdvancedInputs by remember { mutableStateOf(false) }

    fun syncFromHex(hex: String) {
        val color = parseHexColor(hex)
        val hsv = FloatArray(3)
        android.graphics.Color.RGBToHSV(
            (color.red * 255).toInt(),
            (color.green * 255).toInt(),
            (color.blue * 255).toInt(),
            hsv
        )
        hue = hsv[0]
        saturation = hsv[1]
        brightness = hsv[2]
        hexInput = hex.trimStart('#')
        rInput = (color.red * 255).toInt().toString()
        gInput = (color.green * 255).toInt().toString()
        bInput = (color.blue * 255).toInt().toString()
    }

    fun hsvToHex(h: Float, s: Float, v: Float): String {
        val color = android.graphics.Color.HSVToColor(floatArrayOf(h, s, v))
        return String.format(
            "#%02X%02X%02X",
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
        val color = parseHexColor(hex)
        rInput = (color.red * 255).toInt().toString()
        gInput = (color.green * 255).toInt().toString()
        bInput = (color.blue * 255).toInt().toString()
    }

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
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text("Edit one slot at a time", style = MaterialTheme.typography.titleSmall)
                    Text(
                        text = "Pick a direction, adjust the color with the sliders, and only open hex or RGB if you need precise values.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

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

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(parseHexColor(colors[selectedIndex]))
                    .border(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.3f), RoundedCornerShape(8.dp))
            )

            Text(
                text = "Editing ${directionLabels[selectedIndex]}",
                style = MaterialTheme.typography.titleSmall
            )

            Text("Hue", style = MaterialTheme.typography.labelMedium)
            Slider(
                value = hue,
                onValueChange = { hue = it; updateFromHSV() },
                valueRange = 0f..360f,
                modifier = Modifier.fillMaxWidth()
            )

            Text("Saturation", style = MaterialTheme.typography.labelMedium)
            Slider(
                value = saturation,
                onValueChange = { saturation = it; updateFromHSV() },
                valueRange = 0f..1f,
                modifier = Modifier.fillMaxWidth()
            )

            Text("Brightness", style = MaterialTheme.typography.labelMedium)
            Slider(
                value = brightness,
                onValueChange = { brightness = it; updateFromHSV() },
                valueRange = 0f..1f,
                modifier = Modifier.fillMaxWidth()
            )

            TextButton(onClick = { showAdvancedInputs = !showAdvancedInputs }) {
                Text(if (showAdvancedInputs) "Hide Hex and RGB" else "Show Hex and RGB")
            }

            if (showAdvancedInputs) {
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

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedTextField(
                        value = rInput,
                        onValueChange = { input ->
                            val value = input.filter { it.isDigit() }.take(3)
                            rInput = value
                            val red = value.toIntOrNull()?.coerceIn(0, 255) ?: return@OutlinedTextField
                            val green = gInput.toIntOrNull()?.coerceIn(0, 255) ?: return@OutlinedTextField
                            val blue = bInput.toIntOrNull()?.coerceIn(0, 255) ?: return@OutlinedTextField
                            val hex = String.format("#%02X%02X%02X", red, green, blue)
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
                            val value = input.filter { it.isDigit() }.take(3)
                            gInput = value
                            val red = rInput.toIntOrNull()?.coerceIn(0, 255) ?: return@OutlinedTextField
                            val green = value.toIntOrNull()?.coerceIn(0, 255) ?: return@OutlinedTextField
                            val blue = bInput.toIntOrNull()?.coerceIn(0, 255) ?: return@OutlinedTextField
                            val hex = String.format("#%02X%02X%02X", red, green, blue)
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
                            val value = input.filter { it.isDigit() }.take(3)
                            bInput = value
                            val red = rInput.toIntOrNull()?.coerceIn(0, 255) ?: return@OutlinedTextField
                            val green = gInput.toIntOrNull()?.coerceIn(0, 255) ?: return@OutlinedTextField
                            val blue = value.toIntOrNull()?.coerceIn(0, 255) ?: return@OutlinedTextField
                            val hex = String.format("#%02X%02X%02X", red, green, blue)
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
}