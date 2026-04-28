package com.vatoo.erick.shared

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class KeyboardLogicTest {

    private val logic = KeyboardLogic()
    private val testDelegate = object : KeyboardActionDelegate {
        override fun commitText(text: String) = Unit
        override fun sendInputAction(action: InputAction) = Unit
        override fun onModeChanged(mode: KeyboardMode) = Unit
        override fun onSuggestionsUpdated(suggestions: List<String>) = Unit
        override fun getCurrentWordPrefix(): String = ""
        override fun loadPredictionProfile(): String = ""
        override fun savePredictionProfile(serializedProfile: String) = Unit
    }
    private val testScope = CoroutineScope(SupervisorJob() + Dispatchers.Default)

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
    fun sixSection_efficiencyLayoutUsesUpdatedWinnerMapping() {
        logic.dialSectionMode = DialSectionMode.SIX_SECTION
        assertEquals(
            "c",
            logic.getChordResult(Direction.N, Direction.N, KeyboardMode.NORMAL, LayoutType.EFFICIENCY)
        )
        assertEquals(
            "!",
            logic.getChordResult(Direction.S, Direction.N, KeyboardMode.SHIFTED, LayoutType.EFFICIENCY)
        )
        logic.dialSectionMode = DialSectionMode.EIGHT_SECTION
    }

    @Test
    fun eightSection_efficiencyLayoutUsesExactRerunWinner() {
        assertEquals(
            "c",
            logic.getChordResult(Direction.N, Direction.N, KeyboardMode.NORMAL, LayoutType.EFFICIENCY)
        )
        assertEquals(
            "?",
            logic.getChordResult(Direction.N, Direction.SE, KeyboardMode.SHIFTED, LayoutType.EFFICIENCY)
        )
    }

    @Test
    fun spanishLogicalOverlayAddsNWithTildeInEightSection() {
        logic.activeLanguage = KeyboardLanguage.SPANISH

        assertEquals(
            "ñ",
            logic.getChordResult(Direction.SE, Direction.W, KeyboardMode.NORMAL)
        )
        assertEquals(
            "Ñ",
            logic.getChordResult(Direction.SE, Direction.W, KeyboardMode.SHIFTED)
        )

        logic.activeLanguage = KeyboardLanguage.ENGLISH
    }

    @Test
    fun nonEnglishEfficiencyFallsBackToLogicalOverlay() {
        logic.activeLanguage = KeyboardLanguage.SPANISH

        assertEquals(
            "ñ",
            logic.getChordResult(Direction.SE, Direction.W, KeyboardMode.NORMAL, LayoutType.EFFICIENCY)
        )

        logic.activeLanguage = KeyboardLanguage.ENGLISH
    }

    @Test
    fun sixSection_symbolsLayoutUsesOptimizedWinner() {
        logic.dialSectionMode = DialSectionMode.SIX_SECTION
        assertEquals(
            "-",
            logic.getChordResult(Direction.N, Direction.N, KeyboardMode.SYMBOLS)
        )
        assertEquals(
            "\u2260",
            logic.getChordResult(Direction.NW, Direction.N, KeyboardMode.SYMBOLS_SHIFTED)
        )
        logic.dialSectionMode = DialSectionMode.EIGHT_SECTION
    }

    @Test
    fun sixSectionSymbolsOverlayAddsGermanUmlauts() {
        logic.dialSectionMode = DialSectionMode.SIX_SECTION
        logic.activeLanguage = KeyboardLanguage.GERMAN

        assertEquals(
            "ä",
            logic.getChordResult(Direction.N, Direction.N, KeyboardMode.SYMBOLS)
        )
        assertEquals(
            "Ä",
            logic.getChordResult(Direction.N, Direction.N, KeyboardMode.SYMBOLS_SHIFTED)
        )

        logic.activeLanguage = KeyboardLanguage.ENGLISH
        logic.dialSectionMode = DialSectionMode.EIGHT_SECTION
    }

    @Test
    fun sixSection_directionDetectionUsesRotatedGeometry() {
        logic.dialSectionMode = DialSectionMode.SIX_SECTION
        // The 6-section wheel is rotated -30° so horizontal remains the easiest utility axis.
        assertEquals(Direction.SE, logic.getDirectionFromXY(1.0f, 0.0f))
        assertEquals(Direction.S, logic.getDirectionFromXY(0.5f, 0.8660254f))
        assertEquals(Direction.SW, logic.getDirectionFromXY(-0.5f, 0.8660254f))
        assertEquals(Direction.NW, logic.getDirectionFromXY(-1.0f, 0.0f))
        assertEquals(Direction.N, logic.getDirectionFromXY(-0.5f, -0.8660254f))
        assertEquals(Direction.NE, logic.getDirectionFromXY(0.5f, -0.8660254f))
        logic.dialSectionMode = DialSectionMode.EIGHT_SECTION
    }

    @Test
    fun sixSection_singleSwipeNeIsShift() {
        logic.dialSectionMode = DialSectionMode.SIX_SECTION
        val result = logic.getSingleSwipeResult(Direction.NE, KeyboardMode.NORMAL)
        assertEquals(InputAction.TOGGLE_SHIFT, result)
        logic.dialSectionMode = DialSectionMode.EIGHT_SECTION
    }

    @Test
    fun sixSection_singleSwipeNorthIsSymbols() {
        logic.dialSectionMode = DialSectionMode.SIX_SECTION
        val result = logic.getSingleSwipeResult(Direction.N, KeyboardMode.NORMAL)
        assertEquals(InputAction.TOGGLE_SYMBOLS, result)
        logic.dialSectionMode = DialSectionMode.EIGHT_SECTION
    }

    @Test
    fun sixSection_directionDetectionHonorsBoundaryAngles() {
        logic.dialSectionMode = DialSectionMode.SIX_SECTION
        assertEquals(Direction.S, logic.getDirectionFromXY(1.0f, 0.6f))
        assertEquals(Direction.SW, logic.getDirectionFromXY(0.0f, 1.0f))
        assertEquals(Direction.NW, logic.getDirectionFromXY(-1.0f, 0.4f))
        assertEquals(Direction.N, logic.getDirectionFromXY(-1.0f, -0.6f))
        assertEquals(Direction.NE, logic.getDirectionFromXY(0.0f, -1.0f))
        assertEquals(Direction.SE, logic.getDirectionFromXY(1.0f, -0.4f))
        logic.dialSectionMode = DialSectionMode.EIGHT_SECTION
    }

    @Test
    fun sixSection_singleSwipeMappingPreservesRotatedUtilityWheel() {
        logic.dialSectionMode = DialSectionMode.SIX_SECTION
        assertEquals(InputAction.TOGGLE_SHIFT, logic.getSingleSwipeResult(Direction.NE, KeyboardMode.NORMAL))
        assertEquals(InputAction.SPACE, logic.getSingleSwipeResult(Direction.SE, KeyboardMode.NORMAL))
        assertEquals(".", logic.getSingleSwipeResult(Direction.S, KeyboardMode.NORMAL))
        assertEquals(">", logic.getSingleSwipeResult(Direction.S, KeyboardMode.SHIFTED))
        assertEquals(InputAction.ENTER, logic.getSingleSwipeResult(Direction.SW, KeyboardMode.NORMAL))
        assertEquals(InputAction.BACKSPACE, logic.getSingleSwipeResult(Direction.NW, KeyboardMode.NORMAL))
        assertEquals(InputAction.TOGGLE_SYMBOLS, logic.getSingleSwipeResult(Direction.N, KeyboardMode.NORMAL))
        logic.dialSectionMode = DialSectionMode.EIGHT_SECTION
    }

    @Test
    fun sixSection_getDirectionsReturnsRotatedUtilityOrder() {
        logic.dialSectionMode = DialSectionMode.SIX_SECTION
        assertEquals(
            listOf(Direction.N, Direction.NE, Direction.SE, Direction.S, Direction.SW, Direction.NW),
            logic.getDirections()
        )
        logic.dialSectionMode = DialSectionMode.EIGHT_SECTION
    }

    @Test
    fun sixSection_getPreviewDirectionsReturnsVisualDialOrder() {
        logic.dialSectionMode = DialSectionMode.SIX_SECTION
        assertEquals(
            listOf(Direction.NE, Direction.SE, Direction.S, Direction.SW, Direction.NW, Direction.N),
            logic.getPreviewDirections()
        )
        logic.dialSectionMode = DialSectionMode.EIGHT_SECTION
    }

    @Test
    fun sixSection_getDirectionsReturns6() {
        logic.dialSectionMode = DialSectionMode.SIX_SECTION
        val stateMachine = KeyboardStateMachine(testDelegate, testScope)
        stateMachine.setDialSectionMode(DialSectionMode.SIX_SECTION)
        assertEquals(6, stateMachine.getDirections().size)
        stateMachine.setDialSectionMode(DialSectionMode.EIGHT_SECTION)
        assertEquals(8, stateMachine.getDirections().size)
        logic.dialSectionMode = DialSectionMode.EIGHT_SECTION
    }
}
