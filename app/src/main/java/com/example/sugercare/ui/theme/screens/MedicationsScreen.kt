package com.example.sugercare.ui.theme.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.sugarcare.app.navigation.Screen
import com.sugarcare.app.ui.theme.*

data class Medication(val name: String, var taken: Boolean = true)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MedicationsScreen(navController: NavHostController) {
    var medications by remember {
        mutableStateOf(listOf(
            Medication("insulin c. pre-meal", true),
            Medication("metformin",            true),
            Medication("lisinopril (daily)",   true)
        ))
    }

    val isDark    = LocalDarkTheme.current.value
    val bgColor   = if (isDark) BackgroundDark else BackgroundLight
    val cardColor = if (isDark) SurfaceDark    else Color.White
    val textColor = if (isDark) TextDarkMode   else TextDark
    val navColor  = if (isDark) SurfaceDark    else Color.White
    val navText   = if (isDark) TextMediumDark else TextMedium

    Column(Modifier.fillMaxSize().background(bgColor)) {

        // ── Header ────────────────────────────────────────────
        Row(
            Modifier.fillMaxWidth().padding(start = 24.dp, end = 24.dp, top = 48.dp, bottom = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("❤ My Medications 🌿",
                style = MaterialTheme.typography.headlineMedium,
                color = TealPrimary, fontWeight = FontWeight.Bold, fontSize = 24.sp)
        }

        Icon(Icons.Filled.Medication, null, tint = TealPrimary,
            modifier = Modifier.align(Alignment.CenterHorizontally).size(48.dp))

        Spacer(Modifier.height(12.dp))

        // ── Medications Card ──────────────────────────────────
        Card(
            Modifier.padding(horizontal = 24.dp).fillMaxWidth(),
            shape     = RoundedCornerShape(20.dp),
            colors    = CardDefaults.cardColors(containerColor = cardColor),
            elevation = CardDefaults.cardElevation(0.dp)
        ) {
            Column(Modifier.padding(20.dp)) {
                Text("Medications", fontWeight = FontWeight.Bold,
                    fontSize = 20.sp, color = textColor)
                Spacer(Modifier.height(16.dp))

                medications.forEachIndexed { index, med ->
                    MedicationRow(
                        name            = med.name,
                        checked         = med.taken,
                        isDark          = isDark,
                        textColor       = textColor,
                        onCheckedChange = { checked ->
                            medications = medications.toMutableList().also {
                                it[index] = it[index].copy(taken = checked)
                            }
                        }
                    )
                    if (index < medications.lastIndex) Spacer(Modifier.height(12.dp))
                }

                Spacer(Modifier.height(20.dp))

                Button(
                    onClick = { medications = medications + Medication("New Medication", false) },
                    modifier = Modifier.fillMaxWidth().height(56.dp),
                    shape = RoundedCornerShape(28.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = TealPrimary),
                    elevation = ButtonDefaults.buttonElevation(0.dp)
                ) {
                    Text("Add medication", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                    Spacer(Modifier.width(8.dp))
                    Icon(Icons.Filled.Add, null)
                }
            }
        }

        Spacer(Modifier.weight(1f))

        // ── Bottom Nav ────────────────────────────────────────
        NavigationBar(containerColor = navColor, tonalElevation = 0.dp) {
            listOf(
                Triple("Home",    Icons.Filled.Home,       Screen.Home.route),
                Triple("Logs",    Icons.Filled.Favorite,   Screen.Logs.route),
                Triple("Meals",   Icons.Filled.Restaurant, Screen.MealPlan.route),
                Triple("Profile", Icons.Filled.Person,     Screen.Profile.route)
            ).forEach { (label, icon, route) ->
                NavigationBarItem(
                    selected = route == Screen.Medications.route,
                    onClick  = { navController.navigate(route) { launchSingleTop = true } },
                    icon     = { Icon(icon, null) },
                    label    = { Text(label, fontSize = 12.sp) },
                    colors   = NavigationBarItemDefaults.colors(
                        selectedIconColor    = TealPrimary,
                        unselectedIconColor  = navText,
                        indicatorColor       = TealPrimary.copy(0.2f)
                    )
                )
            }
        }
    }
}

@Composable
private fun MedicationRow(
    name           : String,
    checked        : Boolean,
    isDark         : Boolean,
    textColor      : Color,
    onCheckedChange: (Boolean) -> Unit
) {
    val rowBg = if (isDark)
        if (checked) TealPrimary.copy(0.25f) else SurfaceDark.copy(0.5f)
    else
        if (checked) TealPrimary.copy(0.15f) else TealPrimary.copy(0.05f)

    Row(Modifier.fillMaxWidth().height(56.dp), verticalAlignment = Alignment.CenterVertically) {
        Surface(
            modifier = Modifier.weight(1f).fillMaxHeight(),
            shape    = RoundedCornerShape(28.dp),
            color    = rowBg,
            border   = BorderStroke(1.5.dp, if (checked) TealPrimary else TealPrimary.copy(0.4f))
        ) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.CenterStart) {
                Text(name, modifier = Modifier.padding(start = 20.dp),
                    fontSize = 16.sp, color = textColor, fontWeight = FontWeight.Medium)
            }
        }
        Spacer(Modifier.width(10.dp))
        Surface(Modifier.size(44.dp), shape = RoundedCornerShape(50),
            color = if (checked) TealPrimary else TealPrimary.copy(0.4f)) {
            IconToggleButton(checked = checked, onCheckedChange = onCheckedChange) {
                Icon(Icons.Filled.Check, null, tint = Color.White, modifier = Modifier.size(22.dp))
            }
        }
    }
}