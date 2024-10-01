package com.example.healthapplication.utils

import android.util.Log
import kotlin.math.abs
import kotlin.math.pow
import kotlin.math.sqrt

// Function to calculate respiratory rate from accelerometer data
fun respiratoryRateCalculator(
    accelValuesX: MutableList<Float>,
    accelValuesY: MutableList<Float>,
    accelValuesZ: MutableList<Float>
): Int {
    Log.d("RespiratoryRateCalculator", "Starting calculation")

    var previousValue = 10f
    var k = 0

    for (i in 11 until accelValuesY.size) {
        val currentValue = sqrt(
            accelValuesZ[i].toDouble().pow(2.0) + accelValuesX[i].toDouble().pow(2.0) + accelValuesY[i].toDouble().pow(2.0)
        ).toFloat()

        if (abs(previousValue - currentValue) > 0.15) {
            k++
        }
        previousValue = currentValue
    }

    val rate = (k.toDouble() / 45.00) // Calculate breaths per second
    val respiratoryRate = (rate * 60).toInt() // Convert to breaths per minute
    Log.d("RespiratoryRateCalculator", "Respiratory rate calculated: $respiratoryRate breaths per minute")

    return respiratoryRate
}