package com.osmiumai.app.views

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View

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

    private val dataPoints = listOf(150f, 140f, 180f, 255f)

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        val w = width.toFloat()
        val h = height.toFloat()
        val maxValue = 300f
        val padding = 20f

        val points = dataPoints.mapIndexed { index, value ->
            val x = padding + (w - 2 * padding) * index / (dataPoints.size - 1)
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

        points.forEach { point ->
            canvas.drawCircle(point.x, point.y, 8f, pointPaint)
            canvas.drawCircle(point.x, point.y, 8f, pointStrokePaint)
        }
    }
}
