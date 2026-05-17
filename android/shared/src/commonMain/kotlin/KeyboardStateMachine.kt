package com.vatoo.erick.shared

import kotlinx.coroutines.*
import kotlin.math.hypot

class KeyboardStateMachine(
    private val delegate: KeyboardActionDelegate,
    private val coroutineScope: CoroutineScope // Lifecycle-bound scope provided by Android/iOS
) {
    private val processor = KeyboardLogic()
    private var currentLanguage = KeyboardLanguage.ENGLISH
    private var currentPredictionDomain = PredictionDomain.GENERAL
    private var predictionProfilesByLanguage = mutableMapOf<KeyboardLanguage, String>()
    private var predictor = WordPredictionEngine.createWithDefaultDictionary(currentLanguage)
    private val DEADZONE_RADIUS = 40f
    private var controllerDeadZone = 0.25f
    private var controllerYAxisMultiplier = 1f

    // Word buffer: tracks the current word being typed
    private val wordBuffer = StringBuilder()
    // Tracks the last completed word for next-word prediction
    private var lastCompletedWord = ""
    // Whether current suggestions are next-word predictions (buffer empty)
    var isNextWordMode: Boolean = false
        private set

    // Current suggestions (visible to platforms)
    var currentSuggestions: List<String> = emptyList()
        private set

    init {
        predictionProfilesByLanguage = PredictionProfileBundle.deserialize(delegate.loadPredictionProfile()).toMutableMap()
        processor.activeLanguage = currentLanguage
        predictor = WordPredictionEngine.createWithDefaultDictionary(currentLanguage)
        predictor.setPredictionDomain(currentPredictionDomain)
        predictor.importLearnedProfile(predictionProfilesByLanguage[currentLanguage].orEmpty())
        // Show default suggestions when keyboard first opens
        updateSuggestions()
    }

    // Core state
    private var leftActiveDir = Direction.NONE
    private var rightActiveDir = Direction.NONE
    private var leftActiveSource: InputSource? = null
    private var rightActiveSource: InputSource? = null
    private var leftTouchDir = Direction.NONE
    private var rightTouchDir = Direction.NONE
    private var leftControllerDir = Direction.NONE
    private var rightControllerDir = Direction.NONE
    var currentMode = KeyboardMode.NORMAL
        private set(value) {
            if (field != value) {
                field = value
                delegate.onModeChanged(value)
            }
        }
    var currentLayoutType = LayoutType.LOGICAL
        private set
    var currentPaletteType = ColorPaletteType.DEFAULT
        private set
    var leftHandedMode = false
        private set
    var activeCustomLayout: CustomLayout? = null
    private var isChordExecuted = false

    // Symbols mode: remembers the mode before entering symbols
    private var preSymbolsMode = KeyboardMode.NORMAL
    private var preEmojiMode = KeyboardMode.NORMAL

    // Input mode
    var inputMode = InputMode.INSTANT
        private set

    // Assisted mode: locked left direction
    var lockedLeftDir = Direction.NONE
        private set

    // Accelerating backspace state
    private var backspaceRepeatJob: Job? = null
    private var backspaceHoldFired = false  // true if hold-repeat already deleted chars

    // Receives touch updates from the native platform
    fun handleTouch(x: Float, y: Float, isLeft: Boolean, actionDownOrMove: Boolean, actionUp: Boolean) {
        if (currentMode == KeyboardMode.EMOJI) {
            return
        }

        val effectiveIsLeft = getEffectiveSide(isLeft)

        val distance = hypot(x.toDouble(), y.toDouble()).toFloat()
        val currentDir = if (distance > DEADZONE_RADIUS) {
            processor.getDirectionFromXY(x, y)
        } else {
            Direction.NONE
        }

        if (actionDownOrMove) {
            updateDirectionalState(InputSource.TOUCH, effectiveIsLeft, currentDir)
        } else if (actionUp) {
            releaseDirectionalState(InputSource.TOUCH, effectiveIsLeft)
        }
    }

    fun setControllerDeadZone(deadZone: Float) {
        controllerDeadZone = deadZone.coerceIn(0f, 1f)
    }

    fun setControllerYAxisInverted(inverted: Boolean) {
        controllerYAxisMultiplier = if (inverted) -1f else 1f
    }

    fun handleControllerInput(leftX: Float, leftY: Float, rightX: Float, rightY: Float) {
        if (currentMode == KeyboardMode.EMOJI) {
            return
        }

        processControllerStick(
            input = normalizeControllerStick(leftX, leftY),
            isLeft = true
        )
        processControllerStick(
            input = normalizeControllerStick(rightX, rightY),
            isLeft = false
        )
    }

    @Suppress("UNUSED_PARAMETER")
    fun handleControllerButton(button: ControllerButton) {
        // Reserved for platform-specific button mapping in a later step.
    }
    fun setLayoutType(layout: LayoutType) {
        currentLayoutType = layout
    }

    fun setColorPalette(palette: ColorPaletteType) {
        currentPaletteType = palette
    }

    fun setLeftHandedMode(enabled: Boolean) {
        leftHandedMode = enabled
    }

    fun setDialSectionMode(mode: DialSectionMode) {
        processor.dialSectionMode = mode
        // If switching from 6-section to 8-section while in symbols mode, exit symbols
        if (mode == DialSectionMode.EIGHT_SECTION &&
            (currentMode == KeyboardMode.SYMBOLS || currentMode == KeyboardMode.SYMBOLS_SHIFTED)) {
            currentMode = KeyboardMode.NORMAL
        }
    }

    fun toggleEmojiPanel() {
        if (currentMode == KeyboardMode.EMOJI) {
            currentMode = preEmojiMode
            updateSuggestions()
            return
        }

        preEmojiMode = currentMode
        clearTransientInputState()
        currentMode = KeyboardMode.EMOJI
        suppressSuggestions()
    }

    fun getDialSectionMode(): DialSectionMode = processor.dialSectionMode

    fun isSymbolsMode(): Boolean = currentMode == KeyboardMode.SYMBOLS || currentMode == KeyboardMode.SYMBOLS_SHIFTED

    fun setInputMode(mode: InputMode) {
        inputMode = mode
        lockedLeftDir = Direction.NONE
        isChordExecuted = false
    }

    fun setPredictionDomain(domain: PredictionDomain) {
        currentPredictionDomain = domain
        predictor.setPredictionDomain(domain)
        updateSuggestions()
    }

    fun setKeyboardLanguage(language: KeyboardLanguage) {
        if (currentLanguage == language) return

        predictionProfilesByLanguage[currentLanguage] = predictor.exportLearnedProfile()
        currentLanguage = language
        processor.activeLanguage = language
        predictor = WordPredictionEngine.createWithDefaultDictionary(language)
        predictor.setPredictionDomain(currentPredictionDomain)
        predictor.importLearnedProfile(predictionProfilesByLanguage[language].orEmpty())
        lastCompletedWord = ""
        persistPredictionProfiles()
        syncWordBufferFromEditor()
    }

    fun getKeyboardLanguage(): KeyboardLanguage = currentLanguage

    fun getCurrentPalette(): List<ColorEntry> {
        return ColorPalettes.getPalette(currentPaletteType)
    }

    // Returns the live preview character for UI rendering
    fun getPreviewText(): String {
        if (currentMode == KeyboardMode.EMOJI) {
            return ""
        }

        val effectiveLeft = if (leftActiveDir != Direction.NONE) leftActiveDir
            else if (inputMode == InputMode.ASSISTED) lockedLeftDir
            else Direction.NONE
        return if (effectiveLeft != Direction.NONE && rightActiveDir != Direction.NONE) {
            processor.getChordResult(effectiveLeft, rightActiveDir, currentMode, currentLayoutType, activeCustomLayout)
        } else {
            ""
        }
    }

    fun getCharactersForDirection(dir: Direction): List<String> {
        if (currentMode == KeyboardMode.EMOJI) {
            return emptyList()
        }

        return processor.getCharactersForDirection(dir, currentMode, currentLayoutType, activeCustomLayout)
    }

    fun getCharactersAtPosition(rightDir: Direction): List<Pair<Direction, String>> {
        if (currentMode == KeyboardMode.EMOJI) {
            return emptyList()
        }

        return processor.getCharactersAtPosition(rightDir, currentMode, currentLayoutType, activeCustomLayout)
    }

    fun getDirections(): List<Direction> {
        return processor.getDirections()
    }

    fun getPreviewDirections(): List<Direction> {
        return processor.getPreviewDirections()
    }

    private fun normalizeControllerStick(x: Float, y: Float): ControllerStickInput {
        val snapshot = ControllerInputProcessor.resolveStick(
            x = x,
            y = y,
            deadZone = controllerDeadZone,
            invertY = controllerYAxisMultiplier < 0f,
            dialSectionMode = getDialSectionMode()
        )

        if (!snapshot.isActive) {
            return ControllerStickInput(0f, 0f, false)
        }

        return ControllerStickInput(
            x = snapshot.directionSpaceX,
            y = snapshot.directionSpaceY,
            isActive = true
        )
    }

    private fun processControllerStick(input: ControllerStickInput, isLeft: Boolean) {
        val effectiveIsLeft = getEffectiveSide(isLeft)

        when {
            input.isActive -> updateDirectionalState(
                source = InputSource.CONTROLLER,
                isLeft = effectiveIsLeft,
                dir = processor.getDirectionFromXY(input.x, input.y)
            )
            getSourceDirection(InputSource.CONTROLLER, effectiveIsLeft) != Direction.NONE ->
                releaseDirectionalState(InputSource.CONTROLLER, effectiveIsLeft)
        }
    }

    private fun updateDirectionalState(source: InputSource, isLeft: Boolean, dir: Direction) {
        setSourceDirection(source, isLeft, dir)
        recomputeActiveDirections()
        checkBackspaceHold()
    }

    private fun releaseDirectionalState(source: InputSource, isLeft: Boolean) {
        val sourceDir = getSourceDirection(source, isLeft)
        val wasEffectiveSource = getEffectiveSource(isLeft) == source
        val leftDirBeforeRelease = leftActiveDir
        val rightDirBeforeRelease = rightActiveDir

        // Cancel any active backspace repeat
        val wasBackspaceHold = backspaceHoldFired
        cancelBackspaceRepeat()

        if (sourceDir != Direction.NONE && wasEffectiveSource) {
            when (inputMode) {
                InputMode.INSTANT -> handleInstantRelease(isLeft, leftDirBeforeRelease, rightDirBeforeRelease, wasBackspaceHold)
                InputMode.CONFIRM -> handleConfirmRelease(isLeft, leftDirBeforeRelease, rightDirBeforeRelease, wasBackspaceHold)
                InputMode.ASSISTED -> handleAssistedRelease(isLeft, leftDirBeforeRelease, rightDirBeforeRelease, wasBackspaceHold)
            }
        }

        setSourceDirection(source, isLeft, Direction.NONE)
        recomputeActiveDirections()

        if (leftActiveDir == Direction.NONE && rightActiveDir == Direction.NONE) {
            isChordExecuted = false
        }
    }

    private fun handleInstantRelease(isLeft: Boolean, leftDir: Direction, rightDir: Direction, wasBackspaceHold: Boolean) {
        if (isLeft) {
            // Left release alone does not fire — left just sets the chord row.
            // The chord fires only when right releases while left is held.
        } else {
            if (leftDir != Direction.NONE) {
                // Right released while left is held — fire chord
                fireChord(leftDir, rightDir)
                // Allow subsequent right swipes to fire while left stays held
                isChordExecuted = false
            } else if (!isChordExecuted && !wasBackspaceHold) {
                handleRightOnlySwipe(rightDir)
            }
        }
    }

    private fun handleConfirmRelease(isLeft: Boolean, leftDir: Direction, rightDir: Direction, wasBackspaceHold: Boolean) {
        // Steady Type: fire chord when either dial releases while both active.
        // Only one chord per left-hold session -- isChordExecuted stays true
        // until both dials return to center.
        if (isLeft) {
            if (rightDir != Direction.NONE && !isChordExecuted) {
                fireChord(leftDir, rightDir)
            }
        } else {
            if (leftDir != Direction.NONE && !isChordExecuted) {
                fireChord(leftDir, rightDir)
            } else if (leftDir == Direction.NONE && !isChordExecuted && !wasBackspaceHold) {
                handleRightOnlySwipe(rightDir)
            }
        }
    }

    private fun handleAssistedRelease(isLeft: Boolean, leftDir: Direction, rightDir: Direction, wasBackspaceHold: Boolean) {
        if (isLeft) {
            // Lock the left direction for use with the right dial
            lockedLeftDir = leftDir
            // If right is also active, fire the chord immediately
            if (rightDir != Direction.NONE && !isChordExecuted) {
                fireChord(leftDir, rightDir)
                lockedLeftDir = Direction.NONE
            }
        } else {
            // Right dial released
            if (leftDir != Direction.NONE && !isChordExecuted) {
                // Both active: fire chord (normal behavior)
                fireChord(leftDir, rightDir)
                lockedLeftDir = Direction.NONE
            } else if (lockedLeftDir != Direction.NONE && leftDir == Direction.NONE && !isChordExecuted && !wasBackspaceHold) {
                // Left is released but locked: fire chord with locked+right
                fireChord(lockedLeftDir, rightDir)
                lockedLeftDir = Direction.NONE
            } else if (lockedLeftDir == Direction.NONE && leftDir == Direction.NONE && !isChordExecuted && !wasBackspaceHold) {
                handleRightOnlySwipe(rightDir)
            }
        }
    }

    private fun setSourceDirection(source: InputSource, isLeft: Boolean, dir: Direction) {
        when {
            source == InputSource.TOUCH && isLeft -> leftTouchDir = dir
            source == InputSource.TOUCH && !isLeft -> rightTouchDir = dir
            source == InputSource.CONTROLLER && isLeft -> leftControllerDir = dir
            else -> rightControllerDir = dir
        }
    }

    private fun getSourceDirection(source: InputSource, isLeft: Boolean): Direction {
        return when {
            source == InputSource.TOUCH && isLeft -> leftTouchDir
            source == InputSource.TOUCH && !isLeft -> rightTouchDir
            source == InputSource.CONTROLLER && isLeft -> leftControllerDir
            else -> rightControllerDir
        }
    }

    private fun recomputeActiveDirections() {
        val (resolvedLeftDir, resolvedLeftSource) = resolveEffectiveDirection(leftTouchDir, leftControllerDir)
        val (resolvedRightDir, resolvedRightSource) = resolveEffectiveDirection(rightTouchDir, rightControllerDir)

        leftActiveDir = resolvedLeftDir
        rightActiveDir = resolvedRightDir
        leftActiveSource = resolvedLeftSource
        rightActiveSource = resolvedRightSource
    }

    private fun resolveEffectiveDirection(
        touchDir: Direction,
        controllerDir: Direction
    ): Pair<Direction, InputSource?> {
        return when {
            touchDir != Direction.NONE -> touchDir to InputSource.TOUCH
            controllerDir != Direction.NONE -> controllerDir to InputSource.CONTROLLER
            else -> Direction.NONE to null
        }
    }

    private fun getEffectiveSource(isLeft: Boolean): InputSource? {
        return if (isLeft) {
            leftActiveSource
        } else {
            rightActiveSource
        }
    }

    private fun getEffectiveSide(isLeft: Boolean): Boolean {
        return if (leftHandedMode) !isLeft else isLeft
    }

    private fun clearTransientInputState() {
        leftActiveDir = Direction.NONE
        rightActiveDir = Direction.NONE
        leftActiveSource = null
        rightActiveSource = null
        leftTouchDir = Direction.NONE
        rightTouchDir = Direction.NONE
        leftControllerDir = Direction.NONE
        rightControllerDir = Direction.NONE
        lockedLeftDir = Direction.NONE
        isChordExecuted = false
        cancelBackspaceRepeat(syncWordBufferFromEditor = false)
    }

    private fun fireChord(left: Direction, right: Direction) {
        if (left == Direction.NONE || right == Direction.NONE) return
        isChordExecuted = true

        val text = processor.getChordResult(left, right, currentMode, currentLayoutType, activeCustomLayout)
        if (text.isNotEmpty()) {
            delegate.commitText(text) // Tell the delegate to commit text!
            onTextCommitted(text)
        }

        // After typing in shifted mode, revert to base mode
        when (currentMode) {
            KeyboardMode.SHIFTED -> currentMode = KeyboardMode.NORMAL
            KeyboardMode.SYMBOLS_SHIFTED -> currentMode = KeyboardMode.SYMBOLS
            KeyboardMode.EMOJI -> currentMode = preEmojiMode
            else -> { /* stay in current mode */ }
        }
    }

    private fun handleRightOnlySwipe(dir: Direction) {
        if (dir == Direction.NONE) return
        executeSingleSwipe(dir)
    }

    private fun executeSingleSwipe(dir: Direction) {
        val customLayout = if (currentLayoutType == LayoutType.CUSTOM) activeCustomLayout else null
        val result = processor.getSingleSwipeResult(dir, currentMode, customLayout)
        when (result) {
            is String -> {
                delegate.commitText(result)
                onTextCommitted(result)
            }
            is InputAction -> {
                when (result) {
                    InputAction.TOGGLE_SHIFT -> {
                        when (currentMode) {
                            KeyboardMode.NORMAL -> currentMode = KeyboardMode.SHIFTED
                            KeyboardMode.SHIFTED -> currentMode = KeyboardMode.NORMAL
                            KeyboardMode.CAPS_LOCKED -> currentMode = KeyboardMode.NORMAL
                            KeyboardMode.SYMBOLS -> currentMode = KeyboardMode.SYMBOLS_SHIFTED
                            KeyboardMode.SYMBOLS_SHIFTED -> currentMode = KeyboardMode.SYMBOLS
                            KeyboardMode.EMOJI -> currentMode = preEmojiMode
                        }
                    }
                    InputAction.TOGGLE_CAPS -> currentMode = if (currentMode == KeyboardMode.CAPS_LOCKED) KeyboardMode.NORMAL else KeyboardMode.CAPS_LOCKED
                    InputAction.TOGGLE_SYMBOLS -> {
                        when (currentMode) {
                            KeyboardMode.SYMBOLS, KeyboardMode.SYMBOLS_SHIFTED -> {
                                // Return to the mode we were in before entering symbols
                                currentMode = preSymbolsMode
                            }
                            else -> {
                                // Enter symbols mode; remember current mode
                                preSymbolsMode = currentMode
                                currentMode = KeyboardMode.SYMBOLS
                            }
                        }
                    }
                    InputAction.TOGGLE_EMOJI -> toggleEmojiPanel()
                    InputAction.BACKSPACE -> {
                        delegate.sendInputAction(result)
                        onBackspace()
                    }
                    InputAction.SPACE, InputAction.ENTER -> {
                        delegate.sendInputAction(result)
                        onWordBoundary()
                    }
                    else -> {
                        delegate.sendInputAction(result)
                        // Cursor-moving actions invalidate our buffer
                        if (result in listOf(
                                InputAction.DPAD_LEFT, InputAction.DPAD_RIGHT,
                                InputAction.DPAD_UP, InputAction.DPAD_DOWN,
                                InputAction.MOVE_HOME, InputAction.MOVE_END,
                                InputAction.PAGE_UP, InputAction.PAGE_DOWN
                            )) {
                            syncWordBufferFromEditor()
                        }
                    }
                }
            }
        }
    }

    // --- Accelerating Backspace Hold Logic ---

    private fun isBackspaceDirection(dir: Direction): Boolean {
        val customLayout = if (currentLayoutType == LayoutType.CUSTOM) activeCustomLayout else null
        val result = processor.getSingleSwipeResult(dir, currentMode, customLayout)
        return result == InputAction.BACKSPACE
    }

    private fun checkBackspaceHold() {
        // Only start hold-repeat when: right dial is in backspace direction,
        // left dial is idle (not a chord, not locked), and no chord has been executed
        val rightDir = rightActiveDir
        if (leftActiveDir == Direction.NONE && lockedLeftDir == Direction.NONE
            && rightDir != Direction.NONE
            && !isChordExecuted && isBackspaceDirection(rightDir)
        ) {
            // Already running? Don't restart
            if (backspaceRepeatJob?.isActive == true) return
            startBackspaceRepeat()
        } else {
            cancelBackspaceRepeat()
        }
    }

    private fun startBackspaceRepeat() {
        backspaceHoldFired = false
        backspaceRepeatJob = coroutineScope.launch {
            // Phase 1: Initial delay before repeating (300ms)
            delay(300L)
            backspaceHoldFired = true
            // Phase 2: Character deletion at 100ms intervals (until 1.5s total = 1200ms more)
            val charRepeatEnd = 1200L // 1.5s total - 300ms initial = 1200ms of char repeats
            var elapsed = 0L
            while (elapsed < charRepeatEnd) {
                delegate.sendInputAction(InputAction.BACKSPACE)
                delay(100L)
                elapsed += 100L
            }
            // Phase 3: Word deletion at 200ms intervals (until 3s total = 1500ms more)
            val wordSlowEnd = 1500L // 3s total - 1.5s = 1500ms of slow word deletion
            elapsed = 0L
            while (elapsed < wordSlowEnd) {
                delegate.sendInputAction(InputAction.DELETE_WORD)
                delay(200L)
                elapsed += 200L
            }
            // Phase 4: Fast word deletion at 100ms intervals (indefinitely until cancelled)
            while (true) {
                delegate.sendInputAction(InputAction.DELETE_WORD)
                delay(100L)
            }
        }
    }

    private fun cancelBackspaceRepeat(syncWordBufferFromEditor: Boolean = true) {
        val wasActive = backspaceRepeatJob?.isActive == true
        backspaceRepeatJob?.cancel()
        backspaceRepeatJob = null
        backspaceHoldFired = false
        if (wasActive && syncWordBufferFromEditor) {
            syncWordBufferFromEditor()
        }
    }

    // ── Word buffer management & prediction ──

    private fun onTextCommitted(text: String) {
        for (ch in text) {
            if (isWordCharacter(ch)) {
                wordBuffer.append(ch)
            } else {
                // Non-letter character (punctuation, etc.) — treat as word boundary
                if (wordBuffer.isNotEmpty()) {
                    finalizeCommittedWord(wordBuffer.toString())
                }
                wordBuffer.clear()
            }
        }
        updateSuggestions()
    }

    private fun onWordBoundary() {
        if (wordBuffer.isNotEmpty()) {
            finalizeCommittedWord(wordBuffer.toString())
        }
        wordBuffer.clear()
        updateSuggestions()
    }

    private fun onBackspace() {
        if (wordBuffer.isNotEmpty()) {
            wordBuffer.deleteAt(wordBuffer.length - 1)
        } else {
            // Buffer was empty — ask the platform what word is before cursor now
            syncWordBufferFromEditor()
            return // syncWordBufferFromEditor already calls updateSuggestions
        }
        updateSuggestions()
    }

    private fun syncWordBufferFromEditor() {
        val prefix = delegate.getCurrentWordPrefix()
        wordBuffer.clear()
        wordBuffer.append(prefix)
        updateSuggestions()
    }

    private fun suppressSuggestions() {
        isNextWordMode = false
        currentSuggestions = emptyList()
        delegate.onSuggestionsUpdated(currentSuggestions)
    }

    private fun updateSuggestions() {
        if (currentMode == KeyboardMode.EMOJI) {
            return
        }

        val prefix = wordBuffer.toString()
        if (prefix.isNotEmpty()) {
            // Currently typing a word — show completions/corrections
            isNextWordMode = false
            currentSuggestions = predictor.getSuggestions(prefix, limit = 3)
        } else {
            // Buffer is empty — show next-word predictions or defaults
            isNextWordMode = true
            currentSuggestions = predictor.getNextWordSuggestions(lastCompletedWord, limit = 3)
        }
        delegate.onSuggestionsUpdated(currentSuggestions)
    }

    /**
     * Called by the platform when the user taps a suggestion word.
     * In normal mode: replaces the current partial word with the full suggestion.
     * In next-word mode: inserts the suggestion (buffer was empty).
     * Returns the number of characters to delete and the text to insert.
     */
    fun acceptSuggestion(
        suggestion: String,
        textBeforeCursor: String = "",
        textAfterCursor: String = ""
    ): SuggestionAcceptance {
        val charsToDelete = wordBuffer.length
        val leadingText = if (charsToDelete == 0 && shouldPrependSpaceBeforeSuggestion(textBeforeCursor)) " " else ""
        val trailingText = if (charsToDelete > 0 && shouldAppendTrailingSpace(textAfterCursor)) " " else ""

        finalizeCommittedWord(suggestion, boost = 2)
        wordBuffer.clear()
        // After accepting, show next-word predictions for the accepted word
        isNextWordMode = true
        currentSuggestions = predictor.getNextWordSuggestions(suggestion, limit = 3)
        delegate.onSuggestionsUpdated(currentSuggestions)
        return SuggestionAcceptance(
            charsToDelete = charsToDelete,
            leadingText = leadingText,
            suggestion = suggestion,
            trailingText = trailingText
        )
    }

    private fun finalizeCommittedWord(word: String, boost: Int = 1) {
        val normalizedWord = normalizeWord(word)
        if (normalizedWord.isBlank()) return

        val isNewWord = !predictor.contains(normalizedWord)
        predictor.learnWord(normalizedWord, count = boost, userAdded = isNewWord)
        if (lastCompletedWord.isNotBlank()) {
            predictor.learnBigram(lastCompletedWord, normalizedWord, count = boost)
        }
        lastCompletedWord = normalizedWord
        persistPredictionProfiles()
    }

    private fun persistPredictionProfiles() {
        predictionProfilesByLanguage[currentLanguage] = predictor.exportLearnedProfile()
        delegate.savePredictionProfile(PredictionProfileBundle.serialize(predictionProfilesByLanguage))
    }

    private fun normalizeWord(word: String): String {
        return word.lowercase().trim().filter { isWordCharacter(it) }
    }

    private fun isWordCharacter(ch: Char): Boolean {
        return ch.isLetterOrDigit() || ch == '\''
    }

    private fun shouldPrependSpaceBeforeSuggestion(textBeforeCursor: String): Boolean {
        if (textBeforeCursor.isEmpty()) return false
        val lastChar = textBeforeCursor.last()
        if (lastChar.isWhitespace()) return false
        return lastChar !in listOf('(', '[', '{', '\n', '\t', '"', '\'')
    }

    private fun shouldAppendTrailingSpace(textAfterCursor: String): Boolean {
        if (textAfterCursor.isEmpty()) return true
        val nextChar = textAfterCursor.first()
        return when {
            nextChar.isWhitespace() -> false
            isWordCharacter(nextChar) -> false
            nextChar in listOf('.', ',', '!', '?', ';', ':', ')', ']', '}', '"', '\'') -> false
            else -> true
        }
    }

    /**
     * Whether both dials are currently at home (NONE) position.
     * When true, the platform should show the suggestion bar instead of preview.
     */
    fun areBothDialsAtHome(): Boolean {
        return leftActiveDir == Direction.NONE && rightActiveDir == Direction.NONE
    }

    /** Returns the current word buffer content (for platform debugging/display). */
    fun getCurrentWordBuffer(): String = wordBuffer.toString()

    // Convenience factory function for iOS initialization
    fun createKeyboardStateMachineForIOS(delegate: KeyboardActionDelegate): KeyboardStateMachine {
        // Automatically creates a main-thread-bound scope on the Kotlin side for iOS
        return KeyboardStateMachine(delegate, kotlinx.coroutines.MainScope())
    }
}
// Kotlin's `object` keyword acts as a global singleton
object KeyboardFactory {
    fun createEngine(delegate: KeyboardActionDelegate): KeyboardStateMachine {
        // Internally assembles the coroutine scope that iOS doesn't need to manage
        return KeyboardStateMachine(delegate, kotlinx.coroutines.MainScope())
    }
}

private enum class InputSource {
    TOUCH,
    CONTROLLER
}

private data class ControllerStickInput(
    val x: Float,
    val y: Float,
    val isActive: Boolean
)
