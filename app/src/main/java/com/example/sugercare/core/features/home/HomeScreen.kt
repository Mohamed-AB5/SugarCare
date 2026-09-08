package com.example.sugercare.core.features.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Assignment
import androidx.compose.material.icons.automirrored.filled.Chat
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.sugercare.core.features.counter.presentation.CounterViewModel
import com.example.sugercare.core.features.profile.presentation.ProfileViewModel
import com.sugarcare.app.navigation.Screen
import com.sugarcare.app.ui.theme.*

// ── Brand colors ──────────────────────────────────────────────
private val CardTeal    = Color(0xFFB2DFDB)   // hero bg
private val CardBlue    = Color(0xFFDCEEFB)   // analytics
private val CardPurple  = Color(0xFFEDE0FF)   // medications
private val CardOrange  = Color(0xFFFFE8D4)   // chatbot / meals
private val CardRed     = Color(0xFFFFE5E5)   // emergency
private val CardGreen   = Color(0xFFDFF5E8)   // challenge
/**
 * Home Screen – main dashboard with Glucose Logs, Meal Plan,
 * Weekly Analytics, and Medication Plan quick tiles.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(    navController: NavHostController,
                   profileViewModel: ProfileViewModel,
                   counterViewModel: CounterViewModel) {

    val isDark  = LocalDarkTheme.current.value
    val bgColor = if (isDark) BackgroundDark else Color(0xFFF5FAFA)
    val state = counterViewModel.uiState.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(bgColor)
    ) {

        // ── Top bar ───────────────────────────────────────────
        TopAppBar(
            title = {
                Column {
                    Text("Today's Health Tasks",
                        fontWeight = FontWeight.ExtraBold,
                        fontSize   = 20.sp,
                        color      = if (isDark) TextDarkMode else Color(0xFF1A2B2B))
                }
            },
            actions = {
                // Challenge streak badge
                Surface(
                    shape = RoundedCornerShape(20.dp),
                    color = Color.White,
                    tonalElevation = 0.dp,
                    shadowElevation = 2.dp
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("🎁", fontSize = 14.sp)
                        Spacer(Modifier.width(4.dp))
                        Text(
                            "${ state.value.bestStreak }",
                            fontSize   = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color      = OrangeDrop)
                    }
                }
                Spacer(Modifier.width(12.dp))
            },
            colors = TopAppBarDefaults.topAppBarColors(containerColor = bgColor)
        )

        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Spacer(Modifier.height(4.dp))

            // ── HERO: Glucose Logs ────────────────────────────
            HeroCard(
                title       = "Glucose Logs",
                value       = "180",
                unit        = "mg/dL",
                buttonText  = "Record",
                icon        = Icons.Filled.Favorite,
                cardColor   = CardTeal,
                accentColor = TealPrimary,
                onClick     = { navController.navigate(Screen.Logs.route) }
            )

            // ── ROW: Analytics | Medications ─────────────────
            Row(
                modifier              = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                SmallCard(
                    modifier    = Modifier.weight(1f),
                    title       = "Weekly\nAnalytics",
                    value       = "122",
                    unit        = "mg/dL avg",
                    icon        = Icons.Filled.BarChart,
                    cardColor   = CardBlue,
                    accentColor = Color(0xFF1870A0),
                    buttonText  = "Analyze",
                    onClick     = { navController.navigate(Screen.WeeklyAnalytics.route) }
                )
                SmallCard(
                    modifier    = Modifier.weight(1f),
                    title       = "Medication\nPlan",
                    value       = "3",
                    unit        = "active",
                    icon        = Icons.Filled.Medication,
                    cardColor   = CardPurple,
                    accentColor = Color(0xFF6030A0),
                    buttonText  = "Manage",
                    onClick     = { navController.navigate(Screen.Medications.route) }
                )
            }

            // ── ROW CARD: Meal Plan ───────────────────────────
            RowCard(
                title       = "Meal Plan",
                value       = "3 meals today",
                icon        = Icons.Filled.Restaurant,
                cardColor   = CardOrange,
                accentColor = OrangeDrop,
                onClick     = { navController.navigate(Screen.MealPlan.route) }
            )


            // ── ROW CARD: Emergency ───────────────────────────
            RowCard(
                title       = "Emergency Contacts",
                value       = "Tap to call immediately",
                icon        = Icons.Filled.Emergency,
                cardColor   = CardRed,
                accentColor = Color(0xFFE53935),
                onClick     = { navController.navigate(Screen.EmergencyContact.route) }
            )
            // Mohamed Abdo : replaced with emergency to be faster to access
            // ── ROW CARD: AI ChatBot ──────────────────────────
            RowCard(
                title       = "AI Sugar Chat",
                value       = "Ask me anything",
                icon        = Icons.AutoMirrored.Filled.Chat,
                cardColor   = CardGreen,
                accentColor = Color(0xFF1F7A4A),
                onClick     = { navController.navigate(Screen.ChatScreen.route) }
            )


            // ── ROW CARD: 90-Day Challenge ────────────────────
            RowCard(
                title       = "90-Day Challenge",
                value       = "${state.value.bestStreak} Highset Streak Ever🐦‍🔥",
                icon        = Icons.Filled.EmojiEvents,
                cardColor   = CardTeal,
                accentColor = TealPrimary,
                onClick     = { navController.navigate(Screen.CounterScreen.route) }
            )

            Spacer(Modifier.height(12.dp))
        }

        // ── Bottom Nav ────────────────────────────────────────
        NavigationBar(
            containerColor = if (isDark) SurfaceDark else Color.White,
            tonalElevation = 0.dp
        ) {
            listOf(
                Triple("Home",    Icons.Filled.Home,                    Screen.Home.route),
                Triple("Logs",    Icons.AutoMirrored.Filled.Assignment, Screen.Logs.route),
                Triple("Meals",   Icons.Filled.Restaurant,              Screen.MealPlan.route),
                Triple("Profile", Icons.Filled.Person,                  Screen.Profile.route)
            ).forEach { (label, icon, route) ->
                NavigationBarItem(
                    selected = route == Screen.Home.route,
                    onClick  = {
                        if (route != Screen.Home.route)
                            navController.navigate(route) { launchSingleTop = true }
                    },
                    icon   = { Icon(icon, contentDescription = label) },
                    label  = { Text(label, fontSize = 11.sp) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor   = TealPrimary,
                        unselectedIconColor = if (isDark) Color(0xFF80CBC4) else TextMedium,
                        indicatorColor      = TealPrimary.copy(alpha = 0.15f)
                    )
                )
            }
        }
    }
}

// ═════════════════════════════════════════════════════════════
//  HERO CARD  — like "Blood Sugar" in the reference
// ═════════════════════════════════════════════════════════════
@Composable
private fun HeroCard(
    title      : String,
    value      : String,
    unit       : String,
    buttonText : String,
    icon       : ImageVector,
    cardColor  : Color,
    accentColor: Color,
    onClick    : () -> Unit
) {
    Card(
        modifier  = Modifier
            .fillMaxWidth()
            .height(160.dp),
        shape     = RoundedCornerShape(24.dp),
        colors    = CardDefaults.cardColors(containerColor = cardColor),
        elevation = CardDefaults.cardElevation(0.dp)
    ) {
        Box(Modifier.fillMaxSize()) {

            // Background large icon (decorative)
            Icon(
                imageVector        = icon,
                contentDescription = null,
                tint               = accentColor.copy(alpha = 0.12f),
                modifier           = Modifier
                    .size(160.dp)
                    .align(Alignment.CenterEnd)
                    .offset(x = 30.dp)
            )

            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 22.dp, vertical = 18.dp),
                verticalAlignment     = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Left content
                Column {
                    Text(title,
                        fontWeight = FontWeight.Bold,
                        fontSize   = 16.sp,
                        color      = accentColor)
                    Spacer(Modifier.height(10.dp))
                    Row(verticalAlignment = Alignment.Bottom) {
                        Text(value,
                            fontWeight = FontWeight.ExtraBold,
                            fontSize   = 30.sp,
                            color      = Color(0xFF1A2B2B))
                        Spacer(Modifier.width(6.dp))
                        Text(unit,
                            fontSize = 14.sp,
                            color    = Color(0xFF4A6565),
                            modifier = Modifier.padding(bottom = 4.dp))
                    }
                    Spacer(Modifier.height(14.dp))
                    Button(
                        onClick        = onClick,
                        shape          = RoundedCornerShape(22.dp),
                        colors         = ButtonDefaults.buttonColors(containerColor = accentColor),
                        elevation      = ButtonDefaults.buttonElevation(0.dp),
                        contentPadding = PaddingValues(horizontal = 24.dp, vertical = 10.dp)
                    ) {
                        Text(buttonText,
                            fontWeight = FontWeight.SemiBold,
                            fontSize   = 14.sp)
                    }
                }

                // Right icon circle
                Box(
                    modifier         = Modifier
                        .size(72.dp)
                        .clip(CircleShape)
                        .background(accentColor.copy(alpha = 0.18f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(icon, null,
                        tint     = accentColor,
                        modifier = Modifier.size(38.dp))
                }
            }
        }
    }
}

// ═════════════════════════════════════════════════════════════
//  SMALL CARD  — like "Heart Rate" / "Blood Pressure"
// ═════════════════════════════════════════════════════════════
@Composable
private fun SmallCard(
    modifier   : Modifier,
    title      : String,
    value      : String,
    unit       : String,
    icon       : ImageVector,
    cardColor  : Color,
    accentColor: Color,
    buttonText : String,
    onClick    : () -> Unit
) {
    Card(
        modifier  = modifier,
        shape     = RoundedCornerShape(24.dp),
        colors    = CardDefaults.cardColors(containerColor = cardColor),
        elevation = CardDefaults.cardElevation(0.dp)
    ) {
        Box(Modifier.fillMaxWidth()) {
            // Decorative icon top-right
            Icon(
                imageVector        = icon,
                contentDescription = null,
                tint               = accentColor.copy(alpha = 0.12f),
                modifier           = Modifier
                    .size(72.dp)
                    .align(Alignment.TopEnd)
                    .offset(x = 14.dp, y = (-8).dp)
            )
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(icon, null,
                        tint     = accentColor,
                        modifier = Modifier.size(15.dp))
                    Spacer(Modifier.width(4.dp))
                    Text(title,
                        fontWeight = FontWeight.Bold,
                        fontSize   = 12.sp,
                        color      = accentColor,
                        lineHeight = 15.sp)
                }
                Spacer(Modifier.height(10.dp))
                Text(value,
                    fontWeight = FontWeight.ExtraBold,
                    fontSize   = 28.sp,
                    color      = Color(0xFF1A2B2B))
                Text(unit,
                    fontSize = 11.sp,
                    color    = Color(0xFF4A6565))
                Spacer(Modifier.height(14.dp))
                Button(
                    onClick        = onClick,
                    shape          = RoundedCornerShape(18.dp),
                    colors         = ButtonDefaults.buttonColors(containerColor = accentColor),
                    elevation      = ButtonDefaults.buttonElevation(0.dp),
                    modifier       = Modifier.fillMaxWidth(),
                    contentPadding = PaddingValues(vertical = 9.dp)
                ) {
                    Text(buttonText,
                        fontSize   = 12.sp,
                        fontWeight = FontWeight.SemiBold)
                }
            }
        }
    }
}

// ═════════════════════════════════════════════════════════════
//  ROW CARD  — like "Drinking Water" / "Step Count"
// ═════════════════════════════════════════════════════════════
@Composable
private fun RowCard(
    title      : String,
    value      : String,
    icon       : ImageVector,
    cardColor  : Color,
    accentColor: Color,
    onClick    : () -> Unit
) {
    Card(
        modifier  = Modifier.fillMaxWidth(),
        shape     = RoundedCornerShape(20.dp),
        colors    = CardDefaults.cardColors(containerColor = cardColor),
        elevation = CardDefaults.cardElevation(0.dp),
        onClick   = onClick
    ) {
        Row(
            modifier          = Modifier
                .fillMaxWidth()
                .padding(horizontal = 18.dp, vertical = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Icon box
            Box(
                modifier         = Modifier
                    .size(56.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(accentColor.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(icon, null,
                    tint     = accentColor,
                    modifier = Modifier.size(30.dp))
            }

            Spacer(Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(title,
                    fontWeight = FontWeight.Bold,
                    fontSize   = 15.sp,
                    color      = Color(0xFF1A2B2B))
                Spacer(Modifier.height(2.dp))
                Text(value,
                    fontSize   = 13.sp,
                    fontWeight = FontWeight.SemiBold,
                    color      = accentColor,
                    maxLines   = 1,
                    overflow   = TextOverflow.Ellipsis)
            }

            // Chevron >>
            Text(">>",
                fontSize   = 14.sp,
                fontWeight = FontWeight.Bold,
                color      = accentColor.copy(alpha = 0.6f))
        }
    }
}