package com.vatoo.erick
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View
import kotlin.math.hypot

class JoystickView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    // --- 兼容旧代码遗留（新架构中已不再使用，仅防报错） ---
    var processor: Any? = null

    // --- 核心状态变量 ---
    var isRightSide: Boolean = false
    private var previewText: String = ""

    // --- 尺寸与坐标 ---
    private var centerX = 0f
    private var centerY = 0f
    private var baseRadius = 0f
    private var thumbRadius = 0f

    // 滑块当前的绝对坐标
    private var thumbX = 0f
    private var thumbY = 0f

    // --- 画笔设置 ---
    private val basePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.parseColor("#E0E0E0") // 底座：浅灰色
        style = Paint.Style.FILL
    }

    private val thumbPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.parseColor("#757575") // 滑块：深灰色
        style = Paint.Style.FILL
    }

    private val textPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.parseColor("#1976D2") // 文字：醒目的蓝色
        textSize = 80f // 预览文字大小
        textAlign = Paint.Align.CENTER
        isFakeBoldText = true
    }

    // --- 尺寸初始化 (当 View 第一次测量大小时调用) ---
    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)

        // 计算圆心（View 的正中心）
        centerX = w / 2f
        centerY = h / 2f

        // 留出一点 padding，底座半径取宽高的较小值的一半
        baseRadius = (Math.min(w, h) / 2f) * 0.85f

        // 滑块半径设定为底座半径的 1/3
        thumbRadius = baseRadius / 3f

        // 初始状态下，滑块在正中心
        resetThumb()
    }

    // --- 核心绘制逻辑 ---
    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        // 1. 画底座
        canvas.drawCircle(centerX, centerY, baseRadius, basePaint)

        // 2. 画预览文字（如果是右摇杆且有文字）
        // 放在滑块下面画，这样如果手指在中心，文字会被遮挡一部分，但推开后能清晰看到
        if (isRightSide && previewText.isNotEmpty()) {
            // 计算文字的垂直居中偏移量
            val textOffsetY = (textPaint.descent() + textPaint.ascent()) / 2
            canvas.drawText(previewText, centerX, centerY - textOffsetY, textPaint)
        }

        // 3. 画滑块（跟随手指）
        canvas.drawCircle(thumbX, thumbY, thumbRadius, thumbPaint)
    }

    // --- 供 Service 调用的 UI 更新方法 ---

    /**
     * 更新滑块位置 (传入的是相对于圆心的偏移量 dx, dy)
     */
    fun updateThumb(dx: Float, dy: Float) {
        // 计算手指离圆心的直线距离 (勾股定理)
        val distance = hypot(dx.toDouble(), dy.toDouble()).toFloat()

        // 计算滑块能移动的最大范围（防止滑块滑出底座）
        val maxRadius = baseRadius - thumbRadius

        if (distance > maxRadius) {
            // 如果手指滑出了最大范围，需要把滑块"钳制"在边缘
            // 利用相似三角形原理，按比例缩放 dx 和 dy
            val ratio = maxRadius / distance
            thumbX = centerX + (dx * ratio)
            thumbY = centerY + (dy * ratio)
        } else {
            // 在范围内，直接跟随
            thumbX = centerX + dx
            thumbY = centerY + dy
        }

        // 通知 Android 重新调用 onDraw 画图
        invalidate()
    }

    /**
     * 松手时，滑块弹回圆心
     */
    fun resetThumb() {
        thumbX = centerX
        thumbY = centerY
        invalidate() // 触发重绘
    }

    /**
     * 设置右摇杆的预览文字
     */
    fun setPreviewText(text: String) {
        if (previewText != text) {
            previewText = text
            invalidate() // 只有文字改变时才重绘，节省性能
        }
    }
}
