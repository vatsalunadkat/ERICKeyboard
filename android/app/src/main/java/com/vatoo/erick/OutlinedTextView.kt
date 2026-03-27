package com.vatoo.erick

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.widget.TextView

/**
 * A TextView that draws a contrasting outline (stroke) around the text
 * to ensure readability on both light and dark backgrounds.
 */
class OutlinedTextView(context: Context) : TextView(context) {

    var strokeColor: Int = Color.BLACK
    var strokeWidthPx: Float = 3f

    override fun onDraw(canvas: Canvas) {
        val textColor = currentTextColor

        // Draw stroke (outline)
        paint.style = Paint.Style.STROKE
        paint.strokeWidth = strokeWidthPx
        paint.strokeJoin = Paint.Join.ROUND
        setTextColor(strokeColor)
        super.onDraw(canvas)

        // Draw fill (actual text)
        paint.style = Paint.Style.FILL
        paint.strokeWidth = 0f
        setTextColor(textColor)
        super.onDraw(canvas)
    }
}
