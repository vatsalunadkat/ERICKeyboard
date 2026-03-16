package com.vatoo.erick.shared

import kotlin.math.hypot

class KeyboardStateMachine(
    private val delegate: KeyboardActionDelegate,
    private var currentLayoutType: LayoutType = LayoutType.LOGICAL
) {
    private val processor = KeyboardLogic()
    private val DEADZONE_RADIUS = 40f

    // 核心状态
    private var leftActiveDir = Direction.NONE
    private var rightActiveDir = Direction.NONE
    private var currentMode = KeyboardMode.NORMAL
    private var isChordExecuted = false

    fun setLayoutType(type: LayoutType) { currentLayoutType = type }
    fun getLayoutType(): LayoutType = currentLayoutType

    fun handleTouch(x: Float, y: Float, isLeft: Boolean, actionDownOrMove: Boolean, actionUp: Boolean) {
        val distance = hypot(x.toDouble(), y.toDouble()).toFloat()
        val currentDir = if (distance > DEADZONE_RADIUS) {
            processor.getDirectionFromXY(x, y)
        } else {
            Direction.NONE
        }

        if (actionDownOrMove) {
            if (isLeft) leftActiveDir = currentDir else rightActiveDir = currentDir
        } else if (actionUp) {
            if (isLeft) {
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

            if (leftActiveDir == Direction.NONE && rightActiveDir == Direction.NONE) {
                isChordExecuted = false
            }
        }
    }

    fun getPreviewText(): String {
        return if (leftActiveDir != Direction.NONE && rightActiveDir != Direction.NONE) {
            processor.getChordResult(leftActiveDir, rightActiveDir, currentMode, currentLayoutType)
        } else {
            ""
        }
    }

    private fun fireChord(left: Direction, right: Direction) {
        if (left == Direction.NONE || right == Direction.NONE) return
        isChordExecuted = true

        val text = processor.getChordResult(left, right, currentMode, currentLayoutType)
        if (text.isNotEmpty()) {
            delegate.commitText(text)
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
        val result = processor.getSingleSwipeResult(dir, currentMode)
        when (result) {
            is String -> delegate.commitText(result)
            is InputAction -> {
                when (result) {
                    InputAction.TOGGLE_SHIFT -> currentMode = if (currentMode == KeyboardMode.NORMAL) KeyboardMode.SHIFTED else KeyboardMode.NORMAL
                    InputAction.TOGGLE_CAPS  -> currentMode = if (currentMode == KeyboardMode.CAPS_LOCKED) KeyboardMode.NORMAL else KeyboardMode.CAPS_LOCKED
                    else -> delegate.sendInputAction(result)
                }
            }
        }
    }
}

// 专门为 iOS 准备的无痛初始化工厂函数
fun createKeyboardStateMachineForIOS(delegate: KeyboardActionDelegate,
                                     layoutType: LayoutType = LayoutType.LOGICAL): KeyboardStateMachine {
    // 自动在 Kotlin 端创建一个绑定主线程的作用域供 iOS 使用
    return KeyboardStateMachine(delegate, layoutType)
}

object KeyboardFactory {
    fun createEngine(delegate: KeyboardActionDelegate,
                     layoutType: LayoutType = LayoutType.LOGICAL): KeyboardStateMachine {
        return KeyboardStateMachine(delegate, layoutType)
    }
}
