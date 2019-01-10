package com.ovidiucristurean.gradientview.shader

import android.graphics.LinearGradient
import android.graphics.Shader
import android.graphics.drawable.Drawable
import android.graphics.drawable.PaintDrawable
import android.graphics.drawable.ShapeDrawable
import android.graphics.drawable.shapes.RectShape

class PaintDrawableFactory {

    fun getPaintDrawable(gradientColors: IntArray, angle: Float, xStart: Float, yStart: Float, xEnd: Float, yEnd: Float): Drawable {
        val sf: ShapeDrawable.ShaderFactory = object : ShapeDrawable.ShaderFactory() {
            override fun resize(width: Int, height: Int): Shader {
                return LinearGradient(xStart, yStart, width.toFloat(), height.toFloat(),
                        gradientColors,
                        floatArrayOf(0f, angle, 1f), Shader.TileMode.MIRROR)
            }
        }

        val paintDrawable = PaintDrawable()
        paintDrawable.shape = RectShape()
        paintDrawable.shaderFactory = sf

        return paintDrawable
    }
}