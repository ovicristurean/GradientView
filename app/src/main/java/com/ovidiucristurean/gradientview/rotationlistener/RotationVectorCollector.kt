package com.ovidiucristurean.gradientview.rotationlistener

import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Handler
import android.util.Log
import kotlin.math.round

class RotationVectorCollector(private val sensorManager: SensorManager, private val collectionHandler: Handler) : SensorEventListener {
    private var rotationChangeListener: RotationChangeListener? = null
    private val mRotationMatrixS = FloatArray(16)
    private val mOrientationS = FloatArray(3)

    fun setRotationChangeListener(rotationChangeListener: RotationChangeListener) {
        this.rotationChangeListener = rotationChangeListener
    }

    fun registerForUpdates() {
        sensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR)?.let { sensorManager.registerListener(this, it, 100, collectionHandler) }
    }

    fun unregisterFromUpdates() {
        sensorManager.unregisterListener(this)
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) = Unit

    override fun onSensorChanged(event: SensorEvent) {
        val array = event.values
        SensorManager.getRotationMatrixFromVector(mRotationMatrixS, array)
        SensorManager.getOrientation(mRotationMatrixS, mOrientationS)
        Log.d("TAG", "Received sensor ${event.sensor.type}, values: ${-mOrientationS[1]}, ${mOrientationS[2]}, ${mOrientationS[0]}")
        rotationChangeListener?.onRotationChanged(mOrientationS[2].round(5))
    }

    private fun Float.round(decimals: Int): Float {
        var multiplier = 1.0
        repeat(decimals) { multiplier *= 10 }
        return (round(this * multiplier) / multiplier).toFloat()
    }
}