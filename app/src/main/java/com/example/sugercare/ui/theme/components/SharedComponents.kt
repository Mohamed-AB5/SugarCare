package com.sugarcare.app.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Medication
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sugarcare.app.ui.theme.*


@Composable
fun SugarCareBackground(content: @Composable BoxScope.() -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundLight),
        content = content
    )
}

@Composable
fun PrimaryButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true
) {
    Button(
        onClick  = onClick,
        enabled  = enabled,
        modifier = modifier
            .fillMaxWidth()
            .height(56.dp),
        shape  = RoundedCornerShape(28.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = TealPrimary,
            contentColor   = Color.White
        ),
        elevation = ButtonDefaults.buttonElevation(defaultElevation = 4.dp)
    ) {
        Text(
            text       = text,
            fontWeight = FontWeight.Bold,
            fontSize   = 18.sp
        )
    }
}

@Composable
fun SecondaryButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true
) {
    Button(
        onClick  = onClick,
        modifier = modifier
            .fillMaxWidth()
            .height(56.dp),
        shape  = RoundedCornerShape(28.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = GreenAccent,
            contentColor   = Color.White
        ),
        elevation = ButtonDefaults.buttonElevation(defaultElevation = 4.dp)
    ) {
        Text(
            text       = text,
            fontWeight = FontWeight.Bold,
            fontSize   = 18.sp
        )
    }
}

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
        value            = value,
        onValueChange    = onValueChange,
        label            = { Text(label) },
        modifier         = modifier.fillMaxWidth(),
        shape            = RoundedCornerShape(16.dp),
        singleLine       = true,
        visualTransformation = visualTransformation,
        trailingIcon     = trailingIcon,
        colors           = OutlinedTextFieldDefaults.colors(
            focusedBorderColor   = TealPrimary,
            unfocusedBorderColor = TealLight,
            focusedLabelColor    = TealPrimary,
            cursorColor          = TealPrimary
        )
    )
}

@Composable
fun SugarCareCard(
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit
) {
    Card(
        modifier  = modifier.fillMaxWidth(),
        shape     = RoundedCornerShape(20.dp),
        colors    = CardDefaults.cardColors(containerColor = SurfaceWhite),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        content   = { Column(modifier = Modifier.padding(16.dp), content = content) }
    )
}

@Composable
fun SugarCareBottomBar(
    currentRoute: String,
    onHome: () -> Unit,
    onMeds: () -> Unit,
    onTrends: () -> Unit,
    onProfile: () -> Unit
) {
    NavigationBar(
        containerColor = SurfaceWhite,
        tonalElevation = 8.dp
    ) {
        NavigationBarItem(
            selected = currentRoute == "home",
            onClick  = onHome,
            icon     = { Icon(androidx.compose.material.icons.Icons.Filled.Home, null) },
            label    = { Text("Home") },
            colors   = NavigationBarItemDefaults.colors(indicatorColor = TealLight)
        )
        NavigationBarItem(
            selected = currentRoute == "medications",
            onClick  = onMeds,
            icon     = { Icon(androidx.compose.material.icons.Icons.Filled.Medication, null) },
            label    = { Text("Meds") },
            colors   = NavigationBarItemDefaults.colors(indicatorColor = TealLight)
        )
        NavigationBarItem(
            selected = currentRoute == "weekly_analytics",
            onClick  = onTrends,
            icon     = { Icon(androidx.compose.material.icons.Icons.Filled.BarChart, null) },
            label    = { Text("Trends") },
            colors   = NavigationBarItemDefaults.colors(indicatorColor = TealLight)
        )
        NavigationBarItem(
            selected = currentRoute == "profile",
            onClick  = onProfile,
            icon     = { Icon(androidx.compose.material.icons.Icons.Filled.Person, null) },
            label    = { Text("Profile") },
            colors   = NavigationBarItemDefaults.colors(indicatorColor = TealLight)
        )
    }
}
