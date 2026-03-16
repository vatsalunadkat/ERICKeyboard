package com.vatoo.erick.shared

object ColorPalettes {
    // Matching the user's provided vibrant design image exactly
    val defaultPaletteHex = mapOf(
        Direction.N  to "#E53935", // Red
        Direction.NE to "#FB8C00", // Orange
        Direction.E  to "#FBC02D", // Yellow / Golden
        Direction.SE to "#43A047", // Green
        Direction.S  to "#1E88E5", // Blue
        Direction.SW to "#5E35B1", // Indigo / Dark Blue
        Direction.W  to "#8E24AA", // Violet / Purple
        Direction.NW to "#212121"  // Black / Dark Gray
    )

    fun getColorForDirectionHex(dir: Direction): String {
        return defaultPaletteHex[dir] ?: "#FAFAFA"
    }
}
