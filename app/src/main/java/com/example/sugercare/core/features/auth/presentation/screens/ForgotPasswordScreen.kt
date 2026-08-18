package com.example.sugercare.core.features.auth.presentation.screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.sugercare.core.features.auth.presentation.AuthViewModel
import com.example.sugercare.core.features.auth.presentation.ResetPassState
import com.example.sugercare.core.features.profile.presentation.screens.newScreenFieldColors
import com.sugarcare.app.ui.components.GradientButton
import com.sugarcare.app.ui.theme.LocalDarkTheme
import com.sugarcare.app.ui.theme.OrangeDrop
import com.sugarcare.app.ui.theme.OrangeDrop2
import com.sugarcare.app.ui.theme.TealLight
import com.sugarcare.app.ui.theme.TealPrimary

// ══════════════════════════════════════════════════════════════
//  2. FORGOT PASSWORD
// ══════════════════════════════════════════════════════════════
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ForgotPasswordScreen(
    navController: NavHostController,
    authViewModel: AuthViewModel
) {
    val email          = authViewModel.email.collectAsState()
    val resetPassState = authViewModel.resetPassState.collectAsState()
    val context        = LocalContext.current

    LaunchedEffect(resetPassState.value) {
        if (resetPassState.value is ResetPassState.Success) {
            Toast.makeText(context, "Reset email sent! Check your inbox.", Toast.LENGTH_LONG).show()
            authViewModel.resetPasswordState()
            navController.popBackStack()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, null,
                            tint = MaterialTheme.colorScheme.onBackground)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background)
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { padding ->
        Column(
            Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 32.dp),
            Arrangement.Center, Alignment.CenterHorizontally
        ) {
            Box(
                Modifier
                    .size(88.dp)
                    .clip(CircleShape)
                    .background(TealLight),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    Icons.Filled.Lock, null,
                    tint = TealPrimary, modifier = Modifier.size(44.dp)
                )
            }
            Spacer(Modifier.height(28.dp))
            Text(
                "Confirm it's you",
                fontSize = 26.sp, fontWeight = FontWeight.Bold,
                color = if (LocalDarkTheme.current.value) Color(0xFFE0F2F1) else Color(0xFF1A2B2B)
            )
            Spacer(Modifier.height(8.dp))
            Text(
                "Enter your registered email and\nwe'll send a verification code.",
                fontSize = 14.sp,
                color = if (LocalDarkTheme.current.value) Color(0xFFE0F2F1).copy(0.6f) else Color(
                    0xFF4A6565
                ),
                textAlign = TextAlign.Center, lineHeight = 22.sp
            )
            Spacer(Modifier.height(36.dp))

            OutlinedTextField(
                value = email.value,
                onValueChange = { authViewModel.updateEmail(it) },
                label = { Text("Email address") },
                leadingIcon = { Icon(Icons.Filled.Email, null, tint = TealPrimary) },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(28.dp),
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                colors = newScreenFieldColors()
            )
            Spacer(Modifier.height(28.dp))


            GradientButton(
                onClick = { authViewModel.sendPasswordReset(email.value) },
                text = "Send Reset Email",
                textSize = 16.sp,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                enabled = email.value.isNotBlank()
                        && resetPassState.value !is ResetPassState.Loading,
                color1 = OrangeDrop,
                color2 = OrangeDrop2
            )
            {
                if (resetPassState.value is ResetPassState.Loading) {
                    CircularProgressIndicator(
                        color = Color.White,
                        modifier = Modifier.size(22.dp), strokeWidth = 2.dp
                    )
                }
                else {
                    Text("Send Reset Email", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                }
            }

/*            Button(
                onClick = { authViewModel.sendPasswordReset(email.value) },
                modifier  = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape     = RoundedCornerShape(28.dp),
                enabled   = email.value.isNotBlank()
                        && resetPassState.value !is ResetPassState.Loading,
                colors    = ButtonDefaults.buttonColors(containerColor = TealPrimary),
                elevation = ButtonDefaults.buttonElevation(0.dp)
            ) {
                if (resetPassState.value is ResetPassState.Loading)
                    CircularProgressIndicator(color = Color.White,
                        modifier = Modifier.size(22.dp), strokeWidth = 2.dp)
                else
                    Text("Send Reset Email", fontWeight = FontWeight.Bold, fontSize = 16.sp)
            }*/

            if (resetPassState.value is ResetPassState.Error) {
                Spacer(Modifier.height(8.dp))
                Text((resetPassState.value as ResetPassState.Error).message,
                    color = Color.Red, fontSize = 12.sp)
            }

            Spacer(Modifier.height(20.dp))
            Text("← Back to Sign In",
                color = TealPrimary, fontSize = 14.sp, fontWeight = FontWeight.SemiBold,
                modifier = Modifier.clickable { navController.popBackStack() })
        }
    }
}

