package com.vatoo.erick

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.HelpOutline
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import kotlin.math.roundToInt

private val quoteTemplates = listOf(
    "Be yourself; everyone else is already taken.",
    "Believe you can and you're halfway there.",
    "The secret of getting ahead is getting started.",
    "Breathe in deeply to bring your mind home to your body.",
    "Sometimes the most productive thing you can do is relax.",
    "Be gentle with yourself. You're doing the best you can.",
    "Call your parents once in a while.",
    "The road to success is always under construction.",
    "Life is short. Smile while you still have teeth.",
    "You are enough just as you are.",
    "Small steps in the right direction can turn out to be the biggest step of your life.",
    "Just breathe.",
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TypingGameScreen(onBack: () -> Unit) {
    val appLanguage = LocalAppLanguageKey.current
    val quotes = remember(appLanguage) { quoteTemplates.map { erickText(appLanguage, it) } }
    var currentQuoteIndex by remember { mutableIntStateOf(0) }
    var typedText by remember { mutableStateOf("") }
    var totalKeystrokes by remember { mutableIntStateOf(0) }
    var correctKeystrokes by remember { mutableIntStateOf(0) }
    var streak by remember { mutableIntStateOf(0) }
    var currentQuoteHasError by remember { mutableStateOf(false) }
    var startTimeMs by remember { mutableLongStateOf(0L) }
    var totalCharsTyped by remember { mutableIntStateOf(0) }
    var wpm by remember { mutableDoubleStateOf(0.0) }
    var quoteOrder by remember(appLanguage) { mutableStateOf(quotes.indices.shuffled()) }
    var showHelpDialog by remember { mutableStateOf(false) }

    val currentQuote = quotes[quoteOrder[currentQuoteIndex % quotes.size]]
    val focusRequester = remember { FocusRequester() }

    fun restartSession() {
        currentQuoteIndex = 0
        typedText = ""
        totalKeystrokes = 0
        correctKeystrokes = 0
        streak = 0
        currentQuoteHasError = false
        startTimeMs = 0L
        totalCharsTyped = 0
        wpm = 0.0
        quoteOrder = quotes.indices.shuffled()
    }

    // Update WPM every 500ms
    LaunchedEffect(Unit) {
        while (true) {
            delay(500)
            if (startTimeMs > 0 && totalCharsTyped > 0) {
                val elapsedMs = System.currentTimeMillis() - startTimeMs
                if (elapsedMs > 0) {
                    wpm = (totalCharsTyped / 5.0) / (elapsedMs / 60000.0)
                }
            }
        }
    }

    // Request focus on first composition
    LaunchedEffect(Unit) {
        focusRequester.requestFocus()
    }

    val isDark = MaterialTheme.colorScheme.background.luminance() < 0.5f
    val bgColor = if (isDark) Color(0xFF1A1A2E) else Color(0xFFF0E6FF)

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(erickText(appLanguage, "Typing Practice")) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = erickText(appLanguage, "Close"))
                    }
                },
                actions = {
                    IconButton(onClick = { showHelpDialog = true }) {
                        Icon(Icons.AutoMirrored.Filled.HelpOutline, contentDescription = erickText(appLanguage, "How quote practice works"))
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Transparent
                )
            )
        },
        containerColor = bgColor
    ) { padding ->
        if (showHelpDialog) {
            AlertDialog(
                onDismissRequest = { showHelpDialog = false },
                title = { Text(erickText(appLanguage, "How quote practice works")) },
                text = {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text(erickText(appLanguage, "Type the highlighted character next."))
                        Text(erickText(appLanguage, "Tap the quote card or anywhere on the screen if you need to refocus the hidden input field."))
                        Text(erickText(appLanguage, "Skip a quote if it is not useful, or restart the session to clear the running stats."))
                    }
                },
                confirmButton = {
                    TextButton(onClick = { showHelpDialog = false }) {
                        Text(erickText(appLanguage, "Close"))
                    }
                }
            )
        }

        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .imePadding()
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null
                ) {
                    focusRequester.requestFocus()
                }
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Stats Bar
                StatsBar(
                    appLanguage = appLanguage,
                    wpm = wpm,
                    accuracy = if (totalKeystrokes > 0) (correctKeystrokes.toDouble() / totalKeystrokes * 100) else 100.0,
                    streak = streak
                )

                Spacer(modifier = Modifier.height(24.dp))

                // Quote Display
                Card(
                    modifier = Modifier
                        .fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.85f)
                    ),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .defaultMinSize(minHeight = 200.dp)
                            .padding(24.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            QuoteDisplay(
                                quote = currentQuote,
                                typedText = typedText
                            )

                            Spacer(modifier = Modifier.height(16.dp))

                            Text(
                                text = "${typedText.length} / ${currentQuote.length}",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Invisible input field
                BasicTextField(
                    value = typedText,
                    onValueChange = { newValue ->
                        // Only process additions (ignore deletions for scoring, but allow them)
                        if (newValue.length > typedText.length) {
                            val newChars = newValue.length - typedText.length
                            // Start timer on first keystroke
                            if (startTimeMs == 0L) {
                                startTimeMs = System.currentTimeMillis()
                            }
                            totalKeystrokes += newChars
                            totalCharsTyped += newChars

                            // Check correctness of newly typed characters
                            for (i in typedText.length until newValue.length) {
                                if (i < currentQuote.length && newValue[i] == currentQuote[i]) {
                                    correctKeystrokes++
                                } else {
                                    currentQuoteHasError = true
                                }
                            }
                        }

                        typedText = newValue

                        // Check if quote is completed
                        if (typedText.length >= currentQuote.length) {
                            val isCorrect = typedText == currentQuote
                            if (isCorrect && !currentQuoteHasError) {
                                streak++
                            } else {
                                streak = 0
                            }
                            // Advance to next quote immediately
                            currentQuoteIndex++
                            if (currentQuoteIndex >= quoteOrder.size) {
                                quoteOrder = quotes.indices.shuffled()
                                currentQuoteIndex = 0
                            }
                            typedText = ""
                            currentQuoteHasError = false
                        }
                    },
                    modifier = Modifier
                        .focusRequester(focusRequester)
                        .fillMaxWidth()
                        .height(1.dp)
                        .background(Color.Transparent),
                    textStyle = TextStyle(color = Color.Transparent, fontSize = 1.sp),
                    cursorBrush = SolidColor(Color.Transparent)
                )

                Spacer(modifier = Modifier.height(12.dp))

                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    OutlinedButton(
                        onClick = {
                            currentQuoteIndex++
                            if (currentQuoteIndex >= quoteOrder.size) {
                                quoteOrder = quotes.indices.shuffled()
                                currentQuoteIndex = 0
                            }
                            typedText = ""
                            currentQuoteHasError = false
                        }
                    ) {
                        Text(erickText(appLanguage, "Skip Quote"))
                    }

                    OutlinedButton(onClick = ::restartSession) {
                        Text(erickText(appLanguage, "Restart Session"))
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = erickText(appLanguage, "Start typing when you are ready."),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(bottom = 24.dp)
                )
            }
        }
    }
}

@Composable
private fun QuoteDisplay(quote: String, typedText: String) {
    var isShaking by remember { mutableStateOf(false) }
    val shakeOffset by animateFloatAsState(
        targetValue = if (isShaking) 2f else 0f,
        animationSpec = if (isShaking) {
            repeatable(
                iterations = 18, // ~1.4 seconds at 80ms per cycle
                animation = tween(80, easing = LinearEasing),
                repeatMode = RepeatMode.Reverse
            )
        } else {
            tween(80)
        },
        finishedListener = { isShaking = false },
        label = "shakeAnim"
    )

    val hasError = typedText.indices.any { i ->
        i < quote.length && typedText[i] != quote[i]
    }

    // Trigger shake when a new error appears
    LaunchedEffect(hasError) {
        if (hasError) {
            isShaking = true
        }
    }

    val annotatedQuote = buildAnnotatedString {
        for (i in quote.indices) {
            when {
                i < typedText.length && typedText[i] == quote[i] -> {
                    // Correctly typed
                    withStyle(SpanStyle(color = Color(0xFF4CAF50), fontWeight = FontWeight.Bold)) {
                        append(quote[i])
                    }
                }
                i < typedText.length -> {
                    // Incorrectly typed
                    withStyle(
                        SpanStyle(
                            color = Color(0xFFF44336),
                            fontWeight = FontWeight.Bold,
                            background = Color(0x33F44336)
                        )
                    ) {
                        append(quote[i])
                    }
                }
                i == typedText.length -> {
                    // Current character (cursor position) — highlighted background
                    withStyle(
                        SpanStyle(
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onPrimary,
                            background = MaterialTheme.colorScheme.primary.copy(alpha = 0.6f)
                        )
                    ) {
                        append(quote[i])
                    }
                }
                else -> {
                    // Not yet typed
                    withStyle(SpanStyle(color = MaterialTheme.colorScheme.onSurfaceVariant)) {
                        append(quote[i])
                    }
                }
            }
        }
    }

    val offsetModifier = if (isShaking) {
        Modifier.offset(x = shakeOffset.dp)
    } else {
        Modifier
    }

    Text(
        text = annotatedQuote,
        style = MaterialTheme.typography.headlineSmall.copy(
            lineHeight = 36.sp,
            textAlign = TextAlign.Center
        ),
        modifier = offsetModifier
    )
}

@Composable
private fun StatsBar(appLanguage: String, wpm: Double, accuracy: Double, streak: Int) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.7f)
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            StatItem(label = erickText(appLanguage, "WPM"), value = wpm.roundToInt().toString())
            StatItem(label = erickText(appLanguage, "Accuracy"), value = "${accuracy.roundToInt()}%")
            StatItem(label = erickText(appLanguage, "Streak"), value = streak.toString())
        }
    }
}

@Composable
private fun StatItem(label: String, value: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = value,
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

private fun Color.luminance(): Float {
    val r = red
    val g = green
    val b = blue
    return 0.2126f * r + 0.7152f * g + 0.0722f * b
}
