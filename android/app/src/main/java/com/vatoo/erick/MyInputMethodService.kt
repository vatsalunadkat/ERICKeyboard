package com.vatoo.erick

import android.inputmethodservice.InputMethodService
import android.view.KeyEvent
import android.view.MotionEvent
import android.view.View
import android.view.inputmethod.EditorInfo
import com.vatoo.erick.shared.InputAction
import com.vatoo.erick.shared.KeyboardActionDelegate
import com.vatoo.erick.shared.KeyboardStateMachine
import android.content.Intent
import android.widget.ImageButton

class MyInputMethodService : InputMethodService(), KeyboardActionDelegate {

    private lateinit var leftJoystick: JoystickView
    private lateinit var rightJoystick: JoystickView

    private lateinit var stateMachine: KeyboardStateMachine

    override fun onCreate() {
        super.onCreate()
        stateMachine = KeyboardStateMachine(this)
    }

    // onDestroy() needs no override — no coroutine scope to cancel anymore

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

        val settingsBtn = view.findViewById<ImageButton>(R.id.btn_settings)
        settingsBtn?.setOnClickListener {
            val intent = Intent(this, SettingsActivity::class.java).apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            startActivity(intent)
        }

        return view
    }

    private fun dispatchTouchToStateMachine(event: MotionEvent, isLeft: Boolean, joystick: JoystickView) {
        val dx = event.x - (joystick.width / 2f)
        val dy = event.y - (joystick.height / 2f)

        val actionMasked = event.actionMasked
        val isDownOrMove = actionMasked == MotionEvent.ACTION_DOWN || actionMasked == MotionEvent.ACTION_MOVE
        val isUpOrCancel = actionMasked == MotionEvent.ACTION_UP || actionMasked == MotionEvent.ACTION_CANCEL

        if (isDownOrMove) {
            joystick.updateThumb(dx, dy)
        } else if (isUpOrCancel) {
            joystick.resetThumb()
        }

        stateMachine.handleTouch(dx, dy, isLeft, isDownOrMove, isUpOrCancel)
        rightJoystick.setPreviewText(stateMachine.getPreviewText())
    }

    override fun commitText(text: String) {
        currentInputConnection?.commitText(text, 1)
    }

    override fun sendInputAction(action: InputAction) {
        val keyCode = when (action) {
            InputAction.SPACE          -> KeyEvent.KEYCODE_SPACE
            InputAction.ENTER          -> KeyEvent.KEYCODE_ENTER
            InputAction.BACKSPACE      -> KeyEvent.KEYCODE_DEL
            InputAction.MOVE_HOME      -> KeyEvent.KEYCODE_MOVE_HOME
            InputAction.MOVE_END       -> KeyEvent.KEYCODE_MOVE_END
            InputAction.TOGGLE_SHIFT,
            InputAction.TOGGLE_CAPS    -> -1
            InputAction.DELETE_FORWARD -> KeyEvent.KEYCODE_FORWARD_DEL
            InputAction.DPAD_UP        -> KeyEvent.KEYCODE_DPAD_UP
            InputAction.DPAD_DOWN      -> KeyEvent.KEYCODE_DPAD_DOWN
            InputAction.DPAD_LEFT      -> KeyEvent.KEYCODE_DPAD_LEFT
            InputAction.DPAD_RIGHT     -> KeyEvent.KEYCODE_DPAD_RIGHT
            InputAction.PAGE_UP        -> KeyEvent.KEYCODE_PAGE_UP
            InputAction.PAGE_DOWN      -> KeyEvent.KEYCODE_PAGE_DOWN
            InputAction.TAB            -> KeyEvent.KEYCODE_TAB
        }

        if (keyCode != -1) {
            currentInputConnection?.sendKeyEvent(KeyEvent(KeyEvent.ACTION_DOWN, keyCode))
            currentInputConnection?.sendKeyEvent(KeyEvent(KeyEvent.ACTION_UP, keyCode))
        }
    }

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