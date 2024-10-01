package com.example.healthapplication.utils

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager

fun setupAccelerometerListener(
    context: Context,
    accelValuesX: MutableList<Float>,
    accelValuesY: MutableList<Float>,
    accelValuesZ: MutableList<Float>
): SensorEventListener {
    val sensorManager = context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
    val accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)

    return object : SensorEventListener {
        override fun onSensorChanged(event: SensorEvent?) {
            event?.let {
                accelValuesX.add(it.values[0])
                accelValuesY.add(it.values[1])
                accelValuesZ.add(it.values[2])
            }
        }

        override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}
    }
}