package com.osmiumai.app.views

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import android.view.animation.DecelerateInterpolator
import kotlin.math.sqrt

class LineChartView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private val linePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.parseColor("#D4AF37")
        strokeWidth = 4f
        style = Paint.Style.STROKE
        strokeCap = Paint.Cap.ROUND
        strokeJoin = Paint.Join.ROUND
    }

    private val fillPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
    }

    private val pointPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.WHITE
        style = Paint.Style.FILL
    }

    private val pointStrokePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.parseColor("#D4AF37")
        strokeWidth = 4f
        style = Paint.Style.STROKE
    }

    private var dataPoints = listOf(150f, 140f, 180f, 255f)
    private var animatedDataPoints = dataPoints.toList()
    private var animationProgress = 1f
    private var selectedPointIndex: Int? = null
    private var points = listOf<PointF>()
    
    private val popupPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.parseColor("#D4AF37")
        style = Paint.Style.FILL
    }
    
    private val popupTextPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.WHITE
        textSize = 20f
        textAlign = Paint.Align.CENTER
        typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
    }

    fun setData(newData: List<Float>, animate: Boolean = true) {
        val oldData = dataPoints
        dataPoints = newData
        
        if (animate) {
            ValueAnimator.ofFloat(0f, 1f).apply {
                duration = 500
                interpolator = DecelerateInterpolator()
                addUpdateListener { animator ->
                    animationProgress = animator.animatedValue as Float
                    animatedDataPoints = dataPoints.mapIndexed { index, value ->
                        val oldValue = oldData.getOrElse(index) { value }
                        oldValue + (value - oldValue) * animationProgress
                    }
                    invalidate()
                }
                start()
            }
        } else {
            animatedDataPoints = dataPoints
            invalidate()
        }
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                val touchX = event.x
                val touchY = event.y
                val touchRadius = 50f
                
                val clickedIndex = points.indexOfFirst { point ->
                    val distance = sqrt((touchX - point.x) * (touchX - point.x) + (touchY - point.y) * (touchY - point.y))
                    distance <= touchRadius
                }
                
                selectedPointIndex = if (clickedIndex != -1) {
                    if (selectedPointIndex == clickedIndex) null else clickedIndex
                } else {
                    null
                }
                
                invalidate()
                return true
            }
        }
        return super.onTouchEvent(event)
    }
    
    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        val w = width.toFloat()
        val h = height.toFloat()
        val maxValue = 300f
        val padding = 20f

        points = animatedDataPoints.mapIndexed { index, value ->
            val x = padding + (w - 2 * padding) * index / (animatedDataPoints.size - 1)
            val y = h - padding - (h - 2 * padding) * (value / maxValue)
            PointF(x, y)
        }

        fillPaint.shader = LinearGradient(
            0f, 0f, 0f, h,
            Color.parseColor("#33D4AF37"),
            Color.parseColor("#11D4AF37"),
            Shader.TileMode.CLAMP
        )

        val fillPath = Path()
        fillPath.moveTo(points[0].x, h - padding)
        fillPath.lineTo(points[0].x, points[0].y)
        
        for (i in 0 until points.size - 1) {
            val p1 = points[i]
            val p2 = points[i + 1]
            val controlX = (p1.x + p2.x) / 2
            fillPath.cubicTo(controlX, p1.y, controlX, p2.y, p2.x, p2.y)
        }
        
        fillPath.lineTo(points.last().x, h - padding)
        fillPath.close()
        canvas.drawPath(fillPath, fillPaint)

        val linePath = Path()
        linePath.moveTo(points[0].x, points[0].y)
        
        for (i in 0 until points.size - 1) {
            val p1 = points[i]
            val p2 = points[i + 1]
            val controlX = (p1.x + p2.x) / 2
            linePath.cubicTo(controlX, p1.y, controlX, p2.y, p2.x, p2.y)
        }
        
        canvas.drawPath(linePath, linePaint)

        points.forEachIndexed { index, point ->
            canvas.drawCircle(point.x, point.y, 8f, pointPaint)
            canvas.drawCircle(point.x, point.y, 8f, pointStrokePaint)
            
            if (selectedPointIndex == index) {
                val value = dataPoints[index].toInt()
                val text = value.toString()
                val textBounds = Rect()
                popupTextPaint.getTextBounds(text, 0, text.length, textBounds)
                
                val popupWidth = textBounds.width() + 20f
                val popupHeight = textBounds.height() + 16f
                val popupX = point.x
                val popupY = point.y - 35f
                
                val rect = RectF(
                    popupX - popupWidth / 2,
                    popupY - popupHeight / 2,
                    popupX + popupWidth / 2,
                    popupY + popupHeight / 2
                )
                canvas.drawRoundRect(rect, 8f, 8f, popupPaint)
                
                canvas.drawText(text, popupX, popupY + textBounds.height() / 2, popupTextPaint)
            }
        }
    }
}
