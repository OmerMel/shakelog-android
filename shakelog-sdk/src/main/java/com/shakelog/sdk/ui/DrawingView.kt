package com.shakelog.sdk.ui

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Path
import android.graphics.Paint
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View

class DrawingView(context: Context, attrs: AttributeSet?) : View(context, attrs) {

    private val drawPath = Path()
    private val drawPaint = Paint()

    init {
        setupPaint()
    }

    private fun setupPaint() {
        drawPaint.color = Color.RED
        drawPaint.isAntiAlias = true
        drawPaint.strokeWidth = 10f
        drawPaint.style = Paint.Style.STROKE
        drawPaint.strokeJoin = Paint.Join.ROUND
        drawPaint.strokeCap = Paint.Cap.ROUND
    }

    fun clearCanvas() {
        drawPath.reset()
        invalidate()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        canvas.drawPath(drawPath, drawPaint)
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent): Boolean {
        val touchX = event.x
        val touchY = event.y

        var shouldInvalidate = false

        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                //Start drawing
                drawPath.moveTo(touchX, touchY)

                shouldInvalidate = true
            }
            MotionEvent.ACTION_MOVE -> {
                //Continue drawing
                drawPath.lineTo(touchX, touchY)

                shouldInvalidate = true
            }
            MotionEvent.ACTION_UP -> {
                //Finish drawing
                drawPath.lineTo(touchX, touchY)

                shouldInvalidate = true
            }
        }

        if (shouldInvalidate) {
            invalidate()
        }

        return true
    }
}