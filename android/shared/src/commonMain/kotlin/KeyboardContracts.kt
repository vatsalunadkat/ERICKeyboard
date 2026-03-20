package com.vatoo.erick.shared

// 1. 纯净的方向定义
enum class Direction {
    NONE, N, NE, E, SE, S, SW, W, NW
}

// 2. 纯净的模式定义
enum class KeyboardMode {
    NORMAL, SHIFTED, CAPS_LOCKED
}

// 2.5 布局类型定义
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
// 3. 跨平台的动作指令集 (替代 Android 的 KeyEvent)
enum class InputAction {
    SPACE, ENTER, BACKSPACE, DELETE_FORWARD,
    TOGGLE_SHIFT, TOGGLE_CAPS,
    MOVE_HOME, MOVE_END,
    DPAD_UP, DPAD_DOWN, DPAD_LEFT, DPAD_RIGHT,
    PAGE_UP, PAGE_DOWN, TAB
}

// 4. 终极"遥控器"接口！Android 和 iOS 必须实现它
interface KeyboardActionDelegate {
    // 注入普通字符 (比如 "a", "A", ",", "!")
    fun commitText(text: String)

    // 执行系统级动作 (比如回车、删除、移动光标)
    fun sendInputAction(action: InputAction)

    // 通知模式变更 (NORMAL, SHIFTED, CAPS_LOCKED)
    fun onModeChanged(mode: KeyboardMode)
}
