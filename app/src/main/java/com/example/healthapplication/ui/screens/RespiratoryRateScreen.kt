package com.example.healthapplication.ui.screens

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import kotlinx.coroutines.delay
import com.example.healthapplication.utils.respiratoryRateCalculator

@Composable
fun RespiratoryRateScreen(navController: NavController) {
    val context = LocalContext.current
    var respiratoryRate by remember { mutableStateOf<Int?>(null) }
    var isMonitoring by remember { mutableStateOf(false) }
    var isRespiratoryRateCalculated by remember { mutableStateOf(false) }
    var isCalculating by remember { mutableStateOf(false) }
    val accelValuesX = remember { mutableListOf<Float>() }
    val accelValuesY = remember { mutableListOf<Float>() }
    val accelValuesZ = remember { mutableListOf<Float>() }

    val sensorManager = context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
    val accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)

    val accelerometerListener = object : SensorEventListener {
        override fun onSensorChanged(event: SensorEvent?) {
            event?.let {
                accelValuesX.add(it.values[0])
                accelValuesY.add(it.values[1])
                accelValuesZ.add(it.values[2])
            }
        }

        override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}
    }

    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        if (isMonitoring) {
            // Register accelerometer listener when monitoring starts
            sensorManager.registerListener(accelerometerListener, accelerometer, SensorManager.SENSOR_DELAY_NORMAL)

            LaunchedEffect(Unit) {
                isCalculating = true
                Log.d("RespiratoryRateScreen", "Collecting accelerometer data for 45 seconds")
                delay(45_000) // Collect accelerometer data for 45 seconds
                isMonitoring = false // Stop monitoring

                // Unregister listener after data collection
                sensorManager.unregisterListener(accelerometerListener)

                // Calculate the respiratory rate from accelerometer data
                val respiratoryRateValue = respiratoryRateCalculator(accelValuesX, accelValuesY, accelValuesZ)
                respiratoryRate = respiratoryRateValue
                isRespiratoryRateCalculated = true
                isCalculating = false

                // Save the respiratory rate to NavController's savedStateHandle
                navController.previousBackStackEntry?.savedStateHandle?.set("respiratoryRate", respiratoryRateValue)
                navController.popBackStack()
            }
        }

        Button(
            onClick = {
                isMonitoring = !isMonitoring
                respiratoryRate = null // Reset respiratory rate when monitoring starts
                isRespiratoryRateCalculated = false
                isCalculating = false
                accelValuesX.clear()
                accelValuesY.clear()
                accelValuesZ.clear()
                Log.d("RespiratoryRateScreen", if (isMonitoring) "Monitoring started" else "Monitoring stopped")
            },
            modifier = Modifier.padding(16.dp)
        ) {
            Text(if (isMonitoring) "Stop Monitoring" else "Start Respiratory Rate Monitoring")
        }

        // Show progress indicator if the respiratory rate is still being calculated
        if (isCalculating) {
            CircularProgressIndicator(modifier = Modifier.padding(16.dp))
        } else {
            Text(
                text = if (isRespiratoryRateCalculated) {
                    "Current Respiratory Rate: ${respiratoryRate ?: "Error"} breaths per minute"
                } else {
                    "Ready to Capture"
                },
                style = MaterialTheme.typography.headlineMedium,
                modifier = Modifier.padding(16.dp)
            )
        }
    }
}