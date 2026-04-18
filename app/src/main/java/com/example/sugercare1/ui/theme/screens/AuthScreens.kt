package com.sugarcare.app.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sugarcare.app.ui.components.*
import com.sugarcare.app.ui.theme.*

// ─────────────────────────────────────────────────────────────
//  Screen 3 – Sign In Flow
// ─────────────────────────────────────────────────────────────
@Composable
fun SignInScreen(
    onSignInSuccess: () -> Unit,
    onNavigateToSignUp: () -> Unit
) {
    var email       by remember { mutableStateOf("") }
    var password    by remember { mutableStateOf("") }
    var showPass    by remember { mutableStateOf(false) }
    var rememberMe  by remember { mutableStateOf(false) }

    SugarCareBackground {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 32.dp),
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text       = "Welcome back",
                style      = MaterialTheme.typography.headlineMedium,
                color      = TealDark,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(32.dp))

            SugarCareTextField(
                value         = email,
                onValueChange = { email = it },
                label         = "Email"
            )

            Spacer(modifier = Modifier.height(16.dp))

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

            Spacer(modifier = Modifier.height(12.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                Checkbox(
                    checked         = rememberMe,
                    onCheckedChange = { rememberMe = it },
                    colors          = CheckboxDefaults.colors(checkedColor = TealPrimary)
                )
                Text(text = "Remember Me", color = TextMedium)
            }

            Spacer(modifier = Modifier.height(24.dp))

            PrimaryButton(
                text    = "Sign in",
                onClick = onSignInSuccess,
                enabled = email.isNotBlank() && password.isNotBlank()
            )

            Spacer(modifier = Modifier.height(20.dp))

            Text(
                text = buildAnnotatedString {
                    append("Already have an account? ")
                    withStyle(SpanStyle(color = TealPrimary, fontWeight = FontWeight.SemiBold)) {
                        append("Sign In")
                    }
                },
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .clickable { onNavigateToSignUp() },
                fontSize = 14.sp
            )

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text     = "Sign In",
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .clickable { onSignInSuccess() },
                color    = TealPrimary,
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}

// ─────────────────────────────────────────────────────────────
//  Screen 2 – User Registration (Sign Up)
// ─────────────────────────────────────────────────────────────
@Composable
fun SignUpScreen(
    onSignUpSuccess: () -> Unit,
    onNavigateToSignIn: () -> Unit
) {
    var fullName         by remember { mutableStateOf("") }
    var email            by remember { mutableStateOf("") }
    var password         by remember { mutableStateOf("") }
    var confirmPassword  by remember { mutableStateOf("") }
    var acceptedPolicy   by remember { mutableStateOf(false) }
    var showPass         by remember { mutableStateOf(false) }

    SugarCareBackground {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 32.dp),
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text       = "Get Started",
                style      = MaterialTheme.typography.headlineMedium,
                color      = GreenAccent,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(24.dp))

            SugarCareTextField(
                value         = fullName,
                onValueChange = { fullName = it },
                label         = "Full Name"
            )
            Spacer(modifier = Modifier.height(12.dp))

            SugarCareTextField(
                value         = email,
                onValueChange = { email = it },
                label         = "Email"
            )
            Spacer(modifier = Modifier.height(12.dp))

            SugarCareTextField(
                value         = password,
                onValueChange = { password = it },
                label         = "Password",
                isPassword    = !showPass
            )
            Spacer(modifier = Modifier.height(12.dp))

            SugarCareTextField(
                value         = confirmPassword,
                onValueChange = { confirmPassword = it },
                label         = "Confirm Password",
                isPassword    = !showPass
            )

            Spacer(modifier = Modifier.height(12.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                Checkbox(
                    checked         = acceptedPolicy,
                    onCheckedChange = { acceptedPolicy = it },
                    colors          = CheckboxDefaults.colors(checkedColor = GreenAccent)
                )
                Text(
                    text = buildAnnotatedString {
                        append("Accept ")
                        withStyle(SpanStyle(color = TealPrimary, fontWeight = FontWeight.SemiBold)) {
                            append("Privacy Policy")
                        }
                        append(" & Registration Rules")
                    },
                    fontSize = 13.sp,
                    color    = TextMedium
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            SecondaryButton(
                text    = "Sign Up",
                onClick = onSignUpSuccess
            )

            Spacer(modifier = Modifier.height(16.dp))

            // ── Social sign-up row ────────────────────────────
            Row(
                modifier              = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                SocialButton(label = "G", color = OrangeDrop)
                SocialButton(label = "f", color = TealPrimary)
                SocialButton(label = "𝕏", color = TealDark)
            }
        }
    }
}

@Composable
private fun SocialButton(label: String, color: androidx.compose.ui.graphics.Color) {
    Surface(
        shape     = androidx.compose.foundation.shape.CircleShape,
        color     = color.copy(alpha = 0.12f),
        modifier  = Modifier.size(52.dp)
    ) {
        Box(contentAlignment = Alignment.Center) {
            Text(
                text       = label,
                color      = color,
                fontWeight = FontWeight.Bold,
                fontSize   = 20.sp
            )
        }
    }
}
