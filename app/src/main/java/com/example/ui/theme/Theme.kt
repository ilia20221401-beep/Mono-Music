package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val MonoDarkColorScheme = darkColorScheme(
    primary = MonoGold,
    onPrimary = Color.Black,
    primaryContainer = Color(0xFF3B2D0A),
    onPrimaryContainer = MonoGoldLight,
    secondary = MonoCyan,
    onSecondary = Color.Black,
    secondaryContainer = Color(0xFF083344),
    onSecondaryContainer = MonoCyanLight,
    tertiary = MonoGreenVU,
    onTertiary = Color.Black,
    background = MonoBlack,
    onBackground = MonoTextPrimary,
    surface = MonoSurfaceDark,
    onSurface = MonoTextPrimary,
    surfaceVariant = MonoCard,
    onSurfaceVariant = MonoTextSecondary,
    outline = MonoBorder,
    outlineVariant = Color(0xFF1E222E),
    error = MonoCrimsonRec,
    onError = Color.White
)

// For Mono Music, even in light mode we keep a studio workstation aesthetic
private val MonoLightColorScheme = MonoDarkColorScheme

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false, // Forced bespoke studio color scheme
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = MonoDarkColorScheme,
        typography = Typography,
        content = content
    )
}
