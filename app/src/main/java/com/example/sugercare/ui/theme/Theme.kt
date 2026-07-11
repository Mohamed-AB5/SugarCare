package com.sugarcare.app.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.graphics.Color

// ── Brand Colors ──────────────────────────────────────────────
val TealPrimary      = Color(0xFF2E9B9B)   // main teal
val TealPrimary2     = Color(0xFF7BE1E3)
val TealDark         = Color(0xFF1F7A7A)
val TealLight        = Color(0xFFB2DFDB)
val TealDark2        = Color(0xFF048C8C)
val GreenAccent      = Color(0xFF5BAD6F)   // "Sign Up" button green
val GreenAccent2     = Color(0xFF93F5A8)
val GreenAccent3     = Color(0xFF88DA99)
val OrangeDrop       = Color(0xFFE87A3E)   // blood-drop accent
val OrangeDrop2      = Color(0xFFF1A985)
val BackgroundLight  = Color(0xFFE8F5F5)   // very light teal bg
val SurfaceWhite     = Color(0xFFFFFFFF)
val TextDark         = Color(0xFF1A2B2B)
val TextMedium       = Color(0xFF4A6565)
val TextLight        = Color(0xFF7A9999)
val White            = Color(0xFFFFFFFF)
val TextGray         = Color(0xFF9AA0A6)
val FireIcon         = Color(0xFFFF6B35)

// ── Dark colors ───────────────────────────────────────────────
val BackgroundDark  = Color(0xFF0D1F1F)
val SurfaceDark     = Color(0xFF1A2E2E)
val TextDarkMode    = Color(0xFFE0F2F1)
val TextMediumDark  = Color(0xFF80CBC4)

// ── Global state: hoisted in MainActivity, shared everywhere ──
val LocalDarkTheme = compositionLocalOf { mutableStateOf(false) }

private val LightColors = lightColorScheme(
    primary          = TealPrimary,
    onPrimary        = Color.White,
    primaryContainer = TealLight,
    secondary        = GreenAccent,
    onSecondary      = Color.White,
    background       = BackgroundLight,
    surface          = SurfaceWhite,
    onBackground     = TextDark,
    onSurface        = TextDark,
    tertiary         = OrangeDrop,
)

private val DarkColors = darkColorScheme(
    primary          = TealPrimary,
    onPrimary        = Color.White,
    primaryContainer = Color(0xFF1A4F4F),
    secondary        = GreenAccent,
    onSecondary      = Color.White,
    background       = BackgroundDark,
    surface          = SurfaceDark,
    onBackground     = TextDarkMode,
    onSurface        = TextDarkMode,
    tertiary         = OrangeDrop,
)

@Composable
fun SugarCareTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content  : @Composable () -> Unit
) {
    // App uses a fixed light theme matching the design
    MaterialTheme(
        colorScheme = if (darkTheme) DarkColors else LightColors,
        typography  = SugarCareTypography,
        content     = content
    )
}
