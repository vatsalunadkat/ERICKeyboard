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
    var sixSectionMode: Boolean = false
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

    fun setLockedDirection(dir: Direction) {
        activeDirection = dir
        invalidate()
    }

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

    // ========== 6-SECTION LOGICAL LAYOUT ==========
    private val leftCharsNormal6 = mapOf(
        Direction.N  to listOf("a", "b", "c", "d", "e", "f"),
        Direction.NE to listOf("g", "h", "i", "j", "k", "l"),
        Direction.SE to listOf("m", "n", "o", "p", "q", "r"),
        Direction.S  to listOf("s", "t", "u", "v", "w", "x"),
        Direction.SW to listOf("y", "z", "1", "2", "3", "4"),
        Direction.NW to listOf("5", "6", "7", "8", "9", "0")
    )

    private val leftCharsShifted6 = mapOf(
        Direction.N  to listOf("A", "B", "C", "D", "E", "F"),
        Direction.NE to listOf("G", "H", "I", "J", "K", "L"),
        Direction.SE to listOf("M", "N", "O", "P", "Q", "R"),
        Direction.S  to listOf("S", "T", "U", "V", "W", "X"),
        Direction.SW to listOf("Y", "Z", "!", "@", "#", "$"),
        Direction.NW to listOf("%", "^", "&", "*", "(", ")")
    )

    // ========== 6-SECTION EFFICIENCY LAYOUT ==========
    private val leftCharsEfficiencyNormal6 = mapOf(
        Direction.N  to listOf("e", "s", "g", "7", "k", "4"),
        Direction.NE to listOf("r", "t", "n", "p", "d", "w"),
        Direction.SE to listOf("l", "h", "a", "y", "m", "f"),
        Direction.S  to listOf("c", "u", "b", "o", "q", "x"),
        Direction.SW to listOf("v", "j", "z", "5", "i", "1"),
        Direction.NW to listOf("2", "3", "6", "8", "9", "0")
    )

    private val leftCharsEfficiencyShifted6 = mapOf(
        Direction.N  to listOf("E", "S", "G", "&", "K", "$"),
        Direction.NE to listOf("R", "T", "N", "P", "D", "W"),
        Direction.SE to listOf("L", "H", "A", "Y", "M", "F"),
        Direction.S  to listOf("C", "U", "B", "O", "Q", "X"),
        Direction.SW to listOf("V", "J", "Z", "%", "I", "!"),
        Direction.NW to listOf("@", "#", "^", "*", "(", ")")
    )

    // ========== 6-SECTION SYMBOLS MODE ==========
    private val leftCharsSymbolsNormal6 = mapOf(
        Direction.N  to listOf("!", "@", "#", "$", "%", "^"),
        Direction.NE to listOf("&", "*", "(", ")", "-", "="),
        Direction.SE to listOf("[", "]", "{", "}", "\\", "|"),
        Direction.S  to listOf(";", ":", "'", "\"", ",", "."),
        Direction.SW to listOf("/", "?", "<", ">", "`", "~"),
        Direction.NW to listOf("+", "_", "", "", "", "")
    )

    private val leftCharsSymbolsShifted6 = mapOf(
        Direction.N  to listOf("\u00A3", "\u20AC", "\u00A5", "\u00A2", "\u2030", "\u00B0"),
        Direction.NE to listOf("\u00D7", "\u00F7", "\u00AB", "\u00BB", "\u2013", "\u2014"),
        Direction.SE to listOf("\u2018", "\u2019", "\u201C", "\u201D", "\u2026", "\u00B7"),
        Direction.S  to listOf("\u00BF", "\u00A1", "\u00B1", "\u2260", "\u2264", "\u2265"),
        Direction.SW to listOf("\u221A", "\u221E", "\u03C0", "\u2211", "\u0394", "\u00B5"),
        Direction.NW to listOf("\u2190", "\u2192", "\u2191", "\u2193", "", "")
    )
    
    private val rightDirs = listOf(
        Direction.N, Direction.NE, Direction.E, Direction.SE, 
        Direction.S, Direction.SW, Direction.W, Direction.NW
    )

    private val directions = listOf(
        Direction.E, Direction.SE, Direction.S, Direction.SW,
        Direction.W, Direction.NW, Direction.N, Direction.NE
    )

    // 6-section drawing order (clockwise from canvas angle -60°)
    private val directions6Drawing = listOf(
        Direction.NE, Direction.SE, Direction.S, Direction.SW, Direction.NW, Direction.N
    )

    // 6-section color reference directions (matches chord map index order)
    private val rightDirs6 = listOf(
        Direction.N, Direction.NE, Direction.SE, Direction.S, Direction.SW, Direction.NW
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
            // Right Dial: colored segments
            val rectF = RectF(centerX - baseRadius, centerY - baseRadius, centerX + baseRadius, centerY + baseRadius)

            val sectionCount = if (sixSectionMode) 6 else 8
            val sweepAngle = if (sixSectionMode) 60f else 45f
            val startOffset = if (sixSectionMode) -90f else -22.5f
            val dirList = if (sixSectionMode) directions6Drawing else directions

            for (i in 0 until sectionCount) {
                val startAngle = startOffset + i * sweepAngle
                val dir = dirList[i]
                val isActive = (dir == activeDirection && activeDirection != Direction.NONE)

                val colorHex = if (sixSectionMode)
                    ColorPalettes.getColorForDirectionHex6(dir, colorPaletteType)
                else
                    ColorPalettes.getColorForDirectionHex(dir, colorPaletteType)
                val parsedColor = Color.parseColor(colorHex)
                activeSegmentPaint.color = if (activeDirection != Direction.NONE && !isActive) darkenColor(parsedColor, 0.4f) else parsedColor
                activeSegmentPaint.alpha = 255

                canvas.drawArc(rectF, startAngle, sweepAngle, true, activeSegmentPaint)
                
                rightSegmentLinePaint.alpha = if (activeDirection != Direction.NONE && !isActive) 60 else 255
                canvas.drawArc(rectF, startAngle, sweepAngle, true, rightSegmentLinePaint)
                
                // Draw Icon and Label
                val (iconName, label) = if (sixSectionMode) {
                    getInfoForDirection6(dir, keyboardMode)
                } else {
                    getInfoForDirection(dir, keyboardMode)
                }
                val paintAlpha = if (activeDirection != Direction.NONE && !isActive) 60 else 255
                val contentColor = Color.parseColor(ColorPalettes.contrastTextColor(colorHex, colorPaletteType))
                
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
        } else if (sixSectionMode) {
            // Left Dial: 6-Section Mode — 2 Concentric Layers with 3 blocks each per section
            val innerHoleRadius = thumbRadius * 0.9f
            val r1 = innerHoleRadius + (baseRadius - innerHoleRadius) * 0.60f

            val rectFOuter = RectF(centerX - baseRadius, centerY - baseRadius, centerX + baseRadius, centerY + baseRadius)
            val rectFInner = RectF(centerX - r1, centerY - r1, centerX + r1, centerY + r1)

            // Draw Base Background
            canvas.drawCircle(centerX, centerY, baseRadius, basePaint)

            // 1. Draw Outer Layer (3 blocks × 20° per section)
            for (i in 0 until 6) {
                val dir = directions6Drawing[i]
                val startAngle = -90f + i * 60f
                val isActive = (dir == activeDirection && activeDirection != Direction.NONE)

                for (j in 0 until 3) {
                    val blockStart = startAngle + j * 20f
                    val parsedColor = Color.parseColor(ColorPalettes.getColorForDirectionHex6(rightDirs6[j], colorPaletteType))
                    activeSegmentPaint.color = if (activeDirection != Direction.NONE && !isActive) darkenColor(parsedColor, 0.4f) else parsedColor
                    activeSegmentPaint.alpha = 255
                    canvas.drawArc(rectFOuter, blockStart, 20f, true, activeSegmentPaint)
                }
            }

            // 2. Draw Inner Layer (3 blocks × 20° per section)
            for (i in 0 until 6) {
                val dir = directions6Drawing[i]
                val startAngle = -90f + i * 60f
                val isActive = (dir == activeDirection && activeDirection != Direction.NONE)

                for (j in 0 until 3) {
                    val blockStart = startAngle + j * 20f
                    val parsedColor = Color.parseColor(ColorPalettes.getColorForDirectionHex6(rightDirs6[3 + j], colorPaletteType))
                    activeSegmentPaint.color = if (activeDirection != Direction.NONE && !isActive) darkenColor(parsedColor, 0.4f) else parsedColor
                    activeSegmentPaint.alpha = 255
                    canvas.drawArc(rectFInner, blockStart, 20f, true, activeSegmentPaint)
                }
            }

            // 3. Draw Center Hole
            canvas.drawCircle(centerX, centerY, innerHoleRadius, basePaint)

            // 4. Draw Separator Circle (between inner and outer layers)
            segmentLinePaint.alpha = 255
            canvas.drawCircle(centerX, centerY, r1, segmentLinePaint)

            // 5. Draw Separator Lines
            for (i in 0 until 6) {
                val startAngle = -90f + i * 60f
                val dir = directions6Drawing[i]

                // Radial separators (every 20° within section, plus boundary)
                for (j in 0..3) {
                    val angleRad = Math.toRadians((startAngle + j * 20f).toDouble())
                    val rStart = if (j == 0 || j == 3) innerHoleRadius else r1
                    val ex = centerX + cos(angleRad).toFloat() * baseRadius
                    val ey = centerY + sin(angleRad).toFloat() * baseRadius
                    val sx = centerX + cos(angleRad).toFloat() * rStart
                    val sy = centerY + sin(angleRad).toFloat() * rStart

                    if (j == 0 || j == 3) {
                        val prevIdx = if (i == 0) 5 else i - 1
                        val nextIdx = if (i == 5) 0 else i + 1
                        val adjacentDir = if (j == 0) directions6Drawing[prevIdx] else directions6Drawing[nextIdx]
                        val isLineActive = (dir == activeDirection || adjacentDir == activeDirection)
                        val lineAlpha = if (activeDirection != Direction.NONE && !isLineActive) 60 else 255
                        mainDirectionLinePaint.alpha = lineAlpha
                        canvas.drawLine(sx, sy, ex, ey, mainDirectionLinePaint)
                    } else {
                        val lineAlpha = if (activeDirection != Direction.NONE && dir != activeDirection) 60 else 255
                        segmentLinePaint.alpha = lineAlpha
                        canvas.drawLine(sx, sy, ex, ey, segmentLinePaint)
                    }
                }

                // Inner ring block separators (from innerHoleRadius to r1)
                for (j in 1..2) {
                    val angleRad = Math.toRadians((startAngle + j * 20f).toDouble())
                    val sx = centerX + cos(angleRad).toFloat() * innerHoleRadius
                    val sy = centerY + sin(angleRad).toFloat() * innerHoleRadius
                    val ex = centerX + cos(angleRad).toFloat() * r1
                    val ey = centerY + sin(angleRad).toFloat() * r1
                    val lineAlpha = if (activeDirection != Direction.NONE && dir != activeDirection) 60 else 255
                    segmentLinePaint.alpha = lineAlpha
                    canvas.drawLine(sx, sy, ex, ey, segmentLinePaint)
                }
            }

            // 6. Draw outer border & inner hole boundary per section
            val rectFInnerHole = RectF(centerX - innerHoleRadius, centerY - innerHoleRadius, centerX + innerHoleRadius, centerY + innerHoleRadius)
            for (i in 0 until 6) {
                val startAngle = -90f + i * 60f
                val dir = directions6Drawing[i]
                val isActive = (dir == activeDirection && activeDirection != Direction.NONE)
                val lineAlpha = if (activeDirection != Direction.NONE && !isActive) 60 else 255

                borderPaint.alpha = lineAlpha
                canvas.drawArc(rectFOuter, startAngle, 60f, false, borderPaint)

                mainDirectionLinePaint.alpha = lineAlpha
                canvas.drawArc(rectFInnerHole, startAngle, 60f, false, mainDirectionLinePaint)
            }

            // 7. Draw Characters
            val currentCharsMap = when {
                keyboardMode == KeyboardMode.SYMBOLS -> leftCharsSymbolsNormal6
                keyboardMode == KeyboardMode.SYMBOLS_SHIFTED -> leftCharsSymbolsShifted6
                layoutType == LayoutType.CUSTOM && keyboardMode == KeyboardMode.NORMAL && customCharsNormal != null -> customCharsNormal!!
                layoutType == LayoutType.CUSTOM && customCharsShifted != null -> customCharsShifted!!
                layoutType == LayoutType.EFFICIENCY && keyboardMode == KeyboardMode.NORMAL -> leftCharsEfficiencyNormal6
                layoutType == LayoutType.EFFICIENCY -> leftCharsEfficiencyShifted6
                keyboardMode == KeyboardMode.NORMAL -> leftCharsNormal6
                else -> leftCharsShifted6
            }
            drawSixSectionCharacters(
                canvas = canvas,
                centerX = centerX,
                centerY = centerY,
                innerHoleRadius = innerHoleRadius,
                splitRadius = r1,
                baseRadius = baseRadius,
                directions = directions6Drawing,
                paletteDirections = rightDirs6,
                currentCharsMap = currentCharsMap,
                activeDirection = activeDirection,
                colorPaletteType = colorPaletteType,
                charTextPaint = charTextPaint
            )
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
            drawEightSectionCharacters(
                canvas = canvas,
                centerX = centerX,
                centerY = centerY,
                innerHoleRadius = innerHoleRadius,
                innerRingOuterRadius = r1,
                middleRingOuterRadius = r2,
                baseRadius = baseRadius,
                directions = directions,
                paletteDirections = rightDirs,
                currentCharsMap = currentCharsMap,
                activeDirection = activeDirection,
                colorPaletteType = colorPaletteType,
                charTextPaint = charTextPaint
            )
        }

        // Draw thumb
        canvas.drawCircle(thumbX, thumbY, thumbRadius, thumbPaint)
        canvas.drawCircle(thumbX, thumbY, thumbRadius * 0.6f, thumbInnerPaint)
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

        // Use larger font for single-char punctuation (.,<>) so they're easily visible
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
                    preferredSizes = listOf(20f, 18f, 16f, 14f, 12f)
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

            activeDirection = if (sixSectionMode) {
                when {
                    degrees < 30.0 || degrees >= 330.0 -> Direction.SE
                    degrees < 90.0 -> Direction.S
                    degrees < 150.0 -> Direction.SW
                    degrees < 210.0 -> Direction.NW
                    degrees < 270.0 -> Direction.N
                    else -> Direction.NE
                }
            } else {
                when {
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
}
