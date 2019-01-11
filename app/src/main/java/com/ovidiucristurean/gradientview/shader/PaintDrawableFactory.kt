package com.ovidiucristurean.gradientview.shader

import android.graphics.LinearGradient
import android.graphics.Shader
import android.graphics.drawable.Drawable
import android.graphics.drawable.PaintDrawable
import android.graphics.drawable.ShapeDrawable
import android.graphics.drawable.shapes.Shape

class PaintDrawableFactory(private var shape: Shape) {

    fun getPaintDrawable(gradientColors: IntArray, angle: Float): Drawable {
        val sf: ShapeDrawable.ShaderFactory = object : ShapeDrawable.ShaderFactory() {
            override fun resize(width: Int, height: Int): Shader {
                return LinearGradient(0f, 0f, width.toFloat(), height.toFloat(),
                        gradientColors,
                        floatArrayOf(0f, angle, 1f), Shader.TileMode.MIRROR)
            }
        }

        val paintDrawable = PaintDrawable()
        paintDrawable.shape = shape
        paintDrawable.shaderFactory = sf

        return paintDrawable
    }

    fun setShape(shape: Shape) {
        this.shape = shape
    }
}