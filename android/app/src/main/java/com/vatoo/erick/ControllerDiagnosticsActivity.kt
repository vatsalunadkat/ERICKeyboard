package com.vatoo.erick

import android.hardware.input.InputManager
import android.os.Bundle
import android.view.InputDevice
import android.view.MotionEvent
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.SportsEsports
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableFloatState
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.core.view.WindowCompat
import androidx.lifecycle.lifecycleScope
import com.vatoo.erick.shared.ControllerConfusionAnalyzer
import com.vatoo.erick.shared.ControllerConfusionDrillSample
import com.vatoo.erick.shared.ControllerConfusionType
import com.vatoo.erick.shared.ControllerPassiveSignal
import com.vatoo.erick.shared.ControllerInputProcessor
import com.vatoo.erick.shared.ControllerStickSnapshot
import com.vatoo.erick.shared.DialSectionMode
import com.vatoo.erick.shared.Direction
import com.vatoo.erick.shared.InputAction
import com.vatoo.erick.shared.InputMode
import com.vatoo.erick.shared.KeyboardActionDelegate
import com.vatoo.erick.shared.KeyboardMode
import com.vatoo.erick.shared.KeyboardStateMachine
import com.vatoo.erick.ui.theme.ERICKTheme
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import java.util.Locale
import kotlin.math.roundToInt

class ControllerDiagnosticsActivity : ComponentActivity() {
    private lateinit var preferencesManager: PreferencesManager
    private lateinit var stateMachine: KeyboardStateMachine
    private lateinit var inputManager: InputManager

    private val leftRawXState = mutableFloatStateOf(0f)
    private val leftRawYState = mutableFloatStateOf(0f)
    private val rightRawXState = mutableFloatStateOf(0f)
    private val rightRawYState = mutableFloatStateOf(0f)
    private val controllerNameState = mutableStateOf<String?>(null)
    private val lockedDirectionState = mutableStateOf(Direction.NONE)
    private val inputModeState = mutableStateOf(InputMode.INSTANT)
    private val leftHandedModeState = mutableStateOf(false)
    private val dialSectionModeState = mutableStateOf(DialSectionMode.EIGHT_SECTION)

    private val diagnosticsScope = kotlinx.coroutines.CoroutineScope(SupervisorJob() + Dispatchers.Main.immediate)

    private val controllerListener = object : InputManager.InputDeviceListener {
        override fun onInputDeviceAdded(deviceId: Int) = refreshControllerStatus()
        override fun onInputDeviceRemoved(deviceId: Int) = refreshControllerStatus()
        override fun onInputDeviceChanged(deviceId: Int) = refreshControllerStatus()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WindowCompat.enableEdgeToEdge(window)
        preferencesManager = PreferencesManager(this)
        inputManager = getSystemService(INPUT_SERVICE) as InputManager
        stateMachine = KeyboardStateMachine(NoOpKeyboardDelegate, diagnosticsScope)
        inputManager.registerInputDeviceListener(controllerListener, null)
        refreshControllerStatus()

        preferencesManager.leftHandedMode.onEach { enabled ->
            leftHandedModeState.value = enabled
            stateMachine.setLeftHandedMode(enabled)
            refreshDerivedState()
        }.launchIn(lifecycleScope)

        preferencesManager.inputMode.onEach { mode ->
            val inputMode = when (mode) {
                PreferencesManager.INPUT_MODE_CONFIRM -> InputMode.CONFIRM
                PreferencesManager.INPUT_MODE_ASSISTED -> InputMode.ASSISTED
                else -> InputMode.INSTANT
            }
            inputModeState.value = inputMode
            stateMachine.setInputMode(inputMode)
            refreshDerivedState()
        }.launchIn(lifecycleScope)

        preferencesManager.sixSectionDial.onEach { enabled ->
            val dialMode = if (enabled) DialSectionMode.SIX_SECTION else DialSectionMode.EIGHT_SECTION
            dialSectionModeState.value = dialMode
            stateMachine.setDialSectionMode(dialMode)
            refreshDerivedState()
        }.launchIn(lifecycleScope)

        preferencesManager.controllerDeadZone.onEach { deadZone ->
            stateMachine.setControllerDeadZone(deadZone)
            refreshDerivedState()
        }.launchIn(lifecycleScope)

        preferencesManager.controllerYAxisInverted.onEach { inverted ->
            stateMachine.setControllerYAxisInverted(inverted)
            refreshDerivedState()
        }.launchIn(lifecycleScope)

        setContent {
            val themeMode by preferencesManager.themeMode.collectAsState(initial = PreferencesManager.THEME_SYSTEM)
            val controllerDeadZone by preferencesManager.controllerDeadZone.collectAsState(initial = PreferencesManager.DEFAULT_CONTROLLER_DEAD_ZONE)
            val controllerYAxisInverted by preferencesManager.controllerYAxisInverted.collectAsState(initial = false)

            ERICKTheme(themeMode = themeMode) {
                ControllerDiagnosticsScreen(
                    controllerName = controllerNameState.value,
                    leftRawX = leftRawXState.floatValue,
                    leftRawY = leftRawYState.floatValue,
                    rightRawX = rightRawXState.floatValue,
                    rightRawY = rightRawYState.floatValue,
                    controllerDeadZone = controllerDeadZone,
                    controllerYAxisInverted = controllerYAxisInverted,
                    leftHandedMode = leftHandedModeState.value,
                    inputMode = inputModeState.value,
                    dialSectionMode = dialSectionModeState.value,
                    lockedLeftDirection = lockedDirectionState.value,
                    onBack = { finish() },
                    onDeadZoneChanged = { value ->
                        lifecycleScope.launch { preferencesManager.setControllerDeadZone(value) }
                    },
                    onYAxisInvertedChanged = { enabled ->
                        lifecycleScope.launch { preferencesManager.setControllerYAxisInverted(enabled) }
                    },
                    onResetCalibration = {
                        lifecycleScope.launch {
                            preferencesManager.setControllerDeadZone(PreferencesManager.DEFAULT_CONTROLLER_DEAD_ZONE)
                            preferencesManager.setControllerYAxisInverted(false)
                        }
                        clearControllerInput()
                    }
                )
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        inputManager.unregisterInputDeviceListener(controllerListener)
        diagnosticsScope.cancel()
    }

    override fun onGenericMotionEvent(event: MotionEvent): Boolean {
        val isJoystickEvent = event.source and InputDevice.SOURCE_JOYSTICK == InputDevice.SOURCE_JOYSTICK
        if (!isJoystickEvent || event.action != MotionEvent.ACTION_MOVE) {
            return super.onGenericMotionEvent(event)
        }

        leftRawXState.floatValue = event.getCenteredAxisValue(MotionEvent.AXIS_X)
        leftRawYState.floatValue = event.getCenteredAxisValue(MotionEvent.AXIS_Y)
        rightRawXState.floatValue = event.getPreferredAxisValue(MotionEvent.AXIS_Z, MotionEvent.AXIS_RX)
        rightRawYState.floatValue = event.getPreferredAxisValue(MotionEvent.AXIS_RZ, MotionEvent.AXIS_RY)

        stateMachine.handleControllerInput(
            leftX = leftRawXState.floatValue,
            leftY = leftRawYState.floatValue,
            rightX = rightRawXState.floatValue,
            rightY = rightRawYState.floatValue
        )
        refreshDerivedState()
        return true
    }

    private fun clearControllerInput() {
        leftRawXState.floatValue = 0f
        leftRawYState.floatValue = 0f
        rightRawXState.floatValue = 0f
        rightRawYState.floatValue = 0f
        stateMachine.handleControllerInput(0f, 0f, 0f, 0f)
        refreshDerivedState()
    }

    private fun refreshDerivedState() {
        lockedDirectionState.value = stateMachine.lockedLeftDir
        inputModeState.value = stateMachine.inputMode
        dialSectionModeState.value = stateMachine.getDialSectionMode()
    }

    private fun refreshControllerStatus() {
        controllerNameState.value = InputDevice.getDeviceIds()
            .asSequence()
            .mapNotNull { InputDevice.getDevice(it) }
            .firstOrNull { it.isCompatibleController() }
            ?.name
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ControllerDiagnosticsScreen(
    controllerName: String?,
    leftRawX: Float,
    leftRawY: Float,
    rightRawX: Float,
    rightRawY: Float,
    controllerDeadZone: Float,
    controllerYAxisInverted: Boolean,
    leftHandedMode: Boolean,
    inputMode: InputMode,
    dialSectionMode: DialSectionMode,
    lockedLeftDirection: Direction,
    onBack: () -> Unit,
    onDeadZoneChanged: (Float) -> Unit,
    onYAxisInvertedChanged: (Boolean) -> Unit,
    onResetCalibration: () -> Unit
) {
    val leftSnapshot = ControllerInputProcessor.resolveStick(
        x = leftRawX,
        y = leftRawY,
        deadZone = controllerDeadZone,
        invertY = controllerYAxisInverted,
        dialSectionMode = dialSectionMode
    )
    val rightSnapshot = ControllerInputProcessor.resolveStick(
        x = rightRawX,
        y = rightRawY,
        deadZone = controllerDeadZone,
        invertY = controllerYAxisInverted,
        dialSectionMode = dialSectionMode
    )
    val targetDirections = remember(dialSectionMode) { ControllerConfusionAnalyzer.directionsForMode(dialSectionMode) }
    val selectedStickState = remember { mutableStateOf(DiagnosticsStick.RIGHT) }
    val targetDirectionIndexState = remember(dialSectionMode) { mutableStateOf(0) }
    val confusionSummaryState = remember(dialSectionMode) { mutableStateOf(ControllerConfusionUiSummary()) }
    val currentTargetDirection = targetDirections[targetDirectionIndexState.value.coerceIn(0, targetDirections.lastIndex)]
    val selectedSnapshot = if (selectedStickState.value == DiagnosticsStick.LEFT) leftSnapshot else rightSnapshot

    TrackPassiveSnapBackSignal(leftSnapshot, controllerDeadZone, confusionSummaryState)
    TrackPassiveSnapBackSignal(rightSnapshot, controllerDeadZone, confusionSummaryState)

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Controller Diagnostics") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
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
            Card(colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.SportsEsports, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                        Spacer(Modifier.width(12.dp))
                        Column {
                            Text("Live Controller Status", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                            Text(
                                text = controllerName?.let { "Connected: $it" } ?: "No controller detected. Connect a controller and move the sticks on this screen.",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    Text(
                        text = "This screen uses the same shared controller normalization and direction resolution logic as the keyboard state machine.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Card {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text("Calibration", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                    Text(
                        text = "Dead zone: ${(controllerDeadZone * 100).roundToInt()}%",
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Slider(
                        value = controllerDeadZone,
                        onValueChange = onDeadZoneChanged,
                        valueRange = 0f..0.6f
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text("Invert Controller Y-Axis", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Medium)
                            Text(
                                text = "Use this if pushing up feels reversed on your controller.",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        Switch(checked = controllerYAxisInverted, onCheckedChange = onYAxisInvertedChanged)
                    }

                    OutlinedButton(onClick = onResetCalibration, modifier = Modifier.fillMaxWidth()) {
                        Icon(Icons.Default.Refresh, contentDescription = null)
                        Spacer(Modifier.width(8.dp))
                        Text("Reset Calibration")
                    }
                }
            }

            Card {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("Shared State", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                    Text("Input mode: ${inputMode.name}", style = MaterialTheme.typography.bodyMedium)
                    Text("Dial mode: ${dialSectionMode.name}", style = MaterialTheme.typography.bodyMedium)
                    Text(
                        "Effective sides: ${if (leftHandedMode) "Physical right = letter dial, physical left = action dial" else "Physical left = letter dial, physical right = action dial"}",
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Text("Assisted lock: ${lockedLeftDirection.name}", style = MaterialTheme.typography.bodyMedium)
                }
            }

            Card {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text("Local Confusion Drill", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                    Text(
                        text = "Record expected-versus-resolved direction buckets locally on this device. ERICK stores only aggregate counts here: no typed text and no raw stick traces.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
                        Button(
                            onClick = { selectedStickState.value = DiagnosticsStick.LEFT },
                            modifier = Modifier.weight(1f),
                            enabled = selectedStickState.value != DiagnosticsStick.LEFT
                        ) {
                            Text("Track Left Stick")
                        }
                        Button(
                            onClick = { selectedStickState.value = DiagnosticsStick.RIGHT },
                            modifier = Modifier.weight(1f),
                            enabled = selectedStickState.value != DiagnosticsStick.RIGHT
                        ) {
                            Text("Track Right Stick")
                        }
                    }

                    Text("Target direction: ${currentTargetDirection.name}", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Medium)
                    Text(
                        text = "Current resolved direction on ${selectedStickState.value.label}: ${selectedSnapshot.direction.name}",
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Text(
                        text = "Current dead-zone band: ${ControllerConfusionAnalyzer.deadZoneBand(controllerDeadZone)}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
                        Button(
                            onClick = {
                                val sample = ControllerConfusionAnalyzer.classifyDrillSample(
                                    expectedDirection = currentTargetDirection,
                                    snapshot = selectedSnapshot,
                                    deadZone = controllerDeadZone,
                                    dialSectionMode = dialSectionMode
                                )
                                confusionSummaryState.value = confusionSummaryState.value.recordSample(selectedStickState.value, sample)
                                targetDirectionIndexState.value = (targetDirectionIndexState.value + 1) % targetDirections.size
                            },
                            modifier = Modifier.weight(1f),
                            enabled = controllerName != null
                        ) {
                            Text("Record Sample")
                        }
                        OutlinedButton(
                            onClick = {
                                targetDirectionIndexState.value = (targetDirectionIndexState.value + 1) % targetDirections.size
                            },
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("Next Target")
                        }
                    }

                    OutlinedButton(
                        onClick = { confusionSummaryState.value = ControllerConfusionUiSummary() },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Reset Drill Counts")
                    }

                    StickMetricRow("Total samples", confusionSummaryState.value.totalSamples.toString())
                    StickMetricRow(
                        "Samples L/R",
                        "${confusionSummaryState.value.leftStickSamples} / ${confusionSummaryState.value.rightStickSamples}"
                    )
                    StickMetricRow("Exact matches", confusionSummaryState.value.exactMatches.toString())
                    StickMetricRow("Adjacent slips", confusionSummaryState.value.adjacentSlips.toString())
                    StickMetricRow("Mirror slips", confusionSummaryState.value.mirrorSlips.toString())
                    StickMetricRow("Dead-zone jitter", confusionSummaryState.value.deadZoneJitters.toString())
                    StickMetricRow("Other mismatches", confusionSummaryState.value.otherMismatches.toString())
                    StickMetricRow("Snap-back releases", confusionSummaryState.value.snapBackReversals.toString())

                    confusionSummaryState.value.topPairs().forEach { (pair, count) ->
                        StickMetricRow("Hot pair", "$pair ×$count")
                    }
                }
            }

            Row(horizontalArrangement = Arrangement.spacedBy(12.dp), modifier = Modifier.fillMaxWidth()) {
                ControllerStickCard(
                    title = "Left Stick",
                    subtitle = if (leftHandedMode) "Action dial on left-handed mode" else "Letter dial on default mode",
                    snapshot = leftSnapshot,
                    deadZone = controllerDeadZone,
                    modifier = Modifier.weight(1f)
                )
                ControllerStickCard(
                    title = "Right Stick",
                    subtitle = if (leftHandedMode) "Letter dial on left-handed mode" else "Action dial on default mode",
                    snapshot = rightSnapshot,
                    deadZone = controllerDeadZone,
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@Composable
private fun ControllerStickCard(
    title: String,
    subtitle: String,
    snapshot: ControllerStickSnapshot,
    deadZone: Float,
    modifier: Modifier = Modifier
) {
    Card(modifier = modifier) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Text(title, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
            Text(subtitle, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            ControllerStickPreview(snapshot = snapshot, deadZone = deadZone)
            StickMetricRow("Raw", formatPair(snapshot.rawX, snapshot.rawY))
            StickMetricRow("Adjusted", formatPair(snapshot.adjustedX, snapshot.adjustedY))
            StickMetricRow("Magnitude", formatValue(snapshot.magnitude))
            StickMetricRow("Direction", snapshot.direction.name)
            StickMetricRow("Active", if (snapshot.isActive) "Yes" else "No")
        }
    }
}

@Composable
private fun ControllerStickPreview(snapshot: ControllerStickSnapshot, deadZone: Float) {
    val previewSize = 140.dp
    val deadZoneDiameter = (previewSize.value * deadZone).coerceAtLeast(18f).dp
    val travel = 42.dp

    Box(
        modifier = Modifier
            .size(previewSize)
            .clip(CircleShape)
            .background(MaterialTheme.colorScheme.surfaceContainerHighest)
            .border(2.dp, MaterialTheme.colorScheme.outline, CircleShape),
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .size(deadZoneDiameter)
                .clip(CircleShape)
                .border(1.dp, MaterialTheme.colorScheme.outlineVariant, CircleShape)
        )
        Box(
            modifier = Modifier
                .size(14.dp)
                .offset(x = travel * snapshot.adjustedX, y = travel * snapshot.adjustedY)
                .clip(CircleShape)
                .background(if (snapshot.isActive) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline)
        )
    }
}

@Composable
private fun StickMetricRow(label: String, value: String) {
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
        Text(label, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Text(value, style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Medium)
    }
}

private fun formatPair(x: Float, y: Float): String = "${formatValue(x)}, ${formatValue(y)}"

private fun formatValue(value: Float): String = String.format(Locale.US, "%.2f", value)

@Composable
private fun TrackPassiveSnapBackSignal(
    snapshot: ControllerStickSnapshot,
    deadZone: Float,
    summaryState: MutableState<ControllerConfusionUiSummary>
) {
    val wasActiveState = remember { mutableStateOf(false) }
    val previousDirectionState = remember { mutableStateOf(Direction.NONE) }
    val lastDirectionState = remember { mutableStateOf(Direction.NONE) }

    LaunchedEffect(snapshot.isActive, snapshot.direction) {
        if (snapshot.isActive && snapshot.direction != Direction.NONE) {
            if (wasActiveState.value && snapshot.direction != lastDirectionState.value) {
                previousDirectionState.value = lastDirectionState.value
            }
            lastDirectionState.value = snapshot.direction
            wasActiveState.value = true
            return@LaunchedEffect
        }

        if (!wasActiveState.value) {
            return@LaunchedEffect
        }

        val signal = ControllerConfusionAnalyzer.detectSnapBackReversal(
            previousDirection = previousDirectionState.value,
            lastDirectionBeforeRelease = lastDirectionState.value,
            deadZone = deadZone,
        )
        if (signal != null) {
            summaryState.value = summaryState.value.recordPassiveSignal(signal)
        }
        wasActiveState.value = false
        previousDirectionState.value = Direction.NONE
        lastDirectionState.value = Direction.NONE
    }
}

private enum class DiagnosticsStick(val label: String) {
    LEFT("left stick"),
    RIGHT("right stick"),
}

private data class ControllerConfusionUiSummary(
    val totalSamples: Int = 0,
    val leftStickSamples: Int = 0,
    val rightStickSamples: Int = 0,
    val exactMatches: Int = 0,
    val adjacentSlips: Int = 0,
    val mirrorSlips: Int = 0,
    val deadZoneJitters: Int = 0,
    val otherMismatches: Int = 0,
    val snapBackReversals: Int = 0,
    val pairCounts: Map<String, Int> = emptyMap(),
) {
    fun recordSample(stick: DiagnosticsStick, sample: ControllerConfusionDrillSample): ControllerConfusionUiSummary {
        val pairKey = "${stick.name}:${sample.expectedDirection.name}->${sample.resolvedDirection.name}"
        val updatedPairs = pairCounts + (pairKey to ((pairCounts[pairKey] ?: 0) + 1))
        return copy(
            totalSamples = totalSamples + 1,
            leftStickSamples = leftStickSamples + if (stick == DiagnosticsStick.LEFT) 1 else 0,
            rightStickSamples = rightStickSamples + if (stick == DiagnosticsStick.RIGHT) 1 else 0,
            exactMatches = exactMatches + if (sample.confusionType == ControllerConfusionType.EXACT_MATCH) 1 else 0,
            adjacentSlips = adjacentSlips + if (sample.confusionType == ControllerConfusionType.ADJACENT_SLIP) 1 else 0,
            mirrorSlips = mirrorSlips + if (sample.confusionType == ControllerConfusionType.MIRROR_SLIP) 1 else 0,
            deadZoneJitters = deadZoneJitters + if (sample.confusionType == ControllerConfusionType.DEAD_ZONE_JITTER) 1 else 0,
            otherMismatches = otherMismatches + if (sample.confusionType == ControllerConfusionType.OTHER_MISMATCH) 1 else 0,
            pairCounts = updatedPairs,
        )
    }

    fun recordPassiveSignal(signal: ControllerPassiveSignal): ControllerConfusionUiSummary {
        return copy(
            snapBackReversals = snapBackReversals + if (signal.confusionType == ControllerConfusionType.SNAP_BACK_REVERSAL) 1 else 0,
        )
    }

    fun topPairs(limit: Int = 4): List<Pair<String, Int>> {
        return pairCounts.entries
            .sortedByDescending { it.value }
            .take(limit)
            .map { it.key.replace(':', ' ') to it.value }
    }
}

private object NoOpKeyboardDelegate : KeyboardActionDelegate {
    override fun commitText(text: String) = Unit
    override fun sendInputAction(action: InputAction) = Unit
    override fun onModeChanged(mode: KeyboardMode) = Unit
    override fun onSuggestionsUpdated(suggestions: List<String>) = Unit
    override fun getCurrentWordPrefix(): String = ""
    override fun loadPredictionProfile(): String = ""
    override fun savePredictionProfile(serializedProfile: String) = Unit
}

private fun MotionEvent.getCenteredAxisValue(axis: Int): Float {
    return getAxisValue(axis)
}

private fun MotionEvent.getPreferredAxisValue(primaryAxis: Int, fallbackAxis: Int): Float {
    val primary = getCenteredAxisValue(primaryAxis)
    return if (primary != 0f) primary else getCenteredAxisValue(fallbackAxis)
}

private fun InputDevice.isCompatibleController(): Boolean {
    val isGamepad = sources and InputDevice.SOURCE_GAMEPAD == InputDevice.SOURCE_GAMEPAD
    val isJoystick = sources and InputDevice.SOURCE_JOYSTICK == InputDevice.SOURCE_JOYSTICK
    return isGamepad || isJoystick
}