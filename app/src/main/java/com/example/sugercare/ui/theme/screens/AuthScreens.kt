package com.sugarcare.app.ui.screens

import android.content.Context
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
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
import com.example.sugercare.viewModels.AuthViewModel
import com.example.sugercare.Authentication.AuthManager
import com.example.sugercare.Authentication.AuthResponse
import com.example.sugercare.viewModels.AuthState
import com.sugarcare.app.ui.components.*
import com.sugarcare.app.ui.theme.*
import kotlinx.coroutines.launch
import com.sugarcare.app.ui.theme.TealLight
import kotlin.text.isNotBlank


// ─────────────────────────────────────────────────────────────
//  Screen 3 – Sign In Flow
// ─────────────────────────────────────────────────────────────


@Composable
fun SignInScreen(
    onSignInSuccess: () -> Unit,
    onNavigateToSignUp: () -> Unit,
    onForgotPassword: () -> Unit,
    authViewModel: AuthViewModel
) {
    val context     = LocalContext.current
    //    ─── For Authentication & Auth View Model ──────────

    val email       = authViewModel.email.collectAsState()
    val password    = authViewModel.password.collectAsState()
    val authState   = authViewModel.authState.collectAsState()
    val showPass    = authViewModel.showPass.collectAsState()
    val rememberMe  = authViewModel.rememberMe.collectAsState()

/*TODO
* Activate
* -Loading
* -Error
* -UnAuthenticated
* Status'
* */

    LaunchedEffect(authState.value) {
        when (authState.value) {
            is AuthState.Loading -> {}
            is AuthState.Authenticated -> onSignInSuccess()
            is AuthState.Error -> {}
            is AuthState.UnAuthenticated -> {}
        }
    }


    SugarCareBackground {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 32.dp),
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "Welcome back",
                style = MaterialTheme.typography.headlineMedium,
                color = TealDark,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(32.dp))

            SugarCareTextField(
                value = email.value,
                onValueChange = { authViewModel.updateEmail(it) },
                label = "Email"
            )

            Spacer(modifier = Modifier.height(16.dp))

            SugarCareTextField(
                value = password.value,
                onValueChange = { authViewModel.updatePassword(it) },
                label = "Password",
                isPassword = !showPass.value,
                trailingIcon = {
                    IconButton(onClick = { authViewModel.toggleShowPass()  }) {
                        Icon(
                            imageVector = if (showPass.value) Icons.Filled.Visibility else Icons.Filled.VisibilityOff,
                            contentDescription = null,
                            tint = TealPrimary
                        )
                    }
                }
            )

            Spacer(modifier = Modifier.height(12.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                Checkbox(
                    checked = rememberMe.value,
                    onCheckedChange = { authViewModel.toggleRememberMe() },
                    colors = CheckboxDefaults.colors(checkedColor = TealPrimary),
                )
                Text(text = "Remember Me", color = TextMedium, fontSize = 12.sp)
                Spacer(modifier = Modifier.width(40.dp))
                // ────── Forgot password link ───────────────────
                Text(
                    "Forgot password?",
                    modifier = Modifier
                        .clickable { onForgotPassword() },
                    color = TealPrimary,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // ─── Loading or Error feedback ────────────────────────────

            when (authState.value) {
                is AuthState.Loading -> {
                    Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator()
                    }
                }
                is AuthState.Error -> {
                    Text(
                        text = (authState.value as AuthState.Error).message,
                        fontSize = 12.sp,
                        color = Color.Red,
                        modifier = Modifier.align(Alignment.CenterHorizontally)
                    )
                }
                is AuthState.UnAuthenticated -> {}
                is AuthState.Authenticated -> {}
            }

            Spacer(modifier = Modifier.height(24.dp))



            PrimaryButton(
                text = "Sign in",
                onClick = {
                    authViewModel.signIn(email.value, password.value)

                    if(rememberMe.value)  authViewModel.saveRememberMeDetails(email.value)
                    else authViewModel.clearRememberMeDetails()
                },
                enabled = email.value.isNotBlank() && password.value.isNotBlank()
            )
            Spacer(modifier = Modifier.height(20.dp))

            // ─── or divider ───────────
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            )
            {

                HorizontalDivider(
                    modifier = Modifier.weight(1f),
                    thickness = 2.dp,
                    color = TealLight
                )

                Text(
                    text = "or",
                    modifier = Modifier.padding(horizontal = 8.dp),
                    fontSize = 17.sp,
                    color = TextLight

                )
                HorizontalDivider(
                    modifier = Modifier.weight(1f),
                    thickness = 2.dp,
                    color = TealLight
                )
            }

            Spacer(modifier = Modifier.height(5.dp))



            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                SocialButton(label = "G", color = OrangeDrop, onClick = {
                    authViewModel.signInWithGoogle(context)
                    if(authState.value is AuthState.Authenticated)  authViewModel.saveRememberMeDetails(email.value)
                    else authViewModel.clearRememberMeDetails()
                })

                // !!! -> onClick will be added later <- !!!!
//                SocialButton(label = "f", color = TealPrimary)
//                SocialButton(label = "𝕏", color = TealDark)
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = buildAnnotatedString {
                    append("No account?  ")
                    withStyle(SpanStyle(color = TealPrimary, fontWeight = FontWeight.SemiBold)) {
                        append("Create one")
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

// ─────────────────────────────────────────────────────────────
//  Screen 2 – User Registration (Sign Up)
// ─────────────────────────────────────────────────────────────
@Composable
fun SignUpScreen(
    onSignUpSuccess: () -> Unit,
    onNavigateToSignIn: () -> Unit,
    authViewModel: AuthViewModel
) {
    var fullName by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var acceptedPolicy by remember { mutableStateOf(false) }
    var showPass by remember { mutableStateOf(false) }

//    ─── For Authentication & coroutine scope ──────────
    val context = LocalContext.current
    val authManager = remember { AuthManager(context) }
    val coroutineScope = rememberCoroutineScope()

    SugarCareBackground {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 32.dp),
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "Get Started",
                style = MaterialTheme.typography.headlineMedium,
                color = GreenAccent,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(24.dp))

            SugarCareTextField(
                value = fullName,
                onValueChange = { fullName = it },
                label = "Full Name"
            )
            Spacer(modifier = Modifier.height(12.dp))

            SugarCareTextField(
                value = email,
                onValueChange = { email = it },
                label = "Email"
            )
            Spacer(modifier = Modifier.height(12.dp))

            SugarCareTextField(
                value = password,
                onValueChange = { password = it },
                label = "Password",
                isPassword = !showPass,
                trailingIcon = {
                    IconButton(onClick = { showPass = !showPass }) {
                        Icon(
                            imageVector = if (showPass) Icons.Filled.Visibility else Icons.Filled.VisibilityOff,
                            contentDescription = null,
                            tint = TealPrimary
                        )
                    }
                }

            )
            Spacer(modifier = Modifier.height(12.dp))

            SugarCareTextField(
                value = confirmPassword,
                onValueChange = { confirmPassword = it },
                label = "Confirm Password",
                isPassword = !showPass,
                trailingIcon = {
                    IconButton(onClick = { showPass = !showPass }) {
                        Icon(
                            imageVector = if (showPass) Icons.Filled.Visibility else Icons.Filled.VisibilityOff,
                            contentDescription = null,
                            tint = TealPrimary
                        )
                    }
                }
            )

            Spacer(modifier = Modifier.height(12.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                Checkbox(
                    checked = acceptedPolicy,
                    onCheckedChange = { acceptedPolicy = it },
                    colors = CheckboxDefaults.colors(checkedColor = GreenAccent)
                )
                Text(
                    text = buildAnnotatedString {
                        append("Accept ")
                        withStyle(
                            SpanStyle(
                                color = TealPrimary,
                                fontWeight = FontWeight.SemiBold
                            )
                        ) {
                            append("Privacy Policy")
                        }
                        append(" & Registration Rules")
                    },
                    fontSize = 13.sp,
                    color = TextMedium
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            SecondaryButton(
                text = "Sign Up",
                onClick = {
                    coroutineScope.launch {
                        authManager.createAccountWithEmail(email, password)
                            .collect { response ->
                                if (response is AuthResponse.Success) {
                                    onSignUpSuccess()
                                }
                            }
                    }
                }
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = buildAnnotatedString {
                    append("Already have an account? ")
                    withStyle(SpanStyle(color = TealPrimary, fontWeight = FontWeight.SemiBold)) {
                        append("Sign In")
                    }
                },
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .clickable { onNavigateToSignIn() },
                fontSize = 14.sp
            )

            Spacer(modifier = Modifier.height(12.dp))

            // ── Social sign-up row ────────────────────────────
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                SocialButton(label = "G", color = OrangeDrop, onClick = {
                    coroutineScope.launch {
                        authManager.signInWithGoogle()
                            .collect { response ->
                                if (response is AuthResponse.Success) {
                                    onSignUpSuccess()
                                }
                            }
                    }
                })

                // !!! -> onClick will be added later <- !!!!
//                SocialButton(label = "f", color = TealPrimary)
//                SocialButton(label = "𝕏", color = TealDark)
            }
        }
    }
}


@Composable
private fun SocialButton(
    label: String,
    color: Color, // androidx.compose.ui.graphics removed -> for cleaner route
    onClick: () -> Unit
) {
    Surface(
        shape = androidx.compose.foundation.shape.CircleShape,
        color = color.copy(alpha = 0.12f),
        modifier = Modifier
            .size(52.dp)
            .clickable { onClick() }
    ) {
        Box(contentAlignment = Alignment.Center) {
            Text(
                text = label,
                color = color,
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp
            )
        }
    }
}


// ---- to be removed

@Preview
@Composable
fun SignUpScreenPreview() {
    SugarCareTheme {
        SignUpScreen(
            onSignUpSuccess = {},
            onNavigateToSignIn = {},
            authViewModel = AuthViewModel(context = LocalContext.current)
        )
    }
}

@Preview
@Composable
fun SignInScreenPreview() {
    SugarCareTheme {
        SignInScreen(
            onSignInSuccess = {},
            onNavigateToSignUp = {},
            authViewModel = AuthViewModel(context = LocalContext.current),
            onForgotPassword = {}
        )
    }
}
