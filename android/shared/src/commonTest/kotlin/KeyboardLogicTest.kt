package com.vatoo.erick.shared

import kotlin.test.Test
import kotlin.test.assertEquals

class KeyboardLogicTest {

    private val logic = KeyboardLogic()

    @Test
    fun shiftedNwYellowReturnsLiteralAsterisk() {
        assertEquals(
            "*",
            logic.getChordResult(Direction.NW, Direction.E, KeyboardMode.SHIFTED)
        )
    }

    @Test
    fun normalBlackSectorStillCommitsMappedCharacter() {
        assertEquals(
            "'",
            logic.getChordResult(Direction.N, Direction.NW, KeyboardMode.NORMAL)
        )
    }

    @Test
    fun emptySlotsDoNotCommitPlaceholderCharacters() {
        assertEquals(
            "",
            logic.getChordResult(Direction.N, Direction.W, KeyboardMode.NORMAL)
        )
    }

    // ===== 6-SECTION MODE TESTS =====

    @Test
    fun sixSection_normalChordReturnsCorrectLetter() {
        logic.dialSectionMode = DialSectionMode.SIX_SECTION
        // N direction, first position → "a"
        assertEquals(
            "a",
            logic.getChordResult(Direction.N, Direction.N, KeyboardMode.NORMAL)
        )
        logic.dialSectionMode = DialSectionMode.EIGHT_SECTION
    }

    @Test
    fun sixSection_shiftedChordReturnsUppercase() {
        logic.dialSectionMode = DialSectionMode.SIX_SECTION
        assertEquals(
            "A",
            logic.getChordResult(Direction.N, Direction.N, KeyboardMode.SHIFTED)
        )
        logic.dialSectionMode = DialSectionMode.EIGHT_SECTION
    }

    @Test
    fun sixSection_symbolsChordReturnsPunctuation() {
        logic.dialSectionMode = DialSectionMode.SIX_SECTION
        // N direction, first position in symbols → "!"
        assertEquals(
            "!",
            logic.getChordResult(Direction.N, Direction.N, KeyboardMode.SYMBOLS)
        )
        logic.dialSectionMode = DialSectionMode.EIGHT_SECTION
    }

    @Test
    fun sixSection_directionDetection60Degrees() {
        logic.dialSectionMode = DialSectionMode.SIX_SECTION
        // 0 degrees (pointing right) → SE in 6-section (0°-60° range, center 30°)
        assertEquals(Direction.SE, logic.getDirectionFromXY(1.0f, 0.0f))
        // 90 degrees (pointing down) → S in 6-section (60°-120° range, center 90°)
        assertEquals(Direction.S, logic.getDirectionFromXY(0.0f, 1.0f))
        // 270 degrees (pointing up) → N in 6-section (240°-300° range, center 270°)
        assertEquals(Direction.N, logic.getDirectionFromXY(0.0f, -1.0f))
        // 180 degrees (pointing left) → NW in 6-section (180°-240° range, center 210°)
        assertEquals(Direction.NW, logic.getDirectionFromXY(-1.0f, 0.0f))
        logic.dialSectionMode = DialSectionMode.EIGHT_SECTION
    }

    @Test
    fun sixSection_singleSwipeNorthIsShift() {
        logic.dialSectionMode = DialSectionMode.SIX_SECTION
        val result = logic.getSingleSwipeResult(Direction.N, KeyboardMode.NORMAL)
        assertEquals(InputAction.TOGGLE_SHIFT, result)
        logic.dialSectionMode = DialSectionMode.EIGHT_SECTION
    }

    @Test
    fun sixSection_singleSwipeNWIsSymbols() {
        logic.dialSectionMode = DialSectionMode.SIX_SECTION
        val result = logic.getSingleSwipeResult(Direction.NW, KeyboardMode.NORMAL)
        assertEquals(InputAction.TOGGLE_SYMBOLS, result)
        logic.dialSectionMode = DialSectionMode.EIGHT_SECTION
    }

    @Test
    fun sixSection_getDirectionsReturns6() {
        logic.dialSectionMode = DialSectionMode.SIX_SECTION
        val stateMachine = KeyboardStateMachine()
        stateMachine.setDialSectionMode(DialSectionMode.SIX_SECTION)
        assertEquals(6, stateMachine.getDirections().size)
        stateMachine.setDialSectionMode(DialSectionMode.EIGHT_SECTION)
        assertEquals(8, stateMachine.getDirections().size)
        logic.dialSectionMode = DialSectionMode.EIGHT_SECTION
    }
}
