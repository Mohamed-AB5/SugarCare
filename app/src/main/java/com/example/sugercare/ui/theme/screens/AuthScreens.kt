package com.example.sugercare.ui.theme.screens

import android.util.Log
import androidx.activity.ComponentActivity
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.sugercare.viewModels.AuthViewModel
import com.example.sugercare.viewModels.AuthState
import com.sugarcare.app.ui.components.*
import com.sugarcare.app.ui.theme.*
import kotlin.text.isNotBlank
import com.sugarcare.app.R


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
    val context = LocalContext.current
    //    ─── For Authentication & Auth View Model ──────────
    val activity      = LocalContext.current as ComponentActivity
    val email         = authViewModel.email.collectAsState()
    val password      = authViewModel.password.collectAsState()
    val visibleFields = authViewModel.visiblePasswordFields.collectAsState()
    val authState     = authViewModel.authState.collectAsState()
    val rememberMe    = authViewModel.rememberMe.collectAsState()

    Log.d("TextState1", "Current email Value: '$email'")
    Log.d("TextState", "Current TextField Value: '$password'")
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
                .verticalScroll(rememberScrollState())
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
                isPassword = "password" !in visibleFields.value,
                trailingIcon = {
                    IconButton(onClick = { authViewModel.togglePasswordVisibility("password") }) {
                        Icon(
                            imageVector = if ("password" in visibleFields.value) Icons.Filled.Visibility else Icons.Filled.VisibilityOff,
                            contentDescription = null,
                            tint = TealPrimary
                        )
                    }
                }
            )

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                "Forgot password?",
                modifier = Modifier
                    .align(Alignment.End)
                    .clickable { onForgotPassword() },
                color = TealPrimary,
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold
            )

            Spacer(Modifier.height(12.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                Checkbox(
                    checked = rememberMe.value,
                    onCheckedChange = { authViewModel.toggleRememberMe() },
                    colors = CheckboxDefaults.colors(checkedColor = TealPrimary)
                )
                Text("Remember Me", color = TextMedium)
            }

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
                    authViewModel.validateSignIn(email.value, password.value)
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
                SocialButton(
                    icon = painterResource(R.drawable.ic_google),
                    color = OrangeDrop,
                    brush = Brush.verticalGradient(
                        listOf(Color(0xFFEED4C8), OrangeDrop2)
                    ),
                    onClick = {
                        authViewModel.signInWithGoogle(context)
                    })
                SocialButton(
                    icon = painterResource(R.drawable.ic_facebook),
                    color = TealPrimary,
                    brush = Brush.verticalGradient(listOf(Color(0xFFC6F1F1), TealPrimary2)),
                    onClick = {
                        authViewModel.signInWithFacebook(activity)
                    })

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
                    .clickable { (onNavigateToSignUp()) },
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
    val context = LocalContext.current

//    ─── For Authentication & View Model ──────────
    val activity        = LocalContext.current as ComponentActivity
    val email           = authViewModel.email.collectAsState()
    val password        = authViewModel.password.collectAsState()
    val authState       = authViewModel.authState.collectAsState()
    val visibleFields   = authViewModel.visiblePasswordFields.collectAsState()
    val fullName        = authViewModel.fullName.collectAsState()
    val confirmPassword = authViewModel.confirmPassword.collectAsState()
    var acceptedPolicy by remember { mutableStateOf(false) }


    LaunchedEffect(authState.value) {
        when (authState.value) {
            is AuthState.Loading -> {}
            is AuthState.Authenticated -> onSignUpSuccess()
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
                text = "Get Started",
                style = MaterialTheme.typography.headlineMedium,
                color = GreenAccent,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(24.dp))

            SugarCareTextField(
                value = fullName.value,
                onValueChange = { authViewModel.updateFullName(it) },
                label = "Full Name"
            )
            Spacer(modifier = Modifier.height(12.dp))

            SugarCareTextField(
                value = email.value,
                onValueChange = { authViewModel.updateEmail(it) },
                label = "Email"
            )
            Spacer(modifier = Modifier.height(12.dp))

            SugarCareTextField(
                value = password.value,
                onValueChange = { authViewModel.updatePassword(it) },
                label = "Password",
                isPassword = "password" !in visibleFields.value,
                trailingIcon = {
                    IconButton(onClick = { authViewModel.togglePasswordVisibility("password") }) {
                        Icon(
                            imageVector = if ("password" in visibleFields.value) Icons.Filled.Visibility else Icons.Filled.VisibilityOff,
                            contentDescription = null,
                            tint = TealPrimary
                        )
                    }
                }

            )
            Spacer(modifier = Modifier.height(12.dp))

            SugarCareTextField(
                value = confirmPassword.value,
                onValueChange = { authViewModel.updateConfirmPassword(it) },
                label = "Confirm Password",
                isPassword = "confirmPassword" !in visibleFields.value,
                trailingIcon = {
                    IconButton(onClick = { authViewModel.togglePasswordVisibility("confirmPassword") }) {
                        Icon(
                            imageVector = if ("confirmPassword" in visibleFields.value) Icons.Filled.Visibility else Icons.Filled.VisibilityOff,
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


            SecondaryButton(
                text = "Sign Up",
                onClick = {
                    authViewModel.clearFields()
                    authViewModel.signUp(email.value, password.value)
                },
                enabled = email.value.isNotBlank()
                        && password.value.isNotBlank()
                        && confirmPassword.value.isNotBlank()
                        && acceptedPolicy
                        && fullName.value.isNotBlank()
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
                SocialButton(
                    icon = painterResource(R.drawable.ic_google),
                    color = OrangeDrop,
                    brush = Brush.verticalGradient(
                        listOf(Color(0xFFEED4C8), OrangeDrop2)
                    ),
                    onClick = {
                        authViewModel.signInWithGoogle(context)
                    })
                SocialButton(
                    icon = painterResource(R.drawable.ic_facebook),
                    color = TealPrimary,
                    brush = Brush.verticalGradient(listOf(Color(0xFFC6F1F1), TealPrimary2)),
                    onClick = {
                        authViewModel.signInWithFacebook(activity)
                    })

            }
        }
    }
}


@Composable
private fun SocialButton(
    icon: Painter,
    color: Color, // androidx.compose.ui.graphics removed -> for cleaner route
    brush: Brush,
    onClick: () -> Unit
) {
    Surface(
        shape = CircleShape,
        shadowElevation = 6.dp,
        color = color.copy(alpha = 0.12f),
    ) {
        Box(
            modifier = Modifier
                .size(52.dp)
                .clip(CircleShape)
                .background(brush = brush)
                .clickable { onClick() },
            contentAlignment = Alignment.Center
        ) {
            Icon(
                painter = icon,
                contentDescription = null,
                modifier = Modifier.size(24.dp),
                tint = Color.Unspecified
            )
        }
    }
}



