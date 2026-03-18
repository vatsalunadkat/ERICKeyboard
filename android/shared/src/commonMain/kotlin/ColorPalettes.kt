package com.vatoo.erick.shared

enum class ColorPaletteType {
    DEFAULT, OKABE_ITO, DEUTERANOPIA, PROTANOPIA, TRITANOPIA, PASTEL
}

data class ColorEntry(val name: String, val hex: String)

object ColorPalettes {

    // Position order: N, NE, E, SE, S, SW, W, NW (matching Direction order on the right dial)
    private val defaultColors = listOf(
        ColorEntry("Red", "#E60012"),
        ColorEntry("Orange", "#F39800"),
        ColorEntry("Yellow", "#FFF100"),
        ColorEntry("Green", "#009944"),
        ColorEntry("Blue", "#0068B7"),
        ColorEntry("Indigo", "#1D2088"),
        ColorEntry("Violet", "#920783"),
        ColorEntry("Black", "#000000")
    )

    private val okabeItoColors = listOf(
        ColorEntry("Orange", "#E69F00"),
        ColorEntry("Sky Blue", "#56B4E9"),
        ColorEntry("Bluish Green", "#009E73"),
        ColorEntry("Yellow", "#F0E442"),
        ColorEntry("Blue", "#0072B2"),
        ColorEntry("Vermillion", "#D55E00"),
        ColorEntry("Reddish Purple", "#CC79A7"),
        ColorEntry("Black", "#000000")
    )

    private val deuteranopiaColors = listOf(
        ColorEntry("Blue", "#0072B2"),
        ColorEntry("Orange", "#E69F00"),
        ColorEntry("Light Blue", "#56B4E9"),
        ColorEntry("Yellow", "#F0E442"),
        ColorEntry("Dark Red", "#CC3311"),
        ColorEntry("Teal", "#009988"),
        ColorEntry("Pink", "#EE7733"),
        ColorEntry("Black", "#000000")
    )

    private val protanopiaColors = listOf(
        ColorEntry("Blue", "#0077BB"),
        ColorEntry("Cyan", "#33BBEE"),
        ColorEntry("Teal", "#009988"),
        ColorEntry("Yellow", "#EE7733"),
        ColorEntry("Orange", "#CC3311"),
        ColorEntry("Magenta", "#EE3377"),
        ColorEntry("Grey", "#BBBBBB"),
        ColorEntry("Black", "#000000")
    )

    private val tritanopiaColors = listOf(
        ColorEntry("Red", "#CC3311"),
        ColorEntry("Blue", "#0077BB"),
        ColorEntry("Yellow", "#EECC66"),
        ColorEntry("Cyan", "#33BBEE"),
        ColorEntry("Magenta", "#EE3377"),
        ColorEntry("Teal", "#009988"),
        ColorEntry("Grey", "#BBBBBB"),
        ColorEntry("Black", "#000000")
    )

    private val pastelColors = listOf(
        ColorEntry("Rose", "#F4A6B0"),
        ColorEntry("Peach", "#F6C9A0"),
        ColorEntry("Lemon", "#FDE9A0"),
        ColorEntry("Mint", "#A8DFC0"),
        ColorEntry("Sky", "#A0C4E8"),
        ColorEntry("Lavender", "#C4A8D8"),
        ColorEntry("Lilac", "#D8A8C8"),
        ColorEntry("Slate", "#8B8B8B")
    )

    fun getPalette(type: ColorPaletteType): List<ColorEntry> {
        return when (type) {
            ColorPaletteType.DEFAULT -> defaultColors
            ColorPaletteType.OKABE_ITO -> okabeItoColors
            ColorPaletteType.DEUTERANOPIA -> deuteranopiaColors
            ColorPaletteType.PROTANOPIA -> protanopiaColors
            ColorPaletteType.TRITANOPIA -> tritanopiaColors
            ColorPaletteType.PASTEL -> pastelColors
        }
    }

    private val directionOrder = listOf(
        Direction.N, Direction.NE, Direction.E, Direction.SE,
        Direction.S, Direction.SW, Direction.W, Direction.NW
    )

    fun getColorForDirectionHex(dir: Direction, paletteType: ColorPaletteType = ColorPaletteType.DEFAULT): String {
        val palette = getPalette(paletteType)
        val index = directionOrder.indexOf(dir)
        return if (index in palette.indices) palette[index].hex else "#FAFAFA"
    }

    /**
     * Returns "#000000" or "#FFFFFF" depending on the perceived luminance of [hex],
     * so text on that background is always legible.
     */
    fun contrastTextColor(hex: String): String {
        val clean = hex.trimStart('#')
        if (clean.length < 6) return "#FFFFFF"
        val r = clean.substring(0, 2).toInt(16)
        val g = clean.substring(2, 4).toInt(16)
        val b = clean.substring(4, 6).toInt(16)
        // W3C relative luminance formula
        val luminance = 0.299 * r + 0.587 * g + 0.114 * b
        return if (luminance > 186) "#000000" else "#FFFFFF"
    }
}
