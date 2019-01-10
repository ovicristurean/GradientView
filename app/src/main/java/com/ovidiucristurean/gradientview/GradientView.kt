package com.ovidiucristurean.gradientview

import android.content.Context
import android.content.res.TypedArray
import android.graphics.Color
import android.graphics.LinearGradient
import android.graphics.Shader
import android.graphics.drawable.GradientDrawable
import android.graphics.drawable.PaintDrawable
import android.graphics.drawable.ShapeDrawable
import android.graphics.drawable.shapes.RectShape
import android.hardware.SensorManager
import android.os.Handler
import android.os.HandlerThread
import android.os.Looper
import android.support.constraint.ConstraintLayout
import android.util.AttributeSet
import android.view.View
import com.ovidiucristurean.gradientview.exception.GradientViewInflateException
import com.ovidiucristurean.gradientview.math.Mapper
import com.ovidiucristurean.gradientview.math.SimpleLinearMapper
import com.ovidiucristurean.gradientview.rotationlistener.RotationChangeListener
import com.ovidiucristurean.gradientview.rotationlistener.RotationVectorCollector

class GradientView(context: Context, attributeSet: AttributeSet) : ConstraintLayout(context, attributeSet), RotationChangeListener {
    private var gradientColors = intArrayOf(0, 0, 0)
    private var gradientDrawable: GradientDrawable? = null
    private val view = View.inflate(context, R.layout.gradient_view, this)
    private var sensorManager = context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
    private val handlerThread = HandlerThread("collectionThread")
    private var rotationVectorCollector: RotationVectorCollector? = null
    private val mainHandler = Handler(Looper.getMainLooper())
    private val attributes: TypedArray = context.obtainStyledAttributes(attributeSet, R.styleable.GradientView)
    private val mapper: Mapper<Double> = SimpleLinearMapper()

    private var isStartColorSet = false
    private var isCenterColorSet = false
    private var isEndColorSet = false

    fun setStartColor(color: Int) {
        gradientColors[0] = color
        isStartColorSet = true
    }

    fun setCenterColor(color: Int) {
        gradientColors[1] = color
        isCenterColorSet = true
    }

    fun setEndColor(color: Int) {
        gradientColors[2] = color
        isEndColorSet = true
    }

    fun startAnimation() {
        try {
            val startColor = if (isStartColorSet) {
                gradientColors[0]
            } else {
                Color.parseColor(attributes.getString(R.styleable.GradientView_colorStart))
            }

            val centerColor = if (isCenterColorSet) {
                gradientColors[1]
            } else {
                Color.parseColor(attributes.getString(R.styleable.GradientView_colorCenter))
            }

            val endColor = if (isEndColorSet) {
                gradientColors[2]
            } else {
                Color.parseColor(attributes.getString(R.styleable.GradientView_colorEnd))
            }

            gradientColors = intArrayOf(startColor, centerColor, endColor)
        } catch (e: RuntimeException) {
            throw GradientViewInflateException("You need to specify startColor, centerColor and endColor in GradientView XML or programmatically")
        }
        gradientDrawable = GradientDrawable(GradientDrawable.Orientation.LEFT_RIGHT, gradientColors)
        view.background = gradientDrawable
        attributes.recycle()

        rotationVectorCollector = RotationVectorCollector(sensorManager, getCollectionHandler())
        rotationVectorCollector?.let {
            it.setRotationChangeListener(this)
            it.registerForUpdates()
        }
    }

    fun stopAnimation() {
        rotationVectorCollector?.unregisterFromUpdates()
    }

    override fun onRotationChanged(angle: Float) {
        //map the [-pi,pi] received value to a [0,1]
        updateGradient(mapper.map(angle.toDouble(), -Math.PI, Math.PI, 0.0, 1.0).toFloat())
    }

    private fun updateGradient(angle: Float) {
        mainHandler.post {
            val sf: ShapeDrawable.ShaderFactory = object : ShapeDrawable.ShaderFactory() {
                override fun resize(width: Int, height: Int): Shader {
                    return LinearGradient(0f, 0f, width.toFloat(), height.toFloat(),
                            gradientColors,
                            floatArrayOf(0f, angle, 1f), Shader.TileMode.MIRROR)
                }
            }

            val p = PaintDrawable()
            p.shape = RectShape()
            p.shaderFactory = sf
            background = p
        }
    }

    private fun getCollectionHandler(): Handler {
        handlerThread.start()
        return Handler(handlerThread.looper)
    }

    private fun Double.round(decimals: Int): Double {
        var multiplier = 1.0
        repeat(decimals) { multiplier *= 10 }
        return (kotlin.math.round(this * multiplier) / multiplier)
    }
}