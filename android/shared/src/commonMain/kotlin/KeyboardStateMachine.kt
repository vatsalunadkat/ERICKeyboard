package com.vatoo.erick.shared

import kotlinx.coroutines.*
import kotlin.math.hypot

class KeyboardStateMachine(
    private val delegate: KeyboardActionDelegate,
    private val coroutineScope: CoroutineScope // Lifecycle-bound scope provided by Android/iOS
) {
    private val processor = KeyboardLogic()
    private val predictor = WordPredictionEngine.createWithDefaultDictionary()
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

    // Accelerating backspace state
    private var backspaceRepeatJob: Job? = null
    private var backspaceHoldFired = false  // true if hold-repeat already deleted chars

    // Receives touch updates from the native platform
    fun handleTouch(x: Float, y: Float, isLeft: Boolean, actionDownOrMove: Boolean, actionUp: Boolean) {
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

    fun getCurrentPalette(): List<ColorEntry> {
        return ColorPalettes.getPalette(currentPaletteType)
    }

    // Returns the live preview character for UI rendering
    fun getPreviewText(): String {
        return if (leftActiveDir != Direction.NONE && rightActiveDir != Direction.NONE) {
            processor.getChordResult(leftActiveDir, rightActiveDir, currentMode, currentLayoutType, activeCustomLayout)
        } else {
            ""
        }
    }

    fun getCharactersForDirection(dir: Direction): List<String> {
        return processor.getCharactersForDirection(dir, currentMode, currentLayoutType, activeCustomLayout)
    }

    fun getCharactersAtPosition(rightDir: Direction): List<Pair<Direction, String>> {
        return processor.getCharactersAtPosition(rightDir, currentMode, currentLayoutType, activeCustomLayout)
    }

    private fun normalizeControllerStick(x: Float, y: Float): ControllerStickInput {
        val clampedX = x.coerceIn(-1f, 1f)
        val clampedY = (y * controllerYAxisMultiplier).coerceIn(-1f, 1f)
        val magnitude = hypot(clampedX.toDouble(), clampedY.toDouble()).toFloat()

        if (magnitude <= controllerDeadZone) {
            return ControllerStickInput(0f, 0f, false)
        }

        val scale = if (controllerDeadZone > 0f) {
            DEADZONE_RADIUS / controllerDeadZone
        } else {
            DEADZONE_RADIUS
        }

        return ControllerStickInput(
            x = clampedX * scale,
            y = clampedY * scale,
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
            if (isLeft) {
                if (rightDirBeforeRelease != Direction.NONE && !isChordExecuted) {
                    fireChord(leftDirBeforeRelease, rightDirBeforeRelease)
                }
            } else {
                if (leftDirBeforeRelease != Direction.NONE && !isChordExecuted) {
                    fireChord(leftDirBeforeRelease, rightDirBeforeRelease)
                } else if (leftDirBeforeRelease == Direction.NONE && !isChordExecuted && !wasBackspaceHold) {
                    handleRightOnlySwipe(rightDirBeforeRelease)
                }
            }
        }

        setSourceDirection(source, isLeft, Direction.NONE)
        recomputeActiveDirections()

        if (leftActiveDir == Direction.NONE && rightActiveDir == Direction.NONE) {
            isChordExecuted = false
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

    private fun fireChord(left: Direction, right: Direction) {
        if (left == Direction.NONE || right == Direction.NONE) return
        isChordExecuted = true

        val text = processor.getChordResult(left, right, currentMode, currentLayoutType, activeCustomLayout)
        if (text.isNotEmpty()) {
            delegate.commitText(text) // Tell the delegate to commit text!
            onTextCommitted(text)
        }

        if (currentMode == KeyboardMode.SHIFTED) {
            currentMode = KeyboardMode.NORMAL
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
                    InputAction.TOGGLE_SHIFT -> currentMode = if (currentMode == KeyboardMode.NORMAL) KeyboardMode.SHIFTED else KeyboardMode.NORMAL
                    InputAction.TOGGLE_CAPS -> currentMode = if (currentMode == KeyboardMode.CAPS_LOCKED) KeyboardMode.NORMAL else KeyboardMode.CAPS_LOCKED
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
        // left dial is idle (not a chord), and no chord has been executed
        val rightDir = rightActiveDir
        if (leftActiveDir == Direction.NONE && rightDir != Direction.NONE
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

    private fun cancelBackspaceRepeat() {
        val wasActive = backspaceRepeatJob?.isActive == true
        backspaceRepeatJob?.cancel()
        backspaceRepeatJob = null
        backspaceHoldFired = false
        if (wasActive) {
            syncWordBufferFromEditor()
        }
    }

    // ── Word buffer management & prediction ──

    private fun onTextCommitted(text: String) {
        for (ch in text) {
            if (ch.isLetterOrDigit() || ch == '\'') {
                wordBuffer.append(ch)
            } else {
                // Non-letter character (punctuation, etc.) — treat as word boundary
                if (wordBuffer.isNotEmpty()) {
                    lastCompletedWord = wordBuffer.toString()
                }
                wordBuffer.clear()
            }
        }
        updateSuggestions()
    }

    private fun onWordBoundary() {
        if (wordBuffer.isNotEmpty()) {
            lastCompletedWord = wordBuffer.toString()
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

    private fun updateSuggestions() {
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
    fun acceptSuggestion(suggestion: String): Pair<Int, String> {
        val charsToDelete = wordBuffer.length
        lastCompletedWord = suggestion
        wordBuffer.clear()
        // After accepting, show next-word predictions for the accepted word
        isNextWordMode = true
        currentSuggestions = predictor.getNextWordSuggestions(suggestion, limit = 3)
        delegate.onSuggestionsUpdated(currentSuggestions)
        return Pair(charsToDelete, suggestion)
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
