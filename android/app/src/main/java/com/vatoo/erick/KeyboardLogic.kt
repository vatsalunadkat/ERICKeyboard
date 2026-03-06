package com.vatoo.erick

import android.view.KeyEvent
import com.vatoo.erick.shared.KeyboardMode
import kotlin.math.atan2
import kotlin.math.PI

// Unified definition of 8 directions + default no direction
enum class Direction {
    NONE, N, NE, E, SE, S, SW, W, NW
}

class KeyboardLogic {

    // ==========================================
    // Part 1: Mathematical Calculation (Coordinate to Direction)
    // ==========================================
    fun getDirectionFromXY(x: Float, y: Float): Direction {
        // Calculate the angle using atan2, and the result is between -π and π
        val radians = atan2(y.toDouble(), x.toDouble())
        // Convert to degrees and adjust to the range of 0-360 degrees
        var degrees = (radians * 180.0 / PI)
        if (degrees < 0) {
            degrees += 360.0
        }

        // In the Android screen coordinate system, (x>0, y>0) is at the bottom-right corner
        // So: 0° is East (E), 90° is South (S), 180° is West (W), 270° is North (N)
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

    // ==========================================
    // Part 2: Chord Data Dictionary (Core Layout)
    // ==========================================

    // Normal mode layout
    private val normalMap = mapOf(
        Direction.N  to listOf("a", "b", "c", "d", "e", "'"),
        Direction.NE to listOf("f", "g", "h", "i", "j", "/"),
        Direction.E  to listOf("k", "l", "m", "n", "o", ";"),
        Direction.SE to listOf("p", "q", "r", "s", "t", "-"),
        Direction.S  to listOf("u", "v", "w", "x", "y", "="),
        Direction.SW to listOf("z", "\\", "[", "]", "`"), // Note: There are only 5
        Direction.W  to listOf("1", "2", "3", "4", "5"),    // Note: There are only 5
        Direction.NW to listOf("6", "7", "8", "9", "0")     // Note: There are only 5
    )

    // Shift/Caps mode
    private val shiftedMap = mapOf(
        Direction.N  to listOf("A", "B", "C", "D", "E", "\""),
        Direction.NE to listOf("F", "G", "H", "I", "J", "?"),
        Direction.E  to listOf("K", "L", "M", "N", "O", ":"),
        Direction.SE to listOf("P", "Q", "R", "S", "T", "_"),
        Direction.S  to listOf("U", "V", "W", "X", "Y", "+"),
        Direction.SW to listOf("Z", "|", "{", "}", "~"),
        Direction.W  to listOf("!", "@", "#", "$", "%"),
        Direction.NW to listOf("^", "&", "*", "(", ")")
    )

    // Right joystick direction -> color level (List index) mapping
    private fun getRightIndex(rightDir: Direction): Int {
        return when (rightDir) {
            Direction.N  -> 0 // Red = 1st
            Direction.NE -> 1 // Orange = 2nd
            Direction.E  -> 2 // Yellow = 3rd
            Direction.SE -> 3 // Green = 4th
            Direction.S  -> 4 // Blue = 5th
            Direction.SW -> 5 // Black = 6th/symbol
            // According to requirements, Indigo (W) and Violet (NW) are not used temporarily, return -1 to indicate invalidity
            Direction.W  -> -1
            Direction.NW -> -1
            else -> -1
        }
    }

    // Core Chord Query Function
    fun getChordResult(leftDir: Direction, rightDir: Direction, mode: KeyboardMode): String {
        if (leftDir == Direction.NONE || rightDir == Direction.NONE) return ""

        val index = getRightIndex(rightDir)
        if (index == -1) return "" //Colors that are not yet used return null safely

        // Select the corresponding dictionary according to the mode
        val currentMap = if (mode == KeyboardMode.NORMAL) normalMap else shiftedMap

        // Retrieve the character list corresponding to the left direction
        val charList = currentMap[leftDir] ?: return ""

        // 【Security Patch】: Use getOrNull to prevent array out-of-bounds.
        // For example, when SW (with 5 elements) is paired with the right joystick Black (which requires the 6th element), an empty string is returned instead of a crash.
        return charList.getOrNull(index) ?: ""
    }

    // ==========================================
    // Part 3: Independent Gesture Mapping for the Right Joystick
    // ==========================================

    fun getSingleSwipeAction(dir: Direction, mode: KeyboardMode): String {
        val isShifted = mode != KeyboardMode.NORMAL
        return when (dir) {
            Direction.N  -> if (isShifted) "END" else "HOME"
            Direction.NE -> if (isShifted) "<" else ","
            Direction.E  -> " " // Spacebar
            Direction.SE -> if (isShifted) ">" else "."
            Direction.S  -> "ENTER"
            Direction.SW -> "TOGGLE_SHIFT"
            Direction.W  -> "BACKSPACE"
            Direction.NW -> "TOGGLE_CAPS"
            else -> ""
        }
    }

    fun getDoubleSwipeKeyCode(dir: Direction): Int {
        return when (dir) {
            Direction.N  -> KeyEvent.KEYCODE_DPAD_UP
            Direction.NE -> KeyEvent.KEYCODE_PAGE_UP
            Direction.E  -> KeyEvent.KEYCODE_DPAD_RIGHT
            Direction.SE -> KeyEvent.KEYCODE_PAGE_DOWN
            Direction.S  -> KeyEvent.KEYCODE_DPAD_DOWN
            Direction.SW -> KeyEvent.KEYCODE_FORWARD_DEL // Delete right
            Direction.W  -> KeyEvent.KEYCODE_DPAD_LEFT
            Direction.NW -> KeyEvent.KEYCODE_TAB
            else -> -1
        }
    }
}
