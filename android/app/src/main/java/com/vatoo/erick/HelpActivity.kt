package com.vatoo.erick

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.vatoo.erick.ui.theme.ERICKTheme

class HelpActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val preferencesManager = PreferencesManager(this)
        setContent {
            val themeMode by preferencesManager.themeMode.collectAsState(initial = PreferencesManager.THEME_SYSTEM)
            ERICKTheme(themeMode = themeMode) {
                HelpScreen(onBack = { finish() })
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HelpScreen(onBack: () -> Unit) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("How to Type") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
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
            SectionCard("Chord Mechanics") {
                Text(
                    "ERICK uses a two-joystick chording system:\n\n" +
                    "1. Swipe the LEFT dial in one of 8 directions to select a character group\n" +
                    "2. Swipe the RIGHT dial to select a specific character within the group\n" +
                    "3. Release both dials — the character is typed\n\n" +
                    "The preview bar above the dials shows which characters are available as you swipe.",
                    style = MaterialTheme.typography.bodyMedium
                )
            }

            SectionCard("Right Dial Shortcuts") {
                Text(
                    "When only the right dial is swiped (left dial at center):\n\n" +
                    "• East → Space\n" +
                    "• West → Backspace\n" +
                    "• North → Enter\n" +
                    "• South → Home (move cursor to start)\n" +
                    "• NE → Period (.)\n" +
                    "• SE → Comma (,)\n" +
                    "• NW → Toggle Caps Lock\n" +
                    "• SW → End (move cursor to end)",
                    style = MaterialTheme.typography.bodyMedium
                )
            }

            SectionCard("Shift & Caps Lock") {
                Text(
                    "• Swipe NW on the right dial to toggle Caps Lock\n" +
                    "• Shift activates automatically after certain punctuation\n" +
                    "• A shift indicator appears below the suggestion bar when active",
                    style = MaterialTheme.typography.bodyMedium
                )
            }

            SectionCard("Word Predictions") {
                Text(
                    "When both dials are at the center position, a suggestion bar appears with " +
                    "up to 3 word predictions. Tap any suggestion to insert it.\n\n" +
                    "Predictions update as you type and learn from common English words.",
                    style = MaterialTheme.typography.bodyMedium
                )
            }

            SectionCard("Logical vs. Efficiency Layout") {
                Text(
                    "• Logical (A–Z): Letters arranged alphabetically — easy to learn\n" +
                    "• Efficiency: Optimized for English letter frequency — faster for experienced users\n" +
                    "• Custom: Create your own chord-to-character mappings in Settings",
                    style = MaterialTheme.typography.bodyMedium
                )
            }

            SectionCard("Physical Controller") {
                Text(
                    "Connect a Bluetooth or USB game controller. The left and right analog " +
                    "sticks map directly to the left and right dials for hands-free typing.",
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
    }
}

@Composable
private fun SectionCard(title: String, content: @Composable () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            content()
        }
    }
}
