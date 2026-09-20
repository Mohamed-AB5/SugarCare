package com.example.sugercare.core.features.profile.presentation.screens

import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.TextFieldColors
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import com.sugarcare.app.ui.theme.LocalDarkTheme
import com.sugarcare.app.ui.theme.SurfaceDark
import com.sugarcare.app.ui.theme.TealLight
import com.sugarcare.app.ui.theme.TealPrimary
import com.sugarcare.app.ui.theme.TextDarkMode
import com.sugarcare.app.ui.theme.TextMedium

//  Mohamed : it was in NewScreens
@Composable
fun newScreenFieldColors(): TextFieldColors {
    val isDark = LocalDarkTheme.current.value

    val textColor      = if (isDark) TextDarkMode        else Color(0xFF1A2B29)
    val labelColor     = if (isDark) Color(0xFF80CBC4)    else TextMedium
    val containerColor = if (isDark) SurfaceDark          else Color.White
    val borderColor    = if (isDark) TealPrimary          else TealLight

    return OutlinedTextFieldDefaults.colors(
        focusedBorderColor        = TealPrimary,
        unfocusedBorderColor      = borderColor,
        focusedLabelColor         = TealPrimary,
        unfocusedLabelColor       = labelColor,
        focusedTextColor          = Color.Blue,
        unfocusedTextColor        = Color.Blue,
        disabledTextColor         = textColor.copy(0.7f),
        cursorColor               = TealPrimary,
        focusedContainerColor     = containerColor,
        unfocusedContainerColor   = containerColor,
        focusedPlaceholderColor   = textColor.copy(0.6f),
        unfocusedPlaceholderColor = textColor.copy(0.6f),
        focusedLeadingIconColor   = TealPrimary,
        unfocusedLeadingIconColor = TealPrimary,
        focusedTrailingIconColor  = TealPrimary,
        unfocusedTrailingIconColor = TealPrimary,
    )
}
