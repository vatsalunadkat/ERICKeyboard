package com.vatoo.erick

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import com.vatoo.erick.shared.ColorPaletteType
import com.vatoo.erick.shared.ColorPalettes
import com.vatoo.erick.shared.Direction
import kotlin.math.cos
import kotlin.math.sin

private val characterPreferredTextSizes = listOf(34f, 32f, 30f, 28f, 26f, 24f, 22f, 20f, 18f)

internal fun drawSixSectionCharacters(
    canvas: Canvas,
    centerX: Float,
    centerY: Float,
    innerHoleRadius: Float,
    splitRadius: Float,
    baseRadius: Float,
    directions: List<Direction>,
    paletteDirections: List<Direction>,
    currentCharsMap: Map<Direction, List<String>>,
    activeDirection: Direction,
    colorPaletteType: ColorPaletteType,
    charTextPaint: Paint
) {
    for (i in directions.indices) {
        val dir = directions[i]
        val startAngle = -90f + i * 60f
        val chars = currentCharsMap[dir] ?: emptyList()
        val alphaVal = inactiveAlpha(activeDirection, dir)

        drawCharacterBand(
            canvas = canvas,
            chars = chars,
            startIndex = 0,
            count = 3,
            paletteDirections = paletteDirections,
            paletteStartIndex = 0,
            ringInnerRadius = splitRadius,
            ringOuterRadius = baseRadius,
            startAngle = startAngle,
            sweepAngle = 20f,
            alphaVal = alphaVal,
            colorPaletteType = colorPaletteType,
            charTextPaint = charTextPaint,
            centerX = centerX,
            centerY = centerY,
            useSixSectionPalette = true
        )
        drawCharacterBand(
            canvas = canvas,
            chars = chars,
            startIndex = 3,
            count = 3,
            paletteDirections = paletteDirections,
            paletteStartIndex = 3,
            ringInnerRadius = innerHoleRadius,
            ringOuterRadius = splitRadius,
            startAngle = startAngle,
            sweepAngle = 20f,
            alphaVal = alphaVal,
            colorPaletteType = colorPaletteType,
            charTextPaint = charTextPaint,
            centerX = centerX,
            centerY = centerY,
            useSixSectionPalette = true,
            radialBias = 0.58f
        )
    }
}

internal fun drawEightSectionCharacters(
    canvas: Canvas,
    centerX: Float,
    centerY: Float,
    innerHoleRadius: Float,
    innerRingOuterRadius: Float,
    middleRingOuterRadius: Float,
    baseRadius: Float,
    directions: List<Direction>,
    paletteDirections: List<Direction>,
    currentCharsMap: Map<Direction, List<String>>,
    activeDirection: Direction,
    colorPaletteType: ColorPaletteType,
    charTextPaint: Paint
) {
    for (i in directions.indices) {
        val dir = directions[i]
        val startAngle = -22.5f + i * 45f
        val chars = currentCharsMap[dir] ?: emptyList()
        val alphaVal = inactiveAlpha(activeDirection, dir)

        drawCharacterBand(
            canvas = canvas,
            chars = chars,
            startIndex = 0,
            count = 3,
            paletteDirections = paletteDirections,
            paletteStartIndex = 0,
            ringInnerRadius = middleRingOuterRadius,
            ringOuterRadius = baseRadius,
            startAngle = startAngle,
            sweepAngle = 15f,
            alphaVal = alphaVal,
            colorPaletteType = colorPaletteType,
            charTextPaint = charTextPaint,
            centerX = centerX,
            centerY = centerY,
            useSixSectionPalette = false
        )
        drawCharacterBand(
            canvas = canvas,
            chars = chars,
            startIndex = 3,
            count = 3,
            paletteDirections = paletteDirections,
            paletteStartIndex = 3,
            ringInnerRadius = innerRingOuterRadius,
            ringOuterRadius = middleRingOuterRadius,
            startAngle = startAngle,
            sweepAngle = 15f,
            alphaVal = alphaVal,
            colorPaletteType = colorPaletteType,
            charTextPaint = charTextPaint,
            centerX = centerX,
            centerY = centerY,
            useSixSectionPalette = false
        )
        drawCharacterBand(
            canvas = canvas,
            chars = chars,
            startIndex = 6,
            count = 2,
            paletteDirections = paletteDirections,
            paletteStartIndex = 6,
            ringInnerRadius = innerHoleRadius,
            ringOuterRadius = innerRingOuterRadius,
            startAngle = startAngle,
            sweepAngle = 22.5f,
            alphaVal = alphaVal,
            colorPaletteType = colorPaletteType,
            charTextPaint = charTextPaint,
            centerX = centerX,
            centerY = centerY,
            useSixSectionPalette = false
        )
    }
}

private fun drawCharacterBand(
    canvas: Canvas,
    chars: List<String>,
    startIndex: Int,
    count: Int,
    paletteDirections: List<Direction>,
    paletteStartIndex: Int,
    ringInnerRadius: Float,
    ringOuterRadius: Float,
    startAngle: Float,
    sweepAngle: Float,
    alphaVal: Int,
    colorPaletteType: ColorPaletteType,
    charTextPaint: Paint,
    centerX: Float,
    centerY: Float,
    useSixSectionPalette: Boolean,
    radialBias: Float = 0.5f
) {
    for (j in 0 until count) {
        val charStr = chars.getOrNull(startIndex + j) ?: continue
        if (charStr.isBlank()) continue

        val bgDirection = paletteDirections[paletteStartIndex + j]
        val bgHex = if (useSixSectionPalette) {
            ColorPalettes.getColorForDirectionHex6(bgDirection, colorPaletteType)
        } else {
            ColorPalettes.getColorForDirectionHex(bgDirection, colorPaletteType)
        }

        drawCharacterText(
            canvas = canvas,
            charStr = charStr,
            ringInnerRadius = ringInnerRadius,
            ringOuterRadius = ringOuterRadius,
            startAngle = startAngle + (j * sweepAngle),
            sweepAngle = sweepAngle,
            alphaVal = alphaVal,
            bgHex = bgHex,
            colorPaletteType = colorPaletteType,
            charTextPaint = charTextPaint,
            centerX = centerX,
            centerY = centerY,
            radialBias = radialBias
        )
    }
}

private fun drawCharacterText(
    canvas: Canvas,
    charStr: String,
    ringInnerRadius: Float,
    ringOuterRadius: Float,
    startAngle: Float,
    sweepAngle: Float,
    alphaVal: Int,
    bgHex: String,
    colorPaletteType: ColorPaletteType,
    charTextPaint: Paint,
    centerX: Float,
    centerY: Float,
    radialBias: Float = 0.5f
) {
    val centerRadius = ringInnerRadius + (ringOuterRadius - ringInnerRadius) * radialBias
    val centerAngle = startAngle + sweepAngle / 2f
    val angleRad = Math.toRadians(centerAngle.toDouble())
    val charX = centerX + cos(angleRad).toFloat() * centerRadius
    val charY = centerY + sin(angleRad).toFloat() * centerRadius

    val arcWidth = centerRadius * Math.toRadians(sweepAngle.toDouble()).toFloat()
    val ringHeight = ringOuterRadius - ringInnerRadius
    val fittedSize = fittedSingleLineTextSize(
        text = charStr,
        basePaint = charTextPaint,
        maxWidth = arcWidth * 0.72f,
        maxHeight = ringHeight * 0.54f,
        preferredSizes = characterPreferredTextSizes
    )

    val textColor = Color.parseColor(ColorPalettes.contrastTextColor(bgHex, colorPaletteType))
    val textPaint = Paint(charTextPaint).apply {
        color = textColor
        alpha = alphaVal
        textSize = fittedSize
    }

    val baseline = charY - (textPaint.descent() + textPaint.ascent()) / 2f
    canvas.drawText(charStr, charX, baseline, textPaint)
}

private fun inactiveAlpha(activeDirection: Direction, direction: Direction): Int {
    return if (activeDirection != Direction.NONE && direction != activeDirection) 60 else 255
}