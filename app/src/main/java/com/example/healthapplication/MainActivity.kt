package com.example.healthapplication

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.healthapplication.ui.screens.HomeScreen
import com.example.healthapplication.ui.screens.HeartRateScreen
import com.example.healthapplication.ui.screens.RespiratoryRateScreen
import com.example.healthapplication.ui.screens.SymptomRecordingScreen
import com.example.healthapplication.ui.theme.HealthApplicationTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            HealthApplicationTheme {
                val navController = rememberNavController()
                Surface {
                    AppNavigation(navController)
                }
            }
        }
    }
}

@Composable
fun AppNavigation(navController: NavHostController) {
    NavHost(navController = navController, startDestination = "home") {
        composable("home") { HomeScreen(navController) }
        composable("heartRate") { HeartRateScreen(navController) }
        composable("respiratoryRate") { RespiratoryRateScreen(navController) }
        composable("symptomRecording") { SymptomRecordingScreen(navController) }
    }
}