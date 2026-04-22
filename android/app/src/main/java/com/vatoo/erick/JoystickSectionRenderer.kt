package com.vatoo.erick

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import com.vatoo.erick.shared.ColorPaletteType
import com.vatoo.erick.shared.ColorPalettes
import com.vatoo.erick.shared.Direction
import kotlin.math.cos
import kotlin.math.sin

internal fun drawSixSectionLeftDial(
    canvas: Canvas,
    centerX: Float,
    centerY: Float,
    innerHoleRadius: Float,
    splitRadius: Float,
    baseRadius: Float,
    directions: List<Direction>,
    paletteDirections: List<Direction>,
    activeDirection: Direction,
    colorPaletteType: ColorPaletteType,
    basePaint: Paint,
    activeSegmentPaint: Paint,
    segmentLinePaint: Paint,
    mainDirectionLinePaint: Paint,
    borderPaint: Paint
) {
    val outerBounds = RectF(centerX - baseRadius, centerY - baseRadius, centerX + baseRadius, centerY + baseRadius)
    val innerBounds = RectF(centerX - splitRadius, centerY - splitRadius, centerX + splitRadius, centerY + splitRadius)

    canvas.drawCircle(centerX, centerY, baseRadius, basePaint)

    drawLayerSegments(
        canvas = canvas,
        bounds = outerBounds,
        directions = directions,
        paletteDirections = paletteDirections,
        sectionStartOffset = -90f,
        sectionSweepAngle = 60f,
        blockSweepAngle = 20f,
        blockCount = 3,
        paletteOffset = 0,
        activeDirection = activeDirection,
        colorPaletteType = colorPaletteType,
        activeSegmentPaint = activeSegmentPaint,
        useSixSectionPalette = true
    )
    drawLayerSegments(
        canvas = canvas,
        bounds = innerBounds,
        directions = directions,
        paletteDirections = paletteDirections,
        sectionStartOffset = -90f,
        sectionSweepAngle = 60f,
        blockSweepAngle = 20f,
        blockCount = 3,
        paletteOffset = 3,
        activeDirection = activeDirection,
        colorPaletteType = colorPaletteType,
        activeSegmentPaint = activeSegmentPaint,
        useSixSectionPalette = true
    )

    canvas.drawCircle(centerX, centerY, innerHoleRadius, basePaint)
    segmentLinePaint.alpha = 255
    canvas.drawCircle(centerX, centerY, splitRadius, segmentLinePaint)

    drawSixSectionSeparators(
        canvas = canvas,
        centerX = centerX,
        centerY = centerY,
        innerHoleRadius = innerHoleRadius,
        splitRadius = splitRadius,
        baseRadius = baseRadius,
        directions = directions,
        activeDirection = activeDirection,
        segmentLinePaint = segmentLinePaint,
        mainDirectionLinePaint = mainDirectionLinePaint
    )

    val innerHoleBounds = RectF(centerX - innerHoleRadius, centerY - innerHoleRadius, centerX + innerHoleRadius, centerY + innerHoleRadius)
    drawSectionBorders(
        canvas = canvas,
        outerBounds = outerBounds,
        innerBounds = innerHoleBounds,
        directions = directions,
        activeDirection = activeDirection,
        borderPaint = borderPaint,
        mainDirectionLinePaint = mainDirectionLinePaint,
        startOffset = -90f,
        sweepAngle = 60f
    )
}

internal fun drawEightSectionLeftDial(
    canvas: Canvas,
    centerX: Float,
    centerY: Float,
    innerHoleRadius: Float,
    innerRingOuterRadius: Float,
    middleRingOuterRadius: Float,
    baseRadius: Float,
    directions: List<Direction>,
    paletteDirections: List<Direction>,
    activeDirection: Direction,
    colorPaletteType: ColorPaletteType,
    basePaint: Paint,
    activeSegmentPaint: Paint,
    segmentLinePaint: Paint,
    mainDirectionLinePaint: Paint,
    borderPaint: Paint
) {
    val outerBounds = RectF(centerX - baseRadius, centerY - baseRadius, centerX + baseRadius, centerY + baseRadius)
    val middleBounds = RectF(centerX - middleRingOuterRadius, centerY - middleRingOuterRadius, centerX + middleRingOuterRadius, centerY + middleRingOuterRadius)
    val innerBounds = RectF(centerX - innerRingOuterRadius, centerY - innerRingOuterRadius, centerX + innerRingOuterRadius, centerY + innerRingOuterRadius)

    canvas.drawCircle(centerX, centerY, baseRadius, basePaint)

    drawLayerSegments(
        canvas = canvas,
        bounds = outerBounds,
        directions = directions,
        paletteDirections = paletteDirections,
        sectionStartOffset = -22.5f,
        sectionSweepAngle = 45f,
        blockSweepAngle = 15f,
        blockCount = 3,
        paletteOffset = 0,
        activeDirection = activeDirection,
        colorPaletteType = colorPaletteType,
        activeSegmentPaint = activeSegmentPaint,
        useSixSectionPalette = false
    )
    drawLayerSegments(
        canvas = canvas,
        bounds = middleBounds,
        directions = directions,
        paletteDirections = paletteDirections,
        sectionStartOffset = -22.5f,
        sectionSweepAngle = 45f,
        blockSweepAngle = 15f,
        blockCount = 3,
        paletteOffset = 3,
        activeDirection = activeDirection,
        colorPaletteType = colorPaletteType,
        activeSegmentPaint = activeSegmentPaint,
        useSixSectionPalette = false
    )
    drawLayerSegments(
        canvas = canvas,
        bounds = innerBounds,
        directions = directions,
        paletteDirections = paletteDirections,
        sectionStartOffset = -22.5f,
        sectionSweepAngle = 45f,
        blockSweepAngle = 22.5f,
        blockCount = 2,
        paletteOffset = 6,
        activeDirection = activeDirection,
        colorPaletteType = colorPaletteType,
        activeSegmentPaint = activeSegmentPaint,
        useSixSectionPalette = false
    )

    canvas.drawCircle(centerX, centerY, innerHoleRadius, basePaint)
    canvas.drawCircle(centerX, centerY, middleRingOuterRadius, segmentLinePaint)
    canvas.drawCircle(centerX, centerY, innerRingOuterRadius, segmentLinePaint)

    drawEightSectionSeparators(
        canvas = canvas,
        centerX = centerX,
        centerY = centerY,
        innerHoleRadius = innerHoleRadius,
        innerRingOuterRadius = innerRingOuterRadius,
        baseRadius = baseRadius,
        directions = directions,
        activeDirection = activeDirection,
        segmentLinePaint = segmentLinePaint,
        mainDirectionLinePaint = mainDirectionLinePaint
    )

    val innerHoleBounds = RectF(centerX - innerHoleRadius, centerY - innerHoleRadius, centerX + innerHoleRadius, centerY + innerHoleRadius)
    drawSectionBorders(
        canvas = canvas,
        outerBounds = outerBounds,
        innerBounds = innerHoleBounds,
        directions = directions,
        activeDirection = activeDirection,
        borderPaint = borderPaint,
        mainDirectionLinePaint = mainDirectionLinePaint,
        startOffset = -22.5f,
        sweepAngle = 45f
    )
}

private fun drawLayerSegments(
    canvas: Canvas,
    bounds: RectF,
    directions: List<Direction>,
    paletteDirections: List<Direction>,
    sectionStartOffset: Float,
    sectionSweepAngle: Float,
    blockSweepAngle: Float,
    blockCount: Int,
    paletteOffset: Int,
    activeDirection: Direction,
    colorPaletteType: ColorPaletteType,
    activeSegmentPaint: Paint,
    useSixSectionPalette: Boolean
) {
    for (i in directions.indices) {
        val direction = directions[i]
        val startAngle = sectionStartOffset + i * sectionSweepAngle
        val isInactive = activeDirection != Direction.NONE && direction != activeDirection

        for (j in 0 until blockCount) {
            val blockStart = startAngle + j * blockSweepAngle
            val blockColor = colorForDirection(
                direction = paletteDirections[paletteOffset + j],
                colorPaletteType = colorPaletteType,
                useSixSectionPalette = useSixSectionPalette
            )
            activeSegmentPaint.color = if (isInactive) darkenColor(blockColor, 0.4f) else blockColor
            activeSegmentPaint.alpha = 255
            canvas.drawArc(bounds, blockStart, blockSweepAngle, true, activeSegmentPaint)
        }
    }
}

private fun drawSixSectionSeparators(
    canvas: Canvas,
    centerX: Float,
    centerY: Float,
    innerHoleRadius: Float,
    splitRadius: Float,
    baseRadius: Float,
    directions: List<Direction>,
    activeDirection: Direction,
    segmentLinePaint: Paint,
    mainDirectionLinePaint: Paint
) {
    for (i in directions.indices) {
        val startAngle = -90f + i * 60f
        val direction = directions[i]

        for (j in 0..3) {
            val angleRad = Math.toRadians((startAngle + j * 20f).toDouble())
            val startRadius = if (j == 0 || j == 3) innerHoleRadius else splitRadius
            val ex = centerX + cos(angleRad).toFloat() * baseRadius
            val ey = centerY + sin(angleRad).toFloat() * baseRadius
            val sx = centerX + cos(angleRad).toFloat() * startRadius
            val sy = centerY + sin(angleRad).toFloat() * startRadius

            if (j == 0 || j == 3) {
                val adjacentDirection = boundaryAdjacentDirection(directions = directions, index = i, usePrevious = j == 0)
                mainDirectionLinePaint.alpha = boundaryAlpha(activeDirection, direction, adjacentDirection)
                canvas.drawLine(sx, sy, ex, ey, mainDirectionLinePaint)
            } else {
                segmentLinePaint.alpha = inactiveAlpha(activeDirection, direction)
                canvas.drawLine(sx, sy, ex, ey, segmentLinePaint)
            }
        }

        for (j in 1..2) {
            val angleRad = Math.toRadians((startAngle + j * 20f).toDouble())
            val sx = centerX + cos(angleRad).toFloat() * innerHoleRadius
            val sy = centerY + sin(angleRad).toFloat() * innerHoleRadius
            val ex = centerX + cos(angleRad).toFloat() * splitRadius
            val ey = centerY + sin(angleRad).toFloat() * splitRadius
            segmentLinePaint.alpha = inactiveAlpha(activeDirection, direction)
            canvas.drawLine(sx, sy, ex, ey, segmentLinePaint)
        }
    }
}

private fun drawEightSectionSeparators(
    canvas: Canvas,
    centerX: Float,
    centerY: Float,
    innerHoleRadius: Float,
    innerRingOuterRadius: Float,
    baseRadius: Float,
    directions: List<Direction>,
    activeDirection: Direction,
    segmentLinePaint: Paint,
    mainDirectionLinePaint: Paint
) {
    for (i in directions.indices) {
        val startAngle = -22.5f + i * 45f
        val direction = directions[i]

        for (j in 0..3) {
            val angleRad = Math.toRadians((startAngle + j * 15f).toDouble())
            val startRadius = if (j == 0 || j == 3) innerHoleRadius else innerRingOuterRadius
            val ex = centerX + cos(angleRad).toFloat() * baseRadius
            val ey = centerY + sin(angleRad).toFloat() * baseRadius
            val sx = centerX + cos(angleRad).toFloat() * startRadius
            val sy = centerY + sin(angleRad).toFloat() * startRadius

            if (j == 0 || j == 3) {
                val adjacentDirection = boundaryAdjacentDirection(directions = directions, index = i, usePrevious = j == 0)
                mainDirectionLinePaint.alpha = boundaryAlpha(activeDirection, direction, adjacentDirection)
                canvas.drawLine(sx, sy, ex, ey, mainDirectionLinePaint)
            } else {
                segmentLinePaint.alpha = inactiveAlpha(activeDirection, direction)
                canvas.drawLine(sx, sy, ex, ey, segmentLinePaint)
            }
        }

        val innerSeparatorAngle = Math.toRadians((startAngle + 22.5f).toDouble())
        val sx = centerX + cos(innerSeparatorAngle).toFloat() * innerHoleRadius
        val sy = centerY + sin(innerSeparatorAngle).toFloat() * innerHoleRadius
        val ex = centerX + cos(innerSeparatorAngle).toFloat() * innerRingOuterRadius
        val ey = centerY + sin(innerSeparatorAngle).toFloat() * innerRingOuterRadius
        segmentLinePaint.alpha = inactiveAlpha(activeDirection, direction)
        canvas.drawLine(sx, sy, ex, ey, segmentLinePaint)
    }
}

private fun drawSectionBorders(
    canvas: Canvas,
    outerBounds: RectF,
    innerBounds: RectF,
    directions: List<Direction>,
    activeDirection: Direction,
    borderPaint: Paint,
    mainDirectionLinePaint: Paint,
    startOffset: Float,
    sweepAngle: Float
) {
    for (i in directions.indices) {
        val direction = directions[i]
        val startAngle = startOffset + i * sweepAngle
        val alpha = inactiveAlpha(activeDirection, direction)

        borderPaint.alpha = alpha
        canvas.drawArc(outerBounds, startAngle, sweepAngle, false, borderPaint)

        mainDirectionLinePaint.alpha = alpha
        canvas.drawArc(innerBounds, startAngle, sweepAngle, false, mainDirectionLinePaint)
    }
}

private fun colorForDirection(
    direction: Direction,
    colorPaletteType: ColorPaletteType,
    useSixSectionPalette: Boolean
): Int {
    val colorHex = if (useSixSectionPalette) {
        ColorPalettes.getColorForDirectionHex6(direction, colorPaletteType)
    } else {
        ColorPalettes.getColorForDirectionHex(direction, colorPaletteType)
    }
    return Color.parseColor(colorHex)
}

private fun boundaryAdjacentDirection(
    directions: List<Direction>,
    index: Int,
    usePrevious: Boolean
): Direction {
    return if (usePrevious) {
        directions[if (index == 0) directions.lastIndex else index - 1]
    } else {
        directions[if (index == directions.lastIndex) 0 else index + 1]
    }
}

private fun boundaryAlpha(activeDirection: Direction, direction: Direction, adjacentDirection: Direction): Int {
    return if (activeDirection != Direction.NONE && activeDirection != direction && activeDirection != adjacentDirection) {
        60
    } else {
        255
    }
}

private fun inactiveAlpha(activeDirection: Direction, direction: Direction): Int {
    return if (activeDirection != Direction.NONE && activeDirection != direction) 60 else 255
}