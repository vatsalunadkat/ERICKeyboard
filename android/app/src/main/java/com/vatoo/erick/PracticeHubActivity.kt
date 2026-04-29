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

            ProvideAppLanguage(preferencesManager = preferencesManager) {
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
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun PracticeHubScreen(
    preferencesManager: PreferencesManager,
    attemptedLessons: Set<String>,
    completedLessons: Set<String>,
    onBack: () -> Unit
) {
    val appLanguage = LocalAppLanguageKey.current
    val keyboardLanguage by preferencesManager.keyboardLanguage.collectAsState(initial = PreferencesManager.LANGUAGE_ENGLISH)
    val localizedLessons = remember(appLanguage) { practiceLessonsForLanguage(appLanguage) }
    val localizedSectionModels = remember(appLanguage) { practiceSectionModelsForLanguage(appLanguage) }
    var selectedLessonId by rememberSaveable { mutableStateOf<String?>(null) }
    var quotePracticeActive by rememberSaveable { mutableStateOf(false) }
    var infoLessonId by rememberSaveable { mutableStateOf<String?>(null) }
    var startLessonFromBeginning by rememberSaveable { mutableStateOf(false) }

    infoLessonId?.let { lessonId ->
        localizedLessons.firstOrNull { it.id == lessonId }?.let { lesson ->
            PracticeLessonInfoDialog(
                lesson = lesson,
                appLanguage = appLanguage,
                setupSummary = lesson.setup?.let { formatLessonSetup(it, appLanguage) },
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
            val lesson = localizedLessons.firstOrNull { it.id == selectedLessonId }
            if (lesson != null) {
                val lessonIndex = localizedLessons.indexOfFirst { it.id == lesson.id }
                if (lesson.id == QUOTE_PRACTICE_LESSON_ID && quotePracticeActive) {
                    TypingGameScreen(onBack = { quotePracticeActive = false })
                    return
                }
                PracticeLessonDetailScreen(
                    preferencesManager = preferencesManager,
                    appLanguage = appLanguage,
                    lesson = lesson,
                    lessons = localizedLessons,
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
                title = { Text(erickText(appLanguage, "Practice Lessons")) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = erickText(appLanguage, "Back")
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
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            val nextRecommendedLesson = localizedLessons.firstOrNull { lesson ->
                lesson.recommendedStep != null && !completedLessons.contains(lesson.id)
            }

            Card(colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        erickText(appLanguage, "Pick a lesson"),
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        erickText(appLanguage, "ERICK applies the lesson setup for you so you can focus on one drill at a time."),
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Text(
                        "${erickText(appLanguage, "Progress")}: ${completedLessons.size} ${erickText(appLanguage, "completed")} / ${attemptedLessons.size} ${erickText(appLanguage, "attempted")}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Card(colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        erickText(appLanguage, "Recommended route"),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        erickText(appLanguage, "Start with the short 6-section lessons, then try the 8-section transition. Assisted and controller drills are follow-up paths."),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                    nextRecommendedLesson?.let { lesson ->
                        Text(
                            "${erickText(appLanguage, "Next recommended lesson")}: ${erickText(appLanguage, "Step")} ${lesson.recommendedStep} - ${lesson.title}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                        Button(
                            onClick = { openLesson(lesson.id, replayFromBeginning = false) },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(erickText(appLanguage, "Open Recommended Lesson"))
                        }
                    } ?: Text(
                        erickText(appLanguage, "You have finished the guided route. Use the follow-up paths or jump into Quote Practice."),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }
            }

            if (keyboardLanguage != PreferencesManager.LANGUAGE_ENGLISH) {
                Card(colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer)) {
                    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        val languageLabel = keyboardLanguageSelfDisplayName(keyboardLanguage)
                        Text(
                            "$languageLabel ${erickText(appLanguage, "typing tip")}",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            erickText(appLanguage, "In 8-section mode, extra language characters appear directly in the logical map. In 6-section mode, open Symbols to reach the extra language characters while the shipped utility wheel stays unchanged."),
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSecondaryContainer
                        )
                    }
                }
            }

            localizedSectionModels.forEach { sectionModel ->
                val sectionLessons = localizedLessons.filter { it.section == sectionModel.section }
                if (sectionLessons.isEmpty()) return@forEach

                Text(sectionModel.title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                Text(
                    sectionModel.summary,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                sectionLessons.forEach { lesson ->
                    val attempted = attemptedLessons.contains(lesson.id)
                    val completed = completedLessons.contains(lesson.id)
                    val isNextRecommended = nextRecommendedLesson?.id == lesson.id
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            containerColor = if (completed) CompletedLessonContainerColor else MaterialTheme.colorScheme.surface
                        )
                    ) {
                        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                    lesson.recommendedStep?.let { step ->
                                        Text(
                                            if (isNextRecommended) {
                                                "${erickText(appLanguage, "Recommended next")} · ${erickText(appLanguage, "Step")} $step"
                                            } else {
                                                "${erickText(appLanguage, "Step")} $step"
                                            },
                                            style = MaterialTheme.typography.labelMedium,
                                            color = if (isNextRecommended) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.tertiary
                                        )
                                    }
                                    Text(lesson.title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                                    Text(
                                        compactLessonSummary(lesson, appLanguage),
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                    if (lesson.setupReason.isNotBlank()) {
                                        Text(
                                            "${erickText(appLanguage, "Why this setup")}: ${lesson.setupReason}",
                                            style = MaterialTheme.typography.bodySmall,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                }
                                IconButton(onClick = { infoLessonId = lesson.id }) {
                                    Icon(
                                        Icons.AutoMirrored.Filled.HelpOutline,
                                        contentDescription = erickText(appLanguage, "Lesson help")
                                    )
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
                                    lessonStatusLabel(appLanguage, attempted, completed),
                                    style = MaterialTheme.typography.labelMedium,
                                    color = if (completed) CompletedLessonAccentColor else MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                            Button(
                                onClick = { openLesson(lesson.id, completed) },
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text(
                                    lessonPrimaryButtonLabel(
                                        languageKey = appLanguage,
                                        attempted = attempted,
                                        completed = completed,
                                        recommendedStep = lesson.recommendedStep,
                                        isNextRecommended = isNextRecommended
                                    )
                                )
                            }
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
    appLanguage: String,
    lesson: PracticeLesson,
    lessons: List<PracticeLesson>,
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
            appLanguage = appLanguage,
            setupSummary = lesson.setup?.let { formatLessonSetup(it, appLanguage) },
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
    val recommendedSetup = lessonSetup?.let { formatLessonSetup(it, appLanguage) }
    val setupMatchesLesson = lessonSetup?.let {
        it.sixSectionDial == sixSectionDial && it.layoutType == layoutType && it.inputMode == inputMode
    } ?: true
    val keyboardActionLabel = when {
        !keyboardStatus.isEnabled -> erickText(appLanguage, "Enable ERICK")
        else -> erickText(appLanguage, "Switch to ERICK")
    }
    val showKeyboardAction = !keyboardStatus.isCurrent
    val showSetupAction = !setupMatchesLesson
    val previousLesson = lessons.getOrNull(lessonIndex - 1)
    val nextLesson = lessons.getOrNull(lessonIndex + 1)
    val completedPartsLabel = if (lesson.isFreeform) {
        erickText(appLanguage, "Freeform practice")
    } else {
        "${completedExerciseIds.size} ${erickText(appLanguage, "of")} ${lesson.exercises.size} ${erickText(appLanguage, "parts done")}"
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(lesson.title) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = erickText(appLanguage, "Back")
                        )
                    }
                },
                actions = {
                    IconButton(onClick = {
                        context.startActivity(Intent(context, SettingsActivity::class.java))
                    }) {
                        Icon(Icons.Default.Settings, contentDescription = erickText(appLanguage, "Settings"))
                    }
                    IconButton(onClick = { showInfoDialog = true }) {
                        Icon(
                            Icons.AutoMirrored.Filled.HelpOutline,
                            contentDescription = erickText(appLanguage, "Lesson help")
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
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Card(colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        lessonHeaderLabel(lesson, currentExerciseIndex, appLanguage),
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
                    if (lesson.setupReason.isNotBlank()) {
                        Text(
                            "${erickText(appLanguage, "Why this setup")}: ${lesson.setupReason}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
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
                            label = { Text(erickText(appLanguage, "Type the drill target here")) },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true
                        )
                    }
                }
            } else {
                Card {
                    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text(
                            erickText(appLanguage, "Launch longer freeform quote practice when you are ready."),
                            style = MaterialTheme.typography.bodyMedium
                        )
                        if (onLaunchFreeform != null) {
                            Button(onClick = onLaunchFreeform, modifier = Modifier.fillMaxWidth()) {
                                Text(erickText(appLanguage, "Launch Quote Practice"))
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
                                label = erickText(appLanguage, "Apply Setup"),
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
                                label = erickText(appLanguage, "Previous Part"),
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
                                label = erickText(appLanguage, "Next Part"),
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
                        Text(
                            erickText(appLanguage, "Lesson complete"),
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = CompletedLessonAccentColor
                        )
                        val completionButtons = buildList {
                            add(
                                PracticeActionButton(
                                    label = erickText(appLanguage, "Replay Lesson"),
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
                                        label = erickText(appLanguage, "Next Lesson"),
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
                            label = erickText(appLanguage, "Previous Lesson"),
                            emphasized = false,
                            onClick = { onOpenLesson(targetLesson.id, completedLessonIds.contains(targetLesson.id)) }
                        )
                    )
                }
                if (!hasMarkedCompleted) {
                    nextLesson?.let { targetLesson ->
                        add(
                            PracticeActionButton(
                                label = erickText(appLanguage, "Next Lesson"),
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
    appLanguage: String,
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
                    Text(
                        "${erickText(appLanguage, "Setup")}: $setupSummary",
                        style = MaterialTheme.typography.bodySmall,
                        fontWeight = FontWeight.SemiBold
                    )
                }
                if (lesson.setupReason.isNotBlank()) {
                    Text(
                        "${erickText(appLanguage, "Why this setup")}: ${lesson.setupReason}",
                        style = MaterialTheme.typography.bodySmall
                    )
                }
                lesson.instructions.forEach { instruction ->
                    Text("• $instruction", style = MaterialTheme.typography.bodySmall)
                }
                Text(lesson.successHint, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text(erickText(appLanguage, "Close"))
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

private fun formatLessonSetup(setup: PracticeLessonSetup, languageKey: String): String {
    val dialLabel = if (setup.sixSectionDial) {
        erickText(languageKey, "6-section")
    } else {
        erickText(languageKey, "8-section")
    }
    val layoutLabel = when (setup.layoutType) {
        PreferencesManager.LAYOUT_EFFICIENCY -> erickText(languageKey, "Efficiency")
        PreferencesManager.LAYOUT_CUSTOM -> erickText(languageKey, "Custom")
        else -> erickText(languageKey, "Logical")
    }
    val inputLabel = when (setup.inputMode) {
        PreferencesManager.INPUT_MODE_CONFIRM -> erickText(languageKey, "Steady Type")
        PreferencesManager.INPUT_MODE_ASSISTED -> erickText(languageKey, "One-Handed")
        else -> erickText(languageKey, "Quick Type")
    }
    return "$dialLabel • $layoutLabel • $inputLabel"
}

private fun compactLessonSummary(lesson: PracticeLesson, languageKey: String): String {
    if (lesson.isFreeform) {
        return erickText(languageKey, "Freeform quote practice")
    }

    val setup = lesson.setup ?: return "${lesson.exercises.size} ${erickText(languageKey, "parts")}" 
    val parts = mutableListOf<String>()
    lesson.recommendedStep?.let { parts.add("${erickText(languageKey, "Step")} $it") }
    parts.add("${lesson.exercises.size} ${erickText(languageKey, "parts")}")
    val dialLabel = if (setup.sixSectionDial) erickText(languageKey, "6-section") else erickText(languageKey, "8-section")
    val inputLabel = when (setup.inputMode) {
        PreferencesManager.INPUT_MODE_CONFIRM -> erickText(languageKey, "Steady Type")
        PreferencesManager.INPUT_MODE_ASSISTED -> erickText(languageKey, "One-Handed")
        else -> erickText(languageKey, "Quick Type")
    }
    parts.add(dialLabel)
    parts.add(inputLabel)
    return parts.joinToString(" • ")
}

private fun keyboardLanguageSelfDisplayName(keyboardLanguage: String): String = when (keyboardLanguage) {
    PreferencesManager.LANGUAGE_SPANISH -> "Espanol"
    PreferencesManager.LANGUAGE_PORTUGUESE -> "Portugues"
    PreferencesManager.LANGUAGE_FRENCH -> "Francais"
    PreferencesManager.LANGUAGE_GERMAN -> "Deutsch"
    PreferencesManager.LANGUAGE_ITALIAN -> "Italiano"
    PreferencesManager.LANGUAGE_NORWEGIAN_BOKMAL -> "Norsk Bokmal"
    PreferencesManager.LANGUAGE_DANISH -> "Dansk"
    PreferencesManager.LANGUAGE_SWEDISH -> "Svenska"
    PreferencesManager.LANGUAGE_FINNISH -> "Suomi"
    else -> "English"
}

private fun lessonStatusLabel(languageKey: String, attempted: Boolean, completed: Boolean): String = when {
    completed -> erickText(languageKey, "Completed")
    attempted -> erickText(languageKey, "In progress")
    else -> erickText(languageKey, "Not started")
}

private fun lessonPrimaryButtonLabel(
    languageKey: String,
    attempted: Boolean,
    completed: Boolean,
    recommendedStep: Int?,
    isNextRecommended: Boolean
): String = when {
    completed -> erickText(languageKey, "Replay Lesson")
    attempted && isNextRecommended -> erickText(languageKey, "Continue Recommended Lesson")
    attempted -> erickText(languageKey, "Continue Lesson")
    isNextRecommended && recommendedStep != null -> "${erickText(languageKey, "Start Step")} $recommendedStep"
    else -> erickText(languageKey, "Start Lesson")
}

private data class PracticeSectionModel(
    val section: PracticeLessonSection,
    val title: String,
    val summary: String
)

private fun practiceSectionModelsForLanguage(languageKey: String) = listOf(
    PracticeSectionModel(
        section = PracticeLessonSection.START_HERE,
        title = erickText(languageKey, "Start Here"),
        summary = erickText(languageKey, "The guided route keeps the dial mode simple first, then adds more surfaces one step at a time.")
    ),
    PracticeSectionModel(
        section = PracticeLessonSection.FOLLOW_UP,
        title = erickText(languageKey, "Mode Follow-Ups"),
        summary = erickText(languageKey, "Use these once the main route feels understandable and you want a specific typing path.")
    ),
    PracticeSectionModel(
        section = PracticeLessonSection.ADVANCED,
        title = erickText(languageKey, "Advanced Practice"),
        summary = erickText(languageKey, "Open-ended practice for when the guided drills already feel easy.")
    )
)

private fun lessonHeaderLabel(
    lesson: PracticeLesson,
    currentExerciseIndex: Int,
    languageKey: String
): String = when {
    lesson.isFreeform -> erickText(languageKey, "Advanced practice")
    lesson.recommendedStep != null -> "${erickText(languageKey, "Recommended step")} ${lesson.recommendedStep}"
    else -> "${erickText(languageKey, "Part")} ${currentExerciseIndex + 1} ${erickText(languageKey, "of")} ${lesson.exercises.size}"
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