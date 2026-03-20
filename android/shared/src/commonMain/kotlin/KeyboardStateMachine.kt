package com.vatoo.erick.shared

import kotlinx.coroutines.*
import kotlin.math.hypot

class KeyboardStateMachine(
    private val delegate: KeyboardActionDelegate,
    private val coroutineScope: CoroutineScope // 由 Android/iOS 传入的生命周期作用域
) {
    private val processor = KeyboardLogic()
    private val DEADZONE_RADIUS = 40f
    private var controllerDeadZone = 0.25f
    private var controllerYAxisMultiplier = 1f

    // 核心状态
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

    // 接收来自原生平台的触摸更新
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
    }

    private fun releaseDirectionalState(source: InputSource, isLeft: Boolean) {
        val sourceDir = getSourceDirection(source, isLeft)
        val wasEffectiveSource = getEffectiveSource(isLeft) == source
        val leftDirBeforeRelease = leftActiveDir
        val rightDirBeforeRelease = rightActiveDir

        if (sourceDir != Direction.NONE && wasEffectiveSource) {
            if (isLeft) {
                if (rightDirBeforeRelease != Direction.NONE && !isChordExecuted) {
                    fireChord(leftDirBeforeRelease, rightDirBeforeRelease)
                }
            } else {
                if (leftDirBeforeRelease != Direction.NONE && !isChordExecuted) {
                    fireChord(leftDirBeforeRelease, rightDirBeforeRelease)
                } else if (leftDirBeforeRelease == Direction.NONE && !isChordExecuted) {
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

private enum class InputSource {
    TOUCH,
    CONTROLLER
}

private data class ControllerStickInput(
    val x: Float,
    val y: Float,
    val isActive: Boolean
)
