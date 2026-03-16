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
}
