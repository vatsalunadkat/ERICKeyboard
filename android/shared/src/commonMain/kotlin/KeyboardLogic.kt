package com.vatoo.erick.shared

import kotlin.math.atan2
import kotlin.math.PI

class KeyboardLogic {

    // --- 第一部分：纯数学计算坐标转方向 ---
    fun getDirectionFromXY(x: Float, y: Float): Direction {
        val radians = atan2(y.toDouble(), x.toDouble())
        var degrees = (radians * 180.0 / PI) // 替换了 Math.toDegrees，使用更纯粹的 Kotlin Math
        if (degrees < 0) {
            degrees += 360.0
        }

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

    // --- 第二部分：和弦数据字典 (保持不变) ---

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

    private fun getRightIndex(rightDir: Direction): Int {
        return when (rightDir) {
            Direction.N -> 0; Direction.NE -> 1; Direction.E -> 2
            Direction.SE -> 3; Direction.S -> 4; Direction.SW -> 5
            Direction.W -> 6; Direction.NW -> 7
            else -> -1
        }
    }

    fun getChordResult(leftDir: Direction, rightDir: Direction, mode: KeyboardMode, layout: LayoutType = LayoutType.LOGICAL, customLayout: CustomLayout? = null): String {
        if (leftDir == Direction.NONE || rightDir == Direction.NONE) return ""
        val index = getRightIndex(rightDir)
        if (index == -1) return ""
        val currentMap = when {
            layout == LayoutType.CUSTOM && customLayout != null -> {
                if (mode == KeyboardMode.NORMAL) customLayout.normalChordMap
                else customLayout.shiftedChordMap
            }
            layout == LayoutType.EFFICIENCY && mode == KeyboardMode.NORMAL -> efficiencyNormalMap
            layout == LayoutType.EFFICIENCY -> efficiencyShiftedMap
            mode == KeyboardMode.NORMAL -> normalMap
            else -> shiftedMap
        }
        val charList = currentMap[leftDir] ?: return ""
        return charList.getOrNull(index) ?: ""
    }

    fun getCharactersForDirection(dir: Direction, mode: KeyboardMode, layout: LayoutType = LayoutType.LOGICAL, customLayout: CustomLayout? = null): List<String> {
        val currentMap = when {
            layout == LayoutType.CUSTOM && customLayout != null -> {
                if (mode == KeyboardMode.NORMAL) customLayout.normalChordMap
                else customLayout.shiftedChordMap
            }
            layout == LayoutType.EFFICIENCY && mode == KeyboardMode.NORMAL -> efficiencyNormalMap
            layout == LayoutType.EFFICIENCY -> efficiencyShiftedMap
            mode == KeyboardMode.NORMAL -> normalMap
            else -> shiftedMap
        }
        return currentMap[dir] ?: emptyList()
    }

    // --- 第三部分：动作映射 (替换为我们刚刚定义的跨平台 InputAction) ---

    // 注意这里：返回值变成了 Any，因为单滑可能返回字符 (String)，也可能返回指令 (InputAction)
    fun getSingleSwipeResult(dir: Direction, mode: KeyboardMode, customLayout: CustomLayout? = null): Any? {
        if (customLayout != null) {
            val map = if (mode != KeyboardMode.NORMAL) customLayout.singleSwipeShiftedMap else customLayout.singleSwipeNormalMap
            val binding = map[dir] ?: return null
            return when (binding) {
                is SingleSwipeBinding.Character -> binding.char
                is SingleSwipeBinding.Action -> binding.action
            }
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
}
