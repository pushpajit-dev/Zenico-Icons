package com.runeicons.app.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val DarkColorScheme = darkColorScheme(
    primary = Color.White,
    onPrimary = Color.Black,
    primaryContainer = ObsidianDarkSurfaceVar,
    onPrimaryContainer = Color.White,
    secondary = Color(0xFF3859FD),
    onSecondary = Color.White,
    background = ObsidianDarkBg,
    onBackground = ObsidianDarkTextPrimary,
    surface = ObsidianDarkSurface,
    onSurface = ObsidianDarkTextPrimary,
    surfaceVariant = ObsidianDarkSurfaceVar,
    onSurfaceVariant = ObsidianDarkTextSecondary,
    outline = ObsidianDarkBorder
)

private val LightColorScheme = lightColorScheme(
    primary = Color.Black,
    onPrimary = Color.White,
    primaryContainer = InvertedLightSurfaceVar,
    onPrimaryContainer = Color.Black,
    secondary = Color(0xFF3859FD),
    onSecondary = Color.White,
    background = InvertedLightBg,
    onBackground = InvertedLightTextPrimary,
    surface = InvertedLightSurface,
    onSurface = InvertedLightTextPrimary,
    surfaceVariant = InvertedLightSurfaceVar,
    onSurfaceVariant = InvertedLightTextSecondary,
    outline = InvertedLightBorder
)

@Composable
fun RuneIconsTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.background.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        content = content
    )
}
