package com.sugarcare.app.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.sugercare.core.features.profile.presentation.ProfileViewModel
import com.sugarcare.app.ui.theme.BackgroundDark
import com.sugarcare.app.ui.theme.BackgroundLight
import com.sugarcare.app.ui.theme.GreenAccent
import com.sugarcare.app.ui.theme.LocalDarkTheme
import com.sugarcare.app.ui.theme.SurfaceDark
import com.sugarcare.app.ui.theme.SurfaceWhite
import com.sugarcare.app.ui.theme.TealDark
import com.sugarcare.app.ui.theme.TealLight
import com.sugarcare.app.ui.theme.TealPrimary
import com.sugarcare.app.ui.theme.White

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


@Composable
fun PrimaryButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true
) {
    Button(
        onClick = onClick,
        enabled = enabled,
        modifier = modifier
            .fillMaxWidth()
            .height(56.dp),
        shape = RoundedCornerShape(28.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = TealPrimary,
            contentColor = Color.White,
            disabledContainerColor = TealLight.copy(alpha = 0.6f)
        ),
        elevation = ButtonDefaults.buttonElevation(0.dp)
    ) {
        Text(text, fontWeight = FontWeight.Bold, fontSize = 18.sp)
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
        onClick = onClick,
        enabled = enabled,
        modifier = modifier
            .fillMaxWidth()
            .height(56.dp),
        shape = RoundedCornerShape(28.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = GreenAccent,
            contentColor = Color.White,
            disabledContainerColor = GreenAccent.copy(alpha = 0.5f)
        ),
        elevation = ButtonDefaults.buttonElevation(0.dp)
    ) {
        Text(text, fontWeight = FontWeight.Bold, fontSize = 18.sp)
    }
}



@Composable
fun GradientButton(
    text: String,
    onClick: () -> Unit,
    color1: Color,
    color2: Color,
    modifier: Modifier = Modifier,
    textSize: TextUnit = 12.sp,
    horizontalPadding: Dp = 16.dp,
    verticalPadding: Dp = 8.dp,
    enabled: Boolean = true,
    content: (@Composable BoxScope.() -> Unit)? = null
) {
    val backgroundBrush = if (enabled) {
        Brush.horizontalGradient(listOf(color1, color2))
    } else {
        Brush.horizontalGradient(
            listOf(
                Color.Gray.copy(alpha = 0.6f),
                Color.Gray.copy(alpha = 0.9f)
            )
        )
    }

//    Mohamed : To pass an icon to the button behind the text
  /*  Box(
        modifier = modifier
            .clip(RoundedCornerShape(28.dp))
            .background(backgroundBrush)
            .clickable(
                enabled = enabled,
                onClick = onClick
            )
            .padding(horizontal = horizontalPadding, vertical = verticalPadding),
        contentAlignment = Alignment.Center
    )
    {
        Text(
            text,
            fontSize = textSize,
            color = if (enabled) White else White.copy(alpha = 0.7f),
            fontWeight = FontWeight.Bold
        )
        content()
    }*/

    Box(
        modifier = modifier
            .width(350.dp)
            .clip(RoundedCornerShape(28.dp))
            .background(backgroundBrush)
            .clickable(enabled = enabled, onClick = onClick)
            .padding(horizontal = horizontalPadding, vertical = verticalPadding),
        contentAlignment = Alignment.Center
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            if (content != null) {
                Box { content() }
                Spacer(Modifier.width(8.dp))
            }
            Text(
                text,
                fontSize = textSize,
                color = if (enabled) White else White.copy(alpha = 0.7f),
                fontWeight = FontWeight.Bold
            )
        }
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
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(28.dp),
        singleLine = true,
        visualTransformation = if (isPassword)
            androidx.compose.ui.text.input.PasswordVisualTransformation()
        else
            androidx.compose.ui.text.input.VisualTransformation.None,
        trailingIcon = trailingIcon,
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = TealPrimary,
            unfocusedBorderColor = TealLight,
            focusedLabelColor = TealPrimary,
            cursorColor = TealPrimary,
            focusedContainerColor = Color.Transparent,
            unfocusedContainerColor = Color.Transparent
        )
    )
}


@Composable
fun SugarCareCard(
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit
) {
  
    val isDark = LocalDarkTheme.current.value
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isDark) SurfaceDark else SurfaceWhite
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = if (isDark) 0.dp else 2.dp),
        content = { Column(modifier = Modifier.padding(16.dp), content = content) }
    )
}

@Composable
fun ProfilePicture(profileViewModel: ProfileViewModel, fontSize: TextUnit = 36.sp) {

    val editableProfile by profileViewModel.editableProfile.collectAsState()
    val name = editableProfile.fullName.trim()

    val initials = name.split(" ")
        .filter { it.isNotEmpty() }
        .take(2)
        .joinToString("") { it.first().uppercase() }
        .ifEmpty { "?" }

    Box(contentAlignment = Alignment.BottomEnd) {
                if (editableProfile.photoUrl.isNullOrEmpty()) {
                    Box(
                        modifier = Modifier
                            .size(108.dp)
                            .clip(CircleShape)
                            .background(TealLight)
                            .border(3.dp, TealPrimary, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text =initials,
                            fontSize = fontSize,
                            fontWeight = FontWeight.Bold,
                            color = TealDark
                        )
                    }
                } else {
                    AsyncImage(
                        model = editableProfile.photoUrl,
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
