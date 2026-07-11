package com.sugarcare.app.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Medication
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.sugercare.viewModels.ProfileViewModel
import com.sugarcare.app.ui.theme.LocalDarkTheme
import com.sugarcare.app.ui.theme.BackgroundDark
import com.sugarcare.app.ui.theme.BackgroundLight
import com.sugarcare.app.ui.theme.*

// ── Background ────────────────────────────────────────────────
@Composable
fun SugarCareBackground(content: @Composable BoxScope.() -> Unit) {
    val isDark = LocalDarkTheme.current.value
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(if (isDark) BackgroundDark else BackgroundLight),
        content = content
    )
}

// ── Primary Button ────────────────────────────────────────────
@Composable
fun PrimaryButton(
    text    : String,
    onClick : () -> Unit,
    modifier: Modifier = Modifier,
    enabled : Boolean  = true
) {
    Button(
        onClick   = onClick,
        enabled   = enabled,
        modifier  = modifier.fillMaxWidth().height(56.dp),
        shape     = RoundedCornerShape(28.dp),
        colors    = ButtonDefaults.buttonColors(
            containerColor         = TealPrimary,
            contentColor           = Color.White,
            disabledContainerColor = TealLight.copy(alpha = 0.6f)
        ),
        elevation = ButtonDefaults.buttonElevation(0.dp)
    ) {
        Text(text, fontWeight = FontWeight.Bold, fontSize = 18.sp)
    }
}

// ── Secondary Button ──────────────────────────────────────────
@Composable
fun SecondaryButton(
    text    : String,
    onClick : () -> Unit,
    modifier: Modifier = Modifier,
    enabled : Boolean  = true        // ← added
) {
    Button(
        onClick   = onClick,
        enabled   = enabled,
        modifier  = modifier.fillMaxWidth().height(56.dp),
        shape     = RoundedCornerShape(28.dp),
        colors    = ButtonDefaults.buttonColors(
            containerColor         = GreenAccent,
            contentColor           = Color.White,
            disabledContainerColor = GreenAccent.copy(alpha = 0.5f)
        ),
        elevation = ButtonDefaults.buttonElevation(0.dp)
    ) {
        Text(text, fontWeight = FontWeight.Bold, fontSize = 18.sp)
    }
}

// ── TextField ─────────────────────────────────────────────────
@Composable
fun SugarCareTextField(
    value        : String,
    onValueChange: (String) -> Unit,
    label        : String,
    modifier     : Modifier = Modifier,
    isPassword   : Boolean  = false,
    trailingIcon : @Composable (() -> Unit)? = null,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
) {
    OutlinedTextField(
        value                = value,
        onValueChange        = onValueChange,
        label                = { Text(label) },
        modifier             = modifier.fillMaxWidth(),
        shape                = RoundedCornerShape(28.dp),
        singleLine           = true,
        visualTransformation = if (isPassword)
            androidx.compose.ui.text.input.PasswordVisualTransformation()
        else
            androidx.compose.ui.text.input.VisualTransformation.None,
        trailingIcon         = trailingIcon,
        colors               = OutlinedTextFieldDefaults.colors(
            focusedBorderColor      = TealPrimary,
            unfocusedBorderColor    = TealLight,
            focusedLabelColor       = TealPrimary,
            cursorColor             = TealPrimary,
            focusedContainerColor   = Color.Transparent,
            unfocusedContainerColor = Color.Transparent
        )
    )
}

// ── Card ──────────────────────────────────────────────────────
@Composable
fun SugarCareCard(
    modifier: Modifier = Modifier,
    content : @Composable ColumnScope.() -> Unit
) {
    Card(
        modifier  = modifier.fillMaxWidth(),
        shape     = RoundedCornerShape(20.dp),
        colors    = CardDefaults.cardColors(containerColor = SurfaceWhite),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        content   = { Column(modifier = Modifier.padding(16.dp), content = content) }
    )
}

// ── ProfilePicture ────────────────────────────────────────────
// Shows initials avatar from the user's name in ProfileViewModel.
// When teammates' full implementation is merged, this will be
// replaced by the real version that loads the actual photo.
@Composable
fun ProfilePicture(
    profileViewModel: ProfileViewModel,
    fontSize        : TextUnit = 18.sp
) {
    val editableProfile by profileViewModel.editableProfile.collectAsState()
    val name = editableProfile.fullName.trim()

    // Build initials (up to 2 letters)
    val initials = name.split(" ")
        .filter { it.isNotEmpty() }
        .take(2)
        .joinToString("") { it.first().uppercase() }
        .ifEmpty { "?" }

    Box(
        modifier = Modifier
            .size(38.dp)
            .clip(CircleShape)
            .background(TealLight),
        contentAlignment = Alignment.Center
    ) {
        if (initials == "?") {
            Icon(Icons.Filled.Person, null, tint = TealPrimary, modifier = Modifier.size(24.dp))
        } else {
            Text(
                text  = initials,
                style = TextStyle(
                    color      = TealDark,
                    fontSize   = fontSize,
                    fontWeight = FontWeight.Bold
                )
            )
        }
    }
}