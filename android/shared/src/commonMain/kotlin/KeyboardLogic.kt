package com.vatoo.erick.shared

import kotlin.math.atan2
import kotlin.math.PI

class KeyboardLogic {

    // Current dial section mode (set by the state machine based on user preference)
    var dialSectionMode: DialSectionMode = DialSectionMode.EIGHT_SECTION

    // --- Part 1: Pure math — convert coordinates to direction ---
    fun getDirectionFromXY(x: Float, y: Float): Direction {
        val radians = atan2(y.toDouble(), x.toDouble())
        var degrees = (radians * 180.0 / PI)
        if (degrees < 0) {
            degrees += 360.0
        }

        return if (dialSectionMode == DialSectionMode.SIX_SECTION) {
            getDirection6Section(degrees)
        } else {
            getDirection8Section(degrees)
        }
    }

    private fun getDirection8Section(degrees: Double): Direction {
        return when {
            degrees >= 337.5 || degrees < 22.5 -> Direction.E
            degrees >= 22.5 && degrees < 67.5 -> Direction.SE
            degrees >= 67.5 && degrees < 112.5 -> Direction.S
            degrees >= 112.5 && degrees < 157.5 -> Direction.SW
            degrees >= 157.5 && degrees < 202.5 -> Direction.W
            degrees >= 202.5 && degrees < 247.5 -> Direction.NW
            degrees >= 247.5 && degrees < 292.5 -> Direction.N
            degrees >= 292.5 && degrees < 337.5 -> Direction.NE
            else -> Direction.NONE
        }
    }

    // 6-section: 6 directions × 60° each (rotated -30° so horizontal = Space/Backspace)
    // NE (300°), SE (0°), S (60°), SW (120°), NW (180°), N (240°)
    private fun getDirection6Section(degrees: Double): Direction {
        return when {
            degrees >= 330.0 || degrees < 30.0 -> Direction.SE   // 0° ± 30° (right = Space)
            degrees >= 30.0 && degrees < 90.0 -> Direction.S     // 60° ± 30°
            degrees >= 90.0 && degrees < 150.0 -> Direction.SW   // 120° ± 30°
            degrees >= 150.0 && degrees < 210.0 -> Direction.NW  // 180° ± 30° (left = Backspace)
            degrees >= 210.0 && degrees < 270.0 -> Direction.N   // 240° ± 30°
            degrees >= 270.0 && degrees < 330.0 -> Direction.NE  // 300° ± 30°
            else -> Direction.NONE
        }
    }

    // --- Part 2: Chord data dictionary (unchanged) ---

    // ========== LOGICAL LAYOUT ==========
    private val normalMap = mapOf(
        Direction.N  to listOf("a", "b", "c", "d", "e", "", "", "'"),
        Direction.NE to listOf("f", "g", "h", "i", "j", "", "", "/"),
        Direction.E  to listOf("k", "l", "m", "n", "o", "", "", ";"),
        Direction.SE to listOf("p", "q", "r", "s", "t", "", "", "-"),
        Direction.S  to listOf("u", "v", "w", "x", "y", "", "", "="),
        Direction.SW to listOf("z", "\\", "[", "]", "`", "", "", ""),
        Direction.W  to listOf("1", "2", "3", "4", "5", "", "", ""),
        Direction.NW to listOf("6", "7", "8", "9", "0", "", "", "")
    )

    private val shiftedMap = mapOf(
        Direction.N  to listOf("A", "B", "C", "D", "E", "", "", "\""),
        Direction.NE to listOf("F", "G", "H", "I", "J", "", "", "?"),
        Direction.E  to listOf("K", "L", "M", "N", "O", "", "", ":"),
        Direction.SE to listOf("P", "Q", "R", "S", "T", "", "", "_"),
        Direction.S  to listOf("U", "V", "W", "X", "Y", "", "", "+"),
        Direction.SW to listOf("Z", "|", "{", "}", "~", "", "", ""),
        Direction.W  to listOf("!", "@", "#", "$", "%", "", "", ""),
        Direction.NW to listOf("^", "&", "*", "(", ")", "", "", "")
    )

    // ========== EFFICIENCY LAYOUT ==========
    // Optimized by English letter frequency: e, t, a, o, i, n, s, h, r, d, l, c, ...
    // Left direction = row, Right direction index: N=0, NE=1, E=2, SE=3, S=4, SW=5, W=6, NW=7
    private val efficiencyNormalMap = mapOf(
        Direction.N  to listOf("t", "s", "g", "7", "=", "", "4", "k"),
        Direction.NE to listOf("i", "a", "n", "p", "/", "", "", "'"),
        Direction.E  to listOf("v", "l", "e", "r", "x", "", "", ";"),
        Direction.SE to listOf("-", "y", "d", "o", "m", "", "", ""),
        Direction.S  to listOf("`", "6", "b", "f", "u", "", "", ""),
        Direction.SW to listOf("\\", "[", "]", "5", "q", "j", "", ""),
        Direction.W  to listOf("", "", "", "", "", "2", "3", "z"),
        Direction.NW to listOf("h", "w", "1", "8", "9", "", "0", "c")
    )

    private val efficiencyShiftedMap = mapOf(
        Direction.N  to listOf("T", "S", "G", "&", "+", "", "$", "K"),
        Direction.NE to listOf("I", "A", "N", "P", "?", "", "", "\""),
        Direction.E  to listOf("V", "L", "E", "R", "X", "", "", ":"),
        Direction.SE to listOf("_", "Y", "D", "O", "M", "", "", ""),
        Direction.S  to listOf("~", "^", "B", "F", "U", "", "", ""),
        Direction.SW to listOf("|", "{", "}", "%", "Q", "J", "", ""),
        Direction.W  to listOf("", "", "", "", "", "@", "#", "Z"),
        Direction.NW to listOf("H", "W", "!", "*", "(", "", ")", "C")
    )

    // ========== 6-SECTION LOGICAL LAYOUT (6x6 = 36 slots) ==========
    // Left direction = row, Right direction: N=0, NE=1, SE=2, S=3, SW=4, NW=5
    private val normalMap6 = mapOf(
        Direction.N  to listOf("a", "b", "c", "d", "e", "f"),
        Direction.NE to listOf("g", "h", "i", "j", "k", "l"),
        Direction.SE to listOf("m", "n", "o", "p", "q", "r"),
        Direction.S  to listOf("s", "t", "u", "v", "w", "x"),
        Direction.SW to listOf("y", "z", "1", "2", "3", "4"),
        Direction.NW to listOf("5", "6", "7", "8", "9", "0")
    )

    private val shiftedMap6 = mapOf(
        Direction.N  to listOf("A", "B", "C", "D", "E", "F"),
        Direction.NE to listOf("G", "H", "I", "J", "K", "L"),
        Direction.SE to listOf("M", "N", "O", "P", "Q", "R"),
        Direction.S  to listOf("S", "T", "U", "V", "W", "X"),
        Direction.SW to listOf("Y", "Z", "!", "@", "#", "$"),
        Direction.NW to listOf("%", "^", "&", "*", "(", ")")
    )

    // ========== 6-SECTION EFFICIENCY LAYOUT (placeholder — needs optimizer re-run) ==========
    // Diagonal slots (same direction both dials) get highest-frequency letters: e, t, a, o, i, n
    private val efficiencyNormalMap6 = mapOf(
        Direction.N  to listOf("e", "s", "g", "7", "k", "4"),
        Direction.NE to listOf("r", "t", "n", "p", "d", "w"),
        Direction.SE to listOf("l", "h", "a", "y", "m", "f"),
        Direction.S  to listOf("c", "u", "b", "o", "q", "x"),
        Direction.SW to listOf("v", "j", "z", "5", "i", "1"),
        Direction.NW to listOf("2", "3", "6", "8", "9", "0")
    )

    private val efficiencyShiftedMap6 = mapOf(
        Direction.N  to listOf("E", "S", "G", "&", "K", "$"),
        Direction.NE to listOf("R", "T", "N", "P", "D", "W"),
        Direction.SE to listOf("L", "H", "A", "Y", "M", "F"),
        Direction.S  to listOf("C", "U", "B", "O", "Q", "X"),
        Direction.SW to listOf("V", "J", "Z", "%", "I", "!"),
        Direction.NW to listOf("@", "#", "^", "*", "(", ")")
    )

    // ========== 6-SECTION SYMBOLS MODE ==========
    private val symbolsNormalMap6 = mapOf(
        Direction.N  to listOf("!", "@", "#", "$", "%", "^"),
        Direction.NE to listOf("&", "*", "(", ")", "-", "="),
        Direction.SE to listOf("[", "]", "{", "}", "\\", "|"),
        Direction.S  to listOf(";", ":", "'", "\"", ",", "."),
        Direction.SW to listOf("/", "?", "<", ">", "`", "~"),
        Direction.NW to listOf("+", "_", "", "", "", "")
    )

    private val symbolsShiftedMap6 = mapOf(
        Direction.N  to listOf("\u00A3", "\u20AC", "\u00A5", "\u00A2", "\u2030", "\u00B0"),
        Direction.NE to listOf("\u00D7", "\u00F7", "\u00AB", "\u00BB", "\u2013", "\u2014"),
        Direction.SE to listOf("\u2018", "\u2019", "\u201C", "\u201D", "\u2026", "\u00B7"),
        Direction.S  to listOf("\u00BF", "\u00A1", "\u00B1", "\u2260", "\u2264", "\u2265"),
        Direction.SW to listOf("\u221A", "\u221E", "\u03C0", "\u2211", "\u0394", "\u00B5"),
        Direction.NW to listOf("\u2190", "\u2192", "\u2191", "\u2193", "", "")
    )

    // Direction lists for each mode
    companion object {
        val directions8 = listOf(
            Direction.N, Direction.NE, Direction.E, Direction.SE,
            Direction.S, Direction.SW, Direction.W, Direction.NW
        )
        val directions6 = listOf(
            Direction.N, Direction.NE, Direction.SE,
            Direction.S, Direction.SW, Direction.NW
        )
    }

    fun getDirections(): List<Direction> {
        return if (dialSectionMode == DialSectionMode.SIX_SECTION) directions6 else directions8
    }

    private fun getRightIndex(rightDir: Direction): Int {
        return if (dialSectionMode == DialSectionMode.SIX_SECTION) {
            getRightIndex6(rightDir)
        } else {
            getRightIndex8(rightDir)
        }
    }

    private fun getRightIndex8(rightDir: Direction): Int {
        return when (rightDir) {
            Direction.N -> 0; Direction.NE -> 1; Direction.E -> 2
            Direction.SE -> 3; Direction.S -> 4; Direction.SW -> 5
            Direction.W -> 6; Direction.NW -> 7
            else -> -1
        }
    }

    private fun getRightIndex6(rightDir: Direction): Int {
        return when (rightDir) {
            Direction.N -> 0; Direction.NE -> 1; Direction.SE -> 2
            Direction.S -> 3; Direction.SW -> 4; Direction.NW -> 5
            else -> -1
        }
    }

    fun getChordResult(leftDir: Direction, rightDir: Direction, mode: KeyboardMode, layout: LayoutType = LayoutType.LOGICAL, customLayout: CustomLayout? = null): String {
        if (leftDir == Direction.NONE || rightDir == Direction.NONE) return ""
        val index = getRightIndex(rightDir)
        if (index == -1) return ""
        val currentMap = resolveChordMap(mode, layout, customLayout)
        val charList = currentMap[leftDir] ?: return ""
        return charList.getOrNull(index) ?: ""
    }

    private fun resolveChordMap(mode: KeyboardMode, layout: LayoutType, customLayout: CustomLayout?): Map<Direction, List<String>> {
        // Symbols mode (6-section only)
        if (mode == KeyboardMode.SYMBOLS) return symbolsNormalMap6
        if (mode == KeyboardMode.SYMBOLS_SHIFTED) return symbolsShiftedMap6

        if (dialSectionMode == DialSectionMode.SIX_SECTION) {
            return when {
                layout == LayoutType.CUSTOM && customLayout != null -> {
                    if (mode == KeyboardMode.NORMAL) customLayout.normalChordMap
                    else customLayout.shiftedChordMap
                }
                layout == LayoutType.EFFICIENCY && mode == KeyboardMode.NORMAL -> efficiencyNormalMap6
                layout == LayoutType.EFFICIENCY -> efficiencyShiftedMap6
                mode == KeyboardMode.NORMAL -> normalMap6
                else -> shiftedMap6
            }
        }

        // 8-section (original)
        return when {
            layout == LayoutType.CUSTOM && customLayout != null -> {
                if (mode == KeyboardMode.NORMAL) customLayout.normalChordMap
                else customLayout.shiftedChordMap
            }
            layout == LayoutType.EFFICIENCY && mode == KeyboardMode.NORMAL -> efficiencyNormalMap
            layout == LayoutType.EFFICIENCY -> efficiencyShiftedMap
            mode == KeyboardMode.NORMAL -> normalMap
            else -> shiftedMap
        }
    }

    fun getCharactersForDirection(dir: Direction, mode: KeyboardMode, layout: LayoutType = LayoutType.LOGICAL, customLayout: CustomLayout? = null): List<String> {
        val currentMap = resolveChordMap(mode, layout, customLayout)
        return currentMap[dir] ?: emptyList()
    }

    /**
     * Returns the character at position [rightDir] across ALL left-dial groups.
     * Used for right-dial-only preview: e.g. holding right-dial N (index 0)
     * returns the first character from every left-dial group.
     * Each entry in the returned list is a pair of (leftDirection, character).
     */
    fun getCharactersAtPosition(rightDir: Direction, mode: KeyboardMode, layout: LayoutType = LayoutType.LOGICAL, customLayout: CustomLayout? = null): List<Pair<Direction, String>> {
        val index = getRightIndex(rightDir)
        if (index == -1) return emptyList()
        val currentMap = resolveChordMap(mode, layout, customLayout)
        val allLeftDirs = getDirections()
        return allLeftDirs.mapNotNull { leftDir ->
            val chars = currentMap[leftDir] ?: return@mapNotNull null
            val ch = chars.getOrNull(index) ?: ""
            if (ch.isNotBlank()) leftDir to ch else null
        }
    }

    // --- Part 3: Action mapping (uses cross-platform InputAction) ---

    // Note: Return type is Any because a single swipe may return a character (String) or a command (InputAction)
    fun getSingleSwipeResult(dir: Direction, mode: KeyboardMode, customLayout: CustomLayout? = null): Any? {
        if (customLayout != null) {
            val map = if (mode != KeyboardMode.NORMAL) customLayout.singleSwipeShiftedMap else customLayout.singleSwipeNormalMap
            val binding = map[dir] ?: return null
            return when (binding) {
                is SingleSwipeBinding.Character -> binding.char
                is SingleSwipeBinding.Action -> binding.action
            }
        }

        if (dialSectionMode == DialSectionMode.SIX_SECTION) {
            return getSingleSwipeResult6(dir, mode)
        }

        val isShifted = mode != KeyboardMode.NORMAL
        return when (dir) {
            Direction.N  -> if (isShifted) InputAction.MOVE_END else InputAction.MOVE_HOME
            Direction.NE -> if (isShifted) "<" else ","
            Direction.E  -> InputAction.SPACE
            Direction.SE -> if (isShifted) ">" else "."
            Direction.S  -> InputAction.ENTER
            Direction.SW -> InputAction.TOGGLE_SHIFT
            Direction.W  -> InputAction.BACKSPACE
            Direction.NW -> InputAction.TOGGLE_CAPS
            else -> null
        }
    }

    // 6-section single-swipe actions (rotated to match 8-section horizontal feel):
    // NE (upper-right) = Shift
    // SE (right)       = Spacebar
    // S  (lower-right) = Period
    // SW (lower-left)  = Enter
    // NW (left)        = Backspace
    // N  (upper-left)  = Symbols toggle
    private fun getSingleSwipeResult6(dir: Direction, mode: KeyboardMode): Any? {
        val isShifted = mode != KeyboardMode.NORMAL && mode != KeyboardMode.SYMBOLS && mode != KeyboardMode.SYMBOLS_SHIFTED
        return when (dir) {
            Direction.NE -> InputAction.TOGGLE_SHIFT
            Direction.SE -> InputAction.SPACE
            Direction.S  -> if (isShifted) ">" else "."
            Direction.SW -> InputAction.ENTER
            Direction.NW -> InputAction.BACKSPACE
            Direction.N  -> InputAction.TOGGLE_SYMBOLS
            else -> null
        }
    }
}
