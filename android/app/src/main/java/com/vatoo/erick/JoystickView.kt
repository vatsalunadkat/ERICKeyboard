package com.vatoo.erick

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.graphics.Typeface
import android.util.AttributeSet
import android.view.View
import com.vatoo.erick.shared.ColorPaletteType
import com.vatoo.erick.shared.ColorPalettes
import com.vatoo.erick.shared.Direction
import com.vatoo.erick.shared.KeyboardMode
import com.vatoo.erick.shared.LayoutType
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.hypot
import kotlin.math.sin

class JoystickView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    var processor: Any? = null
    var isRightSide: Boolean = false
    var keyboardMode: KeyboardMode = KeyboardMode.NORMAL
        set(value) {
            if (field != value) {
                field = value
                invalidate()
            }
        }
    var layoutType: LayoutType = LayoutType.LOGICAL
        set(value) {
            if (field != value) {
                field = value
                invalidate()
            }
        }
    var colorPaletteType: ColorPaletteType = ColorPaletteType.DEFAULT
        set(value) {
            if (field != value) {
                field = value
                invalidate()
            }
        }
    var isDarkMode: Boolean = false
        set(value) {
            if (field != value) {
                field = value
                invalidate()
            }
        }
    var customTypeface: Typeface? = null
        set(value) {
            if (field != value) {
                field = value
                charTextPaint.typeface = value ?: Typeface.DEFAULT_BOLD
                labelTextPaint.typeface = value ?: Typeface.DEFAULT
                invalidate()
            }
        }
    var customCharsNormal: Map<Direction, List<String>>? = null
        set(value) {
            if (field != value) {
                field = value
                invalidate()
            }
        }
    var customCharsShifted: Map<Direction, List<String>>? = null
        set(value) {
            if (field != value) {
                field = value
                invalidate()
            }
        }
    private var previewText: String = ""

    private var centerX = 0f
    private var centerY = 0f
    private var baseRadius = 0f
    private var thumbRadius = 0f
    private var thumbX = 0f
    private var thumbY = 0f
    
    private val iconBitmaps = mutableMapOf<String, Bitmap>()

    var activeDirection: Direction = Direction.NONE
        private set

    private val basePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.parseColor("#121212") // Dark background for the dial base
        style = Paint.Style.FILL
    }
    
    private val activeSegmentPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
    }
    
    private val segmentLinePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.parseColor("#121212") // Use base background for block seams
        style = Paint.Style.STROKE
        strokeWidth = 6f
    }
    
    // Specifically for the right dial separator lines
    private val rightSegmentLinePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.WHITE
        style = Paint.Style.STROKE
        strokeWidth = 4f
    }
    
    // Specifically for the main 8 directions on the left dial
    private val mainDirectionLinePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.WHITE
        style = Paint.Style.STROKE
        strokeWidth = 4f
    }
    
    // For outer thick border of the left dial
    private val borderPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.WHITE // Changed from Golden/Orange to White
        style = Paint.Style.STROKE
        strokeWidth = 10f
    }

    private val iconStrokePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.WHITE
        style = Paint.Style.STROKE
        strokeWidth = 5f
        strokeCap = Paint.Cap.ROUND
    }

    private val iconFillPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.WHITE
        style = Paint.Style.FILL
    }

    private val thumbPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.parseColor("#9E9E9E")
        style = Paint.Style.FILL
        setShadowLayer(8f, 0f, 4f, Color.BLACK)
    }

    private val thumbInnerPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.parseColor("#757575")
        style = Paint.Style.FILL
    }

    private val charTextPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.WHITE
        textSize = 34f
        textAlign = Paint.Align.CENTER
        typeface = Typeface.DEFAULT_BOLD
    }

    private val labelTextPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.WHITE
        textSize = 24f
        textAlign = Paint.Align.CENTER
        typeface = Typeface.DEFAULT
    }

    private val leftCharsNormal = mapOf(
        Direction.N to listOf("a", "b", "c", "d", "e", "", "", "'"),
        Direction.NE to listOf("f", "g", "h", "i", "j", "", "", "/"),
        Direction.E to listOf("k", "l", "m", "n", "o", "", "", ";"),
        Direction.SE to listOf("p", "q", "r", "s", "t", "", "", "-"),
        Direction.S to listOf("u", "v", "w", "x", "y", "", "", "="),
        Direction.SW to listOf("z", "\\", "[", "]", "`", "", "", ""),
        Direction.W to listOf("1", "2", "3", "4", "5", "", "", ""),
        Direction.NW to listOf("6", "7", "8", "9", "0", "", "", "")
    )

    private val leftCharsShifted = mapOf(
        Direction.N to listOf("A", "B", "C", "D", "E", "", "", "\""),
        Direction.NE to listOf("F", "G", "H", "I", "J", "", "", "?"),
        Direction.E to listOf("K", "L", "M", "N", "O", "", "", ":"),
        Direction.SE to listOf("P", "Q", "R", "S", "T", "", "", "_"),
        Direction.S to listOf("U", "V", "W", "X", "Y", "", "", "+"),
        Direction.SW to listOf("Z", "|", "{", "}", "~", "", "", ""),
        Direction.W to listOf("!", "@", "#", "$", "%", "", "", ""),
        Direction.NW to listOf("^", "&", "*", "(", ")", "", "", "")
    )

    // ========== EFFICIENCY LAYOUT ==========
    private val leftCharsEfficiencyNormal = mapOf(
        Direction.N  to listOf("t", "s", "g", "7", "=", "", "4", "k"),
        Direction.NE to listOf("i", "a", "n", "p", "/", "", "", "'"),
        Direction.E  to listOf("v", "l", "e", "r", "x", "", "", ";"),
        Direction.SE to listOf("-", "y", "d", "o", "m", "", "", ""),
        Direction.S  to listOf("`", "6", "b", "f", "u", "", "", ""),
        Direction.SW to listOf("\\", "[", "]", "5", "q", "j", "", ""),
        Direction.W  to listOf("", "", "", "", "", "2", "3", "z"),
        Direction.NW to listOf("h", "w", "1", "8", "9", "", "0", "c")
    )

    private val leftCharsEfficiencyShifted = mapOf(
        Direction.N  to listOf("T", "S", "G", "&", "+", "", "$", "K"),
        Direction.NE to listOf("I", "A", "N", "P", "?", "", "", "\""),
        Direction.E  to listOf("V", "L", "E", "R", "X", "", "", ":"),
        Direction.SE to listOf("_", "Y", "D", "O", "M", "", "", ""),
        Direction.S  to listOf("~", "^", "B", "F", "U", "", "", ""),
        Direction.SW to listOf("|", "{", "}", "%", "Q", "J", "", ""),
        Direction.W  to listOf("", "", "", "", "", "@", "#", "Z"),
        Direction.NW to listOf("H", "W", "!", "*", "(", "", ")", "C")
    )
    
    private val rightDirs = listOf(
        Direction.N, Direction.NE, Direction.E, Direction.SE, 
        Direction.S, Direction.SW, Direction.W, Direction.NW
    )

    private val directions = listOf(
        Direction.E, Direction.SE, Direction.S, Direction.SW,
        Direction.W, Direction.NW, Direction.N, Direction.NE
    )

    init {
        // Enable hardware acceleration/shadows
        setLayerType(LAYER_TYPE_SOFTWARE, null)
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val w = MeasureSpec.getSize(widthMeasureSpec)
        val h = MeasureSpec.getSize(heightMeasureSpec)
        val heightMode = MeasureSpec.getMode(heightMeasureSpec)
        // Request a square view based on width; honour EXACTLY if the parent forces a height
        val finalHeight = if (heightMode == MeasureSpec.EXACTLY) h else w
        setMeasuredDimension(w, finalHeight)
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        centerX = w / 2f
        centerY = h / 2f
        baseRadius = (Math.min(w, h) / 2f) * 0.90f
        thumbRadius = baseRadius * 0.22f
        
        // Bitmaps are only used for left side or legacy if needed.
        // For the right joystick, we are now 100% programmatic.
        iconBitmaps.clear()
        
        resetThumb()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        if (isRightSide) {
            // Right Dial: 8 solid colored segments
            val rectF = RectF(centerX - baseRadius, centerY - baseRadius, centerX + baseRadius, centerY + baseRadius)

            for (i in 0 until 8) {
                val startAngle = -22.5f + i * 45f
                val sweepAngle = 45f
                val dir = directions[i]
                val isActive = (dir == activeDirection && activeDirection != Direction.NONE)

                val colorHex = ColorPalettes.getColorForDirectionHex(dir, colorPaletteType)
                val parsedColor = Color.parseColor(colorHex)
                activeSegmentPaint.color = if (activeDirection != Direction.NONE && !isActive) darkenColor(parsedColor, 0.4f) else parsedColor
                activeSegmentPaint.alpha = 255

                canvas.drawArc(rectF, startAngle, sweepAngle, true, activeSegmentPaint)
                
                rightSegmentLinePaint.alpha = if (activeDirection != Direction.NONE && !isActive) 60 else 255
                canvas.drawArc(rectF, startAngle, sweepAngle, true, rightSegmentLinePaint) // Use white lines for right joystick
                
                // Draw Icon and Label
                val (iconName, label) = getInfoForDirection(dir)
                val paintAlpha = if (activeDirection != Direction.NONE && !isActive) 60 else 255
                val contentColor = Color.parseColor(ColorPalettes.contrastTextColor(colorHex))
                
                // Content area center
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
                    alpha = paintAlpha,
                    textColor = contentColor
                )
            }
        } else {
            // Left Dial: 3 Concentric Layers with discrete blocks
            val innerHoleRadius = thumbRadius * 0.9f  // Expand the inner layers by making hole smaller to hug the thumb tightly
            val layerThickness = (baseRadius - innerHoleRadius) / 3f
            val r1 = innerHoleRadius + layerThickness
            val r2 = innerHoleRadius + layerThickness * 2f
            
            val rectFOuter = RectF(centerX - baseRadius, centerY - baseRadius, centerX + baseRadius, centerY + baseRadius)
            val rectFMiddle = RectF(centerX - r2, centerY - r2, centerX + r2, centerY + r2)
            val rectFInner = RectF(centerX - r1, centerY - r1, centerX + r1, centerY + r1)

            // Draw Base Background
            canvas.drawCircle(centerX, centerY, baseRadius, basePaint)

            // Overlapping Pie Slices Drawing Method
            // 1. Draw Outer Layer
            for (i in 0 until 8) {
                val dir = directions[i]
                val startAngle = -22.5f + i * 45f
                val isActive = (dir == activeDirection && activeDirection != Direction.NONE)

                for(j in 0 until 3) {
                    val blockStart = startAngle + j * 15f
                    val parsedColor = Color.parseColor(ColorPalettes.getColorForDirectionHex(rightDirs[j], colorPaletteType))
                    activeSegmentPaint.color = if (activeDirection != Direction.NONE && !isActive) darkenColor(parsedColor, 0.4f) else parsedColor
                    activeSegmentPaint.alpha = 255
                    canvas.drawArc(rectFOuter, blockStart, 15f, true, activeSegmentPaint)
                }
            }

            // 2. Draw Middle Layer
            for (i in 0 until 8) {
                val dir = directions[i]
                val startAngle = -22.5f + i * 45f
                val isActive = (dir == activeDirection && activeDirection != Direction.NONE)

                for(j in 0 until 3) {
                    val blockStart = startAngle + j * 15f
                    val parsedColor = Color.parseColor(ColorPalettes.getColorForDirectionHex(rightDirs[3 + j], colorPaletteType))
                    activeSegmentPaint.color = if (activeDirection != Direction.NONE && !isActive) darkenColor(parsedColor, 0.4f) else parsedColor
                    activeSegmentPaint.alpha = 255
                    canvas.drawArc(rectFMiddle, blockStart, 15f, true, activeSegmentPaint)
                }
            }

            // 3. Draw Inner Layer
            for (i in 0 until 8) {
                val dir = directions[i]
                val startAngle = -22.5f + i * 45f
                val isActive = (dir == activeDirection && activeDirection != Direction.NONE)

                for(j in 0 until 2) {
                    val blockStart = startAngle + j * 22.5f
                    val parsedColor = Color.parseColor(ColorPalettes.getColorForDirectionHex(rightDirs[6 + j], colorPaletteType))
                    activeSegmentPaint.color = if (activeDirection != Direction.NONE && !isActive) darkenColor(parsedColor, 0.4f) else parsedColor
                    activeSegmentPaint.alpha = 255
                    canvas.drawArc(rectFInner, blockStart, 22.5f, true, activeSegmentPaint)
                }
            }

            // 4. Draw Center Hole to cut out the inner pie tips
            canvas.drawCircle(centerX, centerY, innerHoleRadius, basePaint)

            // 5. Draw Separator Circles (Seams between layers)
            canvas.drawCircle(centerX, centerY, r2, segmentLinePaint)
            canvas.drawCircle(centerX, centerY, r1, segmentLinePaint)

            // 6. Draw Separator Lines (Seams within layers)
            for (i in 0 until 8) {
                val startAngle = -22.5f + i * 45f
                val dir = directions[i]
                
                // For white separation lines, determine alpha based on adjacent blocks
                // The j=0 line is right before this block. The j=3 line is right after.
                
                // Outer/Middle Layer radial separators (every 15 degrees)
                for (j in 0..3) {
                    val angleRad = Math.toRadians((startAngle + j * 15f).toDouble())
                    val rStart = if (j == 0 || j == 3) innerHoleRadius else r1
                    val ex = centerX + cos(angleRad).toFloat() * baseRadius
                    val ey = centerY + sin(angleRad).toFloat() * baseRadius
                    val sx = centerX + cos(angleRad).toFloat() * rStart
                    val sy = centerY + sin(angleRad).toFloat() * rStart
                    
                    // If it is the boundary between the 8 main sets, use the white mainDirectionLinePaint
                    if (j == 0 || j == 3) {
                        // Calculate if EITHER side of this line is active
                        // j=0 is boundary with previous slice. j=3 is boundary with next slice.
                        val prevIdx = if (i == 0) 7 else i - 1
                        val nextIdx = if (i == 7) 0 else i + 1
                        val adjacentDir = if (j == 0) directions[prevIdx] else directions[nextIdx]
                        
                        val isLineActive = (dir == activeDirection || adjacentDir == activeDirection)
                        val lineAlpha = if (activeDirection != Direction.NONE && !isLineActive) 60 else 255
                        
                        mainDirectionLinePaint.alpha = lineAlpha
                        canvas.drawLine(sx, sy, ex, ey, mainDirectionLinePaint)
                    } else {
                        val lineAlpha = if (activeDirection != Direction.NONE && dir != activeDirection) 60 else 255
                        segmentLinePaint.alpha = lineAlpha
                        canvas.drawLine(sx, sy, ex, ey, segmentLinePaint) // Black line inside the set
                    }
                }
                
                // Inner Layer radial separator (at 22.5 degrees)
                val angleRad2 = Math.toRadians((startAngle + 22.5f).toDouble())
                val sx2 = centerX + cos(angleRad2).toFloat() * innerHoleRadius
                val sy2 = centerY + sin(angleRad2).toFloat() * innerHoleRadius
                val ex2 = centerX + cos(angleRad2).toFloat() * r1
                val ey2 = centerY + sin(angleRad2).toFloat() * r1
                val lineAlpha2 = if (activeDirection != Direction.NONE && dir != activeDirection) 60 else 255
                segmentLinePaint.alpha = lineAlpha2
                canvas.drawLine(sx2, sy2, ex2, ey2, segmentLinePaint)
            }
            
            // 7. Draw outer white border & inner base limit per slice for selective alpha
            val rectFInnerHole = RectF(centerX - innerHoleRadius, centerY - innerHoleRadius, centerX + innerHoleRadius, centerY + innerHoleRadius)
            for (i in 0 until 8) {
                val startAngle = -22.5f + i * 45f
                val dir = directions[i]
                val isActive = (dir == activeDirection && activeDirection != Direction.NONE)
                val lineAlpha = if (activeDirection != Direction.NONE && !isActive) 60 else 255

                borderPaint.alpha = lineAlpha
                canvas.drawArc(rectFOuter, startAngle, 45f, false, borderPaint)

                mainDirectionLinePaint.alpha = lineAlpha
                canvas.drawArc(rectFInnerHole, startAngle, 45f, false, mainDirectionLinePaint)
            }

            // 8. Draw Characters
            val currentCharsMap = when {
                layoutType == LayoutType.CUSTOM && keyboardMode == KeyboardMode.NORMAL && customCharsNormal != null -> customCharsNormal!!
                layoutType == LayoutType.CUSTOM && customCharsShifted != null -> customCharsShifted!!
                layoutType == LayoutType.EFFICIENCY && keyboardMode == KeyboardMode.NORMAL -> leftCharsEfficiencyNormal
                layoutType == LayoutType.EFFICIENCY -> leftCharsEfficiencyShifted
                keyboardMode == KeyboardMode.NORMAL -> leftCharsNormal
                else -> leftCharsShifted
            }
            for (i in 0 until 8) {
                val dir = directions[i]
                val startAngle = -22.5f + i * 45f
                val chars = currentCharsMap[dir] ?: emptyList()
                val isActive = (dir == activeDirection && activeDirection != Direction.NONE)
                val alphaVal = if (activeDirection != Direction.NONE && !isActive) 60 else 255
                
                // Outer Text
                for(j in 0 until 3) {
                    val c = chars.getOrNull(j) ?: continue
                    if (c.isBlank()) continue
                    val bgHex = ColorPalettes.getColorForDirectionHex(rightDirs[j], colorPaletteType)
                    drawCharText(
                        canvas = canvas,
                        charStr = c,
                        ringInnerRadius = r2,
                        ringOuterRadius = baseRadius,
                        startAngle = startAngle + j * 15f,
                        sweepAngle = 15f,
                        alphaVal = alphaVal,
                        bgHex = bgHex
                    )
                }
                // Middle Text
                for(j in 0 until 3) {
                    val c = chars.getOrNull(3 + j) ?: continue
                    if (c.isBlank()) continue
                    val bgHex = ColorPalettes.getColorForDirectionHex(rightDirs[3 + j], colorPaletteType)
                    drawCharText(
                        canvas = canvas,
                        charStr = c,
                        ringInnerRadius = r1,
                        ringOuterRadius = r2,
                        startAngle = startAngle + j * 15f,
                        sweepAngle = 15f,
                        alphaVal = alphaVal,
                        bgHex = bgHex
                    )
                }
                // Inner Text
                for(j in 0 until 2) {
                    val c = chars.getOrNull(6 + j) ?: continue
                    if (c.isBlank()) continue
                    val bgHex = ColorPalettes.getColorForDirectionHex(rightDirs[6 + j], colorPaletteType)
                    drawCharText(
                        canvas = canvas,
                        charStr = c,
                        ringInnerRadius = innerHoleRadius,
                        ringOuterRadius = r1,
                        startAngle = startAngle + j * 22.5f,
                        sweepAngle = 22.5f,
                        alphaVal = alphaVal,
                        bgHex = bgHex
                    )
                }
            }
        }

        // Draw thumb
        canvas.drawCircle(thumbX, thumbY, thumbRadius, thumbPaint)
        canvas.drawCircle(thumbX, thumbY, thumbRadius * 0.6f, thumbInnerPaint)
    }

    private fun drawCharText(
        canvas: Canvas,
        charStr: String,
        ringInnerRadius: Float,
        ringOuterRadius: Float,
        startAngle: Float,
        sweepAngle: Float,
        alphaVal: Int,
        bgHex: String = "#000000"
    ) {
        val centerRadius = (ringInnerRadius + ringOuterRadius) / 2f
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
            preferredSizes = listOf(34f, 32f, 30f, 28f, 26f, 24f, 22f, 20f, 18f)
        )

        val textColor = Color.parseColor(ColorPalettes.contrastTextColor(bgHex))
        val textPaint = Paint(charTextPaint).apply {
            color = textColor
            alpha = alphaVal
            textSize = fittedSize
        }

        val baseline = charY - (textPaint.descent() + textPaint.ascent()) / 2f
        canvas.drawText(charStr, charX, baseline, textPaint)
    }

    private fun drawRightDialContent(
        canvas: Canvas,
        iconName: String,
        label: String,
        centerX: Float,
        centerY: Float,
        alpha: Int,
        textColor: Int = Color.WHITE
    ) {
        if (iconName.isEmpty() && label.isEmpty()) return

        val hasIcon = iconName.isNotEmpty()
        val lines = getRightDialLabelLines(label)

        val availableWidth = if (hasIcon) baseRadius * 0.43f else baseRadius * 0.48f
        val availableHeight = if (hasIcon) baseRadius * 0.36f else baseRadius * 0.30f

        val textPaint = Paint(labelTextPaint).apply {
            this.alpha = alpha
            this.color = textColor
            textSize = fittedTextSize(
                lines = lines,
                maxWidth = availableWidth,
                maxHeight = availableHeight,
                preferredSizes = listOf(20f, 18f, 16f, 14f, 12f)
            )
        }

        val lineHeight = textPaint.fontSpacing * 0.9f
        val textBlockHeight = if (lines.isEmpty()) 0f else lineHeight * lines.size
        val iconSize = if (hasIcon) minOf(baseRadius * 0.095f, availableHeight * 0.34f) else 0f
        val spacing = if (hasIcon && lines.isNotEmpty()) baseRadius * 0.024f else 0f
        val totalHeight = iconSize + spacing + textBlockHeight
        var currentCenterY = centerY - totalHeight / 2f

        if (hasIcon) {
            val iconCenterY = currentCenterY + iconSize / 2f
            drawProgrammaticIcon(canvas, iconName, centerX, iconCenterY, iconSize, alpha, textColor)
            currentCenterY += iconSize + spacing
        }

        lines.forEachIndexed { index, line ->
            val lineCenterY = currentCenterY + lineHeight * index + lineHeight / 2f
            val baseline = lineCenterY - (textPaint.descent() + textPaint.ascent()) / 2f
            canvas.drawText(line, centerX, baseline, textPaint)
        }
    }

    private fun fittedTextSize(
        lines: List<String>,
        maxWidth: Float,
        maxHeight: Float,
        preferredSizes: List<Float>
    ): Float {
        val measuringPaint = Paint(labelTextPaint)

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

    private fun getRightDialLabelLines(label: String): List<String> {
        return when (label) {
            "Backspace" -> listOf("Back", "space")
            "New Line" -> listOf("New", "Line")
            "Caps Off" -> listOf("Caps", "Off")
            else -> label.split(" ").filter { it.isNotBlank() }.ifEmpty {
                if (label.isNotBlank()) listOf(label) else emptyList()
            }
        }
    }

    private fun fittedSingleLineTextSize(
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

    fun updateThumb(dx: Float, dy: Float) {
        val distance = hypot(dx.toDouble(), dy.toDouble()).toFloat()
        val maxRadius = if (isRightSide) {
            baseRadius * 0.3f 
        } else {
            baseRadius * 0.4f 
        }

        if (distance > maxRadius) {
            val ratio = maxRadius / distance
            thumbX = centerX + (dx * ratio)
            thumbY = centerY + (dy * ratio)
        } else {
            thumbX = centerX + dx
            thumbY = centerY + dy
        }

        if (distance > baseRadius * 0.15f) {
            val radians = atan2(dy.toDouble(), dx.toDouble())
            var degrees = Math.toDegrees(radians)
            if (degrees < 0) degrees += 360.0

            activeDirection = when {
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
        } else {
            activeDirection = Direction.NONE
        }

        invalidate()
    }
    fun updateThumbFromController(normalizedX: Float, normalizedY: Float, deadZone: Float = 0.25f) {
        val clampedX = normalizedX.coerceIn(-1f, 1f)
        val clampedY = normalizedY.coerceIn(-1f, 1f)
        val magnitude = hypot(clampedX.toDouble(), clampedY.toDouble()).toFloat()

        if (magnitude <= deadZone) {
            resetThumb()
            return
        }

        val maxRadius = if (isRightSide) {
            baseRadius * 0.3f
        } else {
            baseRadius * 0.4f
        }

        val activationRadius = minOf(maxRadius, (baseRadius * 0.15f) + 1f)
        val normalizedMagnitude = if (deadZone >= 1f) {
            1f
        } else {
            ((magnitude - deadZone) / (1f - deadZone)).coerceIn(0f, 1f)
        }
        val remappedRadius = activationRadius + ((maxRadius - activationRadius) * normalizedMagnitude)
        val magnitudeScale = if (magnitude > 0f) remappedRadius / magnitude else 0f

        updateThumb(
            dx = clampedX * magnitudeScale,
            dy = clampedY * magnitudeScale
        )
    }
    private fun getInfoForDirection(dir: Direction): Pair<String, String> {
        val isShifted = keyboardMode == KeyboardMode.SHIFTED
        val isCaps = keyboardMode == KeyboardMode.CAPS_LOCKED
        val isToggled = isShifted || isCaps
        
        return when (dir) {
            Direction.N -> if (isToggled) "end" to "End" else "home" to "Home"
            Direction.NE -> "" to (if (isToggled) "<" else ",")
            Direction.E -> "space" to "Space"
            Direction.SE -> "" to (if (isToggled) ">" else ".")
            Direction.S -> "enter" to (if (isToggled) "New Line" else "Enter")
            Direction.SW -> "shift" to "Shift"
            Direction.W -> "backspace" to "Backspace"
            Direction.NW -> "capslock" to (if (isCaps) "Caps Off" else "Caps")
            else -> "" to ""
        }
    }

    private fun drawProgrammaticIcon(canvas: Canvas, type: String, x: Float, y: Float, size: Float, alpha: Int, color: Int = Color.WHITE) {
        iconStrokePaint.color = color
        iconStrokePaint.alpha = alpha
        iconFillPaint.color = color
        iconFillPaint.alpha = alpha
        iconStrokePaint.strokeWidth = maxOf(3f, size * 0.16f)
        val h = size / 2f
        
        when (type) {
            "home" -> {
                // arrow.up.to.line
                canvas.drawLine(x - h, y - h, x + h, y - h, iconStrokePaint) // Top bar
                val path = android.graphics.Path().apply {
                    moveTo(x, y - h + 5f)
                    lineTo(x - h, y + h)
                    lineTo(x + h, y + h)
                    close()
                }
                canvas.drawPath(path, iconFillPaint)
            }
            "end" -> {
                // arrow.down.to.line
                canvas.drawLine(x - h, y + h, x + h, y + h, iconStrokePaint) // Bottom bar
                val path = android.graphics.Path().apply {
                    moveTo(x, y + h - 5f)
                    lineTo(x - h, y - h)
                    lineTo(x + h, y - h)
                    close()
                }
                canvas.drawPath(path, iconFillPaint)
            }
            "space" -> {
                // Horizontal bracket
                canvas.drawLine(x - h, y + h/2f, x + h, y + h/2f, iconStrokePaint)
                canvas.drawLine(x - h, y, x - h, y + h/2f, iconStrokePaint)
                canvas.drawLine(x + h, y, x + h, y + h/2f, iconStrokePaint)
            }
            "enter" -> {
                // return (L-shape arrow)
                canvas.drawLine(x + h, y - h, x + h, y + h, iconStrokePaint)
                canvas.drawLine(x + h, y + h, x - h, y + h, iconStrokePaint)
                // arrow head
                canvas.drawLine(x - h, y + h, x - h + 6f, y + h - 6f, iconStrokePaint)
                canvas.drawLine(x - h, y + h, x - h + 6f, y + h + 6f, iconStrokePaint)
            }
            "shift" -> {
                // shift.fill
                val path = android.graphics.Path().apply {
                    moveTo(x, y - h)
                    lineTo(x - h, y + h/3f)
                    lineTo(x - h/2f, y + h/3f)
                    lineTo(x - h/2f, y + h)
                    lineTo(x + h/2f, y + h)
                    lineTo(x + h/2f, y + h/3f)
                    lineTo(x + h, y + h/3f)
                    close()
                }
                canvas.drawPath(path, iconFillPaint)
            }
            "backspace" -> {
                // delete.left
                val path = android.graphics.Path().apply {
                    moveTo(x - h * 0.82f, y)
                    lineTo(x - h * 0.22f, y - h * 0.72f)
                    lineTo(x + h * 0.80f, y - h * 0.72f)
                    lineTo(x + h * 0.80f, y + h * 0.72f)
                    lineTo(x - h * 0.22f, y + h * 0.72f)
                    close()
                }
                canvas.drawPath(path, iconStrokePaint)
                // The X
                canvas.drawLine(x - h * 0.02f, y - h * 0.26f, x + h * 0.42f, y + h * 0.26f, iconStrokePaint)
                canvas.drawLine(x + h * 0.42f, y - h * 0.26f, x - h * 0.02f, y + h * 0.26f, iconStrokePaint)
            }
            "capslock" -> {
                // capslock.fill
                val path = android.graphics.Path().apply {
                    moveTo(x, y - h + 5f)
                    lineTo(x - h, y + h/3f)
                    lineTo(x - h/2f, y + h/3f)
                    lineTo(x - h/2f, y + h/2f)
                    lineTo(x + h/2f, y + h/2f)
                    lineTo(x + h/2f, y + h/3f)
                    lineTo(x + h, y + h/3f)
                    close()
                }
                canvas.drawPath(path, iconFillPaint)
                canvas.drawLine(x - h, y + h, x + h, y + h, iconStrokePaint) // Bottom bar
            }
        }
    }

    fun resetThumb() {
        thumbX = centerX
        thumbY = centerY
        activeDirection = Direction.NONE
        invalidate()
    }

    fun setPreviewText(text: String) {
        if (previewText != text) {
            previewText = text
            invalidate()
        }
    }

    private fun darkenColor(color: Int, factor: Float): Int {
        val a = Color.alpha(color)
        val r = (Color.red(color) * factor).toInt().coerceIn(0, 255)
        val g = (Color.green(color) * factor).toInt().coerceIn(0, 255)
        val b = (Color.blue(color) * factor).toInt().coerceIn(0, 255)
        return Color.argb(a, r, g, b)
    }
}
