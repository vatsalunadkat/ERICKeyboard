package com.vatoo.erick

import android.content.Context
import android.content.Intent
import android.hardware.input.InputManager
import android.provider.Settings
import android.view.InputDevice
import android.view.inputmethod.InputMethodManager
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.HelpOutline
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.SportsEsports
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import kotlinx.coroutines.launch

@Composable
fun MainScreen(
    modifier: Modifier = Modifier,
    isKeyboardEnabled: State<Boolean>,
    isKeyboardCurrent: State<Boolean>
) {
    val appLanguage = LocalAppLanguageKey.current
    var showTypingGame by remember { mutableStateOf(false) }

    if (showTypingGame) {
        TypingGameScreen(onBack = { showTypingGame = false })
        return
    }

    val context = LocalContext.current
    val preferencesManager = remember(context) { PreferencesManager(context) }
    val onboardingCompleted by preferencesManager.onboardingCompleted.collectAsState(initial = null)
    val onboardingDismissed by preferencesManager.onboardingDismissed.collectAsState(initial = null)
    val onboardingStep by preferencesManager.onboardingStep.collectAsState(initial = 0)
    val quickstartSteps = remember(appLanguage) { quickstartStepsForLanguage(appLanguage) }
    val quickstartIndex = onboardingStep.coerceIn(0, quickstartSteps.lastIndex)
    val coroutineScope = rememberCoroutineScope()
    var text by remember { mutableStateOf("") }
    var showQuickstart by remember { mutableStateOf(false) }
    var showPrivacyDialog by remember { mutableStateOf(false) }
    var showTryErickHelpDialog by remember { mutableStateOf(false) }
    val isFullyEnabled = isKeyboardEnabled.value && isKeyboardCurrent.value

    LaunchedEffect(text) {
        if (text.trim().equals("start", ignoreCase = true)) {
            text = ""
            showTypingGame = true
        }
    }

    LaunchedEffect(onboardingCompleted, onboardingDismissed) {
        if (onboardingCompleted == false && onboardingDismissed == false) {
            showQuickstart = true
            preferencesManager.setOnboardingDismissed(true)
            preferencesManager.setOnboardingStep(0)
        }
    }

    if (showQuickstart) {
        QuickstartDialog(
            step = quickstartSteps[quickstartIndex],
            stepIndex = quickstartIndex,
            totalSteps = quickstartSteps.size,
            onPrevious = {
                coroutineScope.launch {
                    preferencesManager.setOnboardingStep((quickstartIndex - 1).coerceAtLeast(0))
                }
            },
            onNext = {
                coroutineScope.launch {
                    preferencesManager.setOnboardingStep((quickstartIndex + 1).coerceAtMost(quickstartSteps.lastIndex))
                }
            },
            onSkip = {
                showQuickstart = false
                coroutineScope.launch {
                    preferencesManager.setOnboardingDismissed(true)
                    preferencesManager.setOnboardingStep(quickstartIndex)
                }
            },
            onFinish = {
                showQuickstart = false
                coroutineScope.launch {
                    preferencesManager.setOnboardingCompleted(true)
                }
            },
            onDismiss = {
                showQuickstart = false
                coroutineScope.launch {
                    preferencesManager.setOnboardingDismissed(true)
                    preferencesManager.setOnboardingStep(quickstartIndex)
                }
            }
        )
    }

    if (showPrivacyDialog) {
        MainScreenInfoDialog(
            title = erickText(appLanguage, "Privacy & Security"),
            message = erickText(appLanguage, "ERICKeyboard keeps your typing on your device."),
            bulletPoints = listOf(
                erickText(appLanguage, "No typed text is collected or stored."),
                erickText(appLanguage, "Passwords and personal data stay on your device."),
                erickText(appLanguage, "No text is transmitted from the keyboard."),
                erickText(appLanguage, "Only keyboard preferences are stored locally."),
                erickText(appLanguage, "The app requests no internet access for typing data."),
                erickText(appLanguage, "The project is open source for inspection.")
            ),
            onDismiss = { showPrivacyDialog = false }
        )
    }

    if (showTryErickHelpDialog) {
        MainScreenInfoDialog(
            title = erickText(appLanguage, "Try ERICK"),
            message = erickText(appLanguage, "Use the test field to confirm that the current keyboard and layout feel right."),
            bulletPoints = listOf(
                erickText(appLanguage, "Tap the field and type a short word or sentence."),
                erickText(appLanguage, "If another keyboard appears, switch back to ERICK from the picker."),
                erickText(appLanguage, "Type start to jump into quote practice."),
                erickText(appLanguage, "Use Practice Lessons for guided drills instead of memorizing everything here.")
            ),
            onDismiss = { showTryErickHelpDialog = false }
        )
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Image(
                painter = painterResource(id = R.drawable.erick_logo),
                contentDescription = "ERICK logo",
                modifier = Modifier
                    .size(92.dp)
                    .padding(top = 8.dp, bottom = 12.dp)
            )
            Text(
                text = erickText(appLanguage, "Welcome to ERICKeyboard"),
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
            Text(
                text = erickText(appLanguage, "A radial chorded keyboard for everyone"),
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp)
            )
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(IntrinsicSize.Min)
                .padding(bottom = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            OutlinedButton(
                onClick = {
                    context.startActivity(Intent(context, HelpActivity::class.java))
                },
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
            ) {
                Text("\uD83D\uDCD6 ${erickText(appLanguage, "How to Type")}")
            }
            OutlinedButton(
                onClick = {
                    context.startActivity(Intent(context, SettingsActivity::class.java))
                },
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
            ) {
                Icon(Icons.Default.Settings, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(Modifier.width(6.dp))
                Text(erickText(appLanguage, "Settings"))
            }
        }

        if (isFullyEnabled) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(40.dp)
                    )
                    Spacer(Modifier.width(16.dp))
                    Column {
                        Text(
                            text = erickText(appLanguage, "Keyboard is Enabled!"),
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Text(
                            text = erickText(appLanguage, "You're ready to use ERICKeyboard"),
                            style = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier.padding(top = 4.dp)
                        )
                    }
                }
            }
            KeyboardTestCard(
                text = text,
                onValueChange = { text = it },
                onOpenHelp = { showTryErickHelpDialog = true }
            )
        } else {
            SetupInstructionsSection(
                isKeyboardEnabled = isKeyboardEnabled.value,
                isKeyboardCurrent = isKeyboardCurrent.value,
                context = context,
                onOpenPrivacyInfo = { showPrivacyDialog = true }
            )
        }

        ControllerStatusCard()

        OutlinedButton(
            onClick = {
                context.startActivity(Intent(context, ControllerDiagnosticsActivity::class.java))
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 12.dp)
        ) {
            Icon(Icons.Default.SportsEsports, contentDescription = null)
            Spacer(Modifier.width(8.dp))
            Text(erickText(appLanguage, "Controller Diagnostics"))
        }

        BenefitsOverviewSection(
            modifier = Modifier.padding(top = 8.dp, bottom = 16.dp)
        )
    }
}

@Composable
private fun SetupInstructionsSection(
    isKeyboardEnabled: Boolean,
    isKeyboardCurrent: Boolean,
    context: Context,
    onOpenPrivacyInfo: () -> Unit
) {
    val appLanguage = LocalAppLanguageKey.current
    Text(
        text = erickText(appLanguage, "Finish setup"),
        style = MaterialTheme.typography.titleLarge,
        fontWeight = FontWeight.Bold,
        modifier = Modifier.padding(bottom = 12.dp)
    )

    KeyboardSetupStepCard(
        stepNumber = "1",
        title = erickText(appLanguage, "Enable the Keyboard"),
        description = erickText(appLanguage, "Open keyboard settings and turn on ERICKeyboard."),
        isCompleted = isKeyboardEnabled,
        buttonLabel = erickText(appLanguage, "Open Keyboard Settings"),
        buttonContainerColor = MaterialTheme.colorScheme.primary,
        supportingAction = {
            IconButton(onClick = onOpenPrivacyInfo) {
                Icon(Icons.AutoMirrored.Filled.HelpOutline, contentDescription = erickText(appLanguage, "Privacy & Security"))
            }
        },
        onButtonClick = {
            try {
                context.startActivity(Intent(Settings.ACTION_INPUT_METHOD_SETTINGS))
            } catch (_: Exception) {
                context.startActivity(Intent(Settings.ACTION_SETTINGS))
            }
        }
    )

    KeyboardSetupStepCard(
        stepNumber = "2",
        title = erickText(appLanguage, "Select as Default"),
        description = erickText(appLanguage, "Open the keyboard picker and choose ERICKeyboard."),
        isCompleted = isKeyboardCurrent,
        buttonLabel = erickText(appLanguage, "Choose Input Method"),
        buttonContainerColor = MaterialTheme.colorScheme.secondary,
        onButtonClick = {
            val imeManager = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imeManager.showInputMethodPicker()
        }
    )
}

@Composable
private fun KeyboardSetupStepCard(
    stepNumber: String,
    title: String,
    description: String,
    isCompleted: Boolean,
    buttonLabel: String,
    buttonContainerColor: Color,
    onButtonClick: () -> Unit,
    supportingAction: @Composable (() -> Unit)? = null
) {
    val backgroundColor = if (isCompleted) Color(0xFFE8F5E9) else Color(0xFFFFEBEE)
    val accentColor = if (isCompleted) Color(0xFF4CAF50) else Color(0xFFF44336)

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        colors = CardDefaults.cardColors(
            containerColor = backgroundColor,
            contentColor = Color.Black
        )
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(bottom = if (isCompleted) 0.dp else 12.dp)
            ) {
                Surface(
                    shape = MaterialTheme.shapes.small,
                    color = accentColor,
                    modifier = Modifier.size(32.dp)
                ) {
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier.fillMaxSize()
                    ) {
                        Text(
                            text = stepNumber,
                            color = Color.White,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
                Spacer(Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = title,
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(bottom = 4.dp)
                    )
                    if (!isCompleted) {
                        Text(
                            text = description,
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color.Black
                        )
                    }
                }
                if (!isCompleted && supportingAction != null) {
                    supportingAction()
                }
                Spacer(Modifier.width(12.dp))
                Icon(
                    imageVector = if (isCompleted) Icons.Default.CheckCircle else Icons.Default.Close,
                    contentDescription = if (isCompleted) "Completed" else "Not completed",
                    tint = accentColor,
                    modifier = Modifier.size(28.dp)
                )
            }

            if (!isCompleted) {
                HorizontalDivider(
                    modifier = Modifier.padding(vertical = 12.dp),
                    color = MaterialTheme.colorScheme.outlineVariant
                )

                Button(
                    onClick = onButtonClick,
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = buttonContainerColor)
                ) {
                    Icon(Icons.Default.Settings, contentDescription = null)
                    Spacer(Modifier.width(8.dp))
                    Text(buttonLabel)
                }
            }
        }
    }
}

@Composable
private fun KeyboardTestCard(
    text: String,
    onValueChange: (String) -> Unit,
    onOpenHelp: () -> Unit
) {
    val appLanguage = LocalAppLanguageKey.current
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = erickText(appLanguage, "Try ERICK"),
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.weight(1f)
                )
                IconButton(onClick = onOpenHelp) {
                    Icon(Icons.AutoMirrored.Filled.HelpOutline, contentDescription = erickText(appLanguage, "How to Type"))
                }
            }

            OutlinedTextField(
                value = text,
                onValueChange = onValueChange,
                label = { Text(erickText(appLanguage, "Type here to test ERICK")) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(104.dp),
                maxLines = 4
            )

            Text(
                text = erickText(appLanguage, "Type start to open quote practice."),
                style = MaterialTheme.typography.bodySmall,
                color = Color.Gray
            )
        }
    }
}

@Composable
private fun MainScreenInfoDialog(
    title: String,
    message: String,
    bulletPoints: List<String>,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(title) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(message, style = MaterialTheme.typography.bodyMedium)
                bulletPoints.forEach { bullet ->
                    Text("• $bullet", style = MaterialTheme.typography.bodySmall)
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text(erickText(LocalAppLanguageKey.current, "Close"))
            }
        }
    )
}

@Composable
fun ControllerStatusCard(
    modifier: Modifier = Modifier
) {
    val appLanguage = LocalAppLanguageKey.current
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    var controllerName by remember { mutableStateOf<String?>(null) }

    fun refreshControllerStatus() {
        val inputManager = context.getSystemService(Context.INPUT_SERVICE) as InputManager
        controllerName = InputDevice.getDeviceIds()
            .asSequence()
            .mapNotNull { InputDevice.getDevice(it) }
            .firstOrNull { it.isCompatibleController() }
            ?.name
    }

    DisposableEffect(context, lifecycleOwner) {
        val inputManager = context.getSystemService(Context.INPUT_SERVICE) as InputManager
        val listener = object : InputManager.InputDeviceListener {
            override fun onInputDeviceAdded(deviceId: Int) {
                refreshControllerStatus()
            }

            override fun onInputDeviceRemoved(deviceId: Int) {
                refreshControllerStatus()
            }

            override fun onInputDeviceChanged(deviceId: Int) {
                refreshControllerStatus()
            }
        }
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                refreshControllerStatus()
            }
        }

        refreshControllerStatus()
        inputManager.registerInputDeviceListener(listener, null)
        lifecycleOwner.lifecycle.addObserver(observer)

        onDispose {
            inputManager.unregisterInputDeviceListener(listener)
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(bottom = 16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.SportsEsports,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(36.dp)
            )
            Spacer(Modifier.width(16.dp))
            Column {
                Text(
                    text = erickText(appLanguage, "Controller Status"),
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = controllerName?.let { "${erickText(appLanguage, "Connected")}: $it" }
                        ?: erickText(appLanguage, "No controller detected"),
                    style = MaterialTheme.typography.bodyMedium,
                    color = if (controllerName != null) Color(0xFF2E7D32) else MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }
        }
    }
}

private fun InputDevice.isCompatibleController(): Boolean {
    val isGamepad = sources and InputDevice.SOURCE_GAMEPAD == InputDevice.SOURCE_GAMEPAD
    val isJoystick = sources and InputDevice.SOURCE_JOYSTICK == InputDevice.SOURCE_JOYSTICK
    return isGamepad || isJoystick
}