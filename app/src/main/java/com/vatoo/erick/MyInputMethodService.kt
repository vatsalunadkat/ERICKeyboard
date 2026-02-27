package com.vatoo.erick

import android.inputmethodservice.InputMethodService
import android.view.View
import android.widget.Button

class MyInputMethodService : InputMethodService() {

    override fun onCreateInputView(): View {
        val view = layoutInflater.inflate(R.layout.keyboard_simple, null)

//      Find those two blue buttons through findViewById
        val button1 = view.findViewById<Button>(R.id.button1)
        val button2 = view.findViewById<Button>(R.id.button2)

//      Listening for clicks: Set setOnClickListener for the button
        button1?.setOnClickListener {
            // Bind the input method and input content to the text box
            currentInputConnection?.commitText("1", 1)
        }

        button2?.setOnClickListener {
            currentInputConnection?.commitText("2", 1)
        }

        return view
    }

    override fun onEvaluateInputViewShown(): Boolean {
        // Ensure that the keyboard can always be displayed when needed
        return true
    }
}
