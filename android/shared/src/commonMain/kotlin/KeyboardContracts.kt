package com.vatoo.erick.shared

// 1. Direction definitions
enum class Direction {
    NONE, N, NE, E, SE, S, SW, W, NW
}

// 2. Mode definitions
enum class KeyboardMode {
    NORMAL, SHIFTED, CAPS_LOCKED
}

// 2.5 Layout type definitions
enum class LayoutType {
    LOGICAL, EFFICIENCY, CUSTOM
}
enum class ControllerButton {
    A, B, X, Y,
    LEFT_BUMPER, RIGHT_BUMPER,
    LEFT_TRIGGER, RIGHT_TRIGGER,
    START, SELECT
}

data class ControllerState(
    val isConnected: Boolean,
    val controllerName: String,
    val leftStickX: Float,
    val leftStickY: Float,
    val rightStickX: Float,
    val rightStickY: Float
)
// 3. Cross-platform input action set (replaces Android's KeyEvent)
enum class InputAction {
    SPACE, ENTER, BACKSPACE, DELETE_FORWARD, DELETE_WORD,
    TOGGLE_SHIFT, TOGGLE_CAPS,
    MOVE_HOME, MOVE_END,
    DPAD_UP, DPAD_DOWN, DPAD_LEFT, DPAD_RIGHT,
    PAGE_UP, PAGE_DOWN, TAB
}

// 4. The "remote control" interface — Android and iOS must implement it
interface KeyboardActionDelegate {
    // Commit a plain character (e.g. "a", "A", ",", "!")
    fun commitText(text: String)

    // Execute a system-level action (e.g. enter, backspace, move cursor)
    fun sendInputAction(action: InputAction)

    // Notify mode change (NORMAL, SHIFTED, CAPS_LOCKED)
    fun onModeChanged(mode: KeyboardMode)

    // Called when word predictions/suggestions update
    fun onSuggestionsUpdated(suggestions: List<String>)

    // Retrieve the current word prefix from the text field (for sync after cursor moves etc.)
    // Returns the word fragment immediately before the cursor, or empty string.
    fun getCurrentWordPrefix(): String
}
