package com.example.healthapplication.ui.screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.healthapplication.data.DatabaseProvider
import com.example.healthapplication.data.UserHealthData
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlin.math.round

@Composable
fun HomeScreen(navController: NavController) {
    // State variables to store respiratory rate, heart rate, and symptom data
    var respiratoryRate by remember { mutableStateOf<Int?>(null) }
    var heartRate by remember { mutableStateOf<Int?>(null) }
    var symptoms by remember { mutableStateOf<Map<String, Float>?>(null) }
    val context = LocalContext.current

    // Handle when the user navigates back from the RespiratoryRateScreen, HeartRateScreen, or SymptomRecordingScreen
    LaunchedEffect(Unit) {
        navController.currentBackStackEntry?.savedStateHandle?.getLiveData<Int>("respiratoryRate")
            ?.observe(navController.currentBackStackEntry!!) { rate ->
                respiratoryRate = rate
            }

        navController.currentBackStackEntry?.savedStateHandle?.getLiveData<Int>("heartRate")
            ?.observe(navController.currentBackStackEntry!!) { rate ->
                heartRate = rate // Update the heart rate value when returned from HeartRateScreen
            }

        navController.currentBackStackEntry?.savedStateHandle?.getLiveData<Map<String, Float>>("symptoms")
            ?.observe(navController.currentBackStackEntry!!) { recordedSymptoms ->
                symptoms = recordedSymptoms
            }
    }

    // Apply background color from the theme
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxSize()
        ) {
            Text(
                text = "Welcome to HealthApp!",
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onBackground,
                modifier = Modifier.padding(bottom = 20.dp)
            )

            // Respiratory Rate Monitoring Button
            Button(
                onClick = {
                    navController.navigate("respiratoryRate")
                },
                modifier = Modifier
                    .padding(10.dp)
                    .fillMaxWidth(0.7f)
                    .height(50.dp),
                shape = RoundedCornerShape(10.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.secondary,
                    contentColor = MaterialTheme.colorScheme.onSecondary
                )
            ) {
                Text(
                    text = if (respiratoryRate != null)
                        "Respiratory Rate: $respiratoryRate BPM"
                    else "Start Respiratory Monitoring"
                )
            }

            // Heart Rate Monitoring Button
            Button(
                onClick = {
                    navController.navigate("heartRate")
                },
                modifier = Modifier
                    .padding(10.dp)
                    .fillMaxWidth(0.7f)
                    .height(50.dp),
                shape = RoundedCornerShape(10.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.secondary,
                    contentColor = MaterialTheme.colorScheme.onSecondary
                )
            ) {
                Text(
                    text = if (heartRate != null)
                        "Heart Rate: $heartRate BPM"  // Display the heart rate if available
                    else "Start Heart Rate Monitoring"
                )
            }

            // Symptoms Recording Button
            Button(
                onClick = {
                    navController.navigate("symptomRecording")
                },
                modifier = Modifier
                    .padding(10.dp)
                    .fillMaxWidth(0.7f)
                    .height(50.dp),
                shape = RoundedCornerShape(10.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.secondary,
                    contentColor = MaterialTheme.colorScheme.onSecondary
                )
            ) {
                Text(
                    text = if (symptoms != null) "Symptoms Recorded" else "Record Symptoms"
                )
            }

            // Upload Button (enabled only when all data are available)
            Button(
                onClick = {
                    // Upload to database
                    CoroutineScope(Dispatchers.IO).launch {
                        val db = DatabaseProvider.getDatabase(context)
                        val userHealthData = UserHealthData(
                            respiratoryRate = respiratoryRate,
                            heartRate = heartRate,
                            cough = round(symptoms?.get("Cough") ?: 0f),
                            fatigue = round(symptoms?.get("Fatigue") ?: 0f),
                            fever = round(symptoms?.get("Fever") ?: 0f),
                            headache = round(symptoms?.get("Headache") ?: 0f),
                            soreThroat = round(symptoms?.get("Sore Throat") ?: 0f),
                            shortnessOfBreath = round(symptoms?.get("Shortness of Breath") ?: 0f),
                            nausea = round(symptoms?.get("Nausea") ?: 0f),
                            muscleAche = round(symptoms?.get("Muscle Ache") ?: 0f),
                            lossOfTasteOrSmell = round(symptoms?.get("Loss of Smell or Taste") ?: 0f),
                            diarrhea = round(symptoms?.get("Diarrhea") ?: 0f),
                            feelingTired = round(symptoms?.get("Feeling Tired") ?: 0f)
                        )
                        db.userHealthDao().insertHealthData(userHealthData)

                        // Show a Toast after the upload is complete
                        CoroutineScope(Dispatchers.Main).launch {
                            Toast.makeText(context, "Data uploaded successfully!", Toast.LENGTH_LONG).show()
                        }
                    }
                },
                modifier = Modifier
                    .padding(10.dp)
                    .fillMaxWidth(0.7f)
                    .height(50.dp),
                shape = RoundedCornerShape(10.dp),
                enabled = respiratoryRate != null && heartRate != null && symptoms != null, // Enable only when all are recorded
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary
                )
            ) {
                Text("Upload to Database")
            }
        }
    }
}
