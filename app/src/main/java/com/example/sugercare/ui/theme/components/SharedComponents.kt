package com.sugarcare.app.ui.components

import android.R.attr.shape
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Medication
import androidx.compose.material.icons.filled.Person
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.sugercare.viewModels.ProfileViewModel
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

        val brush = if (enabled) {
            Brush.horizontalGradient(
                colors = listOf(
                    TealPrimary,
                    TealPrimary2
                )
            )
        } else {
            Brush.horizontalGradient(
                colors = listOf(
                    Color(0xFFA3A6A6),
                    Color(0xFF819898)
                )
            )
        }
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(56.dp)
            .clip(RoundedCornerShape(28.dp))
            .background(brush = brush)
            .clickable(enabled = enabled) { onClick() },
        contentAlignment = Alignment.Center
    ){

        Text(
            text       = text,
            fontWeight = FontWeight.Bold,
            fontSize   = 18.sp,
            color      = White
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
    val brush = if (enabled) {
        Brush.horizontalGradient(
            colors = listOf(
                GreenAccent,
                GreenAccent2
            )
        )
    } else {
        Brush.horizontalGradient(
            colors = listOf(
                Color(0xFFA3A6A6),
                Color(0xFF819882)
            )
        )
    }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(56.dp)
            .clip(RoundedCornerShape(28.dp))
            .background(brush = brush)
            .clickable(enabled = enabled) { onClick() },
        contentAlignment = Alignment.Center
    ){


        Text(
            text       = text,
            fontWeight = FontWeight.Bold,
            fontSize   = 18.sp,
            color      = White
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
    trailingIcon: @Composable (() -> Unit)? = null,
    keyboardType: KeyboardType = KeyboardType.Text,
    colors: TextFieldColors = OutlinedTextFieldDefaults.colors(
        focusedBorderColor   = TealPrimary,
        unfocusedBorderColor = TealLight,
        focusedLabelColor    = TealPrimary,
        cursorColor          = TealPrimary
    )
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
        keyboardOptions  = KeyboardOptions(keyboardType = keyboardType),
        colors           = colors
    )
}

@Composable
fun SugarCareCard(
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit
) {
    Card(
        modifier  = modifier.fillMaxWidth()
            .background(Brush.horizontalGradient(listOf(SurfaceWhite, SurfaceWhite))),
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


@Composable
fun ProfilePicture(profileViewModel: ProfileViewModel,fontSize: TextUnit = 36.sp){
    val editableProfile = profileViewModel.editableProfile.collectAsState()

    Box(contentAlignment = Alignment.BottomEnd) {
        if (editableProfile.value.photoUrl.isNullOrEmpty()) {
            Box(
                modifier = Modifier
                    .size(108.dp)
                    .clip(CircleShape)
                    .background(TealLight)
                    .border(3.dp, TealPrimary, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = editableProfile.value.fullName
                        .firstOrNull()?.uppercase() ?: "?",
                    fontSize = fontSize,
                    fontWeight = FontWeight.Bold,
                    color = TealDark
                )
            }
        } else {
            AsyncImage(
                model = editableProfile.value.photoUrl,
                contentDescription = "Profile Photo",
                modifier = Modifier
                    .size(100.dp)
                    .clip(CircleShape)
                    .border(2.dp, TealPrimary, CircleShape),
                contentScale = ContentScale.Crop
            )

        }
    }

}

