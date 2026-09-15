package com.example.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme = darkColorScheme(
    primary = TomnayaGold,
    onPrimary = Color.Black,
    primaryContainer = TomnayaGoldDark,
    onPrimaryContainer = Color.White,
    secondary = TomnayaTeal,
    onSecondary = Color.White,
    secondaryContainer = TomnayaNavySurface,
    onSecondaryContainer = TomnayaTealLight,
    background = TomnayaNavy,
    onBackground = Color.White,
    surface = TomnayaNavySurface,
    onSurface = Color.White,
    surfaceVariant = TomnayaCardDark,
    onSurfaceVariant = Slate200,
    outline = Slate700
)

private val LightColorScheme = lightColorScheme(
    primary = TomnayaGoldDark,
    onPrimary = Color.White,
    primaryContainer = TomnayaGoldLight,
    onPrimaryContainer = Color(0xFF78350F),
    secondary = TomnayaTeal,
    onSecondary = Color.White,
    secondaryContainer = TomnayaTealLight,
    onSecondaryContainer = Color(0xFF134E4A),
    background = Slate50,
    onBackground = Slate900,
    surface = Color.White,
    onSurface = Slate900,
    surfaceVariant = Slate100,
    onSurfaceVariant = Slate700,
    outline = Slate200
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false, // Keep consistent branding colors for transport app
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
