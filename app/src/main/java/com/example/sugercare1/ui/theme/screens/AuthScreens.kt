package com.sugarcare.app.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.sugercare1.Authentication.AuthManager
import com.example.sugercare1.Authentication.AuthResponse
import com.sugarcare.app.ui.components.*
import com.sugarcare.app.ui.theme.*
import kotlinx.coroutines.launch

// Sign In 
@Composable
fun SignInScreen(
    onSignInSuccess: () -> Unit,
    onNavigateToSignUp: () -> Unit
) {
    var email     by remember { mutableStateOf("") }
    var password  by remember { mutableStateOf("") }
    var showPass  by remember { mutableStateOf(false) }
    var rememberMe by remember { mutableStateOf(false) }

    val context      = LocalContext.current
    val authManager  = remember { AuthManager(context) }
    val scope        = rememberCoroutineScope()

    SugarCareBackground {
        Column(
            modifier            = Modifier.fillMaxSize().padding(horizontal = 32.dp),
            verticalArrangement = Arrangement.Center
        ) {
            Text("Welcome back",
                style = MaterialTheme.typography.headlineMedium,
                color = TealDark, fontWeight = FontWeight.Bold)

            Spacer(Modifier.height(32.dp))

            SugarCareTextField(value = email, onValueChange = { email = it }, label = "Email")
            Spacer(Modifier.height(16.dp))

            SugarCareTextField(
                value = password, onValueChange = { password = it },
                label = "Password", isPassword = !showPass,
                trailingIcon = {
                    IconButton(onClick = { showPass = !showPass }) {
                        Icon(if (showPass) Icons.Filled.Visibility else Icons.Filled.VisibilityOff,
                            null, tint = TealPrimary)
                    }
                }
            )
            Spacer(Modifier.height(8.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                Checkbox(checked = rememberMe, onCheckedChange = { rememberMe = it },
                    colors = CheckboxDefaults.colors(checkedColor = TealPrimary))
                Text("Remember Me", color = TextMedium)
                Spacer(Modifier.weight(1f))
                Text("Forgot password?", color = TealPrimary, fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold, modifier = Modifier.clickable { /* handled in SignInScreen.kt */ })
            }

            Spacer(Modifier.height(24.dp))

            PrimaryButton(
                text = "Sign in", enabled = email.isNotBlank() && password.isNotBlank(),
                onClick = {
                    scope.launch {
                        authManager.loginWithEmail(email, password).collect { response ->
                            if (response is AuthResponse.Success) onSignInSuccess()
                        }
                    }
                }
            )

            Spacer(Modifier.height(20.dp))

            // divider
            Row(Modifier.fillMaxWidth().padding(vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically) {
                HorizontalDivider(Modifier.weight(1f), thickness = 1.5.dp, color = TealLight)
                Text(" or ", fontSize = 15.sp, color = TextLight, modifier = Modifier.padding(horizontal = 8.dp))
                HorizontalDivider(Modifier.weight(1f), thickness = 1.5.dp, color = TealLight)
            }

            Spacer(Modifier.height(12.dp))

            //  Social buttons: Google + Facebook
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
                // Google 
                SocialButton(
                    icon  = SocialIcon.Google,
                    label = "Google",
                    onClick = {
                        scope.launch {
                            authManager.signInWithGoogle().collect { response ->
                                if (response is AuthResponse.Success) onSignInSuccess()
                            }
                        }
                    }
                )
                // Facebook 
                SocialButton(
                    icon  = SocialIcon.Facebook,
                    label = "Facebook",
                    onClick = { /* Facebook auth — add later */ }
                )
            }

            Spacer(Modifier.height(16.dp))

            Text(
                buildAnnotatedString {
                    append("No account?  ")
                    withStyle(SpanStyle(color = TealPrimary, fontWeight = FontWeight.SemiBold)) {
                        append("Create one")
                    }
                },
                modifier = Modifier.align(Alignment.CenterHorizontally).clickable { onNavigateToSignUp() },
                fontSize = 14.sp
            )
        }
    }
}

// Sign Up 
@Composable
fun SignUpScreen(
    onSignUpSuccess: () -> Unit,
    onNavigateToSignIn: () -> Unit
) {
    var fullName        by remember { mutableStateOf("") }
    var email           by remember { mutableStateOf("") }
    var password        by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var acceptedPolicy  by remember { mutableStateOf(false) }
    var showPass        by remember { mutableStateOf(false) }

    val context     = LocalContext.current
    val authManager = remember { AuthManager(context) }
    val scope       = rememberCoroutineScope()

    SugarCareBackground {
        Column(
            modifier            = Modifier.fillMaxSize().padding(horizontal = 32.dp),
            verticalArrangement = Arrangement.Center
        ) {
            Text("Get Started",
                style = MaterialTheme.typography.headlineMedium,
                color = GreenAccent, fontWeight = FontWeight.Bold)

            Spacer(Modifier.height(24.dp))

            SugarCareTextField(value = fullName, onValueChange = { fullName = it }, label = "Full Name")
            Spacer(Modifier.height(12.dp))
            SugarCareTextField(value = email, onValueChange = { email = it }, label = "Email")
            Spacer(Modifier.height(12.dp))
            SugarCareTextField(value = password, onValueChange = { password = it },
                label = "Password", isPassword = !showPass,
                trailingIcon = {
                    IconButton(onClick = { showPass = !showPass }) {
                        Icon(if (showPass) Icons.Filled.Visibility else Icons.Filled.VisibilityOff,
                            null, tint = TealPrimary)
                    }
                })
            Spacer(Modifier.height(12.dp))
            SugarCareTextField(value = confirmPassword, onValueChange = { confirmPassword = it },
                label = "Confirm Password", isPassword = !showPass,
                trailingIcon = {
                    IconButton(onClick = { showPass = !showPass }) {
                        Icon(if (showPass) Icons.Filled.Visibility else Icons.Filled.VisibilityOff,
                            null, tint = TealPrimary)
                    }
                })

            Spacer(Modifier.height(12.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                Checkbox(checked = acceptedPolicy, onCheckedChange = { acceptedPolicy = it },
                    colors = CheckboxDefaults.colors(checkedColor = GreenAccent))
                Text(buildAnnotatedString {
                    append("Accept ")
                    withStyle(SpanStyle(color = TealPrimary, fontWeight = FontWeight.SemiBold)) { append("Privacy Policy") }
                    append(" & Registration Rules")
                }, fontSize = 13.sp, color = TextMedium)
            }

            Spacer(Modifier.height(20.dp))

            SecondaryButton(
                text = "Sign Up",
                onClick = {
                    scope.launch {
                        authManager.createAccountWithEmail(email, password).collect { response ->
                            if (response is AuthResponse.Success) onSignUpSuccess()
                        }
                    }
                }
            )

            Spacer(Modifier.height(16.dp))

            Text(
                buildAnnotatedString {
                    append("Already have an account? ")
                    withStyle(SpanStyle(color = TealPrimary, fontWeight = FontWeight.SemiBold)) { append("Sign In") }
                },
                modifier = Modifier.align(Alignment.CenterHorizontally).clickable { onNavigateToSignIn() },
                fontSize = 14.sp
            )

            Spacer(Modifier.height(12.dp))

            //Social sign-up
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
                SocialButton(SocialIcon.Google, "Google") {
                    scope.launch {
                        authManager.signInWithGoogle().collect { response ->
                            if (response is AuthResponse.Success) onSignUpSuccess()
                        }
                    }
                }
                SocialButton(SocialIcon.Facebook, "Facebook") {  }
            }
        }
    }
}


enum class SocialIcon { Google, Facebook }

@Composable
fun SocialButton(icon: SocialIcon, label: String, onClick: () -> Unit) {
    OutlinedButton(
        onClick  = onClick,
        modifier = Modifier.height(48.dp),
        shape    = RoundedCornerShape(24.dp),
        border   = BorderStroke(1.5.dp, if (icon == SocialIcon.Google) Color(0xFFDB4437) else Color(0xFF1877F2)),
        colors   = ButtonDefaults.outlinedButtonColors(containerColor = Color.White)
    ) {
        if (icon == SocialIcon.Google) {
            Text(
                text = buildAnnotatedString {
                    withStyle(SpanStyle(color = Color(0xFF4285F4))) { append("G") }
                    withStyle(SpanStyle(color = Color(0xFFDB4437))) { append("o") }
                    withStyle(SpanStyle(color = Color(0xFFF4B400))) { append("o") }
                    withStyle(SpanStyle(color = Color(0xFF4285F4))) { append("g") }
                    withStyle(SpanStyle(color = Color(0xFF34A853))) { append("l") }
                    withStyle(SpanStyle(color = Color(0xFFDB4437))) { append("e") }
                },
                fontWeight = FontWeight.Bold, fontSize = 16.sp
            )
        } else {
         
            Text(
                text = buildAnnotatedString {
                    withStyle(SpanStyle(color = Color(0xFF1877F2), fontWeight = FontWeight.ExtraBold)) {
                        append("f ")
                    }
                    withStyle(SpanStyle(color = Color(0xFF1877F2))) { append("Facebook") }
                },
                fontSize = 15.sp, fontWeight = FontWeight.SemiBold
            )
        }
    }
}

@Preview
@Composable
fun SignUpScreenPreview() {
    com.sugarcare.app.ui.theme.SugarCareTheme {
        SignUpScreen(onSignUpSuccess = {}, onNavigateToSignIn = {})
    }
}

@Preview
@Composable
fun SignInScreenPreview() {
    com.sugarcare.app.ui.theme.SugarCareTheme {
        SignInScreen(onSignInSuccess = {}, onNavigateToSignUp = {})
    }
}





