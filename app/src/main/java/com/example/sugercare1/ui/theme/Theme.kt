package com.sugarcare.app.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

// app Colors 
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

@Composable
fun SugarCareTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    // App uses a fixed light theme matching the design
    MaterialTheme(
        colorScheme = LightColorScheme,
        typography  = SugarCareTypography,
        content     = content
    )
}

