package com.sugarcare.app.ui.screens

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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.sugercare1.navigation.Screen
import com.sugarcare.app.ui.components.*
import com.sugarcare.app.ui.theme.*

data class Medication(val name: String, var taken: Boolean = true)

@Preview(showBackground = true)
@Composable
fun MedicationsScreenPreview() {
    SugarCareTheme { MedicationsScreen(navController = rememberNavController()) }
}

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

    SugarCareBackground {
        Column(modifier = Modifier.fillMaxSize()) {
            Row(
                modifier = Modifier.fillMaxWidth()
                    .padding(start = 24.dp, end = 24.dp, top = 48.dp, bottom = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("❤ My Medications 🌿",
                    style = MaterialTheme.typography.headlineMedium,
                    color = TealPrimary, fontWeight = FontWeight.Bold)
            }

            Icon(Icons.Filled.Medication, null, tint = TealPrimary,
                modifier = Modifier.align(Alignment.CenterHorizontally).size(48.dp))

            Spacer(Modifier.height(8.dp))

            SugarCareCard(modifier = Modifier.padding(horizontal = 24.dp)) {
                Text("Medications", fontWeight = FontWeight.Bold, fontSize = 18.sp,
                    color = MaterialTheme.colorScheme.onSurface)
                Spacer(Modifier.height(12.dp))
                medications.forEachIndexed { index, med ->
                    MedicationRow(
                        name    = med.name,
                        checked = med.taken,
                        onCheckedChange = { checked ->
                            medications = medications.toMutableList().also {
                                it[index] = it[index].copy(taken = checked)
                            }
                        }
                    )
                    if (index < medications.lastIndex) Spacer(Modifier.height(8.dp))
                }
                Spacer(Modifier.height(16.dp))
                
                
                SugarCareGradientButton(
                    text = "Add Medication",
                    gradientColors = listOf(Color(0xFF3B9E9E), Color(0xFF7FE3E1)),
                    onClick = { medications = medications + Medication("New Medication", false) }
                )
            }

            Spacer(Modifier.weight(1f))

            //  Bottom Nav (Home/Logs/Meals/Profile)
            BottomNavBar(navController, Screen.Medications.route)
        }
    }
}

@Composable
private fun MedicationRow(name: String, checked: Boolean, onCheckedChange: (Boolean) -> Unit) {
    Row(modifier = Modifier.fillMaxWidth().height(52.dp), verticalAlignment = Alignment.CenterVertically) {
        Surface(modifier = Modifier.weight(1f).fillMaxHeight(),
            shape = RoundedCornerShape(26.dp),
            color = if (checked) TealLight.copy(alpha = 0.4f) else Color.LightGray.copy(alpha = 0.2f),
            border = androidx.compose.foundation.BorderStroke(1.dp, TealPrimary.copy(alpha = 0.5f))
        ) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.CenterStart) {
                Text(name, modifier = Modifier.padding(start = 16.dp),
                    fontSize = 15.sp, color = MaterialTheme.colorScheme.onSurface)
            }
        }
        Spacer(Modifier.width(8.dp))
        Surface(Modifier.size(40.dp), shape = RoundedCornerShape(50), color = TealPrimary) {
            IconToggleButton(checked = checked, onCheckedChange = onCheckedChange) {
                Icon(Icons.Filled.Check, null, tint = Color.White, modifier = Modifier.size(20.dp))
            }
        }
    }
}
