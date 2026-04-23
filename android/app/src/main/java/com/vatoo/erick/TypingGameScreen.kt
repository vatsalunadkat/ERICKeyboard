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

private val quotes = listOf(
    // Motivational
    "Be yourself; everyone else is already taken.",
    "It's not the load that breaks you down, it's the way you carry it.",
    "For every minute you are angry, you lose sixty seconds of happiness.",
    "The only way to do great work is to love what you do.",
    "In the middle of difficulty lies opportunity.",
    "Believe you can and you're halfway there.",
    "The best time to plant a tree was twenty years ago. The second best time is now.",
    "You are never too old to set another goal or to dream a new dream.",
    "Happiness is not something ready made. It comes from your own actions.",
    "Every moment is a fresh beginning.",
    "Good decisions come from experience. Experience comes from making bad decisions.",
    "Somewhere, something incredible is waiting to be known.",
    "Never memorize something you can look up.",
    "Reality is broken. Game designers can probably fix it.",
    "The secret of getting ahead is getting started.",
    "It always seems impossible until it's done.",
    "Do what you can, with what you have, where you are.",
    "Everything you can imagine is real.",
    "If you want to lift yourself up, lift up someone else.",
    "What we achieve inwardly will change outer reality.",
    // Relaxing / Calm
    "Breathe in deeply to bring your mind home to your body.",
    "Almost everything will work again if you unplug it for a few minutes, including you.",
    "The quieter you become, the more you can hear.",
    "Nature does not hurry, yet everything is accomplished.",
    "Slow down and everything you are chasing will come around and catch you.",
    "The greatest weapon against stress is our ability to choose one thought over another.",
    "Your calm mind is the ultimate weapon against your challenges.",
    "Calm mind brings inner strength and self-confidence.",
    "Life is ten percent what happens to you and ninety percent how you respond to it.",
    "I think I'm supposed to be sleeping right now.",
    "Sometimes the most productive thing you can do is relax.",
    "You don't always need a plan. Sometimes you just need to breathe and let go.",
    "Not all storms come to disrupt your life. Some come to clear your path.",
    "Be gentle with yourself. You're doing the best you can.",
    // Humorous / Witty
    "Call your parents once in a while.",
    "The road to success is always under construction.",
    "If at first you don't succeed, redefine success.",
    "Sure, honey, let's buy 86 throw pillows.",
    "Adulting is soup, and I'm a fork.",
    "I've got 99 problems and 86 of them are completely made up in my head.",
    "I don't stalk people on Instagram. I deeply research their lives.",
    "You can't rush perfection, especially when you're avoiding it.",
    "I don't always have patience, but when I do, it's very short-lived.",
    "I invented a new word today: Plagiarism.",
    "Age is an issue of mind over matter. If you don't mind, it doesn't matter.",
    "If you can't convince them, confuse them.",
    "The best way to remember your wife's birthday is to forget it once.",
    "Being an adult is like folding a fitted sheet. No one really knows how.",
    "Some things are better left unsaid. Which I generally realize right after I have said them.",
    "The odds of going to the store for a loaf of bread and coming out with only a loaf of bread are three billion to one.",
    "A giraffe's coffee would be cold by the time it reached the bottom of its throat. Ever think about that? No. You only think about yourself.",
    "I never make the same mistake twice. I make it five or six times, you know, just to be sure.",
    "The worst part about online shopping is having to get up and get your card out of your handbag.",
    "If you read a lot of books, you're considered well-read. But if you watch a lot of TV, you're not considered well-viewed.",
    "When I say I won't tell anyone, my sister doesn't count.",
    "Always forgive your enemies; nothing annoys them so much.",
    "Life is short. Smile while you still have teeth.",
    "I used to think I was indecisive, but now I'm not so sure.",
    "Age is of no importance, unless you are cheese.",
    "A clear conscience is usually the sign of a bad memory.",
    "My wallet is like an onion. Opening it makes me cry.",
    "My bed is a magical place where I suddenly remember everything I forgot to do.",
    "I followed my heart, and it led me to the fridge.",
    "I don't have a bucket list, but my to-do list is long enough to qualify.",
    "The elevator to success is out of order. You'll have to take the stairs.",
    "People say nothing is impossible, but I do nothing every day.",
    "Life is too short to remove the USB safely.",
    "If we shouldn't eat at night, why is there a light in the fridge?",
    "My favorite exercise is a cross between a lunge and a crunch. I call it lunch.",
    "I need six months of vacation, twice a year.",
    // Positive / Uplifting
    "You are enough just as you are.",
    "Every day may not be good, but there is something good in every day.",
    "Stars can't shine without darkness.",
    "You are braver than you believe, stronger than you seem, and smarter than you think.",
    "Difficult roads often lead to beautiful destinations.",
    "Be the reason someone smiles today.",
    "The sun will rise and we will try again.",
    "You make the world a better place just by being in it.",
    "One small positive thought can change your whole day.",
    "You don't have to be perfect to be amazing.",
    "You are capable of amazing things.",
    "Your only limit is your mind.",
    "Fall seven times, stand up eight.",
    "What lies behind us and what lies before us are tiny matters compared to what lies within us.",
    "The best is yet to come.",
    "You were born to be real, not to be perfect.",
    "In a world where you can be anything, be kind.",
    "Small steps in the right direction can turn out to be the biggest step of your life.",
    // Short & Sweet
    "Keep going...",
    "This too shall pass.",
    "You've got this.",
    "Dream big.",
    "Stay curious.",
    "Be kind.",
    "Just breathe.",
    "Choose joy.",
    "Progress, not perfection.",
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TypingGameScreen(onBack: () -> Unit) {
    var currentQuoteIndex by remember { mutableIntStateOf(0) }
    var typedText by remember { mutableStateOf("") }
    var totalKeystrokes by remember { mutableIntStateOf(0) }
    var correctKeystrokes by remember { mutableIntStateOf(0) }
    var streak by remember { mutableIntStateOf(0) }
    var currentQuoteHasError by remember { mutableStateOf(false) }
    var startTimeMs by remember { mutableLongStateOf(0L) }
    var totalCharsTyped by remember { mutableIntStateOf(0) }
    var wpm by remember { mutableDoubleStateOf(0.0) }
    var quoteOrder by remember { mutableStateOf(quotes.indices.shuffled()) }
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
                title = { Text("Typing Practice") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = { showHelpDialog = true }) {
                        Icon(Icons.AutoMirrored.Filled.HelpOutline, contentDescription = "Practice help")
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
                title = { Text("How quote practice works") },
                text = {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text("Type the highlighted character next.")
                        Text("Tap the quote card or anywhere on the screen if you need to refocus the hidden input field.")
                        Text("Skip a quote if it is not useful, or restart the session to clear the running stats.")
                    }
                },
                confirmButton = {
                    TextButton(onClick = { showHelpDialog = false }) {
                        Text("Close")
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
                        Text("Skip Quote")
                    }

                    OutlinedButton(onClick = ::restartSession) {
                        Text("Restart Session")
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = "Start typing when you are ready.",
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
private fun StatsBar(wpm: Double, accuracy: Double, streak: Int) {
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
            StatItem(label = "WPM", value = wpm.roundToInt().toString())
            StatItem(label = "Accuracy", value = "${accuracy.roundToInt()}%")
            StatItem(label = "Streak", value = streak.toString())
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
