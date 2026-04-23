package com.vatoo.erick.shared

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

@OptIn(ExperimentalCoroutinesApi::class)
class KeyboardStateMachineTest {

    @Test
    fun shiftedChordReturnsToNormalAfterCommit() = runTest {
        val delegate = RecordingDelegate()
        val stateMachine = KeyboardStateMachine(delegate, this)

        rightSwipe(stateMachine, x = -100f, y = 100f)

        assertEquals(KeyboardMode.SHIFTED, stateMachine.currentMode)

        pressLeft(stateMachine, x = 0f, y = -100f)
        rightSwipe(stateMachine, x = 0f, y = -100f)

        assertEquals(listOf("A"), delegate.committedTexts)
        assertEquals(KeyboardMode.NORMAL, stateMachine.currentMode)

        releaseLeft(stateMachine)
    }

    @Test
    fun sixSectionSymbolsShiftedChordReturnsToSymbols() = runTest {
        val delegate = RecordingDelegate()
        val stateMachine = KeyboardStateMachine(delegate, this)

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
    fun sixSectionSymbolsToggleRestoresPreviousMode() = runTest {
        val delegate = RecordingDelegate()
        val stateMachine = KeyboardStateMachine(delegate, this)

        rightSwipe(stateMachine, x = -100f, y = -100f)
        assertEquals(KeyboardMode.CAPS_LOCKED, stateMachine.currentMode)

        stateMachine.setDialSectionMode(DialSectionMode.SIX_SECTION)
        rightSwipe(stateMachine, x = -100f, y = -173.20508f)
        assertEquals(KeyboardMode.SYMBOLS, stateMachine.currentMode)

        rightSwipe(stateMachine, x = -100f, y = -173.20508f)
        assertEquals(KeyboardMode.CAPS_LOCKED, stateMachine.currentMode)
    }

    @Test
    fun switchingToEightSectionExitsSymbolsMode() = runTest {
        val delegate = RecordingDelegate()
        val stateMachine = KeyboardStateMachine(delegate, this)

        stateMachine.setDialSectionMode(DialSectionMode.SIX_SECTION)
        rightSwipe(stateMachine, x = -100f, y = -173.20508f)
        assertEquals(KeyboardMode.SYMBOLS, stateMachine.currentMode)

        stateMachine.setDialSectionMode(DialSectionMode.EIGHT_SECTION)

        assertEquals(KeyboardMode.NORMAL, stateMachine.currentMode)
    }

    @Test
    fun acceptSuggestionReturnsDeleteCountForCurrentWord() = runTest {
        val delegate = RecordingDelegate()
        val stateMachine = KeyboardStateMachine(delegate, this)

        pressLeft(stateMachine, x = 0f, y = -100f)
        rightSwipe(stateMachine, x = 0f, y = -100f)
        releaseLeft(stateMachine)

        val result = stateMachine.acceptSuggestion("apple")

        assertEquals(1, result.first)
        assertEquals("apple", result.second)
        assertEquals("", stateMachine.getCurrentWordBuffer())
        assertTrue(stateMachine.isNextWordMode)
    }

    @Test
    fun backspaceHoldStartsRepeatingAfterDelay() = runTest {
        val delegate = RecordingDelegate()
        val stateMachine = KeyboardStateMachine(delegate, this)

        stateMachine.handleTouch(-100f, 0f, isLeft = false, actionDownOrMove = true, actionUp = false)
        advanceTimeBy(299)
        runCurrent()
        assertEquals(emptyList(), delegate.inputActions)

        advanceTimeBy(1)
        runCurrent()

        assertEquals(listOf(InputAction.BACKSPACE), delegate.inputActions)

        stateMachine.handleTouch(0f, 0f, isLeft = false, actionDownOrMove = false, actionUp = true)
    }

    @Test
    fun backspaceHoldTransitionsToDeleteWordAfterThreshold() = runTest {
        val delegate = RecordingDelegate()
        val stateMachine = KeyboardStateMachine(delegate, this)

        stateMachine.handleTouch(-100f, 0f, isLeft = false, actionDownOrMove = true, actionUp = false)
        advanceTimeBy(1800)
        runCurrent()

        assertTrue(delegate.inputActions.count { it == InputAction.BACKSPACE } >= 10)
        assertTrue(delegate.inputActions.contains(InputAction.DELETE_WORD))

        stateMachine.handleTouch(0f, 0f, isLeft = false, actionDownOrMove = false, actionUp = true)
    }

    @Test
    fun assistedModeControllerInputLocksLeftDirectionAndFiresChord() = runTest {
        val delegate = RecordingDelegate()
        val stateMachine = KeyboardStateMachine(delegate, this)

        stateMachine.setInputMode(InputMode.ASSISTED)

        stateMachine.handleControllerInput(0f, -1f, 0f, 0f)
        stateMachine.handleControllerInput(0f, 0f, 0f, 0f)

        assertEquals(Direction.N, stateMachine.lockedLeftDir)

        stateMachine.handleControllerInput(0f, 0f, 0f, -1f)

        assertEquals("a", stateMachine.getPreviewText())

        stateMachine.handleControllerInput(0f, 0f, 0f, 0f)

        assertEquals(listOf("a"), delegate.committedTexts)
        assertEquals(Direction.NONE, stateMachine.lockedLeftDir)
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