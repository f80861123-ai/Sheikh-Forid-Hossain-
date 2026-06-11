package com.example.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
    primary = GreenPrimary,
    secondary = RedAccent,
    tertiary = SecondaryGreen,
    background = DeepDarkBg,
    surface = CardDarkBg,
    onPrimary = Color.Black,
    onSecondary = Color.White,
    onBackground = SportsText,
    onSurface = SportsText,
    surfaceVariant = Color(0x33FFFFFF)
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = true, // Force Dark Mode Default as requested
    dynamicColor: Boolean = false, // Keep consistent branding, disable android 12 wallpaper tinting
    content: @Composable () -> Unit,
) {
    MaterialTheme(
        colorScheme = DarkColorScheme,
        typography = Typography,
        content = content
    )
}
