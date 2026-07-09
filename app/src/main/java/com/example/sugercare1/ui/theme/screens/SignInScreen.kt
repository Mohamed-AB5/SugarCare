package com.example.sugercare1.ui.theme.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color 
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sugarcare.app.ui.components.*
import com.sugarcare.app.ui.theme.*

@Composable
fun SignInScreen(
    onSignInSuccess: () -> Unit,
    onNavigateToSignUp: () -> Unit,
    onForgotPassword: () -> Unit   // ← new param
) {
    var email      by remember { mutableStateOf("") }
    var password   by remember { mutableStateOf("") }
    var showPass   by remember { mutableStateOf(false) }
    var rememberMe by remember { mutableStateOf(false) }

    SugarCareBackground {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 32.dp),
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                "Welcome back",
                style      = MaterialTheme.typography.headlineMedium,
                color      = TealDark,
                fontWeight = FontWeight.Bold
            )

            Spacer(Modifier.height(32.dp))

            SugarCareTextField(value = email, onValueChange = { email = it }, label = "Email")

            Spacer(Modifier.height(16.dp))

            SugarCareTextField(
                value         = password,
                onValueChange = { password = it },
                label         = "Password",
                isPassword    = !showPass,
                trailingIcon  = {
                    IconButton(onClick = { showPass = !showPass }) {
                        Icon(
                            imageVector        = if (showPass) Icons.Filled.Visibility else Icons.Filled.VisibilityOff,
                            contentDescription = null,
                            tint               = TealPrimary
                        )
                    }
                }
            )

            Spacer(Modifier.height(8.dp))

            // Forgot password link
            Text(
                "Forgot password?",
                modifier   = Modifier
                    .align(Alignment.End)
                    .clickable { onForgotPassword() },
                color      = TealPrimary,
                fontSize   = 13.sp,
                fontWeight = FontWeight.SemiBold
            )

            Spacer(Modifier.height(12.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                Checkbox(
                    checked         = rememberMe,
                    onCheckedChange = { rememberMe = it },
                    colors          = CheckboxDefaults.colors(checkedColor = TealPrimary)
                )
                Text("Remember Me", color = TextMedium)
            }

            Spacer(Modifier.height(20.dp))

        
            SugarCareGradientButton(
                text = "Sign in",
                gradientColors = listOf(Color(0xFF3B9E9E), Color(0xFF7FE3E1)),
                onClick = {
                    
                    if (email.isNotBlank() && password.isNotBlank()) {
                        onSignInSuccess()
                    }
                }
            )

            Spacer(Modifier.height(20.dp))

            Text(
                text = buildAnnotatedString {
                    append("Don't have an account? ")
                    withStyle(SpanStyle(color = TealPrimary, fontWeight = FontWeight.SemiBold)) {
                        append("Sign Up")
                    }
                },
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .clickable { onNavigateToSignUp() },
                fontSize = 14.sp
            )
        }
    }
}
