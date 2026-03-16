package com.vatoo.erick.shared

// 1. 纯净的方向定义
enum class Direction {
    NONE, N, NE, E, SE, S, SW, W, NW
}

// 2. 纯净的模式定义
enum class KeyboardMode {
    NORMAL, SHIFTED, CAPS_LOCKED
}
// 3. Keyboard Layout type based on logical and efficiency
enum class LayoutType {
    LOGICAL,
    EFFICIENCY
}

// 4. 跨平台的动作指令集 (替代 Android 的 KeyEvent)
enum class InputAction {
    // --- Active: used by single-swipe gestures ---
    SPACE, ENTER, BACKSPACE,
    TOGGLE_SHIFT, TOGGLE_CAPS,
    MOVE_HOME, MOVE_END,

    // --- Reserved for future use (e.g. controller / gamepad support) ---
    // These were previously triggered by double-swipe gestures, which have been removed.
    // Retained here so platform delegates can still handle them when needed.
    DPAD_UP, DPAD_DOWN, DPAD_LEFT, DPAD_RIGHT,
    PAGE_UP, PAGE_DOWN,
    TAB, DELETE_FORWARD
}

// 5. 终极"遥控器"接口！Android 和 iOS 必须实现它
interface KeyboardActionDelegate {
    // 注入普通字符 (比如 "a", "A", ",", "!")
    fun commitText(text: String)

    // 执行系统级动作 (比如回车、删除、移动光标)
    fun sendInputAction(action: InputAction)
}
