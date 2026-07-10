package com.sugarcare.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.sugarcare.app.navigation.Screen
import com.sugarcare.app.ui.components.SugarCareBackground
import com.sugarcare.app.ui.components.SugarCareCard
import com.sugarcare.app.ui.theme.*

data class Medication(val name: String, var taken: Boolean = true)

data class MeasurementSchedule(
    val id: String = java.util.UUID.randomUUID().toString(),
    val name: String,
    val time: String,
    val isEnabled: Boolean = true
)

fun timeToMinutes(timeStr: String): Int {
    try {
        val parts = timeStr.trim().split(" ")
        if (parts.size < 2) return 0
        val timeParts = parts[0].split(":")
        if (timeParts.size < 2) return 0
        var hour = timeParts[0].toIntOrNull() ?: 0
        val minute = timeParts[1].toIntOrNull() ?: 0
        val amPm = parts[1].uppercase()
        
        if (amPm == "PM" && hour < 12) hour += 12
        if (amPm == "AM" && hour == 12) hour = 0
        return hour * 60 + minute
    } catch (e: Exception) {
        return 0
    }
}

@Preview(showBackground = true)
@Composable
fun MedicationsScreenPreview() {
    SugarCareTheme { MedicationsScreen(navController = rememberNavController()) }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MedicationsScreen(navController: NavHostController) {
    var isMedicinesTab by remember { mutableStateOf(true) }

    var medications by remember {
        mutableStateOf(listOf(
            Medication("insulin c. pre-meal", true),
            Medication("metformin",            true),
            Medication("lisinopril (daily)",   true)
        ))
    }

    var schedules by remember {
        mutableStateOf(listOf(
            MeasurementSchedule(name = "Fasting (Morning)", time = "07:30 AM", isEnabled = true),
            MeasurementSchedule(name = "Post-Breakfast (2h)", time = "10:00 AM", isEnabled = true),
            MeasurementSchedule(name = "Pre-Dinner", time = "06:30 PM", isEnabled = true)
        ).sortedBy { timeToMinutes(it.time) })
    }

    // Dialog state
    var showAddMedDialog by remember { mutableStateOf(false) }
    var newMedName by remember { mutableStateOf("") }

    var showAddScheduleDialog by remember { mutableStateOf(false) }
    var newScheduleName by remember { mutableStateOf("") }
    var newScheduleHour by remember { mutableStateOf("") }
    var newScheduleMinute by remember { mutableStateOf("") }
    var newScheduleAmPm by remember { mutableStateOf("AM") }

    SugarCareBackground {
        Column(modifier = Modifier.fillMaxSize()) {
            Row(
                modifier = Modifier.fillMaxWidth()
                    .padding(start = 24.dp, end = 24.dp, top = 48.dp, bottom = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = if (isMedicinesTab) " My Medications 🌿" else " Measurement Schedules ⏰",
                    style = MaterialTheme.typography.headlineMedium,
                    color = TealPrimary, fontWeight = FontWeight.Bold
                )
            }

            Icon(
                imageVector = if (isMedicinesTab) Icons.Filled.Medication else Icons.Filled.Schedule,
                contentDescription = null,
                tint = TealPrimary,
                modifier = Modifier.align(Alignment.CenterHorizontally).size(48.dp)
            )

            Spacer(Modifier.height(8.dp))

            // Tab Toggle Switch (Medicines vs Schedules)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp, vertical = 8.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(TealLight.copy(alpha = 0.3f))
                    .padding(4.dp),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(12.dp))
                        .background(if (isMedicinesTab) TealPrimary else Color.Transparent)
                        .clickable { isMedicinesTab = true }
                        .padding(vertical = 8.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        "Medicines",
                        color = if (isMedicinesTab) Color.White else TealDark,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )
                }
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(12.dp))
                        .background(if (!isMedicinesTab) TealPrimary else Color.Transparent)
                        .clickable { isMedicinesTab = false }
                        .padding(vertical = 8.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        "Schedules",
                        color = if (!isMedicinesTab) Color.White else TealDark,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )
                }
            }

            Spacer(Modifier.height(8.dp))

            SugarCareCard(modifier = Modifier.padding(horizontal = 24.dp)) {
                if (isMedicinesTab) {
                    Text("Medications", fontWeight = FontWeight.Bold, fontSize = 18.sp,
                        color = MaterialTheme.colorScheme.onSurface)
                    Spacer(Modifier.height(10.dp))
                    medications.forEachIndexed { index, med ->
                        MedicationRow(
                            name    = med.name,
                            checked = med.taken,
                            onCheckedChange = { checked ->
                                medications = medications.toMutableList().also {
                                    it[index] = it[index].copy(taken = checked)
                                }
                            },
                            onDelete = {
                                medications = medications.filterIndexed { i, _ -> i != index }
                            }
                        )
                        if (index < medications.lastIndex) Spacer(Modifier.height(8.dp))
                    }
                    Spacer(Modifier.height(12.dp))
                    Button(
                        onClick  = { showAddMedDialog = true },
                        modifier = Modifier.fillMaxWidth().height(45.dp),
                        shape    = RoundedCornerShape(20.dp),
                        colors   = ButtonDefaults.buttonColors(containerColor = TealPrimary),
                        elevation = ButtonDefaults.buttonElevation(0.dp)
                    ) {
                        Text("Add medication", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                        Spacer(Modifier.width(6.dp))
                        Icon(Icons.Filled.Add, null)
                    }
                } else {
                    Text("Measurement Schedules", fontWeight = FontWeight.Bold, fontSize = 18.sp,
                        color = MaterialTheme.colorScheme.onSurface)
                    Spacer(Modifier.height(10.dp))
                    schedules.forEachIndexed { index, sched ->
                        ScheduleRow(
                            schedule = sched,
                            onEnabledChange = { isEnabled ->
                                schedules = schedules.toMutableList().also {
                                    it[index] = it[index].copy(isEnabled = isEnabled)
                                }
                            },
                            onDelete = {
                                schedules = schedules.filterIndexed { i, _ -> i != index }
                            }
                        )
                        if (index < schedules.lastIndex) Spacer(Modifier.height(8.dp))
                    }
                    Spacer(Modifier.height(12.dp))
                    Button(
                        onClick  = { showAddScheduleDialog = true },
                        modifier = Modifier.fillMaxWidth().height(45.dp),
                        shape    = RoundedCornerShape(15.dp),
                        colors   = ButtonDefaults.buttonColors(containerColor = TealPrimary),
                        elevation = ButtonDefaults.buttonElevation(0.dp)
                    ) {
                        Text("Add schedule", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                        Spacer(Modifier.width(6.dp))
                        Icon(Icons.Filled.Add, null)
                    }
                }
            }

            Spacer(Modifier.weight(1f))

            //  Bottom Nav (Home/Logs/Meals/Profile)
            SugarCareBottomNavBar(navController, Screen.Medications.route)
        }
    }

    // Modal Dialog for adding a new medication
    if (showAddMedDialog) {
        AlertDialog(
            onDismissRequest = { showAddMedDialog = false },
            title = { Text("Add Medication", color = TealDark, fontWeight = FontWeight.Bold) },
            text = {
                OutlinedTextField(
                    value = newMedName,
                    onValueChange = { newMedName = it },
                    label = { Text("Medication Name") },
                    singleLine = true,
                    colors = medicationFieldColors(),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (newMedName.isNotBlank()) {
                            medications = medications + Medication(newMedName, false)
                            newMedName = ""
                            showAddMedDialog = false
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = TealPrimary)
                ) {
                    Text("Add")
                }
            },
            dismissButton = {
                TextButton(onClick = { showAddMedDialog = false }) {
                    Text("Cancel", color = TextMedium)
                }
            },
            containerColor = Color.White,
            shape = RoundedCornerShape(16.dp)
        )
    }

    // Modal Dialog for adding a new measurement schedule
    if (showAddScheduleDialog) {
        AlertDialog(
            onDismissRequest = { showAddScheduleDialog = false },
            title = { Text("Add Schedule", color = TealDark, fontWeight = FontWeight.Bold) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    OutlinedTextField(
                        value = newScheduleName,
                        onValueChange = { newScheduleName = it },
                        label = { Text("Schedule Name (e.g. Fasting)") },
                        singleLine = true,
                        colors = medicationFieldColors(),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    )

                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        OutlinedTextField(
                            value = newScheduleHour,
                            onValueChange = { if (it.length <= 2) newScheduleHour = it },
                            label = { Text("HH") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(12.dp),
                            colors = medicationFieldColors()
                        )
                        Text(":", style = MaterialTheme.typography.titleLarge)
                        OutlinedTextField(
                            value = newScheduleMinute,
                            onValueChange = { if (it.length <= 2) newScheduleMinute = it },
                            label = { Text("MM") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(12.dp),
                            colors = medicationFieldColors()
                        )

                        Box(
                            modifier = Modifier
                                .height(56.dp)
                                .width(72.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(
                                    Brush.horizontalGradient(
                                        listOf(GreenAccent, GreenAccent2)
                                    )
                                )
                                .clickable {
                                    newScheduleAmPm = if (newScheduleAmPm == "AM") "PM" else "AM"
                                },
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = newScheduleAmPm,
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp
                            )
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (newScheduleName.isNotBlank() && newScheduleHour.isNotBlank() && newScheduleMinute.isNotBlank()) {
                            val hr = newScheduleHour.toIntOrNull() ?: 12
                            val min = newScheduleMinute.toIntOrNull() ?: 0
                            val validatedHour = hr.coerceIn(1, 12).toString().padStart(2, '0')
                            val validatedMinute = min.coerceIn(0, 59).toString().padStart(2, '0')
                            val formattedTime = "$validatedHour:$validatedMinute $newScheduleAmPm"
                            val newSchedule = MeasurementSchedule(name = newScheduleName, time = formattedTime, isEnabled = true)
                            schedules = (schedules + newSchedule).sortedBy { timeToMinutes(it.time) }
                            newScheduleName = ""
                            newScheduleHour = ""
                            newScheduleMinute = ""
                            newScheduleAmPm = "AM"
                            showAddScheduleDialog = false
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = TealPrimary)
                ) {
                    Text("Add")
                }
            },
            dismissButton = {
                TextButton(onClick = { showAddScheduleDialog = false }) {
                    Text("Cancel", color = TextMedium)
                }
            },
            containerColor = Color.White,
            shape = RoundedCornerShape(16.dp)
        )
    }
}

@Composable
private fun MedicationRow(
    name: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    onDelete: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth().height(64.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Surface(
            modifier = Modifier.weight(1f).fillMaxHeight(),
            shape = RoundedCornerShape(26.dp),
            color = if (checked) TealLight.copy(alpha = 0.4f) else Color.LightGray.copy(alpha = 0.2f),
            border = androidx.compose.foundation.BorderStroke(1.dp, TealPrimary.copy(alpha = 0.5f))
        ) {
            Row(
                modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(verticalArrangement = Arrangement.Center) {
                    Text(
                        text = name,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurface
                    )

                }

                Switch(
                    checked = checked,
                    onCheckedChange = onCheckedChange,
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = Color.White,
                        checkedTrackColor = TealPrimary,
                        uncheckedThumbColor = Color.Gray,
                        uncheckedTrackColor = Color.LightGray
                    )
                )
            }
        }
        Spacer(Modifier.width(6.dp))
        IconButton(
            onClick = onDelete,
            modifier = Modifier
                .size(35.dp)
                .clip(RoundedCornerShape(15.dp))
                .background(Color(0xFFFFEBEE))
        ) {
            Icon(
                imageVector = Icons.Default.Delete,
                contentDescription = "Delete",
                tint = Color(0xFFD32F2F),
                modifier = Modifier.size(20.dp)
            )
        }
    }
}

@Composable
private fun ScheduleRow(
    schedule: MeasurementSchedule,
    onEnabledChange: (Boolean) -> Unit,
    onDelete: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth().height(64.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Surface(
            modifier = Modifier.weight(1f).fillMaxHeight(),
            shape = RoundedCornerShape(26.dp),
            color = if (schedule.isEnabled) TealLight.copy(alpha = 0.4f) else Color.LightGray.copy(alpha = 0.2f),
            border = androidx.compose.foundation.BorderStroke(1.dp, TealPrimary.copy(alpha = 0.5f))
        ) {
            Row(
                modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(verticalArrangement = Arrangement.Center) {
                    Text(
                        text = schedule.name,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = schedule.time,
                        fontSize = 12.sp,
                        color = TextMedium
                    )
                }

                Switch(
                    checked = schedule.isEnabled,
                    onCheckedChange = onEnabledChange,
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = Color.White,
                        checkedTrackColor = TealPrimary,
                        uncheckedThumbColor = Color.Gray,
                        uncheckedTrackColor = Color.LightGray
                    )
                )
            }
        }
        Spacer(Modifier.width(6.dp))
        IconButton(
            onClick = onDelete,
            modifier = Modifier
                .size(35.dp)
                .clip(RoundedCornerShape(15.dp))
                .background(Color(0xFFFFEBEE))
        ) {
            Icon(
                imageVector = Icons.Default.Delete,
                contentDescription = "Delete",
                tint = Color(0xFFD32F2F),
                modifier = Modifier.size(15.dp)
            )
        }
    }
}

@Composable
private fun medicationFieldColors() = OutlinedTextFieldDefaults.colors(
    focusedBorderColor = TealPrimary,
    unfocusedBorderColor = TealPrimary.copy(alpha = 0.5f),
    focusedLabelColor = TealPrimary,
    unfocusedLabelColor = TextDark.copy(alpha = 0.7f),
    focusedTextColor = TextDark,
    unfocusedTextColor = TextDark,
    cursorColor = TealPrimary
)
