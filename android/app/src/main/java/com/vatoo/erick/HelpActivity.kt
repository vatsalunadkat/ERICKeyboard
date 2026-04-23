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
import androidx.compose.ui.platform.LocalContext
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
    val context = LocalContext.current
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
            SectionCard("Learning Path") {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text(
                        "Start with the quickstart on the main screen, then work through 6-section basics, utility swipes, assisted one-handed typing, controller drills, and finally quote practice.",
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Button(
                        onClick = {
                            context.startActivity(android.content.Intent(context, PracticeHubActivity::class.java))
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Open Practice Hub")
                    }
                }
            }

            SectionCard("Chord Mechanics") {
                Text(
                    "ERICK uses a two-dial chording system:\n\n" +
                    "1. Move the LEFT dial to choose a row of letters\n" +
                    "2. Move the RIGHT dial to choose the specific letter in that row\n" +
                    "3. Release both dials to commit the chord\n\n" +
                    "The preview bar always shows the currently available letters for the selected row.",
                    style = MaterialTheme.typography.bodyMedium
                )
            }

            SectionCard("6-Section Utility Wheel") {
                Text(
                    "In 6-section mode, the rotated utility wheel is:\n\n" +
                    "• N → Symbols\n" +
                    "• NE → Shift\n" +
                    "• SE → Space\n" +
                    "• S → Period\n" +
                    "• SW → Enter\n" +
                    "• NW → Backspace\n\n" +
                    "In 8-section mode, the full 8-direction utility wheel remains available.",
                    style = MaterialTheme.typography.bodyMedium
                )
            }

            SectionCard("Input Modes") {
                Text(
                    "• Instant commits the chord as soon as both dials release\n" +
                    "• Confirm lets you preview and confirm before committing\n" +
                    "• Assisted locks the left-side row so one-handed users can finish from the letter side",
                    style = MaterialTheme.typography.bodyMedium
                )
            }

            SectionCard("Layouts and Predictions") {
                Text(
                    "• Logical (A–Z) keeps the alphabet easy to learn\n" +
                    "• Efficiency optimizes common English letters\n" +
                    "• Custom layouts stay available in 8-section mode\n\n" +
                    "When both dials rest at center, the suggestion bar shows up to three word predictions.",
                    style = MaterialTheme.typography.bodyMedium
                )
            }

            SectionCard("Controller Typing") {
                Text(
                    "Bluetooth and USB game controllers mirror the left and right dials with the analog sticks. Use Controller Diagnostics from the main screen or Settings to calibrate dead zone and Y-axis inversion before controller drills.",
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
