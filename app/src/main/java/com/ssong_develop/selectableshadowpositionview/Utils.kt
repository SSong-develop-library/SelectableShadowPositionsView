package com.ssong_develop.selectableshadowpositionview

import android.content.Context
import kotlin.math.roundToInt

object Utils {
    fun Context.dpToPixel(dp : Int) : Int = (dp * resources.displayMetrics.density).roundToInt()

    fun Context.dpToPixelFloat(dp : Int) : Float =
        (dp * resources.displayMetrics.density).roundToInt().toFloat()
}
