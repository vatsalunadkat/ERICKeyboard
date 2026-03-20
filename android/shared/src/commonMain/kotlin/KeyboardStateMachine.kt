package com.vatoo.erick.shared

import kotlinx.coroutines.*
import kotlin.math.hypot

class KeyboardStateMachine(
    private val delegate: KeyboardActionDelegate,
    private val coroutineScope: CoroutineScope // 由 Android/iOS 传入的生命周期作用域
) {
    private val processor = KeyboardLogic()
    private val DEADZONE_RADIUS = 40f

    // 核心状态
    private var leftActiveDir = Direction.NONE
    private var rightActiveDir = Direction.NONE
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

    // 接收来自原生平台的触摸更新
    fun handleTouch(x: Float, y: Float, isLeft: Boolean, actionDownOrMove: Boolean, actionUp: Boolean) {
        // In left-handed mode, swap dial roles so the physical left joystick
        // acts as the right (color/action) dial and vice versa.
        val effectiveIsLeft = if (leftHandedMode) !isLeft else isLeft

        val distance = hypot(x.toDouble(), y.toDouble()).toFloat()
        val currentDir = if (distance > DEADZONE_RADIUS) {
            processor.getDirectionFromXY(x, y)
        } else {
            Direction.NONE
        }

        if (actionDownOrMove) {
            if (effectiveIsLeft) leftActiveDir = currentDir else rightActiveDir = currentDir
        } else if (actionUp) {
            if (effectiveIsLeft) {
                if (rightActiveDir != Direction.NONE && !isChordExecuted) {
                    fireChord(leftActiveDir, rightActiveDir)
                }
                leftActiveDir = Direction.NONE
            } else {
                if (leftActiveDir != Direction.NONE && !isChordExecuted) {
                    fireChord(leftActiveDir, rightActiveDir)
                } else if (leftActiveDir == Direction.NONE && !isChordExecuted) {
                    handleRightOnlySwipe(rightActiveDir)
                }
                rightActiveDir = Direction.NONE
            }

            // 解锁
            if (leftActiveDir == Direction.NONE && rightActiveDir == Direction.NONE) {
                isChordExecuted = false
            }
        }
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

    // 获取用于 UI 渲染的实时预览字符
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

    private fun fireChord(left: Direction, right: Direction) {
        if (left == Direction.NONE || right == Direction.NONE) return
        isChordExecuted = true

        val text = processor.getChordResult(left, right, currentMode, currentLayoutType, activeCustomLayout)
        if (text.isNotEmpty()) {
            delegate.commitText(text) // 呼叫代理上屏！
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
            is String -> delegate.commitText(result)
            is InputAction -> {
                when (result) {
                    InputAction.TOGGLE_SHIFT -> currentMode = if (currentMode == KeyboardMode.NORMAL) KeyboardMode.SHIFTED else KeyboardMode.NORMAL
                    InputAction.TOGGLE_CAPS -> currentMode = if (currentMode == KeyboardMode.CAPS_LOCKED) KeyboardMode.NORMAL else KeyboardMode.CAPS_LOCKED
                    else -> delegate.sendInputAction(result) // 交给原生平台处理回车、退格等
                }
            }
        }
    }
    // 专门为 iOS 准备的无痛初始化工厂函数
    fun createKeyboardStateMachineForIOS(delegate: KeyboardActionDelegate): KeyboardStateMachine {
        // 自动在 Kotlin 端创建一个绑定主线程的作用域供 iOS 使用
        return KeyboardStateMachine(delegate, kotlinx.coroutines.MainScope())
    }
}
// Kotlin 的 object 关键字相当于全局单例
object KeyboardFactory {
    fun createEngine(delegate: KeyboardActionDelegate): KeyboardStateMachine {
        // 我们在兵工厂内部，悄悄把 iOS 不懂的协程作用域给装配好
        return KeyboardStateMachine(delegate, kotlinx.coroutines.MainScope())
    }
}
