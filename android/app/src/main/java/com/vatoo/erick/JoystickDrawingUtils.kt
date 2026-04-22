package com.vatoo.erick

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import com.vatoo.erick.shared.Direction
import com.vatoo.erick.shared.KeyboardMode

internal fun getInfoForDirection(dir: Direction, keyboardMode: KeyboardMode): Pair<String, String> {
    val isShifted = keyboardMode == KeyboardMode.SHIFTED
    val isCaps = keyboardMode == KeyboardMode.CAPS_LOCKED
    val isToggled = isShifted || isCaps

    return when (dir) {
        Direction.N -> if (isToggled) "end" to "End" else "home" to "Home"
        Direction.NE -> "" to if (isToggled) "<" else ","
        Direction.E -> "space" to "Space"
        Direction.SE -> "" to if (isToggled) ">" else "."
        Direction.S -> "enter" to if (isToggled) "New Line" else "Enter"
        Direction.SW -> "shift" to "Shift"
        Direction.W -> "backspace" to "Backspace"
        Direction.NW -> "capslock" to if (isCaps) "Caps Off" else "Caps"
        else -> "" to ""
    }
}

internal fun getInfoForDirection6(dir: Direction, keyboardMode: KeyboardMode): Pair<String, String> {
    val isShifted = keyboardMode == KeyboardMode.SHIFTED || keyboardMode == KeyboardMode.SYMBOLS_SHIFTED
    val isCaps = keyboardMode == KeyboardMode.CAPS_LOCKED
    val isSymbols = keyboardMode == KeyboardMode.SYMBOLS || keyboardMode == KeyboardMode.SYMBOLS_SHIFTED

    return when (dir) {
        Direction.NE -> "shift" to if (isCaps) "Caps Off" else "Shift"
        Direction.SE -> "space" to "Space"
        Direction.S -> "" to if (isShifted || isCaps) ">" else "."
        Direction.SW -> "enter" to if (isShifted || isCaps) "New Line" else "Enter"
        Direction.NW -> "backspace" to "Backspace"
        Direction.N -> "" to if (isSymbols) "ABC" else "#+="
        else -> "" to ""
    }
}

internal fun fittedTextSize(
    basePaint: Paint,
    lines: List<String>,
    maxWidth: Float,
    maxHeight: Float,
    preferredSizes: List<Float>
): Float {
    val measuringPaint = Paint(basePaint)

    for (size in preferredSizes) {
        measuringPaint.textSize = size
        val widestLine = lines.maxOfOrNull { measuringPaint.measureText(it) } ?: 0f
        val totalHeight = measuringPaint.fontSpacing * 0.9f * lines.size.coerceAtLeast(1)
        if (widestLine <= maxWidth && totalHeight <= maxHeight) {
            return size
        }
    }

    return preferredSizes.last()
}

internal fun getRightDialLabelLines(label: String): List<String> {
    return when (label) {
        "Backspace" -> listOf("Back", "space")
        "New Line" -> listOf("New", "Line")
        "Caps Off" -> listOf("Caps", "Off")
        else -> label.split(" ").filter { it.isNotBlank() }.ifEmpty {
            if (label.isNotBlank()) listOf(label) else emptyList()
        }
    }
}

internal fun fittedSingleLineTextSize(
    text: String,
    basePaint: Paint,
    maxWidth: Float,
    maxHeight: Float,
    preferredSizes: List<Float>
): Float {
    val measuringPaint = Paint(basePaint)

    for (size in preferredSizes) {
        measuringPaint.textSize = size
        val textWidth = measuringPaint.measureText(text)
        val textHeight = measuringPaint.fontSpacing
        if (textWidth <= maxWidth && textHeight <= maxHeight) {
            return size
        }
    }

    return preferredSizes.last()
}

internal fun drawProgrammaticIcon(
    canvas: Canvas,
    type: String,
    x: Float,
    y: Float,
    size: Float,
    alpha: Int,
    color: Int = Color.WHITE,
    strokePaint: Paint,
    fillPaint: Paint
) {
    strokePaint.color = color
    strokePaint.alpha = alpha
    fillPaint.color = color
    fillPaint.alpha = alpha
    strokePaint.strokeWidth = maxOf(3f, size * 0.16f)
    val halfSize = size / 2f

    when (type) {
        "home" -> {
            canvas.drawLine(x - halfSize, y - halfSize, x + halfSize, y - halfSize, strokePaint)
            val path = Path().apply {
                moveTo(x, y - halfSize + 5f)
                lineTo(x - halfSize, y + halfSize)
                lineTo(x + halfSize, y + halfSize)
                close()
            }
            canvas.drawPath(path, fillPaint)
        }
        "end" -> {
            canvas.drawLine(x - halfSize, y + halfSize, x + halfSize, y + halfSize, strokePaint)
            val path = Path().apply {
                moveTo(x, y + halfSize - 5f)
                lineTo(x - halfSize, y - halfSize)
                lineTo(x + halfSize, y - halfSize)
                close()
            }
            canvas.drawPath(path, fillPaint)
        }
        "space" -> {
            canvas.drawLine(x - halfSize, y + halfSize / 2f, x + halfSize, y + halfSize / 2f, strokePaint)
            canvas.drawLine(x - halfSize, y, x - halfSize, y + halfSize / 2f, strokePaint)
            canvas.drawLine(x + halfSize, y, x + halfSize, y + halfSize / 2f, strokePaint)
        }
        "enter" -> {
            canvas.drawLine(x + halfSize, y - halfSize, x + halfSize, y + halfSize, strokePaint)
            canvas.drawLine(x + halfSize, y + halfSize, x - halfSize, y + halfSize, strokePaint)
            canvas.drawLine(x - halfSize, y + halfSize, x - halfSize + 6f, y + halfSize - 6f, strokePaint)
            canvas.drawLine(x - halfSize, y + halfSize, x - halfSize + 6f, y + halfSize + 6f, strokePaint)
        }
        "shift" -> {
            val path = Path().apply {
                moveTo(x, y - halfSize)
                lineTo(x - halfSize, y + halfSize / 3f)
                lineTo(x - halfSize / 2f, y + halfSize / 3f)
                lineTo(x - halfSize / 2f, y + halfSize)
                lineTo(x + halfSize / 2f, y + halfSize)
                lineTo(x + halfSize / 2f, y + halfSize / 3f)
                lineTo(x + halfSize, y + halfSize / 3f)
                close()
            }
            canvas.drawPath(path, fillPaint)
        }
        "backspace" -> {
            val path = Path().apply {
                moveTo(x - halfSize * 0.82f, y)
                lineTo(x - halfSize * 0.22f, y - halfSize * 0.72f)
                lineTo(x + halfSize * 0.80f, y - halfSize * 0.72f)
                lineTo(x + halfSize * 0.80f, y + halfSize * 0.72f)
                lineTo(x - halfSize * 0.22f, y + halfSize * 0.72f)
                close()
            }
            canvas.drawPath(path, strokePaint)
            canvas.drawLine(x - halfSize * 0.02f, y - halfSize * 0.26f, x + halfSize * 0.42f, y + halfSize * 0.26f, strokePaint)
            canvas.drawLine(x + halfSize * 0.42f, y - halfSize * 0.26f, x - halfSize * 0.02f, y + halfSize * 0.26f, strokePaint)
        }
        "capslock" -> {
            val path = Path().apply {
                moveTo(x, y - halfSize + 5f)
                lineTo(x - halfSize, y + halfSize / 3f)
                lineTo(x - halfSize / 2f, y + halfSize / 3f)
                lineTo(x - halfSize / 2f, y + halfSize / 2f)
                lineTo(x + halfSize / 2f, y + halfSize / 2f)
                lineTo(x + halfSize / 2f, y + halfSize / 3f)
                lineTo(x + halfSize, y + halfSize / 3f)
                close()
            }
            canvas.drawPath(path, fillPaint)
            canvas.drawLine(x - halfSize, y + halfSize, x + halfSize, y + halfSize, strokePaint)
        }
    }
}

internal fun darkenColor(color: Int, factor: Float): Int {
    val alpha = Color.alpha(color)
    val red = (Color.red(color) * factor).toInt().coerceIn(0, 255)
    val green = (Color.green(color) * factor).toInt().coerceIn(0, 255)
    val blue = (Color.blue(color) * factor).toInt().coerceIn(0, 255)
    return Color.argb(alpha, red, green, blue)
}