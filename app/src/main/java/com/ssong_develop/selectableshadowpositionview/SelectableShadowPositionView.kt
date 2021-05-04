package com.ssong_develop.selectableshadowpositionview

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.res.use
import com.ssong_develop.selectableshadowpositionview.Utils.dpToPixelFloat
import java.lang.Float.MIN_VALUE

class SelectableShadowPositionView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null
) : ConstraintLayout(context, attrs) {
    private val defaultRectBackgroundColor = Color.WHITE

    private val shadowPaint = Paint()
    private val borderPaint = Paint()
    private val layoutPaint = Paint()

    private val shadowTopPath = Path()
    private val shadowBottomPath = Path()
    private val shadowStartPath = Path()
    private val shadowEndPath = Path()

    private val layoutBackgroundPath = Path()
    private val clipPath = Path()

    private val borderRectF = RectF()
    private val clipRectF = RectF()
    private val layoutBackgroundRectF = RectF()

    private val porterDuffXferMode = PorterDuffXfermode(PorterDuff.Mode.SRC)

    private var shadowColor = Color.BLACK

    private var shadowStrokeWidth = context.dpToPixelFloat(dp = 8)

    private var borderColor = Color.BLACK
    private var blurRadius = context.dpToPixelFloat(dp = 16)
    private var shadowStartY = MIN_VALUE
    private var shadowStartOffset = 0f
    private var shadowEndOffset = 0f
    private var shadowTopOffset = 0f
    private var shadowBottomOffset = 0f

    private var enableShadow = true
    private var enableBorder = false

    private var enableShadowTop = false
    private var enableShadowBottom = true
    private var enableShadowStart = true
    private var enableShadowEnd = true
    private var borderHeight = 0f
    private var cornerRadius = context.dpToPixelFloat(dp = 16)

    private val blurMaskFilter = BlurMaskFilter(blurRadius, BlurMaskFilter.Blur.NORMAL)

    init {
        setBackgroundColor(defaultRectBackgroundColor)
        if (attrs != null)
            getStyleableAttrs(attrs)
    }

    private fun getStyleableAttrs(attrs: AttributeSet) {
        context.obtainStyledAttributes(attrs, R.styleable.SelectableShadowPositionView, 0, 0).use {
            shadowTopOffset = it.getDimension(
                R.styleable.SelectableShadowPositionView_shadowTopOffset,
                context.dpToPixelFloat(0)
            )
            shadowBottomOffset = it.getDimension(
                R.styleable.SelectableShadowPositionView_shadowBottomOffset,
                context.dpToPixelFloat(0)
            )
            shadowStartOffset = it.getDimension(
                R.styleable.SelectableShadowPositionView_shadowStartOffset,
                context.dpToPixelFloat(0)
            )
            shadowEndOffset = it.getDimension(
                R.styleable.SelectableShadowPositionView_shadowEndOffset,
                context.dpToPixelFloat(0)
            )
            shadowStartY = it.getDimension(
                R.styleable.SelectableShadowPositionView_shadowStartY,
                MIN_VALUE
            )
            shadowStrokeWidth = it.getDimension(
                R.styleable.SelectableShadowPositionView_shadowStrokeWidth,
                context.dpToPixelFloat(4)
            )
            cornerRadius = it.getDimension(
                R.styleable.SelectableShadowPositionView_cornerRadius,
                context.dpToPixelFloat(4)
            )
            blurRadius = it.getDimension(
                R.styleable.SelectableShadowPositionView_blurRadius,
                context.dpToPixelFloat(16)
            )
            borderHeight = it.getDimension(
                R.styleable.SelectableShadowPositionView_borderHeight,
                0f
            )
            shadowColor = it.getColor(
                R.styleable.SelectableShadowPositionView_shadowColor,
                Color.BLACK
            )
            borderColor = it.getColor(
                R.styleable.SelectableShadowPositionView_borderColor,
                Color.BLACK
            )
            enableShadow = it.getBoolean(
                R.styleable.SelectableShadowPositionView_enableShadow,
                true
            )
            enableBorder = it.getBoolean(
                R.styleable.SelectableShadowPositionView_enableBorder,
                false
            )
            enableShadowTop = it.getBoolean(
                R.styleable.SelectableShadowPositionView_enableShadowTop,
                false
            )
            enableShadowBottom = it.getBoolean(
                R.styleable.SelectableShadowPositionView_enableShadowBottom,
                true
            )
            enableShadowStart = it.getBoolean(
                R.styleable.SelectableShadowPositionView_enableShadowStart,
                true
            )
            enableShadowEnd = it.getBoolean(
                R.styleable.SelectableShadowPositionView_enableShadowEnd,
                true
            )
        }
    }

    override fun dispatchDraw(canvas: Canvas) {
        clipRoundCorner(canvas)
        super.dispatchDraw(canvas)
    }

    override fun onDraw(canvas: Canvas) {
        if (enableShadow) {
            initializeShadowPaint()
            initializeShadowOffset()
            drawShadow(canvas)
        }
        drawLayoutBackground(canvas)
        if (enableBorder)
            drawBorder(canvas)
        super.onDraw(canvas)
    }

    private fun clipRoundCorner(canvas: Canvas) {
        clipPath.reset()

        clipRectF.apply {
            top = 0f
            left = 0f
            right = canvas.width.toFloat()
            bottom = canvas.height.toFloat()
        }
        clipPath.addRoundRect(clipRectF, cornerRadius, cornerRadius, Path.Direction.CW)
    }

    private fun initializeShadowPaint() {
        shadowPaint.apply {
            isAntiAlias = true
            style = Paint.Style.STROKE
            color = shadowColor
            strokeWidth = shadowStrokeWidth
            xfermode = porterDuffXferMode
            maskFilter = blurMaskFilter
        }
    }

    private fun initializeShadowOffset() {
        shadowTopOffset = context.dpToPixelFloat(6)
        shadowBottomOffset = context.dpToPixelFloat(-2)
        shadowStartOffset = context.dpToPixelFloat(2)
        shadowEndOffset = context.dpToPixelFloat(-2)
    }

    private fun drawShadow(canvas: Canvas) {
        if (enableShadowTop) drawShadowTop(canvas)
        if (enableShadowBottom) drawShadowBottom(canvas)
        if (enableShadowStart) drawShadowStart(canvas)
        if (enableShadowEnd) drawShadowEnd(canvas)
    }

    private fun drawShadowTop(canvas: Canvas) {
        shadowTopPath.apply {
            reset()
            moveTo((width + shadowEndOffset), (shadowStartY + shadowTopOffset))
            lineTo(shadowStartOffset, (shadowStartY + shadowTopOffset))
        }
        canvas.drawPath(shadowTopPath, shadowPaint)
        canvas.save()
    }

    private fun drawShadowStart(canvas: Canvas) {
        shadowStartPath.apply {
            reset()
            moveTo(shadowStartOffset, (shadowStartY + shadowTopOffset))
            lineTo(shadowStartOffset, (height + shadowBottomOffset))
        }
        canvas.drawPath(shadowStartPath, shadowPaint)
        canvas.save()
    }

    private fun drawShadowBottom(canvas: Canvas) {
        shadowBottomPath.apply {
            reset()
            moveTo(shadowStartOffset, (height + shadowBottomOffset))
            lineTo((width + shadowEndOffset), (height + shadowBottomOffset))
        }
        canvas.drawPath(shadowBottomPath, shadowPaint)
        canvas.save()
    }

    private fun drawShadowEnd(canvas: Canvas) {
        shadowEndPath.apply {
            reset()
            moveTo((width + shadowEndOffset), (height + shadowBottomOffset))
            lineTo((width + shadowEndOffset), shadowStartY + shadowTopOffset)
        }
        canvas.drawPath(shadowEndPath, shadowPaint)
        canvas.save()
    }

    private fun drawLayoutBackground(canvas: Canvas) {
        layoutPaint.apply {
            style = Paint.Style.FILL
            color = Color.WHITE
            xfermode = porterDuffXferMode
        }

        layoutBackgroundRectF.apply {
            top = 0f
            left = 0f
            right = width.toFloat()
            bottom = height.toFloat()
        }

        layoutBackgroundPath.apply {
            reset()
            addRect(layoutBackgroundRectF, Path.Direction.CW)
        }

        canvas.drawRoundRect(layoutBackgroundRectF, cornerRadius, cornerRadius, layoutPaint)
    }

    private fun drawBorder(canvas: Canvas) {
        borderPaint.apply {
            style = Paint.Style.STROKE
            color = borderColor
            strokeWidth = context.dpToPixelFloat(1)
        }

        borderRectF.apply {
            top = 0f
            left = 0f
            right = width.toFloat()
            bottom = height.toFloat()
        }

        canvas.drawRoundRect(borderRectF, cornerRadius, cornerRadius, borderPaint)
    }

}