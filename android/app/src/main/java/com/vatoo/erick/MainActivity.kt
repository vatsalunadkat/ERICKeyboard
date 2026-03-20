package com.vatoo.erick

import android.content.Context
import android.content.Intent
import android.hardware.input.InputManager
import android.os.Bundle
import android.provider.Settings
import android.view.InputDevice
import android.view.inputmethod.InputMethodManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.SportsEsports
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import com.vatoo.erick.ui.theme.ERICKTheme

class MainActivity : ComponentActivity() {
    private var isKeyboardEnabledState = mutableStateOf(false)
    private var isKeyboardCurrentState = mutableStateOf(false)
    private lateinit var preferencesManager: PreferencesManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        updateKeyboardStatus()
        preferencesManager = PreferencesManager(this)
        setContent {
            val themeMode by preferencesManager.themeMode.collectAsState(initial = PreferencesManager.THEME_SYSTEM)
            ERICKTheme(themeMode = themeMode) {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    MainScreen(
                        modifier = Modifier.padding(innerPadding),
                        isKeyboardEnabled = isKeyboardEnabledState,
                        isKeyboardCurrent = isKeyboardCurrentState
                    )
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        updateKeyboardStatus()
    }

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        if (hasFocus) {
            updateKeyboardStatus()
        }
    }

    private fun updateKeyboardStatus() {
        isKeyboardEnabledState.value = isKeyboardEnabled(this)
        isKeyboardCurrentState.value = isCurrentInputMethod(this)
    }
}

@Composable
fun MainScreen(
    modifier: Modifier = Modifier,
    isKeyboardEnabled: State<Boolean>,
    isKeyboardCurrent: State<Boolean>
) {
    val context = LocalContext.current
    var text by remember { mutableStateOf("") }
    val isFullyEnabled = isKeyboardEnabled.value && isKeyboardCurrent.value

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
                text = "Welcome to ERICKeyboard",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
            Text(
                text = "A radial chorded keyboard for everyone",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp)
            )
        }

        // Show success message if fully enabled, otherwise show instructions
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
                            text = "Keyboard is Enabled!",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Text(
                            text = "You're ready to use ERICKeyboard",
                            style = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier.padding(top = 4.dp)
                        )
                    }
                }
            }
        } else {
            // Instructions with status indicators
            Text(
                text = "Setup Instructions:",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 12.dp)
            )

            // Step 1 with Privacy & Security content
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                colors = CardDefaults.cardColors(
                    containerColor = if (isKeyboardEnabled.value) {
                        Color(0xFFE8F5E9) // Light green
                    } else {
                        Color(0xFFFFEBEE) // Light red
                    }
                )
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(bottom = if (isKeyboardEnabled.value) 0.dp else 12.dp)
                    ) {
                        Surface(
                            shape = MaterialTheme.shapes.small,
                            color = if (isKeyboardEnabled.value) {
                                Color(0xFF4CAF50) // Green
                            } else {
                                Color(0xFFF44336) // Red
                            },
                            modifier = Modifier.size(32.dp)
                        ) {
                            Box(
                                contentAlignment = Alignment.Center,
                                modifier = Modifier.fillMaxSize()
                            ) {
                                Text(
                                    text = "1",
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                        Spacer(Modifier.width(12.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Enable the Keyboard",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(bottom = 4.dp)
                            )
                            // Only show description when not completed
                            if (!isKeyboardEnabled.value) {
                                Text(
                                    text = "Go to Android Settings > System > Languages & input > On-screen keyboard > Manage keyboards, then toggle on \"ERICKeyboard\".",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                        Spacer(Modifier.width(12.dp))
                        Icon(
                            imageVector = if (isKeyboardEnabled.value) Icons.Default.CheckCircle else Icons.Default.Close,
                            contentDescription = if (isKeyboardEnabled.value) "Completed" else "Not completed",
                            tint = if (isKeyboardEnabled.value) {
                                Color(0xFF4CAF50) // Green
                            } else {
                                Color(0xFFF44336) // Red
                            },
                            modifier = Modifier.size(28.dp)
                        )
                    }

                    // Only show details when not completed
                    if (!isKeyboardEnabled.value) {
                        HorizontalDivider(
                            modifier = Modifier.padding(vertical = 12.dp),
                            color = MaterialTheme.colorScheme.outlineVariant
                        )

                        // Privacy & Security inside Step 1 - Purple box
                        Card(
                            modifier = Modifier.fillMaxWidth()
                                .padding(bottom = 12.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.tertiaryContainer
                            )
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Text(
                                    text = "🔒 Privacy & Security",
                                    style = MaterialTheme.typography.titleSmall,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(bottom = 8.dp)
                                )
                                Text(
                                    text = "Your privacy is our priority. ERICKeyboard:",
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = FontWeight.SemiBold,
                                    modifier = Modifier.padding(bottom = 8.dp)
                                )
                                Text(
                                    text = "✓ Does NOT collect any text you type\n" +
                                            "✓ Does NOT store passwords or personal data\n" +
                                            "✓ Does NOT transmit any data from your device\n" +
                                            "✓ Only stores your keyboard preferences locally\n" +
                                            "✓ Has no internet permissions\n" +
                                            "✓ Is 100% open source for full transparency",
                                    style = MaterialTheme.typography.bodySmall
                                )
                            }
                        }

                        Button(
                            onClick = {
                                try {
                                    context.startActivity(Intent(Settings.ACTION_INPUT_METHOD_SETTINGS))
                                } catch (e: Exception) {
                                    context.startActivity(Intent(Settings.ACTION_SETTINGS))
                                }
                            },
                            modifier = Modifier.fillMaxWidth(),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.primary
                            )
                        ) {
                            Icon(Icons.Default.Settings, contentDescription = null)
                            Spacer(Modifier.width(8.dp))
                            Text("Open Keyboard Settings")
                        }
                    }
                }
            }

            // Step 2
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                colors = CardDefaults.cardColors(
                    containerColor = if (isKeyboardCurrent.value) {
                        Color(0xFFE8F5E9) // Light green
                    } else {
                        Color(0xFFFFEBEE) // Light red
                    }
                )
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(bottom = if (isKeyboardCurrent.value) 0.dp else 12.dp)
                    ) {
                        Surface(
                            shape = MaterialTheme.shapes.small,
                            color = if (isKeyboardCurrent.value) {
                                Color(0xFF4CAF50) // Green
                            } else {
                                Color(0xFFF44336) // Red
                            },
                            modifier = Modifier.size(32.dp)
                        ) {
                            Box(
                                contentAlignment = Alignment.Center,
                                modifier = Modifier.fillMaxSize()
                            ) {
                                Text(
                                    text = "2",
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                        Spacer(Modifier.width(12.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Select as Default",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(bottom = 4.dp)
                            )
                            // Only show description when not completed
                            if (!isKeyboardCurrent.value) {
                                Text(
                                    text = "Tap the button below or tap any text field, then select \"ERICKeyboard\" from the keyboard picker.",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                        Spacer(Modifier.width(12.dp))
                        Icon(
                            imageVector = if (isKeyboardCurrent.value) Icons.Default.CheckCircle else Icons.Default.Close,
                            contentDescription = if (isKeyboardCurrent.value) "Completed" else "Not completed",
                            tint = if (isKeyboardCurrent.value) {
                                Color(0xFF4CAF50) // Green
                            } else {
                                Color(0xFFF44336) // Red
                            },
                            modifier = Modifier.size(28.dp)
                        )
                    }

                    // Only show button when not completed
                    if (!isKeyboardCurrent.value) {
                        HorizontalDivider(
                            modifier = Modifier.padding(vertical = 12.dp),
                            color = MaterialTheme.colorScheme.outlineVariant
                        )

                        Button(
                            onClick = {
                                val imeManager = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                                imeManager.showInputMethodPicker()
                            },
                            modifier = Modifier.fillMaxWidth(),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.secondary
                            )
                        ) {
                            Icon(Icons.Default.Settings, contentDescription = null)
                            Spacer(Modifier.width(8.dp))
                            Text("Choose Input Method")
                        }
                    }
                }
            }
        }
        ControllerStatusCard()
        // Test Field - moved to top
        Text(
            text = "Test Your Keyboard:",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        OutlinedTextField(
            value = text,
            onValueChange = { text = it },
            label = { Text("Tap here to test the keyboard") },
            modifier = Modifier
                .fillMaxWidth()
                .height(120.dp)
                .padding(bottom = 16.dp),
            maxLines = 4
        )

        // Additional Info
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant
            )
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "💡 Tips:",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                Text(
                    text = "• Use the left joystick to select letter groups\n" +
                            "• Use the right joystick to select specific letters\n" +
                            "• Swipe right on the right joystick for space\n" +
                            "• Tap the settings icon (⚙) on the keyboard to customize",
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
    }
}

@Composable
fun InstructionStep(
    number: String,
    title: String,
    description: String
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 12.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp)
        ) {
            Surface(
                shape = MaterialTheme.shapes.small,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(32.dp)
            ) {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier.fillMaxSize()
                ) {
                    Text(
                        text = number,
                        color = MaterialTheme.colorScheme.onPrimary,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
            Spacer(Modifier.width(12.dp))
            Column {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 4.dp)
                )
                Text(
                    text = description,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
fun InstructionStepWithStatus(
    number: String,
    title: String,
    description: String,
    isCompleted: Boolean
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isCompleted) {
                Color(0xFFE8F5E9) // Light green
            } else {
                Color(0xFFFFEBEE) // Light red
            }
        )
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                shape = MaterialTheme.shapes.small,
                color = if (isCompleted) {
                    Color(0xFF4CAF50) // Green
                } else {
                    Color(0xFFF44336) // Red
                },
                modifier = Modifier.size(32.dp)
            ) {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier.fillMaxSize()
                ) {
                    Text(
                        text = number,
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
                Text(
                    text = description,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Spacer(Modifier.width(12.dp))
            Icon(
                imageVector = if (isCompleted) Icons.Default.CheckCircle else Icons.Default.Close,
                contentDescription = if (isCompleted) "Completed" else "Not completed",
                tint = if (isCompleted) {
                    Color(0xFF4CAF50) // Green
                } else {
                    Color(0xFFF44336) // Red
                },
                modifier = Modifier.size(28.dp)
            )
        }
    }
}
@Composable
fun ControllerStatusCard(
    modifier: Modifier = Modifier
) {
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
                    text = "Controller Status",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = controllerName?.let { "Connected: $it" } ?: "No controller detected",
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
fun isKeyboardEnabled(context: Context): Boolean {
    val imeManager = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    val enabledIMEs = imeManager.enabledInputMethodList
    return enabledIMEs.any { it.packageName == context.packageName }
}

fun isCurrentInputMethod(context: Context): Boolean {
    val imeManager = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    val currentIme = Settings.Secure.getString(
        context.contentResolver,
        Settings.Secure.DEFAULT_INPUT_METHOD
    )
    return currentIme?.contains(context.packageName) == true
}
