package com.example.healthapplication.ui.screens

import android.Manifest
import android.app.Activity
import android.content.ContentResolver
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.MediaStore
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.healthapplication.utils.heartRateCalculator
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.PermissionStatus
import com.google.accompanist.permissions.rememberPermissionState
import androidx.navigation.NavController

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun HeartRateScreen(navController: NavController) { // Add NavController as a parameter
    val context = LocalContext.current
    var heartRate by remember { mutableStateOf<Int?>(null) }
    var isMonitoring by remember { mutableStateOf(false) }
    var videoUri by remember { mutableStateOf<Uri?>(null) }
    var isHeartRateCalculated by remember { mutableStateOf(false) }
    var isCalculating by remember { mutableStateOf(false) }

    val cameraPermissionState = rememberPermissionState(Manifest.permission.CAMERA)
    val contentResolver = context.contentResolver

    // Launcher for capturing video
    val videoCaptureLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            // Get the URI of the recorded video
            videoUri = result.data?.data
            isMonitoring = false // Stop monitoring after video is captured
            Log.d("HeartRateScreen", "Video URI received: $videoUri") // Log the received URI
        } else {
            Log.e("HeartRateScreen", "Failed to capture video")
        }
    }

    // Start heart rate calculation when video is captured
    LaunchedEffect(videoUri) {
        if (videoUri != null) {
            Log.d("HeartRateScreen", "Starting heart rate calculation for URI: $videoUri")
            isCalculating = true

            // Launch coroutine to calculate heart rate using helper function
            launch {
                val heartRateValue = heartRateCalculator(videoUri!!, contentResolver)
                heartRate = heartRateValue
                isHeartRateCalculated = true
                isCalculating = false
                Log.d("HeartRateScreen", "Heart rate calculated: $heartRateValue BPM")

                // Pass the heart rate back to the HomeScreen
                navController.previousBackStackEntry?.savedStateHandle?.set("heartRate", heartRate)
                navController.popBackStack() // Navigate back to the HomeScreen
            }
        }
    }

    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        when (val permissionStatus = cameraPermissionState.status) {
            PermissionStatus.Granted -> {
                Button(
                    onClick = {
                        if (!isMonitoring) {
                            // Start capturing video
                            val videoIntent = Intent(MediaStore.ACTION_VIDEO_CAPTURE)
                            videoCaptureLauncher.launch(videoIntent)
                            Log.d("HeartRateScreen", "Launching video capture")
                        } else {
                            isMonitoring = false
                            videoUri = null
                        }
                        heartRate = null
                        isHeartRateCalculated = false
                        isCalculating = false
                    },
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(if (isMonitoring) "Stop Monitoring" else "Start Heart Rate Monitoring")
                }

                if (isHeartRateCalculated) {
                    // Display the calculated heart rate
                    Text(text = "Heart Rate: ${heartRate ?: "Error"} BPM", style = MaterialTheme.typography.headlineMedium)
                } else if (isCalculating) {
                    CircularProgressIndicator(modifier = Modifier.padding(16.dp))
                } else {
                    Text(text = "Ready to Capture", style = MaterialTheme.typography.bodyLarge)
                    Text(text = "Click on button to open camera, then record a 45 sec video with finger on camera, then click OK.", style = MaterialTheme.typography.bodySmall)
                }
            }
            is PermissionStatus.Denied -> {
                if (permissionStatus.shouldShowRationale) {
                    Text("Camera permission is needed to monitor heart rate")
                    Button(onClick = { cameraPermissionState.launchPermissionRequest() }) {
                        Text("Request permission")
                    }
                } else {
                    Button(onClick = { cameraPermissionState.launchPermissionRequest() }) {
                        Text("Request camera permission")
                    }
                }
            }
        }
    }
}