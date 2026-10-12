package com.animegen.app.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
    primary = Color(0xFF6200EE),
    onPrimary = Color.White,
    primaryContainer = Color(0xFF3700B3),
    onPrimaryContainer = Color(0xFFBB86FC),
    secondary = Color(0xFF03DAC5),
    onSecondary = Color.Black,
    secondaryContainer = Color(0xFF018786),
    onSecondaryContainer = Color(0xFFA7FFEB),
    tertiary = Color(0xFF03DAC5),
    onTertiary = Color.Black,
    error = Color(0xFFB3261E),
    errorContainer = Color(0xFFF9DEDC),
    onError = Color.White,
    background = Color(0xFF121212),
    onBackground = Color.White,
    surface = Color(0xFF1E1E1E),
    onSurface = Color.White,
)

private val LightColorScheme = lightColorScheme(
    primary = Color(0xFF6200EE),
    onPrimary = Color.White,
    primaryContainer = Color(0xFFEADDFF),
    onPrimaryContainer = Color(0xFF21005D),
    secondary = Color(0xFF03DAC5),
    onSecondary = Color.Black,
    secondaryContainer = Color(0xFFB2EBF2),
    onSecondaryContainer = Color(0xFF00363F),
    tertiary = Color(0xFF018786),
    onTertiary = Color.White,
    error = Color(0xFFB3261E),
    errorContainer = Color(0xFFF9DEDC),
    onError = Color.White,
    background = Color(0xFFFFFBFE),
    onBackground = Color(0xFF1C1B1F),
    surface = Color.White,
    onSurface = Color(0xFF1C1B1F),
)

@Composable
fun AnimeGeneratorTheme(
    darkTheme: Boolean = true,
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
