package com.example.healthapplication.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController

@Composable
fun SymptomRecordingScreen(navController: NavController) {
    val symptomsList = listOf(
        "Cough", "Fatigue", "Fever", "Headache", "Sore Throat", "Shortness of Breath",
        "Nausea", "Muscle Ache", "Loss of Smell or Taste", "Diarrhea", "Feeling Tired"
    )
    val symptomRatings = remember { mutableStateListOf<Float>().apply { repeat(symptomsList.size) { add(0f) } } }

    // Remember scroll state for vertical scrolling
    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState) // Enable vertical scrolling
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(text = "Record Symptoms", style = MaterialTheme.typography.titleLarge)

        // Render each symptom with a slider
        symptomsList.forEachIndexed { index, symptom ->
            Text(text = symptom)
            Slider(
                value = symptomRatings[index],
                onValueChange = { newValue -> symptomRatings[index] = newValue },
                valueRange = 0f..5f,
                steps = 4,
                modifier = Modifier.fillMaxWidth()
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Save symptoms button
        Button(
            onClick = {
                val recordedSymptoms = symptomsList.mapIndexed { index, symptom ->
                    symptom to symptomRatings[index]
                }.toMap()

                // Return the recorded symptoms to the home screen
                navController.previousBackStackEntry?.savedStateHandle?.set("symptoms", recordedSymptoms)
                navController.popBackStack()
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Save Symptoms")
        }
    }
}
