package com.ssong_develop.selectableshadowpositionview

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.res.use
import com.google.android.material.shape.MaterialShapeDrawable
import com.google.android.material.shape.ShapeAppearanceModel
import com.ssong_develop.selectableshadowpositionview.Utils.dpToPixelFloat

/**
 * 함수의 구조를 조금 바꿔야 될거 같아요
 * 지금 현재 layoutParam을 바꾸게 되면 그림이 되게 이상해지는데
 * onSizeChanged메소드가 호출됐을때 이를 다시한번 그려줄 수 있는 무언가가 필요해보입니다.
 *
 * 구조를 다시 한번 생각해보자
 * updateShadow -> top , bottom , start , end shadow update
 * updateBorder
 * updateBackground
 * 함수를 만들어 주고
 *
 * size가 change되면 그에 맞게 처리해준다.
 *
 * 상단 문제 해결
 *
 * 이제 문제는 padding임
 */
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

    private val borderRectF = RectF()
    private val layoutBackgroundRectF = RectF()

    private val porterDuffXferMode = PorterDuffXfermode(PorterDuff.Mode.SRC)

    private var shadowColor = Color.BLACK

    private var shadowStrokeWidth = context.dpToPixelFloat(dp = 8)

    private var borderColor = Color.BLACK
    private var blurRadius = context.dpToPixelFloat(dp = 16)
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

    var cornerRadius by OnChangeProp(context.dpToPixelFloat(16)) {
        updateBackground()
    }

    var layoutBackgroundColor by OnChangeProp(Color.WHITE) {
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

    private fun updateBackground() {
        background =
            MaterialShapeDrawable(ShapeAppearanceModel().withCornerSize(cornerRadius))
    }

    override fun onDraw(canvas: Canvas) {
        if (enableShadow) {
            canvas.apply {
                drawPath(shadowTopPath, shadowPaint)
                drawPath(shadowBottomPath, shadowPaint)
                drawPath(shadowStartPath, shadowPaint)
                drawPath(shadowEndPath, shadowPaint)
            }
        }
        if (enableBorder)
            canvas.drawRoundRect(borderRectF, cornerRadius, cornerRadius, borderPaint)
        canvas.drawRoundRect(layoutBackgroundRectF, cornerRadius, cornerRadius, layoutPaint)

        super.onDraw(canvas)
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        updateShadow()
        updateBorder()
        updateLayout()
        updateBackground()
    }

    private fun updateShadow() {
        val useableWidth = width - (paddingLeft + paddingRight)
        val useableHeight = height - (paddingTop + paddingBottom)
        shadowPaint.apply {
            isAntiAlias = true
            style = Paint.Style.STROKE
            color = shadowColor
            strokeWidth = shadowStrokeWidth
            xfermode = porterDuffXferMode
            maskFilter = blurMaskFilter
        }
        shadowTopPath.apply {
            reset()
            moveTo((useableWidth + shadowEndOffset), shadowTopOffset)
            lineTo(shadowStartOffset, shadowTopOffset)
        }
        shadowStartPath.apply {
            reset()
            moveTo(shadowStartOffset, shadowTopOffset)
            lineTo(shadowStartOffset, (useableHeight + shadowBottomOffset))
        }
        shadowBottomPath.apply {
            reset()
            moveTo(shadowStartOffset, (useableHeight + shadowBottomOffset))
            lineTo((useableWidth + shadowEndOffset), (useableHeight + shadowBottomOffset))
        }
        shadowEndPath.apply {
            reset()
            moveTo((useableWidth + shadowEndOffset), (useableHeight + shadowBottomOffset))
            lineTo((useableWidth + shadowEndOffset), shadowTopOffset)
        }

        invalidate()
    }

    private fun updateBorder() {
        val useableWidth = width - (paddingLeft + paddingRight)
        val useableHeight = height - (paddingTop + paddingBottom)
        borderPaint.apply {
            style = Paint.Style.STROKE
            color = borderColor
            strokeWidth = context.dpToPixelFloat(1)
        }

        borderRectF.apply {
            top = 0f
            left = 0f
            right = useableWidth.toFloat()
            bottom = useableHeight.toFloat()
        }
        invalidate()
    }

    private fun updateLayout() {
        val useableWidth = width - (paddingLeft + paddingRight)
        val useableHeight = height - (paddingTop + paddingBottom)
        layoutPaint.apply {
            style = Paint.Style.FILL
            color = layoutBackgroundColor
            xfermode = porterDuffXferMode
        }
        layoutBackgroundRectF.apply {
            top = 0f
            left = 0f
            right = useableWidth.toFloat()
            bottom = useableHeight.toFloat()
        }
        layoutBackgroundPath.apply {
            reset()
            addRect(layoutBackgroundRectF, Path.Direction.CW)
        }

        invalidate()
    }
}