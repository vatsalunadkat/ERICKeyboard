package com.vatoo.erick

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.view.inputmethod.InputMethodManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.lifecycleScope
import com.vatoo.erick.ui.theme.ERICKTheme
import kotlinx.coroutines.launch

class PracticeHubActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val preferencesManager = PreferencesManager(this)
        setContent {
            val themeMode by preferencesManager.themeMode.collectAsState(initial = PreferencesManager.THEME_SYSTEM)
            val attemptedLessons by preferencesManager.practiceAttemptedLessons.collectAsState(initial = emptySet())
            val completedLessons by preferencesManager.practiceCompletedLessons.collectAsState(initial = emptySet())

            ERICKTheme(themeMode = themeMode) {
                PracticeHubScreen(
                    preferencesManager = preferencesManager,
                    attemptedLessons = attemptedLessons,
                    completedLessons = completedLessons,
                    onBack = { finish() }
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun PracticeHubScreen(
    preferencesManager: PreferencesManager,
    attemptedLessons: Set<String>,
    completedLessons: Set<String>,
    onBack: () -> Unit
) {
    var selectedLessonId by rememberSaveable { mutableStateOf<String?>(null) }
    var quotePracticeActive by rememberSaveable { mutableStateOf(false) }

    when (selectedLessonId) {
        null -> Unit
        else -> {
            val lesson = practiceLessons.firstOrNull { it.id == selectedLessonId }
            if (lesson != null) {
                if (lesson.id == QUOTE_PRACTICE_LESSON_ID && quotePracticeActive) {
                    TypingGameScreen(onBack = { quotePracticeActive = false })
                    return
                }
                PracticeLessonDetailScreen(
                    preferencesManager = preferencesManager,
                    lesson = lesson,
                    isCompleted = completedLessons.contains(lesson.id),
                    onBack = { selectedLessonId = null },
                    onLaunchFreeform = if (lesson.isFreeform) {
                        { quotePracticeActive = true }
                    } else {
                        null
                    },
                    onMarkAttempted = {
                        preferencesManager.markPracticeLessonAttempted(lesson.id)
                    },
                    onMarkCompleted = {
                        preferencesManager.markPracticeLessonCompleted(lesson.id)
                    }
                )
                return
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Practice Lessons") },
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
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Card(colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("Learning Path", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                    Text(
                        "Start with 6-section basics, then utility swipes, assisted one-handed typing, controller drills, and finally quote practice.",
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Text(
                        "Progress: ${completedLessons.size} completed / ${attemptedLessons.size} attempted",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            practiceLessons.forEach { lesson ->
                val attempted = attemptedLessons.contains(lesson.id)
                val completed = completedLessons.contains(lesson.id)
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = if (completed) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surface
                    )
                ) {
                    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text(lesson.title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                        Text(lesson.focus, style = MaterialTheme.typography.bodyMedium)
                        if (!lesson.isFreeform) {
                            Text(
                                "${lesson.exercises.size} guided drills covering letters, numbers, and symbols.",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        Text(
                            when {
                                completed -> "Completed"
                                attempted -> "Attempted"
                                else -> "Not started"
                            },
                            style = MaterialTheme.typography.labelMedium,
                            color = if (completed) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Button(
                            onClick = { selectedLessonId = lesson.id },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(if (attempted) "Resume Lesson" else "Start Lesson")
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun PracticeLessonDetailScreen(
    preferencesManager: PreferencesManager,
    lesson: PracticeLesson,
    isCompleted: Boolean,
    onBack: () -> Unit,
    onLaunchFreeform: (() -> Unit)? = null,
    onMarkAttempted: suspend () -> Unit,
    onMarkCompleted: suspend () -> Unit
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val keyboardStatus = rememberKeyboardStatus(context)
    val lifecycleOwner = LocalLifecycleOwner.current
    val layoutType by preferencesManager.layoutType.collectAsState(initial = PreferencesManager.LAYOUT_LOGICAL)
    val inputMode by preferencesManager.inputMode.collectAsState(initial = PreferencesManager.INPUT_MODE_INSTANT)
    val sixSectionDial by preferencesManager.sixSectionDial.collectAsState(initial = false)

    var typedText by rememberSaveable(lesson.id) { mutableStateOf("") }
    var hasMarkedCompleted by remember(lesson.id, isCompleted) { mutableStateOf(isCompleted) }
    var currentExerciseIndex by rememberSaveable(lesson.id) { mutableStateOf(0) }
    var completedExerciseIds by rememberSaveable(lesson.id) { mutableStateOf(emptySet<String>()) }

    DisposableEffect(lifecycleOwner, context) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                keyboardStatus.refresh()
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    LaunchedEffect(lesson.id) {
        onMarkAttempted()
        applyPracticeLessonSetup(preferencesManager, lesson.setup)
        keyboardStatus.refresh()
    }

    LaunchedEffect(typedText, currentExerciseIndex, lesson.id) {
        val target = lesson.exercises.getOrNull(currentExerciseIndex)?.targetText
        if (!lesson.isFreeform && !hasMarkedCompleted && target != null && typedText.trim().equals(target, ignoreCase = true)) {
            val completedExercise = lesson.exercises[currentExerciseIndex]
            completedExerciseIds = completedExerciseIds + completedExercise.id
            typedText = ""
            if (currentExerciseIndex >= lesson.exercises.lastIndex) {
                onMarkCompleted()
                hasMarkedCompleted = true
            } else {
                currentExerciseIndex += 1
            }
        }
    }

    val currentExercise = lesson.exercises.getOrNull(currentExerciseIndex)
    val lessonSetup = lesson.setup
    val recommendedSetup = lessonSetup?.let(::formatLessonSetup)
    val currentSetup = formatLessonSetup(
        PracticeLessonSetup(
            sixSectionDial = sixSectionDial,
            layoutType = layoutType,
            inputMode = inputMode
        )
    )
    val setupMatchesLesson = lessonSetup?.let {
        it.sixSectionDial == sixSectionDial && it.layoutType == layoutType && it.inputMode == inputMode
    } ?: true
    val keyboardActionLabel = when {
        !keyboardStatus.isEnabled -> "Enable ERICK"
        keyboardStatus.isCurrent -> "Keyboard Picker"
        else -> "Switch to ERICK"
    }
    val keyboardStatusText = when {
        !keyboardStatus.isEnabled -> "ERICK is not enabled in the system keyboard list yet."
        !keyboardStatus.isCurrent -> "ERICK is enabled, but it is not the active keyboard for the practice field below."
        else -> "ERICK is active for this lesson. Keep the practice field focused while you drill."
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(lesson.title) },
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
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Card(colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(lesson.focus, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                    lesson.instructions.forEachIndexed { index, instruction ->
                        Text("${index + 1}. $instruction", style = MaterialTheme.typography.bodyMedium)
                    }
                    Text(lesson.successHint, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }

            Card(colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("Lesson Setup", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                    if (recommendedSetup != null) {
                        Text("Recommended: $recommendedSetup", style = MaterialTheme.typography.bodyMedium)
                    }
                    Text(
                        if (setupMatchesLesson) "Current keyboard preset already matches this lesson." else "Current keyboard preset: $currentSetup",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                    Text(
                        keyboardStatusText,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }
            }

            if (!lesson.isFreeform) {
                Card {
                    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text("Guided Drills", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                        lesson.exercises.forEachIndexed { index, exercise ->
                            val statusLabel = when {
                                completedExerciseIds.contains(exercise.id) -> "Done"
                                index == currentExerciseIndex && !hasMarkedCompleted -> "Current"
                                else -> "Next"
                            }
                            Text(
                                "${index + 1}. ${exercise.title} - $statusLabel",
                                style = MaterialTheme.typography.bodyMedium,
                                color = if (statusLabel == "Done") MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                }

                Card {
                    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text(
                            if (hasMarkedCompleted) "Lesson Complete" else "Drill ${currentExerciseIndex + 1} of ${lesson.exercises.size}",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold
                        )
                        currentExercise?.let { exercise ->
                            Text(exercise.title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                            Text(exercise.coaching, style = MaterialTheme.typography.bodyMedium)
                            Text(exercise.targetText, style = MaterialTheme.typography.headlineMedium)
                        }
                        OutlinedTextField(
                            value = typedText,
                            onValueChange = { typedText = it },
                            label = { Text("Type the drill target here") },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true
                        )
                        if (hasMarkedCompleted) {
                            Text(
                                "Lesson complete. You can replay the drills, switch presets, or go back to the hub.",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                }
            } else {
                Card {
                    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text("Advanced Freeform Mode", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                        Text("Launch the quote practice experience when you want longer sessions.", style = MaterialTheme.typography.bodyMedium)
                        if (onLaunchFreeform != null) {
                            Button(onClick = onLaunchFreeform, modifier = Modifier.fillMaxWidth()) {
                                Text("Launch Quote Practice")
                            }
                        }
                    }
                }
            }

            Card(colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text("Lesson Actions", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                    BoxWithConstraints(modifier = Modifier.fillMaxWidth()) {
                        val stackActions = maxWidth < 420.dp
                        if (stackActions) {
                            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                                Button(
                                    onClick = {
                                        if (keyboardStatus.isEnabled) {
                                            showKeyboardPicker(context)
                                        } else {
                                            context.startActivity(Intent(Settings.ACTION_INPUT_METHOD_SETTINGS))
                                        }
                                    },
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Text(keyboardActionLabel)
                                }
                                OutlinedButton(
                                    onClick = {
                                        context.startActivity(Intent(context, SettingsActivity::class.java))
                                    },
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Text("Lesson Settings")
                                }
                                OutlinedButton(
                                    onClick = {
                                        scope.launch {
                                            applyPracticeLessonSetup(preferencesManager, lesson.setup)
                                        }
                                    },
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Text("Reapply Setup")
                                }
                            }
                        } else {
                            Row(horizontalArrangement = Arrangement.spacedBy(12.dp), modifier = Modifier.fillMaxWidth()) {
                                Button(
                                    onClick = {
                                        if (keyboardStatus.isEnabled) {
                                            showKeyboardPicker(context)
                                        } else {
                                            context.startActivity(Intent(Settings.ACTION_INPUT_METHOD_SETTINGS))
                                        }
                                    },
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Text(keyboardActionLabel)
                                }
                                OutlinedButton(
                                    onClick = {
                                        context.startActivity(Intent(context, SettingsActivity::class.java))
                                    },
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Text("Lesson Settings")
                                }
                                OutlinedButton(
                                    onClick = {
                                        scope.launch {
                                            applyPracticeLessonSetup(preferencesManager, lesson.setup)
                                        }
                                    },
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Text("Reapply Setup")
                                }
                            }
                        }
                    }
                }
            }

            Row(horizontalArrangement = Arrangement.spacedBy(12.dp), modifier = Modifier.fillMaxWidth()) {
                OutlinedButton(onClick = onBack, modifier = Modifier.weight(1f)) {
                    Text("Back to Hub")
                }
                if (!lesson.isFreeform) {
                    Button(onClick = { typedText = "" }, modifier = Modifier.weight(1f)) {
                        Text("Clear Drill")
                    }
                }
            }
        }
    }
}

private data class KeyboardStatusState(
    val isEnabled: Boolean,
    val isCurrent: Boolean,
    val refresh: () -> Unit
)

@Composable
private fun rememberKeyboardStatus(context: Context): KeyboardStatusState {
    val statusState = remember {
        mutableStateOf(
            Pair(
                isKeyboardEnabled(context),
                isCurrentInputMethod(context)
            )
        )
    }

    return KeyboardStatusState(
        isEnabled = statusState.value.first,
        isCurrent = statusState.value.second,
        refresh = {
            statusState.value = Pair(
                isKeyboardEnabled(context),
                isCurrentInputMethod(context)
            )
        }
    )
}

private suspend fun applyPracticeLessonSetup(
    preferencesManager: PreferencesManager,
    setup: PracticeLessonSetup?
) {
    if (setup == null) {
        return
    }

    preferencesManager.setSixSectionDial(setup.sixSectionDial)
    preferencesManager.setLayoutType(setup.layoutType)
    preferencesManager.setInputMode(setup.inputMode)
}

private fun formatLessonSetup(setup: PracticeLessonSetup): String {
    val dialLabel = if (setup.sixSectionDial) "6-section" else "8-section"
    val layoutLabel = when (setup.layoutType) {
        PreferencesManager.LAYOUT_EFFICIENCY -> "Efficiency"
        PreferencesManager.LAYOUT_CUSTOM -> "Custom"
        else -> "Logical"
    }
    val inputLabel = when (setup.inputMode) {
        PreferencesManager.INPUT_MODE_CONFIRM -> "Steady Type"
        PreferencesManager.INPUT_MODE_ASSISTED -> "One-Handed"
        else -> "Quick Type"
    }
    return "$dialLabel • $layoutLabel • $inputLabel"
}

private fun showKeyboardPicker(context: Context) {
    val inputMethodManager = context.getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
    inputMethodManager?.showInputMethodPicker()
}