package com.sugarcare.app.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.graphics.Color

//Brand Colors
val TealPrimary     = Color(0xFF2E9B9B)
val TealDark        = Color(0xFF1F7A7A)
val TealLight       = Color(0xFFB2DFDB)
val GreenAccent     = Color(0xFF5BAD6F)
val OrangeDrop      = Color(0xFFE87A3E)
val BackgroundLight = Color(0xFFE8F5F5)
val SurfaceWhite    = Color(0xFFFFFFFF)
val TextDark        = Color(0xFF1A2B2B)
val TextMedium      = Color(0xFF4A6565)
val TextLight       = Color(0xFF7A9999)

// Dark Mode Colors 
val BackgroundDark  = Color(0xFF0D1F1F)
val SurfaceDark     = Color(0xFF1A2E2E)
val TextDarkMode    = Color(0xFFE0F2F1)
val TextMediumDark  = Color(0xFF80CBC4)

//Global dark theme state
val LocalDarkTheme = compositionLocalOf { mutableStateOf(false) }

private val LightColorScheme = lightColorScheme(
    primary          = TealPrimary,
    onPrimary        = Color.White,
    primaryContainer = TealLight,
    secondary        = GreenAccent,
    onSecondary      = Color.White,
    tertiary         = OrangeDrop,
    background       = BackgroundLight,
    surface          = SurfaceWhite,
    onBackground     = TextDark,
    onSurface        = TextDark,
)

private val DarkColorScheme = darkColorScheme(
    primary          = TealPrimary,
    onPrimary        = Color.White,
    primaryContainer = Color(0xFF1F5F5F),
    secondary        = GreenAccent,
    onSecondary      = Color.White,
    tertiary         = OrangeDrop,
    background       = BackgroundDark,
    surface          = SurfaceDark,
    onBackground     = TextDarkMode,
    onSurface        = TextDarkMode,
)

@Composable
fun SugarCareTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme
    MaterialTheme(
        colorScheme = colorScheme,
        typography  = SugarCareTypography,
        content     = content
    )
}
