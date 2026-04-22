package com.vatoo.erick

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import com.vatoo.erick.shared.ColorPaletteType
import com.vatoo.erick.shared.ColorPalettes
import com.vatoo.erick.shared.Direction
import com.vatoo.erick.shared.KeyboardMode
import kotlin.math.cos
import kotlin.math.sin

private val rightDialLabelSizes = listOf(20f, 18f, 16f, 14f, 12f)

internal fun drawRightDial(
    canvas: Canvas,
    centerX: Float,
    centerY: Float,
    baseRadius: Float,
    sixSectionMode: Boolean,
    directions: List<Direction>,
    directions6: List<Direction>,
    activeDirection: Direction,
    keyboardMode: KeyboardMode,
    colorPaletteType: ColorPaletteType,
    activeSegmentPaint: Paint,
    rightSegmentLinePaint: Paint,
    labelTextPaint: Paint,
    iconStrokePaint: Paint,
    iconFillPaint: Paint
) {
    val dialBounds = RectF(centerX - baseRadius, centerY - baseRadius, centerX + baseRadius, centerY + baseRadius)
    val directionOrder = if (sixSectionMode) directions6 else directions
    val sweepAngle = if (sixSectionMode) 60f else 45f
    val startOffset = if (sixSectionMode) -90f else -22.5f

    for (i in directionOrder.indices) {
        val direction = directionOrder[i]
        val startAngle = startOffset + i * sweepAngle
        val inactiveAlpha = inactiveAlpha(activeDirection, direction)
        val colorHex = if (sixSectionMode) {
            ColorPalettes.getColorForDirectionHex6(direction, colorPaletteType)
        } else {
            ColorPalettes.getColorForDirectionHex(direction, colorPaletteType)
        }
        val parsedColor = Color.parseColor(colorHex)

        activeSegmentPaint.color = if (inactiveAlpha == 60) darkenColor(parsedColor, 0.4f) else parsedColor
        activeSegmentPaint.alpha = 255
        canvas.drawArc(dialBounds, startAngle, sweepAngle, true, activeSegmentPaint)

        rightSegmentLinePaint.alpha = inactiveAlpha
        canvas.drawArc(dialBounds, startAngle, sweepAngle, true, rightSegmentLinePaint)

        val (iconName, label) = if (sixSectionMode) {
            getInfoForDirection6(direction, keyboardMode)
        } else {
            getInfoForDirection(direction, keyboardMode)
        }
        val contentColor = Color.parseColor(ColorPalettes.contrastTextColor(colorHex, colorPaletteType))
        val angleRad = Math.toRadians((startAngle + sweepAngle / 2f).toDouble())
        val contentCenterRadius = baseRadius * 0.66f
        val contentCenterX = centerX + cos(angleRad).toFloat() * contentCenterRadius
        val contentCenterY = centerY + sin(angleRad).toFloat() * contentCenterRadius

        drawRightDialContent(
            canvas = canvas,
            iconName = iconName,
            label = label,
            centerX = contentCenterX,
            centerY = contentCenterY,
            alpha = inactiveAlpha,
            textColor = contentColor,
            baseRadius = baseRadius,
            labelTextPaint = labelTextPaint,
            iconStrokePaint = iconStrokePaint,
            iconFillPaint = iconFillPaint
        )
    }
}

private fun drawRightDialContent(
    canvas: Canvas,
    iconName: String,
    label: String,
    centerX: Float,
    centerY: Float,
    alpha: Int,
    textColor: Int,
    baseRadius: Float,
    labelTextPaint: Paint,
    iconStrokePaint: Paint,
    iconFillPaint: Paint
) {
    if (iconName.isEmpty() && label.isEmpty()) return

    val hasIcon = iconName.isNotEmpty()
    val lines = getRightDialLabelLines(label)
    val availableWidth = if (hasIcon) baseRadius * 0.43f else baseRadius * 0.48f
    val availableHeight = if (hasIcon) baseRadius * 0.36f else baseRadius * 0.30f
    val isBigPunctuation = !hasIcon && lines.size == 1 && lines[0].length == 1 && lines[0][0] in ".,<>"
    val textPaint = Paint(labelTextPaint).apply {
        this.alpha = alpha
        this.color = textColor
        textSize = if (isBigPunctuation) {
            minOf(baseRadius * 0.22f, availableHeight)
        } else {
            fittedTextSize(
                basePaint = labelTextPaint,
                lines = lines,
                maxWidth = availableWidth,
                maxHeight = availableHeight,
                preferredSizes = rightDialLabelSizes
            )
        }
    }

    val lineHeight = textPaint.fontSpacing * 0.9f
    val textBlockHeight = if (lines.isEmpty()) 0f else lineHeight * lines.size
    val iconSize = if (hasIcon) minOf(baseRadius * 0.095f, availableHeight * 0.34f) else 0f
    val spacing = if (hasIcon && lines.isNotEmpty()) baseRadius * 0.024f else 0f
    val totalHeight = iconSize + spacing + textBlockHeight
    var currentCenterY = centerY - totalHeight / 2f

    if (hasIcon) {
        val iconCenterY = currentCenterY + iconSize / 2f
        drawProgrammaticIcon(
            canvas = canvas,
            type = iconName,
            x = centerX,
            y = iconCenterY,
            size = iconSize,
            alpha = alpha,
            color = textColor,
            strokePaint = iconStrokePaint,
            fillPaint = iconFillPaint
        )
        currentCenterY += iconSize + spacing
    }

    lines.forEachIndexed { index, line ->
        val lineCenterY = currentCenterY + lineHeight * index + lineHeight / 2f
        val baseline = lineCenterY - (textPaint.descent() + textPaint.ascent()) / 2f
        canvas.drawText(line, centerX, baseline, textPaint)
    }
}

private fun inactiveAlpha(activeDirection: Direction, direction: Direction): Int {
    return if (activeDirection != Direction.NONE && activeDirection != direction) 60 else 255
}