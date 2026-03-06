package com.vatoo.erick

import android.inputmethodservice.InputMethodService
import android.view.KeyEvent
import android.view.MotionEvent
import android.view.View
import android.view.inputmethod.EditorInfo
import com.vatoo.erick.shared.InputAction
import com.vatoo.erick.shared.KeyboardActionDelegate
import com.vatoo.erick.shared.KeyboardStateMachine
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import android.content.Intent
import android.widget.ImageButton

// 注意：如果报红，请使用 Alt+Enter 导入你在 Shared 模块中写的类 (InputAction, KeyboardStateMachine 等)

class MyInputMethodService : InputMethodService(), KeyboardActionDelegate {

    private lateinit var leftJoystick: JoystickView
    private lateinit var rightJoystick: JoystickView

    // --- 协程生命周期管理 ---
    // 必须给状态机提供一个作用域，当输入法关闭时，销毁所有倒计时任务防止内存泄漏
    private val serviceJob = SupervisorJob()
    private val serviceScope = CoroutineScope(Dispatchers.Main + serviceJob)

    // 引入我们在 Shared 模块里写的跨平台大脑
    private lateinit var stateMachine: KeyboardStateMachine

    override fun onCreate() {
        super.onCreate()
        // 输入法创建时，组装大脑，并把自己 (this) 作为代理传进去
        stateMachine = KeyboardStateMachine(this, serviceScope)
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

        // 3. 从状态机获取结果，更新预览 UI
        rightJoystick.setPreviewText(stateMachine.getPreviewText())
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
