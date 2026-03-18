package com.vatoo.erick

import android.inputmethodservice.InputMethodService
import android.view.KeyEvent
import android.view.MotionEvent
import android.view.View
import android.view.inputmethod.EditorInfo
import com.vatoo.erick.shared.ColorPaletteType
import com.vatoo.erick.shared.InputAction
import com.vatoo.erick.shared.KeyboardActionDelegate
import com.vatoo.erick.shared.KeyboardStateMachine
import com.vatoo.erick.shared.LayoutType
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import android.content.Intent
import android.widget.ImageButton
import android.widget.FrameLayout
import android.widget.TextView
import android.graphics.Color
import android.text.SpannableString
import android.text.SpannableStringBuilder
import android.text.style.ForegroundColorSpan
import android.text.style.RelativeSizeSpan
import android.text.style.StyleSpan
import android.graphics.Typeface
import com.vatoo.erick.shared.ColorPalettes
import com.vatoo.erick.shared.Direction

// 注意：如果报红，请使用 Alt+Enter 导入你在 Shared 模块中写的类 (InputAction, KeyboardStateMachine 等)

class MyInputMethodService : InputMethodService(), KeyboardActionDelegate {

    private lateinit var leftJoystick: JoystickView
    private lateinit var rightJoystick: JoystickView
    private lateinit var previewContainer: FrameLayout
    private lateinit var previewText: TextView

    // --- 协程生命周期管理 ---
    // 必须给状态机提供一个作用域，当输入法关闭时，销毁所有倒计时任务防止内存泄漏
    private val serviceJob = SupervisorJob()
    private val serviceScope = CoroutineScope(Dispatchers.Main + serviceJob)

    // 引入我们在 Shared 模块里写的跨平台大脑
    private lateinit var stateMachine: KeyboardStateMachine
    private lateinit var preferencesManager: PreferencesManager

    override fun onCreate() {
        super.onCreate()
        // 输入法创建时，组装大脑，并把自己 (this) 作为代理传进去
        stateMachine = KeyboardStateMachine(this, serviceScope)

        // 监听布局偏好变化，实时切换布局 (使用与 SettingsScreen 相同的 PreferencesManager)
        preferencesManager = PreferencesManager(this)
        preferencesManager.layoutType.onEach { layout ->
            val layoutType = when (layout) {
                PreferencesManager.LAYOUT_EFFICIENCY -> LayoutType.EFFICIENCY
                else -> LayoutType.LOGICAL
            }
            stateMachine.setLayoutType(layoutType)
            if (::leftJoystick.isInitialized) leftJoystick.layoutType = layoutType
            if (::rightJoystick.isInitialized) rightJoystick.layoutType = layoutType
        }.launchIn(serviceScope)

        preferencesManager.colorblindMode.combine(preferencesManager.colorPalette) { enabled, palette ->
            if (enabled) {
                when (palette) {
                    PreferencesManager.PALETTE_DEUTERANOPIA -> ColorPaletteType.DEUTERANOPIA
                    PreferencesManager.PALETTE_PROTANOPIA -> ColorPaletteType.PROTANOPIA
                    PreferencesManager.PALETTE_TRITANOPIA -> ColorPaletteType.TRITANOPIA
                    PreferencesManager.PALETTE_PASTEL -> ColorPaletteType.PASTEL
                    else -> ColorPaletteType.OKABE_ITO
                }
            } else {
                ColorPaletteType.DEFAULT
            }
        }.onEach { paletteType ->
            stateMachine.setColorPalette(paletteType)
            if (::leftJoystick.isInitialized) leftJoystick.colorPaletteType = paletteType
            if (::rightJoystick.isInitialized) rightJoystick.colorPaletteType = paletteType
        }.launchIn(serviceScope)
    }

    override fun onDestroy() {
        super.onDestroy()
        serviceJob.cancel() // 输入法销毁时，清理所有的协程定时器
    }

    override fun onCreateInputView(): View {
        val view = layoutInflater.inflate(R.layout.keyboard_simple, null)

        leftJoystick = view.findViewById(R.id.left_joystick)
        rightJoystick = view.findViewById(R.id.right_joystick)
        rightJoystick.isRightSide = true

        // Apply current layout type to the newly created joystick views
        val currentLayout = stateMachine.currentLayoutType
        leftJoystick.layoutType = currentLayout
        rightJoystick.layoutType = currentLayout

        // Apply current color palette to the newly created joystick views
        val currentPalette = stateMachine.currentPaletteType
        leftJoystick.colorPaletteType = currentPalette
        rightJoystick.colorPaletteType = currentPalette

        previewContainer = view.findViewById(R.id.live_preview_container)
        previewText = view.findViewById(R.id.live_preview_text)

        leftJoystick.setOnTouchListener { v, event ->
            v.performClick()
            dispatchTouchToStateMachine(event, isLeft = true, joystick = leftJoystick)
            true
        }

        rightJoystick.setOnTouchListener { v, event ->
            v.performClick()
            dispatchTouchToStateMachine(event, isLeft = false, joystick = rightJoystick)
            true
        }
        //Setting
        val settingsBtn = view.findViewById<ImageButton>(R.id.btn_settings)
        settingsBtn?.setOnClickListener {
            val intent = Intent(this, SettingsActivity::class.java).apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            startActivity(intent)
        }

        return view
    }

    // --- 核心：将 Android 触摸事件翻译并喂给大脑 ---
    private fun dispatchTouchToStateMachine(event: MotionEvent, isLeft: Boolean, joystick: JoystickView) {
        // 计算相对于圆心的偏移量
        val dx = event.x - (joystick.width / 2f)
        val dy = event.y - (joystick.height / 2f)

        val actionMasked = event.actionMasked
        val isDownOrMove = actionMasked == MotionEvent.ACTION_DOWN || actionMasked == MotionEvent.ACTION_MOVE
        val isUpOrCancel = actionMasked == MotionEvent.ACTION_UP || actionMasked == MotionEvent.ACTION_CANCEL

        // 1. 更新纯粹的 UI 渲染
        if (isDownOrMove) {
            joystick.updateThumb(dx, dy)
        } else if (isUpOrCancel) {
            joystick.resetThumb()
        }

        // 2. 将数据喂给跨平台状态机 (它不需要知道什么是 MotionEvent)
        stateMachine.handleTouch(dx, dy, isLeft, isDownOrMove, isUpOrCancel)

        // 3. Update right joystick mode if needed
        rightJoystick.keyboardMode = stateMachine.currentMode

        // 3. 从状态机获取原先的预览逻辑（移除旧的摇杆文字预览）
        // rightJoystick.setPreviewText(stateMachine.getPreviewText())

        updateLivePreview()
    }

    private fun updateLivePreview() {
        val leftDir = leftJoystick.activeDirection
        val rightDir = rightJoystick.activeDirection

        if (leftDir == Direction.NONE) {
            previewContainer.visibility = View.INVISIBLE
            return
        }

        previewContainer.visibility = View.VISIBLE

        val chars = stateMachine.getCharactersForDirection(leftDir)
        if (chars.isEmpty()) {
            previewText.text = ""
            return
        }

        val builder = SpannableStringBuilder()

        // 8 possible right directions in clockwise order
        val allRightDirs = listOf(
            Direction.N, Direction.NE, Direction.E, Direction.SE,
            Direction.S, Direction.SW, Direction.W, Direction.NW
        )

        for (i in chars.indices) {
            val charStr = chars[i]
            if (charStr.isBlank()) continue

            val dirForChar = allRightDirs.getOrNull(i) ?: Direction.NONE

            val start = builder.length
            builder.append(charStr)
            builder.append("  ") // spacing

            val colorHex = ColorPalettes.getColorForDirectionHex(dirForChar, stateMachine.currentPaletteType)
            val color = Color.parseColor(colorHex)

            builder.setSpan(
                ForegroundColorSpan(color),
                start,
                start + charStr.length,
                SpannableString.SPAN_EXCLUSIVE_EXCLUSIVE
            )

            // Enlarge if this is the active right direction
            if (dirForChar == rightDir && rightDir != Direction.NONE) {
                builder.setSpan(
                    RelativeSizeSpan(1.5f),
                    start,
                    start + charStr.length,
                    SpannableString.SPAN_EXCLUSIVE_EXCLUSIVE
                )
                builder.setSpan(
                    StyleSpan(Typeface.BOLD),
                    start,
                    start + charStr.length,
                    SpannableString.SPAN_EXCLUSIVE_EXCLUSIVE
                )
            }
        }

        previewText.text = builder
    }

    // ==========================================
    // 实现 KeyboardActionDelegate 接口 (接收大脑的命令并执行)
    // ==========================================

    override fun commitText(text: String) {
        currentInputConnection?.commitText(text, 1)
    }

    override fun sendInputAction(action: InputAction) {
        // 将跨平台的 InputAction 翻译成 Android 原生的 KeyEvent
        val keyCode = when (action) {
            InputAction.SPACE -> KeyEvent.KEYCODE_SPACE
            InputAction.ENTER -> KeyEvent.KEYCODE_ENTER
            InputAction.BACKSPACE -> KeyEvent.KEYCODE_DEL
            InputAction.DELETE_FORWARD -> KeyEvent.KEYCODE_FORWARD_DEL
            InputAction.MOVE_HOME -> KeyEvent.KEYCODE_MOVE_HOME
            InputAction.MOVE_END -> KeyEvent.KEYCODE_MOVE_END
            InputAction.DPAD_UP -> KeyEvent.KEYCODE_DPAD_UP
            InputAction.DPAD_DOWN -> KeyEvent.KEYCODE_DPAD_DOWN
            InputAction.DPAD_LEFT -> KeyEvent.KEYCODE_DPAD_LEFT
            InputAction.DPAD_RIGHT -> KeyEvent.KEYCODE_DPAD_RIGHT
            InputAction.PAGE_UP -> KeyEvent.KEYCODE_PAGE_UP
            InputAction.PAGE_DOWN -> KeyEvent.KEYCODE_PAGE_DOWN
            InputAction.TAB -> KeyEvent.KEYCODE_TAB
            // 大小写切换已经在状态机内部消化，无需在这里处理
            InputAction.TOGGLE_SHIFT, InputAction.TOGGLE_CAPS -> -1
        }

        if (keyCode != -1) {
            currentInputConnection?.sendKeyEvent(KeyEvent(KeyEvent.ACTION_DOWN, keyCode))
            currentInputConnection?.sendKeyEvent(KeyEvent(KeyEvent.ACTION_UP, keyCode))
        }
    }

    override fun onModeChanged(mode: com.vatoo.erick.shared.KeyboardMode) {
        leftJoystick.keyboardMode = mode
        rightJoystick.keyboardMode = mode
    }

    // --- 彻底禁止全屏的"四重防火墙" (保持不变) ---
    override fun onStartInput(attribute: EditorInfo?, restarting: Boolean) {
        attribute?.let { it.imeOptions = it.imeOptions or EditorInfo.IME_FLAG_NO_EXTRACT_UI }
        super.onStartInput(attribute, restarting)
    }
    override fun onStartInputView(info: EditorInfo?, restarting: Boolean) {
        info?.let { it.imeOptions = it.imeOptions or EditorInfo.IME_FLAG_NO_EXTRACT_UI }
        super.onStartInputView(info, restarting)
    }
    override fun onEvaluateFullscreenMode(): Boolean = false
    override fun onUpdateExtractingVisibility(ei: EditorInfo?) { setExtractViewShown(false) }
    override fun onEvaluateInputViewShown(): Boolean {
        super.onEvaluateInputViewShown()
        return true
    }


}
