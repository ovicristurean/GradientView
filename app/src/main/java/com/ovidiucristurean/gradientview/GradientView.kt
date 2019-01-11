package com.ovidiucristurean.gradientview

import android.content.Context
import android.content.res.TypedArray
import android.graphics.Color
import android.graphics.drawable.shapes.ArcShape
import android.graphics.drawable.shapes.OvalShape
import android.graphics.drawable.shapes.RectShape
import android.graphics.drawable.shapes.Shape
import android.hardware.SensorManager
import android.os.Handler
import android.os.HandlerThread
import android.os.Looper
import android.support.constraint.ConstraintLayout
import android.util.AttributeSet
import com.ovidiucristurean.gradientview.exception.GradientViewInflateException
import com.ovidiucristurean.gradientview.math.Mapper
import com.ovidiucristurean.gradientview.math.SimpleLinearMapper
import com.ovidiucristurean.gradientview.rotationlistener.RotationChangeListener
import com.ovidiucristurean.gradientview.rotationlistener.RotationVectorCollector
import com.ovidiucristurean.gradientview.shader.PaintDrawableFactory

class GradientView(context: Context, attributeSet: AttributeSet) : ConstraintLayout(context, attributeSet), RotationChangeListener {
    private var gradientColors = intArrayOf(0, 0, 0)
    private var sensorManager = context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
    private var rotationVectorCollector: RotationVectorCollector? = null
    private val mainHandler = Handler(Looper.getMainLooper())
    private val collectionHandler = getCollectionHandler()
    private val attributes: TypedArray = context.obtainStyledAttributes(attributeSet, R.styleable.GradientView)
    private val mapper: Mapper<Double> = SimpleLinearMapper()
    //TODO retrieve the desired shape from attributes
    private val paintDrawableFactory = PaintDrawableFactory(getGradientShape())

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

    fun setShape(shape: Shape) {
        paintDrawableFactory.setShape(shape)
    }

    fun startAnimation() {
        try {
            if (!isStartColorSet) {
                gradientColors[0] = Color.parseColor(attributes.getString(R.styleable.GradientView_colorStart))
            }
            if (!isCenterColorSet) {
                gradientColors[1] = Color.parseColor(attributes.getString(R.styleable.GradientView_colorCenter))
            }
            if (!isEndColorSet)
                gradientColors[2] = Color.parseColor(attributes.getString(R.styleable.GradientView_colorEnd))
        } catch (e: RuntimeException) {
            throw GradientViewInflateException("You need to specify startColor, centerColor and endColor in GradientView XML or programmatically, ${e.message}")
        }

        rotationVectorCollector = RotationVectorCollector(sensorManager, collectionHandler)
        rotationVectorCollector?.let {
            it.setRotationChangeListener(this)
            it.registerForUpdates()
        }
    }

    fun stopAnimation() {
        rotationVectorCollector?.unregisterFromUpdates()
    }

    override fun onRotationChanged(angle: Float) {
        mainHandler.post {
            //map the [-pi,pi] received value to a value in the [0,1] interval
            val transformedAngle = mapper.map(angle.toDouble(), -Math.PI, Math.PI, 0.0, 1.0).toFloat()
            background = paintDrawableFactory.getPaintDrawable(gradientColors, transformedAngle)
        }
    }

    private fun getCollectionHandler(): Handler {
        val handlerThread = HandlerThread("collectionThread")
        handlerThread.start()
        return Handler(handlerThread.looper)
    }

    /**
     * default shape is rectangular
     */
    private fun getGradientShape(): Shape {
        return when (attributes.getInt(R.styleable.GradientView_shape, 9)) {
            0 -> RectShape()
            1 -> OvalShape()
            2 -> ArcShape(45F, 180F)
            else -> RectShape()
        }
    }
}