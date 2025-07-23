package com.studentanalytics.app.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
    primary = MapuaBlue,
    onPrimary = Color.White,
    primaryContainer = MapuaDarkBlue,
    onPrimaryContainer = Color.White,
    secondary = MapuaRed,
    onSecondary = Color.White,
    secondaryContainer = MapuaLightRed,
    onSecondaryContainer = Color.White,
    tertiary = MapuaLightBlue,
    onTertiary = Color.White,
    error = MapuaRed,
    onError = Color.White,
    background = MapuaDarkGray,
    onBackground = Color.White,
    surface = MapuaDarkGray,
    onSurface = Color.White,
    surfaceVariant = MapuaGray,
    onSurfaceVariant = Color.White
)

private val LightColorScheme = lightColorScheme(
    primary = MapuaBlue,
    onPrimary = Color.White,
    primaryContainer = MapuaLightBlue,
    onPrimaryContainer = Color.White,
    secondary = MapuaRed,
    onSecondary = Color.White,
    secondaryContainer = MapuaLightRed,
    onSecondaryContainer = Color.White,
    tertiary = MapuaDarkBlue,
    onTertiary = Color.White,
    error = MapuaRed,
    onError = Color.White,
    background = MapuaLightGray,
    onBackground = MapuaDarkGray,
    surface = Color.White,
    onSurface = MapuaDarkGray,
    surfaceVariant = MapuaLightGray,
    onSurfaceVariant = MapuaGray
)

@Composable
fun StudentAnalyticsTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography(),
        content = content
    )
}