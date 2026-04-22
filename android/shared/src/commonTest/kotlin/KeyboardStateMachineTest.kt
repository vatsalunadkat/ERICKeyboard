package com.vatoo.erick.shared

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlin.test.Test
import kotlin.test.assertEquals

class KeyboardStateMachineTest {

    private val testScope = CoroutineScope(SupervisorJob() + Dispatchers.Default)

    @Test
    fun shiftedChordReturnsToNormalAfterCommit() {
        val delegate = RecordingDelegate()
        val stateMachine = KeyboardStateMachine(delegate, testScope)

        rightSwipe(stateMachine, x = -100f, y = 100f)

        assertEquals(KeyboardMode.SHIFTED, stateMachine.currentMode)

        pressLeft(stateMachine, x = 0f, y = -100f)
        rightSwipe(stateMachine, x = 0f, y = -100f)

        assertEquals(listOf("A"), delegate.committedTexts)
        assertEquals(KeyboardMode.NORMAL, stateMachine.currentMode)

        releaseLeft(stateMachine)
    }

    @Test
    fun sixSectionSymbolsShiftedChordReturnsToSymbols() {
        val delegate = RecordingDelegate()
        val stateMachine = KeyboardStateMachine(delegate, testScope)

        stateMachine.setDialSectionMode(DialSectionMode.SIX_SECTION)
        rightSwipe(stateMachine, x = -100f, y = -173.20508f)
        rightSwipe(stateMachine, x = 100f, y = -173.20508f)

        assertEquals(KeyboardMode.SYMBOLS_SHIFTED, stateMachine.currentMode)

        pressLeft(stateMachine, x = -100f, y = -173.20508f)
        rightSwipe(stateMachine, x = -100f, y = -173.20508f)

        assertEquals(listOf("£"), delegate.committedTexts)
        assertEquals(KeyboardMode.SYMBOLS, stateMachine.currentMode)

        releaseLeft(stateMachine)
    }

    @Test
    fun sixSectionSymbolsToggleRestoresPreviousMode() {
        val delegate = RecordingDelegate()
        val stateMachine = KeyboardStateMachine(delegate, testScope)

        rightSwipe(stateMachine, x = -100f, y = -100f)
        assertEquals(KeyboardMode.CAPS_LOCKED, stateMachine.currentMode)

        stateMachine.setDialSectionMode(DialSectionMode.SIX_SECTION)
        rightSwipe(stateMachine, x = -100f, y = -173.20508f)
        assertEquals(KeyboardMode.SYMBOLS, stateMachine.currentMode)

        rightSwipe(stateMachine, x = -100f, y = -173.20508f)
        assertEquals(KeyboardMode.CAPS_LOCKED, stateMachine.currentMode)
    }

    @Test
    fun switchingToEightSectionExitsSymbolsMode() {
        val delegate = RecordingDelegate()
        val stateMachine = KeyboardStateMachine(delegate, testScope)

        stateMachine.setDialSectionMode(DialSectionMode.SIX_SECTION)
        rightSwipe(stateMachine, x = -100f, y = -173.20508f)
        assertEquals(KeyboardMode.SYMBOLS, stateMachine.currentMode)

        stateMachine.setDialSectionMode(DialSectionMode.EIGHT_SECTION)

        assertEquals(KeyboardMode.NORMAL, stateMachine.currentMode)
    }

    private fun pressLeft(stateMachine: KeyboardStateMachine, x: Float, y: Float) {
        stateMachine.handleTouch(x, y, isLeft = true, actionDownOrMove = true, actionUp = false)
    }

    private fun releaseLeft(stateMachine: KeyboardStateMachine) {
        stateMachine.handleTouch(0f, 0f, isLeft = true, actionDownOrMove = false, actionUp = true)
    }

    private fun rightSwipe(stateMachine: KeyboardStateMachine, x: Float, y: Float) {
        stateMachine.handleTouch(x, y, isLeft = false, actionDownOrMove = true, actionUp = false)
        stateMachine.handleTouch(0f, 0f, isLeft = false, actionDownOrMove = false, actionUp = true)
    }

    private class RecordingDelegate : KeyboardActionDelegate {
        val committedTexts = mutableListOf<String>()
        val inputActions = mutableListOf<InputAction>()
        val modeChanges = mutableListOf<KeyboardMode>()
        val suggestionsSnapshots = mutableListOf<List<String>>()

        override fun commitText(text: String) {
            committedTexts += text
        }

        override fun sendInputAction(action: InputAction) {
            inputActions += action
        }

        override fun onModeChanged(mode: KeyboardMode) {
            modeChanges += mode
        }

        override fun onSuggestionsUpdated(suggestions: List<String>) {
            suggestionsSnapshots += suggestions
        }

        override fun getCurrentWordPrefix(): String = ""
    }
}