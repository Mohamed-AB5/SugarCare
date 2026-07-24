package com.sugarcare.app.ui.screens

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

import androidx.core.content.ContextCompat
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.sugarcare.app.navigation.Screen
import com.example.sugercare.notifications.NotificationHelper
import com.example.sugercare.notifications.ReminderScheduler
import com.sugarcare.app.ui.components.SugarCareBackground
import com.sugarcare.app.ui.components.SugarCareCard
import com.sugarcare.app.ui.theme.*

@Preview(showBackground = true)
@Composable
fun MedicationsScreenPreview() {
    SugarCareTheme {
        MedicationsScreen(navController = rememberNavController())
    }
}

/**
 * Medications Screen – displays a list of medications with toggle buttons
 * to mark them as taken, and a button to add new ones.
 *
 * Extended to support:
 *   • MedicationItem model (replaces old Medication stub)
 *   • Add Medication dialog with name field + time picker
 *   • Daily reminder scheduling via ReminderScheduler
 *   • POST_NOTIFICATIONS runtime permission (Android 13+)
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MedicationsScreen(navController: NavHostController) {

    val context = LocalContext.current

    // ── Medication list state ─────────────────────────────────
    // Firestore upgrade: replace this remember block with a StateFlow
    // loaded from users/{uid}/medications — UI stays unchanged.
    var medications by remember {
        mutableStateOf(
            listOf(
                MedicationItem(
                    id             = 1,
                    name           = "Insulin c. pre-meal",
                    taken          = true,
                    reminderHour   = 7,
                    reminderMinute = 0,
                    reminderLabel  = "07:00 AM"
                ),
                MedicationItem(
                    id             = 2,
                    name           = "Metformin",
                    taken          = true,
                    reminderHour   = 8,
                    reminderMinute = 0,
                    reminderLabel  = "08:00 AM"
                ),
                MedicationItem(
                    id             = 3,
                    name           = "Lisinopril (daily)",
                    taken          = true,
                    reminderHour   = 9,
                    reminderMinute = 0,
                    reminderLabel  = "09:00 AM"
                )
            )
        )
    }

    // ── Dialog visibility ─────────────────────────────────────
    var showAddDialog by remember { mutableStateOf(false) }

    // ── POST_NOTIFICATIONS permission (Android 13+) ───────────
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { /* permission result — alarm schedules regardless */ }

    val isDark = LocalDarkTheme.current.value
    val bgColor = if (isDark) BackgroundDark else BackgroundLight
    val cardColor = if (isDark) SurfaceDark else Color.White
    val textColor = if (isDark) Color(0xFFE0F2F1) else TextDark
    val subColor = if (isDark) Color(0xFF80CBC4) else TextMedium
    val navColor = if (isDark) SurfaceDark else Color.White
    val navText = if (isDark) Color(0xFF80CBC4) else TextMedium

    val currentRoute = navController.currentBackStackEntryAsState().value?.destination?.route

    LaunchedEffect(Unit) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(
                    context,
                    Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                permissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        }
        NotificationHelper.createNotificationChannel(context)
    }

    // ── UI ────────────────────────────────────────────────────
    SugarCareBackground {
        Column(modifier = Modifier.fillMaxSize()) {

            // ── Header ────────────────────────────────────────
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 24.dp, end = 24.dp, top = 48.dp, bottom = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text       = "❤ My Medications 🌿",
                    style      = MaterialTheme.typography.headlineMedium,
                    color      = TealPrimary,
                    fontWeight = FontWeight.Bold
                )
            }

            Icon(
                Icons.Filled.Medication,
                contentDescription = null,
                tint     = TealPrimary,
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .size(48.dp)
            )

            Spacer(Modifier.height(8.dp))

            // ── Medications Card ──────────────────────────────
            SugarCareCard(modifier = Modifier.padding(horizontal = 24.dp)) {
                Text(
                    "Medications",
                    fontWeight = FontWeight.Bold,
                    fontSize   = 18.sp,
                    color      = TextDark
                )

                Spacer(Modifier.height(12.dp))

                medications.forEachIndexed { index, med ->
                    MedicationRow(
                        name          = med.name,
                        reminderLabel = med.reminderLabel,
                        checked       = med.taken,
                        onCheckedChange = { checked ->
                            medications = medications.toMutableList().also {
                                it[index] = it[index].copy(taken = checked)
                            }
                        }
                    )
                    if (index < medications.lastIndex) {
                        Spacer(Modifier.height(8.dp))
                    }
                }

                Spacer(Modifier.height(16.dp))

                // ── Add medication button ─────────────────────
                Button(
                    onClick = {
                        android.util.Log.d("TEST", "Button Clicked")
                        showAddDialog = true
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp),
                    shape    = RoundedCornerShape(26.dp),
                    colors   = ButtonDefaults.buttonColors(containerColor = TealPrimary)
                ) {
                    Text("Add medication", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    Spacer(Modifier.width(8.dp))
                    Icon(Icons.Filled.Add, contentDescription = null)
                }
            }

            Spacer(Modifier.weight(1f))

            // ── Bottom Nav ────────────────────────────────────
            NavigationBar(containerColor = Color.White, tonalElevation = 8.dp) {
                listOf(
                    Triple("Home",    Icons.Filled.Home,       Screen.Home.route),
                    Triple("Meds",    Icons.Filled.Medication,  Screen.Medications.route),
                    Triple("Trends",  Icons.Filled.BarChart,    Screen.WeeklyAnalytics.route),
                    Triple("Profile", Icons.Filled.Person,      Screen.Profile.route)
                ).forEach { (label, icon, route) ->
                    NavigationBarItem(
                        selected = route == currentRoute,
                        onClick  = { navController.navigate(route) },
                        icon     = { Icon(icon, contentDescription = label) },
                        label    = { Text(label, fontSize = 11.sp, color = textColor) },
                        colors   = NavigationBarItemDefaults.colors(
                            selectedIconColor = TealPrimary,
                            indicatorColor    = TealLight
                        )
                    )
                }
            }
        }

        // ── Add Medication Dialog ─────────────────────────────
        if (showAddDialog) {
            AddMedicationDialog(
                onDismiss = { showAddDialog = false },
                onSave    = { newMed ->
                    medications = medications + newMed
                    ReminderScheduler.scheduleDailyReminder(
                        context        = context,
                        medicationId   = newMed.id,
                        medicationName = newMed.name,
                        hour           = newMed.reminderHour,
                        minute         = newMed.reminderMinute
                    )
                    showAddDialog = false
                }
            )
        }
    }
}

// ─────────────────────────────────────────────────────────────
//  Add Medication Dialog
// ─────────────────────────────────────────────────────────────

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AddMedicationDialog(
    onDismiss: () -> Unit,
    onSave: (MedicationItem) -> Unit
) {
    var medicationName by remember { mutableStateOf("") }
    var nameError      by remember { mutableStateOf(false) }

    val timePickerState = rememberTimePickerState(
        initialHour   = 8,
        initialMinute = 0,
        is24Hour      = false
    )

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor   = SurfaceWhite,
        shape            = RoundedCornerShape(20.dp),
        title = {
            Text(
                text       = "Add Medication",
                fontWeight = FontWeight.Bold,
                fontSize   = 18.sp,
                color      = TextDark
            )
        },
        text = {
            Column(

                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Button(
                    onClick = {
                        medicationName = "Test"
                    }
                ) {
                    Text("Test")
                }
                // ── Medication name ───────────────────────────
                OutlinedTextField(
                    value         = medicationName,
                    onValueChange = {
                        medicationName = it
                        nameError = false
                    },
                    label      = { Text("Medication Name") },
                    singleLine = true,
                    isError    = nameError,
                    supportingText = if (nameError) {
                        { Text("Please enter a name.", color = MaterialTheme.colorScheme.error) }
                    } else null,
                    shape  = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor   = TealPrimary,
                        unfocusedBorderColor = TealLight,
                        focusedLabelColor    = TealPrimary,
                        cursorColor          = TealPrimary
                    ),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(Modifier.height(12.dp))

                Text(
                    text       = "Reminder Time",
                    fontSize   = 13.sp,
                    color      = TextMedium,
                    fontWeight = FontWeight.SemiBold,
                    modifier   = Modifier.align(Alignment.Start)
                )

                Spacer(Modifier.height(8.dp))

                TimePicker(
                    state  = timePickerState,
                    colors = TimePickerDefaults.colors(
                        clockDialColor                       = TealLight.copy(alpha = 0.3f),
                        selectorColor                        = TealPrimary,
                        containerColor                       = SurfaceWhite,
                        periodSelectorBorderColor            = TealPrimary,
                        clockDialSelectedContentColor        = Color.White,
                        clockDialUnselectedContentColor      = TextDark,
                        timeSelectorSelectedContainerColor   = TealPrimary,
                        timeSelectorUnselectedContainerColor = TealLight.copy(alpha = 0.3f),
                        timeSelectorSelectedContentColor     = Color.White,
                        timeSelectorUnselectedContentColor   = TextDark
                    )
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (medicationName.isBlank()) {
                        nameError = true
                        return@Button
                    }
                    val hour   = timePickerState.hour
                    val minute = timePickerState.minute
                    onSave(
                        MedicationItem(
                            id             = System.currentTimeMillis().toInt(),
                            name           = medicationName.trim(),
                            taken          = false,
                            reminderHour   = hour,
                            reminderMinute = minute,
                            reminderLabel  = formatTimeLabel(hour, minute)
                        )
                    )
                },
                shape  = RoundedCornerShape(24.dp),
                colors = ButtonDefaults.buttonColors(containerColor = TealPrimary)
            ) {
                Text("Save", fontWeight = FontWeight.Bold, color = Color.White)
            }
        },
        dismissButton = {
            OutlinedButton(
                onClick = onDismiss,
                shape   = RoundedCornerShape(24.dp),
                border  = BorderStroke(1.dp, TealPrimary)
            ) {
                Text("Cancel", color = TealPrimary, fontWeight = FontWeight.Bold)
            }
        }
    )
}

// ─────────────────────────────────────────────────────────────
//  Helpers
// ─────────────────────────────────────────────────────────────

/**
 * Converts 24-hour hour/minute values to a readable AM/PM label.
 * Example: (8, 30) → "08:30 AM" | (13, 0) → "01:00 PM"
 */
private fun formatTimeLabel(hour: Int, minute: Int): String {
    val amPm   = if (hour < 12) "AM" else "PM"
    val hour12 = when (hour) {
        0         -> 12
        in 13..23 -> hour - 12
        else      -> hour
    }
    return "%02d:%02d %s".format(hour12, minute, amPm)
}

@Composable
private fun MedicationRow(
    name: String,
    reminderLabel: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(60.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {

        Surface(
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight(),
            shape = RoundedCornerShape(26.dp),
            color = if (checked)
                TealLight.copy(alpha = 0.4f)
            else
                Color.LightGray.copy(alpha = 0.2f),
            border = BorderStroke(
                1.dp,
                TealPrimary.copy(alpha = 0.5f)
            )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(start = 16.dp),
                verticalArrangement = Arrangement.Center
            ) {

                Text(
                    text = name,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = TextDark
                )

                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {

                    Icon(
                        imageVector = Icons.Default.Schedule,
                        contentDescription = null,
                        modifier = Modifier.size(11.dp),
                        tint = TextMedium
                    )

                    Spacer(Modifier.width(4.dp))

                    Text(
                        text = reminderLabel,
                        fontSize = 11.sp,
                        color = TextMedium
                    )
                }
            }
        }

        Spacer(Modifier.width(8.dp))

        Surface(
            modifier = Modifier.size(40.dp),
            shape = RoundedCornerShape(50),
            color = TealPrimary
        ) {

            IconToggleButton(
                checked = checked,
                onCheckedChange = onCheckedChange
            ) {

                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = null,
                    tint = Color.White
                )
            }
        }
    }
}
