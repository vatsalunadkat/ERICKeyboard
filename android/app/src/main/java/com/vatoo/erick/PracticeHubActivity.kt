package com.vatoo.erick

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
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

    when (selectedLessonId) {
        QUOTE_PRACTICE_LESSON_ID -> {
            LaunchedEffect(Unit) {
                preferencesManager.markPracticeLessonAttempted(QUOTE_PRACTICE_LESSON_ID)
            }
            TypingGameScreen(onBack = { selectedLessonId = null })
            return
        }
        null -> Unit
        else -> {
            val lesson = practiceLessons.firstOrNull { it.id == selectedLessonId }
            if (lesson != null) {
                PracticeLessonDetailScreen(
                    lesson = lesson,
                    isCompleted = completedLessons.contains(lesson.id),
                    onBack = { selectedLessonId = null },
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
    lesson: PracticeLesson,
    isCompleted: Boolean,
    onBack: () -> Unit,
    onMarkAttempted: suspend () -> Unit,
    onMarkCompleted: suspend () -> Unit
) {
    var typedText by rememberSaveable(lesson.id) { mutableStateOf("") }
    var hasMarkedCompleted by remember(lesson.id, isCompleted) { mutableStateOf(isCompleted) }

    LaunchedEffect(lesson.id) {
        onMarkAttempted()
    }

    LaunchedEffect(typedText, lesson.id) {
        val target = lesson.targetText
        if (!lesson.isFreeform && !hasMarkedCompleted && target != null && typedText.trim().equals(target, ignoreCase = true)) {
            onMarkCompleted()
            hasMarkedCompleted = true
        }
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

            if (lesson.targetText != null) {
                Card {
                    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text("Target", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                        Text(lesson.targetText, style = MaterialTheme.typography.headlineMedium)
                        OutlinedTextField(
                            value = typedText,
                            onValueChange = { typedText = it },
                            label = { Text("Type the target here") },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true
                        )
                        if (hasMarkedCompleted) {
                            Text(
                                "Lesson complete. You can replay it or go back to the hub.",
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
                    }
                }
            }

            Row(horizontalArrangement = Arrangement.spacedBy(12.dp), modifier = Modifier.fillMaxWidth()) {
                OutlinedButton(onClick = onBack, modifier = Modifier.weight(1f)) {
                    Text("Back to Hub")
                }
                if (!lesson.isFreeform) {
                    Button(onClick = { typedText = "" }, modifier = Modifier.weight(1f)) {
                        Text("Clear Target")
                    }
                }
            }
        }
    }
}