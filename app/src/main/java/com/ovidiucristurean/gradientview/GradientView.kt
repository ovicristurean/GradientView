package com.ovidiucristurean.gradientview

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.support.constraint.ConstraintLayout
import android.util.AttributeSet
import android.view.View

class GradientView(context: Context, attributeSet: AttributeSet) : ConstraintLayout(context, attributeSet) {
    private var switch = false
    private val gradientInit = intArrayOf(Color.parseColor("#FF00FF00"), Color.parseColor("#c4ff00"))
    private val gradientEnd = intArrayOf(Color.parseColor("#c4ff00"), Color.parseColor("#FF7BFF00"))
    private val state = arrayOf(intArrayOf(android.R.attr.state_enabled))
    var gradientDrawable = GradientDrawable(GradientDrawable.Orientation.LEFT_RIGHT, gradientInit)
    val view = View.inflate(context, R.layout.gradient_view, this)

    init {
        val attributes = context.obtainStyledAttributes(attributeSet, R.styleable.GradientView)
        view.background = gradientDrawable
        attributes.recycle()
    }

    fun changeGradient() {
        switch = !switch
        if (switch) {
            gradientDrawable.colors = gradientEnd
        } else {
            gradientDrawable.colors = gradientInit
        }
    }
}