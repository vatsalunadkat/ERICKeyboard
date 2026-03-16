package com.vatoo.erick

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
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
    val colorblindMode by preferencesManager.colorblindMode.collectAsState(initial = false)
    val leftHandedMode by preferencesManager.leftHandedMode.collectAsState(initial = false)

    val scope = rememberCoroutineScope()

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
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {
            // Layout Section
            Text(
                text = "Keyboard Layout",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(bottom = 8.dp)
            )

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

            HorizontalDivider(modifier = Modifier.padding(vertical = 16.dp))

            // Appearance Section
            Text(
                text = "Appearance",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            SettingToggle(
                title = "Dark Theme",
                checked = darkTheme,
                enabled = true,
                onCheckedChange = { checked ->
                    scope.launch {
                        preferencesManager.setDarkTheme(checked)
                    }
                }
            )

            HorizontalDivider(modifier = Modifier.padding(vertical = 16.dp))

            // Accessibility Section
            Text(
                text = "Accessibility",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            SettingToggle(
                title = "Colorblind Mode",
                checked = colorblindMode,
                enabled = true,
                onCheckedChange = { checked ->
                    scope.launch {
                        preferencesManager.setColorblindMode(checked)
                    }
                }
            )

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

            HorizontalDivider(modifier = Modifier.padding(vertical = 16.dp))

            // Privacy & Security Section
            Text(
                text = "Privacy & Security",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.tertiaryContainer
                )
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
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
