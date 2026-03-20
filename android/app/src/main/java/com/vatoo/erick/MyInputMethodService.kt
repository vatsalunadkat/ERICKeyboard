package com.vatoo.erick

import android.inputmethodservice.InputMethodService
import android.view.KeyEvent
import android.view.MotionEvent
import android.view.View
import android.view.inputmethod.EditorInfo
import com.vatoo.erick.shared.ColorPaletteType
import com.vatoo.erick.shared.CustomLayoutManager
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
import android.widget.LinearLayout
import android.widget.TextView
import android.graphics.Color
import android.graphics.Typeface
import android.util.TypedValue
import android.view.Gravity
import android.view.animation.AccelerateDecelerateInterpolator
import com.vatoo.erick.shared.ColorPalettes
import com.vatoo.erick.shared.Direction

// 注意：如果报红，请使用 Alt+Enter 导入你在 Shared 模块中写的类 (InputAction, KeyboardStateMachine 等)

class MyInputMethodService : InputMethodService(), KeyboardActionDelegate {

    private lateinit var leftJoystick: JoystickView
    private lateinit var rightJoystick: JoystickView
    private lateinit var previewContainer: FrameLayout
    private lateinit var previewCapsule: LinearLayout
    private lateinit var shiftIndicator: TextView
    private var lastHighlightedIndex: Int = -1

    // --- 协程生命周期管理 ---
    // 必须给状态机提供一个作用域，当输入法关闭时，销毁所有倒计时任务防止内存泄漏
    private val serviceJob = SupervisorJob()
    private val serviceScope = CoroutineScope(Dispatchers.Main + serviceJob)

    // 引入我们在 Shared 模块里写的跨平台大脑
    private lateinit var stateMachine: KeyboardStateMachine
    private lateinit var preferencesManager: PreferencesManager
    private lateinit var customLayoutManager: CustomLayoutManager
    private var currentThemeMode: String = PreferencesManager.THEME_SYSTEM
    private var currentFontPreference: String = PreferencesManager.FONT_SYSTEM
    private var keyboardRootView: View? = null

    override fun onCreate() {
        super.onCreate()
        // 输入法创建时，组装大脑，并把自己 (this) 作为代理传进去
        stateMachine = KeyboardStateMachine(this, serviceScope)

        // 监听布局偏好变化，实时切换布局 (使用与 SettingsScreen 相同的 PreferencesManager)
        preferencesManager = PreferencesManager(this)
        customLayoutManager = CustomLayoutManager(preferencesManager.createCustomLayoutStorage())

        // Combine layout type and custom layout ID so we can apply both together
        preferencesManager.layoutType.combine(preferencesManager.customLayoutId) { layout, customId ->
            Pair(layout, customId)
        }.onEach { (layout, customId) ->
            val layoutType = when (layout) {
                PreferencesManager.LAYOUT_EFFICIENCY -> LayoutType.EFFICIENCY
                PreferencesManager.LAYOUT_CUSTOM -> LayoutType.CUSTOM
                else -> LayoutType.LOGICAL
            }
            stateMachine.setLayoutType(layoutType)
            if (layoutType == LayoutType.CUSTOM && customId.isNotEmpty()) {
                val cl = customLayoutManager.getById(customId)
                stateMachine.activeCustomLayout = cl
                if (::leftJoystick.isInitialized) {
                    leftJoystick.customCharsNormal = cl?.normalChordMap
                    leftJoystick.customCharsShifted = cl?.shiftedChordMap
                }
                if (::rightJoystick.isInitialized) {
                    rightJoystick.customCharsNormal = cl?.normalChordMap
                    rightJoystick.customCharsShifted = cl?.shiftedChordMap
                }
            } else {
                stateMachine.activeCustomLayout = null
                if (::leftJoystick.isInitialized) {
                    leftJoystick.customCharsNormal = null
                    leftJoystick.customCharsShifted = null
                }
                if (::rightJoystick.isInitialized) {
                    rightJoystick.customCharsNormal = null
                    rightJoystick.customCharsShifted = null
                }
            }
            if (::leftJoystick.isInitialized) leftJoystick.layoutType = layoutType
            if (::rightJoystick.isInitialized) rightJoystick.layoutType = layoutType
        }.launchIn(serviceScope)

        preferencesManager.leftHandedMode.onEach { isLeftHanded ->
            stateMachine.setLeftHandedMode(isLeftHanded)
            if (::leftJoystick.isInitialized && ::rightJoystick.isInitialized) {
                leftJoystick.isRightSide = isLeftHanded
                rightJoystick.isRightSide = !isLeftHanded
                leftJoystick.invalidate()
                rightJoystick.invalidate()
            }
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

        // Monitor theme mode changes
        preferencesManager.themeMode.onEach { mode ->
            currentThemeMode = mode
            applyKeyboardTheme()
        }.launchIn(serviceScope)

        // Monitor font preference changes
        preferencesManager.fontPreference.onEach { font ->
            currentFontPreference = font
            applyKeyboardFont()
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

        // Apply left-handed mode to the newly created joystick views
        val isLeftHanded = stateMachine.leftHandedMode
        leftJoystick.isRightSide = isLeftHanded
        rightJoystick.isRightSide = !isLeftHanded

        // Apply current layout type to the newly created joystick views
        val currentLayout = stateMachine.currentLayoutType
        leftJoystick.layoutType = currentLayout
        rightJoystick.layoutType = currentLayout

        // Apply custom layout data if active
        val cl = stateMachine.activeCustomLayout
        if (currentLayout == LayoutType.CUSTOM && cl != null) {
            leftJoystick.customCharsNormal = cl.normalChordMap
            leftJoystick.customCharsShifted = cl.shiftedChordMap
            rightJoystick.customCharsNormal = cl.normalChordMap
            rightJoystick.customCharsShifted = cl.shiftedChordMap
        }

        // Apply current color palette to the newly created joystick views
        val currentPalette = stateMachine.currentPaletteType
        leftJoystick.colorPaletteType = currentPalette
        rightJoystick.colorPaletteType = currentPalette

        previewContainer = view.findViewById(R.id.live_preview_container)
        previewCapsule = view.findViewById(R.id.live_preview_capsule)
        shiftIndicator = view.findViewById(R.id.shift_indicator)

        keyboardRootView = view
        applyKeyboardTheme()
        applyKeyboardFont()

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

        // 3. Update the action-wheel joystick mode (whichever currently shows right-side content)
        val actionJoystick = if (stateMachine.leftHandedMode) leftJoystick else rightJoystick
        actionJoystick.keyboardMode = stateMachine.currentMode

        // 3. 从状态机获取原先的预览逻辑（移除旧的摇杆文字预览）
        // rightJoystick.setPreviewText(stateMachine.getPreviewText())

        updateLivePreview()
    }

    private fun isEffectiveDarkMode(): Boolean {
        return when (currentThemeMode) {
            PreferencesManager.THEME_DARK -> true
            PreferencesManager.THEME_LIGHT -> false
            else -> {
                val nightMode = resources.configuration.uiMode and android.content.res.Configuration.UI_MODE_NIGHT_MASK
                nightMode == android.content.res.Configuration.UI_MODE_NIGHT_YES
            }
        }
    }

    private fun applyKeyboardTheme() {
        val root = keyboardRootView ?: return
        val isDark = isEffectiveDarkMode()

        // Keyboard background
        root.setBackgroundColor(if (isDark) Color.parseColor("#1E1E1E") else Color.parseColor("#ECEFF1"))

        // Preview capsule background
        if (::previewCapsule.isInitialized) {
            previewCapsule.background = android.graphics.drawable.GradientDrawable().apply {
                shape = android.graphics.drawable.GradientDrawable.RECTANGLE
                cornerRadius = 999f * resources.displayMetrics.density
                setColor(if (isDark) Color.argb(245, 50, 50, 50) else Color.argb(245, 255, 255, 255))
            }
        }

        // Joystick views
        if (::leftJoystick.isInitialized) {
            leftJoystick.isDarkMode = isDark
            leftJoystick.invalidate()
        }
        if (::rightJoystick.isInitialized) {
            rightJoystick.isDarkMode = isDark
            rightJoystick.invalidate()
        }

        // Re-apply shift indicator colors for the new theme
        if (::leftJoystick.isInitialized) {
            updateShiftIndicator(stateMachine.currentMode)
        }
    }

    private fun resolveTypeface(): Typeface? {
        return when (currentFontPreference) {
            PreferencesManager.FONT_VERDANA -> Typeface.create("sans-serif", Typeface.NORMAL)
            PreferencesManager.FONT_GEORGIA -> Typeface.create("serif", Typeface.NORMAL)
            PreferencesManager.FONT_OPENDYSLEXIC -> {
                try {
                    resources.getFont(R.font.opendyslexic_regular)
                } catch (_: Exception) {
                    null
                }
            }
            else -> null // system default
        }
    }

    private fun applyKeyboardFont() {
        val tf = resolveTypeface()
        if (::leftJoystick.isInitialized) leftJoystick.customTypeface = tf
        if (::rightJoystick.isInitialized) rightJoystick.customTypeface = tf
        // Preview bar TextViews will pick up the font on next updateLivePreview() rebuild
        if (::previewCapsule.isInitialized) {
            previewCapsule.removeAllViews()
        }
        updateLivePreview()
    }

    private fun updateLivePreview() {
        if (!::leftJoystick.isInitialized || !::rightJoystick.isInitialized || !::previewContainer.isInitialized) return
        // In left-handed mode the letter-group dial is the physical right joystick,
        // and the color dial is the physical left joystick.
        val isLH = stateMachine.leftHandedMode
        val letterDir = if (isLH) rightJoystick.activeDirection else leftJoystick.activeDirection
        val colorDir  = if (isLH) leftJoystick.activeDirection  else rightJoystick.activeDirection

        // 8 possible right directions in clockwise order
        val allRightDirs = listOf(
            Direction.N, Direction.NE, Direction.E, Direction.SE,
            Direction.S, Direction.SW, Direction.W, Direction.NW
        )

        // Determine preview data: left-dial hold, right-dial hold, or nothing
        data class PreviewChar(val text: String, val colorHex: String, val dirForColor: Direction)
        val previewChars = mutableListOf<PreviewChar>()
        var highlightIndex = -1

        if (letterDir != Direction.NONE) {
            // Left-dial hold: show all characters in that group
            val chars = stateMachine.getCharactersForDirection(letterDir)
            for (i in chars.indices) {
                val charStr = chars[i]
                if (charStr.isBlank()) continue
                val dirForChar = allRightDirs.getOrNull(i) ?: Direction.NONE
                val colorHex = ColorPalettes.getColorForDirectionHex(dirForChar, stateMachine.currentPaletteType)
                previewChars.add(PreviewChar(charStr, colorHex, dirForChar))
                if (dirForChar == colorDir && colorDir != Direction.NONE) {
                    highlightIndex = previewChars.size - 1
                }
            }
        } else if (colorDir != Direction.NONE) {
            // Right-dial-only hold: show character at this color position across all left-dial groups
            val positionChars = stateMachine.getCharactersAtPosition(colorDir)
            val colorHex = ColorPalettes.getColorForDirectionHex(colorDir, stateMachine.currentPaletteType)
            for ((_, ch) in positionChars) {
                previewChars.add(PreviewChar(ch, colorHex, colorDir))
            }
            // No specific highlight for right-dial-only preview
        }

        if (previewChars.isEmpty()) {
            previewContainer.visibility = View.INVISIBLE
            lastHighlightedIndex = -1
            return
        }

        previewContainer.visibility = View.VISIBLE

        // Rebuild capsule child views if count changed
        if (previewCapsule.childCount != previewChars.size) {
            previewCapsule.removeAllViews()
            val spacingPx = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 8f, resources.displayMetrics).toInt()
            for (i in previewChars.indices) {
                val tv = TextView(this).apply {
                    textSize = 22f
                    val baseTf = resolveTypeface() ?: Typeface.DEFAULT
                    typeface = Typeface.create(baseTf, Typeface.BOLD)
                    gravity = Gravity.CENTER
                    minWidth = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 20f, resources.displayMetrics).toInt()
                    includeFontPadding = true
                    setShadowLayer(1.5f, 0f, 0f, Color.argb(166, 255, 255, 255))
                }
                val lp = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                ).apply {
                    if (i > 0) marginStart = spacingPx
                }
                previewCapsule.addView(tv, lp)
            }
        }

        // Update each character view
        for (i in previewChars.indices) {
            val tv = previewCapsule.getChildAt(i) as? TextView ?: continue
            val pc = previewChars[i]
            tv.text = pc.text
            tv.setTextColor(Color.parseColor(pc.colorHex))

            val isHighlighted = (i == highlightIndex)
            val targetSize = if (isHighlighted) 27f else 22f
            val targetScale = if (isHighlighted) 1.08f else 1.0f
            val targetTypeface = if (isHighlighted) {
                val baseTf = resolveTypeface() ?: Typeface.DEFAULT
                Typeface.create(baseTf, 900, false)
            } else {
                val baseTf = resolveTypeface() ?: Typeface.DEFAULT
                Typeface.create(baseTf, Typeface.BOLD)
            }

            // Animate size and scale
            if ((i == lastHighlightedIndex || i == highlightIndex) && lastHighlightedIndex != highlightIndex) {
                tv.animate()
                    .scaleX(targetScale)
                    .scaleY(targetScale)
                    .setDuration(120)
                    .setInterpolator(AccelerateDecelerateInterpolator())
                    .start()
                tv.textSize = targetSize
            } else {
                tv.scaleX = targetScale
                tv.scaleY = targetScale
                tv.textSize = targetSize
            }
            tv.typeface = targetTypeface
        }

        lastHighlightedIndex = highlightIndex
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
        updateShiftIndicator(mode)
    }

    private fun updateShiftIndicator(mode: com.vatoo.erick.shared.KeyboardMode) {
        if (!::shiftIndicator.isInitialized) return
        val isDark = isEffectiveDarkMode()
        when (mode) {
            com.vatoo.erick.shared.KeyboardMode.SHIFTED -> {
                shiftIndicator.text = "⬆ Shift"
                shiftIndicator.setTextColor(if (isDark) Color.WHITE else Color.DKGRAY)
                shiftIndicator.visibility = View.VISIBLE
                shiftIndicator.contentDescription = "Shift mode active"
            }
            com.vatoo.erick.shared.KeyboardMode.CAPS_LOCKED -> {
                shiftIndicator.text = "⬆⬆ CAPS"
                shiftIndicator.setTextColor(Color.WHITE)
                shiftIndicator.setBackgroundColor(Color.parseColor("#D32F2F"))
                shiftIndicator.setPadding(
                    (6 * resources.displayMetrics.density).toInt(),
                    (2 * resources.displayMetrics.density).toInt(),
                    (6 * resources.displayMetrics.density).toInt(),
                    (2 * resources.displayMetrics.density).toInt()
                )
                shiftIndicator.visibility = View.VISIBLE
                shiftIndicator.contentDescription = "Caps Lock active"
            }
            else -> {
                shiftIndicator.visibility = View.GONE
                shiftIndicator.background = null
                shiftIndicator.setPadding(0, 0, 0, 0)
            }
        }
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
