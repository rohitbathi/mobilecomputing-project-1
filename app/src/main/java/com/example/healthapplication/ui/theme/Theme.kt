package com.example.healthapplication.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val LightColorScheme = lightColorScheme(
    primary = BluePrimary,
    secondary = LightGreenSecondary,
    background = BackgroundColor,
    surface = SurfaceColor,
    onPrimary = Color.White,      // Text on primary buttons (white)
    onSecondary = Color.White,    // Text on secondary buttons (white)
    onBackground = TextColor,     // Text color on background (dark gray)
    onSurface = TextColor,        // Text color on surfaces (dark gray)
    error = ErrorColor,           // Error color (red)
    onError = Color.White         // Text on error background (white)
)

private val DarkColorScheme = darkColorScheme(
    primary = BluePrimary,
    secondary = LightGreenSecondary,
    background = Color(0xFF121212), // Dark mode background
    surface = Color(0xFF1E1E1E),    // Dark mode surface
    onPrimary = Color.White,
    onSecondary = Color.White,
    onBackground = Color.White,     // Light text on dark background
    onSurface = Color.White,        // Light text on dark surface
    error = ErrorColor,
    onError = Color.White
)

@Composable
fun HealthApplicationTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colors = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colors,
        typography = Typography,  // Uses typography from Type.kt
        content = content
    )
}