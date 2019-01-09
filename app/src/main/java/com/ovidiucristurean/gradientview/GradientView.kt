package com.ovidiucristurean.gradientview

import android.content.Context
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
import android.util.Log
import android.view.View
import com.ovidiucristurean.gradientview.exception.GradientViewInflateException
import com.ovidiucristurean.gradientview.rotationlistener.RotationChangeListener
import com.ovidiucristurean.gradientview.rotationlistener.RotationVectorCollector

class GradientView(context: Context, attributeSet: AttributeSet) : ConstraintLayout(context, attributeSet), RotationChangeListener {
    private val gradientInit: IntArray
    private var gradientDrawable: GradientDrawable
    private val view = View.inflate(context, R.layout.gradient_view, this)
    private var sensorManager = context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
    private val handlerThread = HandlerThread("collectionThread")
    private var rotationVectorCollector: RotationVectorCollector
    private val mainHandler = Handler(Looper.getMainLooper())

    init {
        val attributes = context.obtainStyledAttributes(attributeSet, R.styleable.GradientView)
        try {
            gradientInit = intArrayOf(Color.parseColor(attributes.getString(R.styleable.GradientView_colorStart)),
                    Color.parseColor(attributes.getString(R.styleable.GradientView_colorCenter)),
                    Color.parseColor(attributes.getString(R.styleable.GradientView_colorEnd)))
        } catch (e: RuntimeException) {
            throw GradientViewInflateException("You need to specify startColor, centerColor and endColor in GradientView XML or programmatically")
        }

        gradientDrawable = GradientDrawable(GradientDrawable.Orientation.LEFT_RIGHT, gradientInit)
        view.background = gradientDrawable
        attributes.recycle()

        rotationVectorCollector = RotationVectorCollector(sensorManager, getCollectionHandler())
        rotationVectorCollector.setRotationChangeListener(this)
    }

    fun startAnimation() {
        rotationVectorCollector.registerForUpdates()
    }

    fun stopAnimation() {
        rotationVectorCollector.unregisterFromUpdates()
    }

    override fun onRotationChanged(angle: Float) {
        //map the [-pi,pi] received value to a [0,1] interval with the formula:
        //[A,B]->[a,b] => (val-A)*(b-a)/(B-A)+a
        val transformedValue = (angle + Math.PI) * (1 - 0) / (Math.PI + Math.PI) + 0
        Log.d("TAG", "Transformed value ${transformedValue.round(5)}, for angle $angle")
        updateGradient(transformedValue.toFloat())
    }

    private fun updateGradient(angle: Float) {
        mainHandler.post {
            val sf: ShapeDrawable.ShaderFactory = object : ShapeDrawable.ShaderFactory() {
                override fun resize(width: Int, height: Int): Shader {
                    return LinearGradient(0f, 0f, width.toFloat(), height.toFloat(),
                            gradientInit,
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