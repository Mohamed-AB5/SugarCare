package com.example.sugercare.ui.screens


import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.sugercare.data.meal.MealEntity
import com.sugarcare.app.ui.theme.*

/**
 * Reusable Add / Edit meal dialog.
 *
 * Pass [existingMeal] = null  → Add mode (empty fields, "Add Meal" title).
 * Pass [existingMeal] = meal  → Edit mode (pre-filled fields, "Edit Meal" title).
 *
 * @param onDismiss Called when the user taps Cancel or outside the dialog.
 * @param onSave    Called with the field values when the user taps Save.
 *                  Caller decides whether to call addMeal() or updateMeal().
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MealDialog(
    existingMeal: MealEntity? = null,
    onDismiss: () -> Unit,
    onSave: (
        mealName: String,
        mealType: String,
        description: String,
        calories: Int?,
        mealTime: String
    ) -> Unit
) {
    val isEditMode = existingMeal != null
    val title      = if (isEditMode) "Edit Meal" else "Add Meal"

    // ── Field state ───────────────────────────────────────────
    var mealName    by remember { mutableStateOf(existingMeal?.mealName    ?: "") }
    var mealType    by remember { mutableStateOf(existingMeal?.mealType    ?: "Breakfast") }
    var description by remember { mutableStateOf(existingMeal?.description ?: "") }
    var caloriesStr by remember { mutableStateOf(existingMeal?.calories?.toString() ?: "") }
    var mealTime    by remember { mutableStateOf(existingMeal?.mealTime    ?: "") }

    // ── Validation ────────────────────────────────────────────
    var mealNameError by remember { mutableStateOf(false) }

    // ── Meal type dropdown ────────────────────────────────────
    var dropdownExpanded by remember { mutableStateOf(false) }
    val mealTypes = listOf("Breakfast", "Lunch", "Dinner", "Snack")

    // ── Time picker dialog ────────────────────────────────────
    var showTimePicker        by remember { mutableStateOf(false) }
    val timePickerState = rememberTimePickerState(
        initialHour   = 8,
        initialMinute = 0,
        is24Hour      = false
    )

    if (showTimePicker) {
        AlertDialog(
            onDismissRequest = { showTimePicker = false },
            containerColor   = SurfaceWhite,
            shape            = RoundedCornerShape(20.dp),
            title = {
                Text("Select Time", fontWeight = FontWeight.Bold, color = TextDark)
            },
            text = {
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
            },
            confirmButton = {
                TextButton(onClick = {
                    mealTime = formatMealTime(timePickerState.hour, timePickerState.minute)
                    showTimePicker = false
                }) {
                    Text("OK", color = TealPrimary, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showTimePicker = false }) {
                    Text("Cancel", color = TextMedium)
                }
            }
        )
    }

    // ── Main dialog ───────────────────────────────────────────
    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor   = SurfaceWhite,
        shape            = RoundedCornerShape(20.dp),
        title = {
            Text(title, fontWeight = FontWeight.Bold, fontSize = 18.sp, color = TextDark)
        },
        text = {
            Column(
                modifier            = Modifier.verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // ── Meal Name ─────────────────────────────────
                OutlinedTextField(
                    value         = mealName,
                    onValueChange = { mealName = it; mealNameError = false },
                    label         = { Text("Meal Name *") },
                    singleLine    = true,
                    isError       = mealNameError,
                    supportingText = if (mealNameError) {
                        { Text("Meal name is required.", color = MaterialTheme.colorScheme.error) }
                    } else null,
                    shape   = RoundedCornerShape(12.dp),
                    colors  = mealFieldColors(),
                    modifier = Modifier.fillMaxWidth()
                )

                // ── Meal Type dropdown ────────────────────────
                ExposedDropdownMenuBox(
                    expanded        = dropdownExpanded,
                    onExpandedChange = { dropdownExpanded = it }
                ) {
                    OutlinedTextField(
                        value         = mealType,
                        onValueChange = {},
                        readOnly      = true,
                        label         = { Text("Meal Type") },
                        trailingIcon  = {
                            Icon(Icons.Filled.ArrowDropDown, null, tint = TealPrimary)
                        },
                        shape    = RoundedCornerShape(12.dp),
                        colors   = mealFieldColors(),
                        modifier = Modifier
                            .fillMaxWidth()
                            .menuAnchor(type = MenuAnchorType.PrimaryNotEditable)
                    )
                    ExposedDropdownMenu(
                        expanded        = dropdownExpanded,
                        onDismissRequest = { dropdownExpanded = false },
                        containerColor  = SurfaceWhite
                    ) {
                        mealTypes.forEach { type ->
                            DropdownMenuItem(
                                text    = { Text(type, color = TextDark) },
                                onClick = {
                                    mealType        = type
                                    dropdownExpanded = false
                                }
                            )
                        }
                    }
                }

                // ── Meal Time ─────────────────────────────────
                OutlinedTextField(
                    value         = mealTime.ifBlank { "Tap to select time" },
                    onValueChange = {},
                    readOnly      = true,
                    label         = { Text("Meal Time") },
                    trailingIcon  = {
                        IconButton(onClick = { showTimePicker = true }) {
                            Icon(
                                Icons.Filled.ArrowDropDown,
                                contentDescription = "Pick time",
                                tint = TealPrimary
                            )
                        }
                    },
                    shape    = RoundedCornerShape(12.dp),
                    colors   = mealFieldColors(),
                    modifier = Modifier.fillMaxWidth()
                )

                // ── Description ───────────────────────────────
                OutlinedTextField(
                    value         = description,
                    onValueChange = { description = it },
                    label         = { Text("Description") },
                    minLines      = 2,
                    maxLines      = 4,
                    shape         = RoundedCornerShape(12.dp),
                    colors        = mealFieldColors(),
                    modifier      = Modifier.fillMaxWidth()
                )

                // ── Calories (optional) ───────────────────────
                OutlinedTextField(
                    value         = caloriesStr,
                    onValueChange = { caloriesStr = it.filter { c -> c.isDigit() } },
                    label         = { Text("Calories (optional)") },
                    singleLine    = true,
                    shape         = RoundedCornerShape(12.dp),
                    colors        = mealFieldColors(),
                    modifier      = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (mealName.isBlank()) {
                        mealNameError = true
                        return@Button
                    }
                    onSave(
                        mealName.trim(),
                        mealType,
                        description.trim(),
                        caloriesStr.toIntOrNull(),
                        mealTime
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
                border  = androidx.compose.foundation.BorderStroke(1.dp, TealPrimary)
            ) {
                Text("Cancel", color = TealPrimary, fontWeight = FontWeight.Bold)
            }
        }
    )
}

// ─────────────────────────────────────────────────────────────
//  Helpers
// ─────────────────────────────────────────────────────────────

@Composable
private fun mealFieldColors() = OutlinedTextFieldDefaults.colors(
    focusedBorderColor   = TealPrimary,
    unfocusedBorderColor = TealLight,
    focusedLabelColor    = TealPrimary,
    cursorColor          = TealPrimary
)

private fun formatMealTime(hour: Int, minute: Int): String {
    val amPm   = if (hour < 12) "AM" else "PM"
    val hour12 = when (hour) {
        0         -> 12
        in 13..23 -> hour - 12
        else      -> hour
    }
    return "%02d:%02d %s".format(hour12, minute, amPm)
}
