package com.sugarcare.app.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sugarcare.app.R
import com.sugarcare.app.ui.components.*
import com.sugarcare.app.ui.theme.*

/**
 * Screen 1 – Welcome & Landing
 * Shows the SugarCare logo, tagline, and Sign In / Sign Up buttons.
 */
@Composable
fun WelcomeScreen(
    onSignIn: () -> Unit,
    onSignUp: () -> Unit
) {
    SugarCareBackground {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 40.dp),
            verticalArrangement   = Arrangement.Center,
            horizontalAlignment   = Alignment.CenterHorizontally
        ) {
            // ── Logo ──────────────────────────────────────────
            // Replace R.drawable.ic_logo with your actual vector asset
            Image(
                painter            = painterResource(id = R.drawable.ic_logo),
                contentDescription = "SugarCare logo",
                modifier           = Modifier.size(160.dp)
            )

            Spacer(modifier = Modifier.height(24.dp))

            // ── App name ──────────────────────────────────────
            Text(
                text       = "Sugar",
                color      = TealPrimary,
                fontSize   = 40.sp,
                fontWeight = FontWeight.Bold,
                lineHeight = 44.sp
            )
            Text(
                text       = "Care",
                color      = GreenAccent,
                fontSize   = 40.sp,
                fontWeight = FontWeight.Bold,
                lineHeight = 44.sp
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text      = "Welcome",
                style     = MaterialTheme.typography.headlineMedium,
                color     = TealPrimary,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text      = "Already have an account? or New user?",
                color     = TextMedium,
                fontSize  = 14.sp,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(32.dp))

            // ── CTA buttons ───────────────────────────────────
            PrimaryButton(text = "Sign In", onClick = onSignIn)

            Spacer(modifier = Modifier.height(16.dp))

            SecondaryButton(text = "Sign Up", onClick = onSignUp)
        }
    }
}
