package com.sugarcare.app.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sugarcare.app.ui.components.*
import com.sugarcare.app.ui.theme.*

/**
 * Health Information Screen – captures diabetic type, age, HbA1c
 * after successful sign-up before entering the main app.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HealthInfoScreen(onSaved: () -> Unit) {
    var selectedType by remember { mutableStateOf("") }
    var age          by remember { mutableStateOf("") }
    var hba1c        by remember { mutableStateOf("") }
    var expanded     by remember { mutableStateOf(false) }

    val diabeticTypes = listOf("Type 1", "Type 2", "Gestational", "Pre-Diabetes")

    SugarCareBackground {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 32.dp, vertical = 48.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text(
                    text       = "Health Information",
                    style      = MaterialTheme.typography.displayLarge,
                    color      = TealPrimary,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(36.dp))

                // ── Diabetic Type dropdown ─────────────────────
                Text(
                    text       = "diabetic type",
                    fontWeight = FontWeight.SemiBold,
                    fontSize   = 16.sp,
                    color      = TextDark
                )
                Spacer(modifier = Modifier.height(8.dp))

                ExposedDropdownMenuBox(
                    expanded         = expanded,
                    onExpandedChange = { expanded = !expanded }
                ) {
                    OutlinedTextField(
                        value         = selectedType.ifEmpty { "type1, type2" },
                        onValueChange = {},
                        readOnly      = true,
                        modifier      = Modifier
                            .fillMaxWidth()
                            .menuAnchor(),
                        shape         = RoundedCornerShape(16.dp),
                        trailingIcon  = {
                            Icon(Icons.Filled.ArrowDropDown, null, tint = TealPrimary)
                        },
                        colors        = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor   = TealPrimary,
                            unfocusedBorderColor = TealLight,
                            focusedTextColor     = if (selectedType.isEmpty()) TextLight else TextDark,
                            unfocusedTextColor   = if (selectedType.isEmpty()) TextLight else TextDark
                        )
                    )
                    ExposedDropdownMenu(
                        expanded         = expanded,
                        onDismissRequest = { expanded = false }
                    ) {
                        diabeticTypes.forEach { type ->
                            DropdownMenuItem(
                                text    = { Text(type) },
                                onClick = {
                                    selectedType = type
                                    expanded     = false
                                }
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // ── Age field ─────────────────────────────────
                Text(
                    text       = "Age",
                    fontWeight = FontWeight.SemiBold,
                    fontSize   = 16.sp,
                    color      = TextDark
                )
                Spacer(modifier = Modifier.height(8.dp))
                SugarCareTextField(
                    value         = age,
                    onValueChange = { age = it },
                    label         = ""
                )

                Spacer(modifier = Modifier.height(24.dp))

                // ── HbA1c field ───────────────────────────────
                Text(
                    text       = "HbA1c",
                    fontWeight = FontWeight.SemiBold,
                    fontSize   = 16.sp,
                    color      = TextDark
                )
                Spacer(modifier = Modifier.height(8.dp))
                SugarCareTextField(
                    value         = hba1c,
                    onValueChange = { hba1c = it },
                    label         = ""
                )
            }

            // ── Save button ───────────────────────────────────
            PrimaryButton(
                text    = "Save Health Info",
                onClick = onSaved,
                enabled = selectedType.isNotEmpty() && age.isNotEmpty() && hba1c.isNotEmpty()
            )
        }
    }
}
