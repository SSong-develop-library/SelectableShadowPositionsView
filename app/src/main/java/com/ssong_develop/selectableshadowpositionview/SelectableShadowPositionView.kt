package com.ssong_develop.selectableshadowpositionview

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.*
import android.util.AttributeSet
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.res.use
import com.google.android.material.shape.MaterialShapeDrawable
import com.google.android.material.shape.ShapeAppearanceModel
import com.ssong_develop.selectableshadowpositionview.Utils.dpToPixelFloat
import java.lang.Float.MIN_VALUE

class SelectableShadowPositionView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null
) : ConstraintLayout(context, attrs) {
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

    private var enableShadowTop = true
    private var enableShadowBottom = true
    private var enableShadowStart = true
    private var enableShadowEnd = true

    private val blurMaskFilter = BlurMaskFilter(blurRadius, BlurMaskFilter.Blur.NORMAL)

    var cornerRadius by OnChangeProp(context.dpToPixelFloat(16)){
        updateBackground()
    }

    var layoutBackgroundColor by OnChangeProp(Color.WHITE){
        updateBackground()
    }
    init {
        if (attrs != null)
            getStyleableAttrs(attrs)
        updateBackground()
    }

    private fun getStyleableAttrs(attrs: AttributeSet) {
        context.obtainStyledAttributes(attrs, R.styleable.SelectableShadowPositionView, 0, 0).use {
            shadowTopOffset = it.getDimension(
                R.styleable.SelectableShadowPositionView_shadow_top_offset,
                context.dpToPixelFloat(1)
            )
            shadowBottomOffset = it.getDimension(
                R.styleable.SelectableShadowPositionView_shadow_bottom_offset,
                context.dpToPixelFloat(1)
            )
            shadowStartOffset = it.getDimension(
                R.styleable.SelectableShadowPositionView_shadow_start_offset,
                context.dpToPixelFloat(1)
            )
            shadowEndOffset = it.getDimension(
                R.styleable.SelectableShadowPositionView_shadow_end_offset,
                context.dpToPixelFloat(1)
            )
            shadowStrokeWidth = it.getDimension(
                R.styleable.SelectableShadowPositionView_shadow_stroke_width,
                context.dpToPixelFloat(4)
            )
            cornerRadius = it.getDimension(
                R.styleable.SelectableShadowPositionView_corner_radius,
                context.dpToPixelFloat(4)
            )
            blurRadius = it.getDimension(
                R.styleable.SelectableShadowPositionView_blur_radius,
                context.dpToPixelFloat(16)
            )
            shadowColor = it.getColor(
                R.styleable.SelectableShadowPositionView_shadow_color,
                Color.BLACK
            )
            layoutBackgroundColor = it.getColor(
                R.styleable.SelectableShadowPositionView_card_background_color,
                Color.WHITE
            )
            borderColor = it.getColor(
                R.styleable.SelectableShadowPositionView_border_color,
                Color.BLACK
            )
            enableShadow = it.getBoolean(
                R.styleable.SelectableShadowPositionView_enable_shadow,
                true
            )
            enableBorder = it.getBoolean(
                R.styleable.SelectableShadowPositionView_enable_border,
                false
            )
            enableShadowTop = it.getBoolean(
                R.styleable.SelectableShadowPositionView_enable_shadow_top,
                true
            )
            enableShadowBottom = it.getBoolean(
                R.styleable.SelectableShadowPositionView_enable_shadow_bottom,
                true
            )
            enableShadowStart = it.getBoolean(
                R.styleable.SelectableShadowPositionView_enable_shadow_start,
                true
            )
            enableShadowEnd = it.getBoolean(
                R.styleable.SelectableShadowPositionView_enable_shadow_end,
                true
            )
        }
    }

    private fun updateBackground(){
        background = MaterialShapeDrawable(ShapeAppearanceModel().withCornerSize(cornerRadius)).apply {
            fillColor = ColorStateList.valueOf(layoutBackgroundColor)
        }
    }

    override fun dispatchDraw(canvas: Canvas) {
        /*clipRoundCorner(canvas)*/
        super.dispatchDraw(canvas)
    }

    override fun onDraw(canvas: Canvas) {
        if (enableShadow) {
            initializeShadowPaint()
            drawShadow(canvas)
        }
        drawLayoutBackground(canvas)
        if (enableBorder)
            drawBorder(canvas)
        super.onDraw(canvas)
    }

/*    private fun clipRoundCorner(canvas: Canvas) {
        clipPath.reset()

        clipRectF.apply {
            top = 0f
            left = 0f
            right = canvas.width.toFloat()
            bottom = canvas.height.toFloat()
        }
        clipPath.addRoundRect(clipRectF, cornerRadius, cornerRadius, Path.Direction.CW)
    }*/

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