package com.sugarcare.app.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Assignment
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sugarcare.app.ui.theme.*

// Background
@Composable
fun SugarCareBackground(content: @Composable BoxScope.() -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        content  = content
    )
}

// Primary Button 
@Composable
fun PrimaryButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean   = true
) {
    Button(
        onClick   = onClick,
        enabled   = enabled,
        modifier  = modifier.fillMaxWidth().height(56.dp),
        shape     = RoundedCornerShape(28.dp),
        colors    = ButtonDefaults.buttonColors(
            containerColor         = TealPrimary,
            contentColor           = Color.White,
            disabledContainerColor = TealLight
        ),
        elevation = ButtonDefaults.buttonElevation(defaultElevation = 0.dp) // no shadow square
    ) {
        Text(text = text, fontWeight = FontWeight.Bold, fontSize = 18.sp)
    }
}

// Secondary Button — Green pill (matches "Sign Up" on Welcome)
@Composable
fun SecondaryButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Button(
        onClick   = onClick,
        modifier  = modifier.fillMaxWidth().height(56.dp),
        shape     = RoundedCornerShape(28.dp),
        colors    = ButtonDefaults.buttonColors(
            containerColor = GreenAccent,
            contentColor   = Color.White
        ),
        elevation = ButtonDefaults.buttonElevation(defaultElevation = 0.dp) // no shadow square
    ) {
        Text(text = text, fontWeight = FontWeight.Bold, fontSize = 18.sp)
    }
}

// Gradient Button 
@Composable
fun SugarCareGradientButton(
    text: String,
    gradientColors: List<Color>,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Button(
        onClick = onClick,
        modifier = modifier
            .fillMaxWidth()
            .height(56.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = Color.Transparent
        ),
        contentPadding = PaddingValues(0.dp),
        elevation = ButtonDefaults.buttonElevation(defaultElevation = 0.dp)
    ) {
        Box(
            modifier = Modifier
                .background(
                    brush = Brush.horizontalGradient(colors = gradientColors),
                    shape = RoundedCornerShape(28.dp)
                )
                .fillMaxWidth()
                .height(56.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = text,
                color = Color.White,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

// Text Field rounded
@Composable
fun SugarCareTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    modifier: Modifier = Modifier,
    isPassword: Boolean = false,
    trailingIcon: @Composable (() -> Unit)? = null
) {
    val visualTransformation = if (isPassword)
        androidx.compose.ui.text.input.PasswordVisualTransformation()
    else
        androidx.compose.ui.text.input.VisualTransformation.None

    OutlinedTextField(
        value                = value,
        onValueChange        = onValueChange,
        label                = { Text(label) },
        modifier             = modifier.fillMaxWidth(),
        shape                = RoundedCornerShape(28.dp),
        singleLine           = true,
        visualTransformation = visualTransformation,
        trailingIcon         = trailingIcon,
        colors               = OutlinedTextFieldDefaults.colors(
            focusedBorderColor      = TealPrimary,
            unfocusedBorderColor    = TealLight,
            focusedLabelColor       = TealPrimary,
            cursorColor             = TealPrimary,
            focusedContainerColor   = MaterialTheme.colorScheme.surface,
            unfocusedContainerColor = MaterialTheme.colorScheme.surface
        )
    )
}

// no square shadow
@Composable
fun SugarCareCard(
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit
) {
    Card(
        modifier  = modifier.fillMaxWidth(),
        shape     = RoundedCornerShape(20.dp),
        colors    = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        content   = { Column(modifier = Modifier.padding(16.dp), content = content) }
    )
}
