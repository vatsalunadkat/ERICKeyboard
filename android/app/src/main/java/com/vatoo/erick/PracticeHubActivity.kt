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
import androidx.compose.material.icons.automirrored.filled.HelpOutline
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.AlertDialog
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
import androidx.compose.material3.TextButton
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.core.view.WindowCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.lifecycleScope
import com.vatoo.erick.ui.theme.ERICKTheme
import kotlinx.coroutines.launch

class PracticeHubActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WindowCompat.enableEdgeToEdge(window)
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
    var infoLessonId by rememberSaveable { mutableStateOf<String?>(null) }
    var startLessonFromBeginning by rememberSaveable { mutableStateOf(false) }

    infoLessonId?.let { lessonId ->
        practiceLessons.firstOrNull { it.id == lessonId }?.let { lesson ->
            PracticeLessonInfoDialog(
                lesson = lesson,
                setupSummary = lesson.setup?.let(::formatLessonSetup),
                onDismiss = { infoLessonId = null }
            )
        }
    }

    fun openLesson(lessonId: String, replayFromBeginning: Boolean) {
        selectedLessonId = lessonId
        startLessonFromBeginning = replayFromBeginning
        quotePracticeActive = false
    }

    when (selectedLessonId) {
        null -> Unit
        else -> {
            val lesson = practiceLessons.firstOrNull { it.id == selectedLessonId }
            if (lesson != null) {
                val lessonIndex = practiceLessons.indexOfFirst { it.id == lesson.id }
                if (lesson.id == QUOTE_PRACTICE_LESSON_ID && quotePracticeActive) {
                    TypingGameScreen(onBack = { quotePracticeActive = false })
                    return
                }
                PracticeLessonDetailScreen(
                    preferencesManager = preferencesManager,
                    lesson = lesson,
                    lessonIndex = lessonIndex,
                    completedLessonIds = completedLessons,
                    isCompleted = completedLessons.contains(lesson.id),
                    startFromBeginning = startLessonFromBeginning,
                    onBack = { selectedLessonId = null },
                    onOpenLesson = ::openLesson,
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
                    Text("Pick a lesson", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                    Text(
                        "ERICK applies the lesson setup for you so you can focus on the drill.",
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
                        containerColor = if (completed) CompletedLessonContainerColor else MaterialTheme.colorScheme.surface
                    )
                ) {
                    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                Text(lesson.title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                                Text(
                                    compactLessonSummary(lesson),
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                            IconButton(onClick = { infoLessonId = lesson.id }) {
                                Icon(Icons.AutoMirrored.Filled.HelpOutline, contentDescription = "Lesson help")
                            }
                        }
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            if (completed) {
                                Icon(
                                    imageVector = Icons.Default.CheckCircle,
                                    contentDescription = null,
                                    tint = CompletedLessonAccentColor
                                )
                            }
                            Text(
                                lessonStatusLabel(attempted = attempted, completed = completed),
                                style = MaterialTheme.typography.labelMedium,
                                color = if (completed) CompletedLessonAccentColor else MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        Button(
                            onClick = { openLesson(lesson.id, completed) },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(lessonPrimaryButtonLabel(attempted = attempted, completed = completed))
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
    lessonIndex: Int,
    completedLessonIds: Set<String>,
    isCompleted: Boolean,
    startFromBeginning: Boolean,
    onBack: () -> Unit,
    onOpenLesson: (lessonId: String, replayFromBeginning: Boolean) -> Unit,
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
    var showInfoDialog by remember { mutableStateOf(false) }

    if (showInfoDialog) {
        PracticeLessonInfoDialog(
            lesson = lesson,
            setupSummary = lesson.setup?.let(::formatLessonSetup),
            onDismiss = { showInfoDialog = false }
        )
    }

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

    LaunchedEffect(lesson.id, startFromBeginning) {
        onMarkAttempted()
        applyPracticeLessonSetup(preferencesManager, lesson.setup)
        keyboardStatus.refresh()
        if (startFromBeginning) {
            typedText = ""
            hasMarkedCompleted = false
            currentExerciseIndex = 0
            completedExerciseIds = emptySet()
        }
    }

    LaunchedEffect(typedText, currentExerciseIndex, lesson.id) {
        val target = lesson.exercises.getOrNull(currentExerciseIndex)?.targetText
        if (!lesson.isFreeform && !hasMarkedCompleted && target != null && typedText.trim().equals(target, ignoreCase = true)) {
            val completedExercise = lesson.exercises[currentExerciseIndex]
            val updatedCompletedExercises = completedExerciseIds + completedExercise.id
            completedExerciseIds = updatedCompletedExercises
            typedText = ""
            if (updatedCompletedExercises.size == lesson.exercises.size) {
                onMarkCompleted()
                hasMarkedCompleted = true
            } else {
                currentExerciseIndex = findNextIncompleteExerciseIndex(
                    currentIndex = currentExerciseIndex,
                    exercises = lesson.exercises,
                    completedExerciseIds = updatedCompletedExercises
                )
            }
        }
    }

    val currentExercise = lesson.exercises.getOrNull(currentExerciseIndex)
    val lessonSetup = lesson.setup
    val recommendedSetup = lessonSetup?.let(::formatLessonSetup)
    val setupMatchesLesson = lessonSetup?.let {
        it.sixSectionDial == sixSectionDial && it.layoutType == layoutType && it.inputMode == inputMode
    } ?: true
    val keyboardActionLabel = when {
        !keyboardStatus.isEnabled -> "Enable ERICK"
        else -> "Switch to ERICK"
    }
    val showKeyboardAction = !keyboardStatus.isCurrent
    val showSetupAction = !setupMatchesLesson
    val previousLesson = practiceLessons.getOrNull(lessonIndex - 1)
    val nextLesson = practiceLessons.getOrNull(lessonIndex + 1)
    val completedPartsLabel = if (lesson.isFreeform) {
        "Freeform practice"
    } else {
        "${completedExerciseIds.size} of ${lesson.exercises.size} parts done"
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
                actions = {
                    IconButton(onClick = {
                        context.startActivity(Intent(context, SettingsActivity::class.java))
                    }) {
                        Icon(Icons.Default.Settings, contentDescription = "Settings")
                    }
                    IconButton(onClick = { showInfoDialog = true }) {
                        Icon(Icons.AutoMirrored.Filled.HelpOutline, contentDescription = "Lesson help")
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
                    Text(
                        if (lesson.isFreeform) "Advanced practice" else "Part ${currentExerciseIndex + 1} of ${lesson.exercises.size}",
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        currentExercise?.title ?: lesson.focus,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                    Text(completedPartsLabel, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    if (recommendedSetup != null) {
                        Text(recommendedSetup, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
            }

            if (!lesson.isFreeform) {
                Card {
                    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        currentExercise?.let { exercise ->
                            Text(exercise.coaching, style = MaterialTheme.typography.bodyMedium)
                            Text(exercise.targetText, style = MaterialTheme.typography.headlineMedium)
                        }
                        OutlinedTextField(
                            value = typedText,
                            onValueChange = { typedText = it },
                            label = { Text("Type here") },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true
                        )
                    }
                }
            } else {
                Card {
                    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text("Launch longer freeform quote practice when you are ready.", style = MaterialTheme.typography.bodyMedium)
                        if (onLaunchFreeform != null) {
                            Button(onClick = onLaunchFreeform, modifier = Modifier.fillMaxWidth()) {
                                Text("Launch Quote Practice")
                            }
                        }
                    }
                }
            }

            if (showKeyboardAction || showSetupAction) {
                val actionButtons = buildList {
                    if (showKeyboardAction) {
                        add(
                            PracticeActionButton(
                                label = keyboardActionLabel,
                                emphasized = true,
                                onClick = {
                                    if (keyboardStatus.isEnabled) {
                                        showKeyboardPicker(context)
                                    } else {
                                        context.startActivity(Intent(Settings.ACTION_INPUT_METHOD_SETTINGS))
                                    }
                                }
                            )
                        )
                    }
                    if (showSetupAction) {
                        add(
                            PracticeActionButton(
                                label = "Apply Setup",
                                emphasized = false,
                                onClick = {
                                    scope.launch {
                                        applyPracticeLessonSetup(preferencesManager, lesson.setup)
                                    }
                                }
                            )
                        )
                    }
                }
                PracticeActionRow(actionButtons)
            }

            if (!hasMarkedCompleted && !lesson.isFreeform && lesson.exercises.size > 1) {
                val drillButtons = buildList {
                    if (currentExerciseIndex > 0) {
                        add(
                            PracticeActionButton(
                                label = "Previous Part",
                                emphasized = false,
                                onClick = {
                                    currentExerciseIndex -= 1
                                    typedText = ""
                                }
                            )
                        )
                    }
                    if (currentExerciseIndex < lesson.exercises.lastIndex) {
                        add(
                            PracticeActionButton(
                                label = "Next Part",
                                emphasized = true,
                                onClick = {
                                    currentExerciseIndex += 1
                                    typedText = ""
                                }
                            )
                        )
                    }
                }
                if (drillButtons.isNotEmpty()) {
                    PracticeActionRow(drillButtons)
                }
            }

            if (hasMarkedCompleted) {
                Card(colors = CardDefaults.cardColors(containerColor = CompletedLessonContainerColor)) {
                    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        Text("Lesson complete", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = CompletedLessonAccentColor)
                        val completionButtons = buildList {
                            add(
                                PracticeActionButton(
                                    label = "Replay Lesson",
                                    emphasized = false,
                                    onClick = {
                                        typedText = ""
                                        hasMarkedCompleted = false
                                        currentExerciseIndex = 0
                                        completedExerciseIds = emptySet()
                                    }
                                )
                            )
                            nextLesson?.let { targetLesson ->
                                add(
                                    PracticeActionButton(
                                        label = "Next Lesson",
                                        emphasized = true,
                                        onClick = {
                                            onOpenLesson(targetLesson.id, completedLessonIds.contains(targetLesson.id))
                                        }
                                    )
                                )
                            }
                        }
                        PracticeActionRow(completionButtons)
                    }
                }
            }

            val lessonButtons = buildList {
                previousLesson?.let { targetLesson ->
                    add(
                        PracticeActionButton(
                            label = "Previous Lesson",
                            emphasized = false,
                            onClick = { onOpenLesson(targetLesson.id, completedLessonIds.contains(targetLesson.id)) }
                        )
                    )
                }
                if (!hasMarkedCompleted) {
                    nextLesson?.let { targetLesson ->
                        add(
                            PracticeActionButton(
                                label = "Next Lesson",
                                emphasized = true,
                                onClick = { onOpenLesson(targetLesson.id, completedLessonIds.contains(targetLesson.id)) }
                            )
                        )
                    }
                }
            }
            if (lessonButtons.isNotEmpty()) {
                PracticeActionRow(lessonButtons)
            }
        }
    }
}

@Composable
private fun PracticeLessonInfoDialog(
    lesson: PracticeLesson,
    setupSummary: String?,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(lesson.title) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(lesson.focus, style = MaterialTheme.typography.bodyMedium)
                if (setupSummary != null) {
                    Text("Setup: $setupSummary", style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.SemiBold)
                }
                lesson.instructions.forEach { instruction ->
                    Text("• $instruction", style = MaterialTheme.typography.bodySmall)
                }
                Text(lesson.successHint, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Close")
            }
        }
    )
}

@Composable
private fun PracticeActionRow(buttons: List<PracticeActionButton>) {
    BoxWithConstraints(modifier = Modifier.fillMaxWidth()) {
        val stackButtons = maxWidth < 420.dp || buttons.size > 2
        if (stackButtons) {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                buttons.forEach { button ->
                    PracticeActionButtonView(button = button, modifier = Modifier.fillMaxWidth())
                }
            }
        } else {
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp), modifier = Modifier.fillMaxWidth()) {
                buttons.forEach { button ->
                    PracticeActionButtonView(button = button, modifier = Modifier.weight(1f))
                }
            }
        }
    }
}

@Composable
private fun PracticeActionButtonView(button: PracticeActionButton, modifier: Modifier = Modifier) {
    if (button.emphasized) {
        Button(onClick = button.onClick, modifier = modifier) {
            Text(button.label)
        }
    } else {
        OutlinedButton(onClick = button.onClick, modifier = modifier) {
            Text(button.label)
        }
    }
}

private data class PracticeActionButton(
    val label: String,
    val emphasized: Boolean,
    val onClick: () -> Unit
)

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

private fun compactLessonSummary(lesson: PracticeLesson): String {
    if (lesson.isFreeform) {
        return "Freeform quote practice"
    }

    val setup = lesson.setup ?: return "${lesson.exercises.size} parts"
    val dialLabel = if (setup.sixSectionDial) "6-section" else "8-section"
    val inputLabel = when (setup.inputMode) {
        PreferencesManager.INPUT_MODE_CONFIRM -> "Steady Type"
        PreferencesManager.INPUT_MODE_ASSISTED -> "One-Handed"
        else -> "Quick Type"
    }
    return "${lesson.exercises.size} parts • $dialLabel • $inputLabel"
}

private fun lessonStatusLabel(attempted: Boolean, completed: Boolean): String = when {
    completed -> "Completed"
    attempted -> "In progress"
    else -> "Not started"
}

private fun lessonPrimaryButtonLabel(attempted: Boolean, completed: Boolean): String = when {
    completed -> "Replay Lesson"
    attempted -> "Continue Lesson"
    else -> "Start Lesson"
}

private fun findNextIncompleteExerciseIndex(
    currentIndex: Int,
    exercises: List<PracticeExercise>,
    completedExerciseIds: Set<String>
): Int {
    for (index in currentIndex + 1..exercises.lastIndex) {
        if (!completedExerciseIds.contains(exercises[index].id)) {
            return index
        }
    }

    for (index in exercises.indices) {
        if (!completedExerciseIds.contains(exercises[index].id)) {
            return index
        }
    }

    return currentIndex
}

private val CompletedLessonContainerColor = Color(0xFFE6F4EA)
private val CompletedLessonAccentColor = Color(0xFF2E7D32)

private fun showKeyboardPicker(context: Context) {
    val inputMethodManager = context.getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
    inputMethodManager?.showInputMethodPicker()
}