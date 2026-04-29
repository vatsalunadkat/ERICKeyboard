package com.vatoo.erick

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.core.view.WindowCompat
import com.vatoo.erick.ui.theme.ERICKTheme
import kotlinx.coroutines.launch

class HelpActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WindowCompat.enableEdgeToEdge(window)
        val preferencesManager = PreferencesManager(this)
        setContent {
            val themeMode by preferencesManager.themeMode.collectAsState(initial = PreferencesManager.THEME_SYSTEM)
            ProvideAppLanguage(preferencesManager = preferencesManager) {
                ERICKTheme(themeMode = themeMode) {
                    HelpScreen(onBack = { finish() })
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HelpScreen(onBack: () -> Unit) {
    val context = LocalContext.current
    val appLanguage = LocalAppLanguageKey.current
    val preferencesManager = androidx.compose.runtime.remember(context) { PreferencesManager(context) }
    val quickstartSteps = androidx.compose.runtime.remember(appLanguage) { quickstartStepsForLanguage(appLanguage) }
    val coroutineScope = rememberCoroutineScope()
    var expandedSections by rememberSaveable {
        mutableStateOf(setOf<HelpSectionId>(HelpSectionId.CHORDS, HelpSectionId.UTILITY))
    }
    var showQuickstart by rememberSaveable { mutableStateOf(false) }
    var quickstartStep by rememberSaveable { mutableStateOf(0) }

    if (showQuickstart) {
        val boundedStep = quickstartStep.coerceIn(0, quickstartSteps.lastIndex)
        QuickstartDialog(
            step = quickstartSteps[boundedStep],
            stepIndex = boundedStep,
            totalSteps = quickstartSteps.size,
            onPrevious = {
                quickstartStep = (boundedStep - 1).coerceAtLeast(0)
            },
            onNext = {
                quickstartStep = (boundedStep + 1).coerceAtMost(quickstartSteps.lastIndex)
            },
            onSkip = {
                showQuickstart = false
                coroutineScope.launch {
                    preferencesManager.setOnboardingDismissed(true)
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
                }
            }
        )
    }

    fun toggleSection(sectionId: HelpSectionId) {
        expandedSections = if (expandedSections.contains(sectionId)) {
            expandedSections - sectionId
        } else {
            expandedSections + sectionId
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(erickText(appLanguage, "How to Type")) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = erickText(appLanguage, "Close"))
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
            SectionCard(erickText(appLanguage, "Start Here")) {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text(
                        erickText(appLanguage, "Use this order so you only learn the next thing you need."),
                        style = MaterialTheme.typography.bodyMedium
                    )
                    HelpBullet(erickText(appLanguage, "Open Quickstart for the core dial model."))
                    HelpBullet(erickText(appLanguage, "Use Practice Lessons for guided drills instead of memorizing rules here."))
                    HelpBullet(erickText(appLanguage, "Open Controller Diagnostics only when you plan to type with a gamepad."))
                    Button(
                        onClick = {
                            quickstartStep = 0
                            showQuickstart = true
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(erickText(appLanguage, "Replay Quickstart"))
                    }
                    Button(
                        onClick = {
                            context.startActivity(android.content.Intent(context, PracticeHubActivity::class.java))
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(erickText(appLanguage, "Open Practice Hub"))
                    }
                }
            }

            ExpandableHelpCard(
                title = erickText(appLanguage, "Who ERICK Can Help"),
                summary = erickText(appLanguage, "Examples across physical access, cognitive support, and everyday use."),
                expanded = expandedSections.contains(HelpSectionId.BENEFITS),
                onToggle = { toggleSection(HelpSectionId.BENEFITS) }
            ) {
                BenefitsOverviewContent()
            }

            ExpandableHelpCard(
                title = erickText(appLanguage, "Chord Mechanics"),
                summary = erickText(appLanguage, "Left picks the row. Right picks the letter. Release both to type."),
                expanded = expandedSections.contains(HelpSectionId.CHORDS),
                onToggle = { toggleSection(HelpSectionId.CHORDS) }
            ) {
                HelpBullet(erickText(appLanguage, "Move the left dial first to reveal a row."))
                HelpBullet(erickText(appLanguage, "Move the right dial to the character you want."))
                HelpBullet(erickText(appLanguage, "Release both dials to commit the chord."))
                HelpBullet(erickText(appLanguage, "Watch the preview bar instead of trying to memorize every row."))
            }

            ExpandableHelpCard(
                title = erickText(appLanguage, "6-Section Utility Wheel"),
                summary = erickText(appLanguage, "N Symbols, NE Shift, SE Space, S Period, SW Enter, NW Backspace."),
                expanded = expandedSections.contains(HelpSectionId.UTILITY),
                onToggle = { toggleSection(HelpSectionId.UTILITY) }
            ) {
                HelpMappingRow("N", erickText(appLanguage, "Symbols"))
                HelpMappingRow("NE", erickText(appLanguage, "Shift"))
                HelpMappingRow("SE", erickText(appLanguage, "Space"))
                HelpMappingRow("S", erickText(appLanguage, "Period"))
                HelpMappingRow("SW", erickText(appLanguage, "Enter"))
                HelpMappingRow("NW", erickText(appLanguage, "Backspace"))
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    erickText(appLanguage, "In 8-section mode, the right dial exposes the full 8-direction utility wheel."),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            ExpandableHelpCard(
                title = erickText(appLanguage, "Input Modes"),
                summary = erickText(appLanguage, "Instant is fastest. Confirm is cautious. Assisted is the one-handed path."),
                expanded = expandedSections.contains(HelpSectionId.MODES),
                onToggle = { toggleSection(HelpSectionId.MODES) }
            ) {
                HelpBullet(erickText(appLanguage, "Instant commits as soon as both dials release."))
                HelpBullet(erickText(appLanguage, "Confirm lets you preview before committing."))
                HelpBullet(erickText(appLanguage, "Assisted keeps the left-side row locked so you can finish from the letter side."))
            }

            ExpandableHelpCard(
                title = erickText(appLanguage, "Layouts and Predictions"),
                summary = erickText(appLanguage, "Logical is easiest to learn. Efficiency is faster later. Predictions appear at rest."),
                expanded = expandedSections.contains(HelpSectionId.LAYOUTS),
                onToggle = { toggleSection(HelpSectionId.LAYOUTS) }
            ) {
                HelpBullet(erickText(appLanguage, "Logical keeps the alphabet easy to learn."))
                HelpBullet(erickText(appLanguage, "Efficiency optimizes common English letters."))
                HelpBullet(erickText(appLanguage, "Custom layouts stay available in 8-section mode."))
                HelpBullet(erickText(appLanguage, "When both dials rest at center, ERICK shows up to three predictions."))
            }

            ExpandableHelpCard(
                title = erickText(appLanguage, "Controller Typing"),
                summary = erickText(appLanguage, "A controller mirrors the two dials with both analog sticks."),
                expanded = expandedSections.contains(HelpSectionId.CONTROLLER),
                onToggle = { toggleSection(HelpSectionId.CONTROLLER) }
            ) {
                HelpBullet(erickText(appLanguage, "Use both analog sticks the same way you use the touch dials."))
                HelpBullet(erickText(appLanguage, "Calibrate dead zone and Y-axis inversion in Controller Diagnostics before drills."))
                HelpBullet(erickText(appLanguage, "Start controller drills only after the touch version feels comfortable."))
            }
        }
    }
}

private enum class HelpSectionId {
    BENEFITS,
    CHORDS,
    UTILITY,
    MODES,
    LAYOUTS,
    CONTROLLER
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

@Composable
private fun ExpandableHelpCard(
    title: String,
    summary: String,
    expanded: Boolean,
    onToggle: () -> Unit,
    content: @Composable ColumnScope.() -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text(title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                    Text(
                        text = summary,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                IconButton(onClick = onToggle) {
                    Icon(
                        imageVector = if (expanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                        contentDescription = if (expanded) "Collapse $title" else "Expand $title"
                    )
                }
            }

            if (expanded) {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp), content = content)
            }
        }
    }
}

@Composable
private fun HelpBullet(text: String) {
    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
        Text("•", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold)
        Text(text, style = MaterialTheme.typography.bodyMedium)
    }
}

@Composable
private fun HelpMappingRow(direction: String, action: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
            text = direction,
            style = MaterialTheme.typography.labelLarge,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.width(44.dp)
        )
        Text(text = action, style = MaterialTheme.typography.bodyMedium)
    }
}

