package com.shakelog.sdk

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.util.Log
import com.shakelog.sdk.utils.ShakeConstants
import kotlin.math.sqrt

class ShakeDetector(private val context: Context) : SensorEventListener{

    val sensorManager = context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
    val accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)

    private var setOnShakeListener: (() -> Unit)? = null
    fun setOnShakeListener(listener: () -> Unit) {
        setOnShakeListener = listener
    }

    var lastShakeTime = 0L

    fun start() {
        sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_NORMAL)
    }

    fun stop() {
        sensorManager.unregisterListener(this)
    }

    override fun onSensorChanged(event: SensorEvent?) {
        if(event == null) return

        if(event.sensor.type == Sensor.TYPE_ACCELEROMETER) {
            val x = event.values[0]
            val y = event.values[1]
            val z = event.values[2]

            // Normalize the acceleration values to gravity
            val gX = x / SensorManager.GRAVITY_EARTH
            val gY = y / SensorManager.GRAVITY_EARTH
            val gZ = z / SensorManager.GRAVITY_EARTH

            // Compute gForce
            val gForce = sqrt((gX * gX + gY * gY + gZ * gZ).toDouble()).toFloat()

            if(gForce > ShakeConstants.SHAKE_THRESHOLD_GRAVITY) {
                if(System.currentTimeMillis() - lastShakeTime < ShakeConstants.SHAKE_SLOP_TIME_MS)
                    return

                // Shake detected
                //TODO: Check if amount of time since last shake is more than SHAKE_COUNT_RESET_TIME_MS
                lastShakeTime = System.currentTimeMillis()
                Log.d("ShakeLog", "Shake detected with gForce: $gForce")

                setOnShakeListener?.invoke()
            }
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
        // Not need to implemented
    }
}