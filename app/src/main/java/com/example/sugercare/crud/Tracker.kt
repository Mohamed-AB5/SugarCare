// FILE: app/src/main/java/com/example/sugercare/crud/Tracker.kt
package com.example.sugercare.crud

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import co.yml.charts.axis.AxisData
import co.yml.charts.common.model.Point
import co.yml.charts.ui.linechart.LineChart
import co.yml.charts.ui.linechart.model.LineChartData
import co.yml.charts.ui.linechart.model.LinePlotData
import co.yml.charts.ui.linechart.model.LineStyle
import com.sugarcare.app.ui.theme.GreenAccent
import com.sugarcare.app.ui.theme.TealDark
import com.sugarcare.app.ui.theme.TealPrimary
import com.sugarcare.app.ui.theme.TealLight
import com.example.sugercare.app.SugarViewModel
import com.example.sugarcare.app.ui.theme.GreenAccent2
import com.example.sugarcare.app.ui.theme.TealPrimary2
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SugarTrackerScreen(
    viewModel: SugarViewModel = androidx.lifecycle.viewmodel.compose.viewModel()
) {
    var glucoseInput  by remember { mutableStateOf("") }
    var note          by remember { mutableStateOf("") }
    var hourInput     by remember { mutableStateOf("") }
    var minuteInput   by remember { mutableStateOf("") }
    var amPmSelection by remember { mutableStateOf("AM") }
    val readings       = viewModel.readingsList.value
    val mealOptions    = listOf("Before", "After")
    var selectedMeal  by remember { mutableStateOf("") }
    var showMealOptions by remember { mutableStateOf(false) }

    // ✅ Use MaterialTheme colors — adapts to dark/light automatically
    val bgColor      = MaterialTheme.colorScheme.background
    val surfaceColor = MaterialTheme.colorScheme.surface
    val onSurface    = MaterialTheme.colorScheme.onSurface
    val onBg         = MaterialTheme.colorScheme.onBackground

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Glucose Tracker",
                        color = onBg,
                        fontWeight = FontWeight.Bold
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = bgColor
                )
            )
        },
        containerColor = bgColor
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // ── Glucose Level ─────────────────────────────────
            OutlinedTextField(
                value           = glucoseInput,
                onValueChange   = { glucoseInput = it },
                label           = { Text("Glucose Level (mg/dL)", color = onSurface.copy(alpha = 0.7f)) },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier        = Modifier.fillMaxWidth(),
                shape           = RoundedCornerShape(14.dp),
                textStyle       = LocalTextStyle.current.copy(color = onSurface),
                colors          = trackerFieldColors()
            )

            Spacer(Modifier.height(6.dp))

            // ── Notes ─────────────────────────────────────────
            OutlinedTextField(
                value         = note,
                onValueChange = { note = it },
                label         = { Text("Notes", color = onSurface.copy(alpha = 0.7f)) },
                modifier      = Modifier.fillMaxWidth(),
                shape         = RoundedCornerShape(14.dp),
                textStyle     = LocalTextStyle.current.copy(color = onSurface),
                colors        = trackerFieldColors()
            )

            Spacer(Modifier.height(6.dp))

            // ── Before / After Meal ───────────────────────────
            ExposedDropdownMenuBox(
                expanded         = showMealOptions,
                onExpandedChange = { showMealOptions = !showMealOptions }
            ) {
                OutlinedTextField(
                    value         = selectedMeal,
                    onValueChange = {},
                    readOnly      = true,
                    label         = { Text("Before / After Meal", color = onSurface.copy(alpha = 0.7f)) },
                    placeholder   = { Text("Select", color = onSurface.copy(alpha = 0.4f)) },
                    trailingIcon  = {
                        ExposedDropdownMenuDefaults.TrailingIcon(expanded = showMealOptions)
                    },
                    modifier  = Modifier.fillMaxWidth()
                        .menuAnchor(MenuAnchorType.PrimaryNotEditable),
                    shape     = RoundedCornerShape(14.dp),
                    textStyle = LocalTextStyle.current.copy(color = onSurface),
                    colors    = trackerFieldColors()
                )
                ExposedDropdownMenu(
                    expanded         = showMealOptions,
                    onDismissRequest = { showMealOptions = false },
                    modifier         = Modifier.background(surfaceColor)
                ) {
                    mealOptions.forEach { option ->
                        DropdownMenuItem(
                            text = {
                                Text(
                                    option,
                                    color = onSurface,
                                    fontWeight = if (option == selectedMeal)
                                        FontWeight.SemiBold else FontWeight.Normal
                                )
                            },
                            onClick  = { selectedMeal = option; showMealOptions = false },
                            modifier = Modifier.background(surfaceColor)
                        )
                    }
                }
            }

            Spacer(Modifier.height(6.dp))

            // ── Time row ──────────────────────────────────────
            Row(
                modifier              = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment     = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value           = hourInput,
                    onValueChange   = { if (it.length <= 2) hourInput = it },
                    label           = { Text("HH", color = onSurface.copy(alpha = 0.7f)) },
                    placeholder     = { Text("12", color = onSurface.copy(alpha = 0.4f)) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier        = Modifier.weight(1f),
                    shape           = RoundedCornerShape(16.dp),
                    textStyle       = LocalTextStyle.current.copy(color = onSurface),
                    colors          = trackerFieldColors()
                )

                Text(":", style = MaterialTheme.typography.titleLarge, color = onBg)

                OutlinedTextField(
                    value           = minuteInput,
                    onValueChange   = { if (it.length <= 2) minuteInput = it },
                    label           = { Text("MM", color = onSurface.copy(alpha = 0.7f)) },
                    placeholder     = { Text("00", color = onSurface.copy(alpha = 0.4f)) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier        = Modifier.weight(1f),
                    shape           = RoundedCornerShape(16.dp),
                    textStyle       = LocalTextStyle.current.copy(color = onSurface),
                    colors          = trackerFieldColors()
                )

                // AM/PM toggle
                Box(
                    modifier = Modifier
                        .height(56.dp)
                        .width(72.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(
                            Brush.horizontalGradient(listOf(GreenAccent, GreenAccent2))
                        )
                        .clickable {
                            amPmSelection = if (amPmSelection == "AM") "PM" else "AM"
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        amPmSelection,
                        color      = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize   = 16.sp
                    )
                }
            }

            Spacer(Modifier.height(6.dp))

            // ── Save Reading button ───────────────────────────
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .clip(RoundedCornerShape(28.dp))
                    .background(
                        Brush.horizontalGradient(listOf(TealDark, TealPrimary2))
                    )
                    .clickable {
                        val level = glucoseInput.toIntOrNull() ?: return@clickable
                        viewModel.addReading(level = level, note = note)
                        glucoseInput  = ""
                        selectedMeal  = ""
                        note          = ""
                        hourInput     = ""
                        minuteInput   = ""
                        amPmSelection = "AM"
                    },
                contentAlignment = Alignment.Center
            ) {
                Row(
                    verticalAlignment     = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Icon(Icons.Default.Add, null, tint = Color.White)
                    Spacer(Modifier.width(8.dp))
                    Text(
                        "Save Reading",
                        color      = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize   = 18.sp
                    )
                }
            }

            Spacer(Modifier.height(24.dp))

            // ── Chart ─────────────────────────────────────────
            if (readings.size > 1) {
                val points = readings.mapIndexed { index, r ->
                    Point(index.toFloat(), r.glucoseLevel.toFloat())
                }
                val xAxisData = AxisData.Builder()
                    .axisStepSize(40.dp)
                    .steps(points.size - 1)
                    .labelData { i -> (i + 1).toString() }
                    .axisLabelColor(onBg)
                    .axisLineColor(TealPrimary)
                    .build()

                val yAxisData = AxisData.Builder()
                    .steps(5)
                    .labelData { i -> (i * 50).toString() }
                    .axisLabelColor(onBg)
                    .axisLineColor(TealPrimary)
                    .build()

                val lineChartData = LineChartData(
                    linePlotData  = LinePlotData(
                        lines = listOf(
                            co.yml.charts.ui.linechart.model.Line(
                                dataPoints = points,
                                lineStyle  = LineStyle(color = TealPrimary)
                            )
                        )
                    ),
                    xAxisData     = xAxisData,
                    yAxisData     = yAxisData,
                    backgroundColor = surfaceColor
                )

                Card(
                    modifier  = Modifier.fillMaxWidth(),
                    shape     = RoundedCornerShape(16.dp),
                    colors    = CardDefaults.cardColors(containerColor = surfaceColor),
                    elevation = CardDefaults.cardElevation(2.dp)
                ) {
                    LineChart(
                        modifier      = Modifier
                            .height(200.dp)
                            .fillMaxWidth()
                            .padding(8.dp),
                        lineChartData = lineChartData
                    )
                }
            } else {
                Text(
                    "Enter at least 2 readings to view the graph",
                    // ✅ use onBackground not hardcoded gray
                    color    = onBg.copy(alpha = 0.6f),
                    fontSize = 14.sp
                )
            }

            Spacer(Modifier.height(16.dp))

            // ── Readings list ─────────────────────────────────
            LazyColumn(modifier = Modifier.fillMaxSize()) {
                items(readings) { reading ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        shape  = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = surfaceColor),
                        elevation = CardDefaults.cardElevation(1.dp)
                    ) {
                        Row(
                            modifier              = Modifier.fillMaxWidth().padding(16.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment     = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    "Reading: ${reading.glucoseLevel} mg/dL",
                                    color      = onSurface,
                                    fontWeight = FontWeight.SemiBold
                                )
                                if (reading.note.isNotEmpty()) {
                                    Text(
                                        "Details: ${reading.note}",
                                        color    = onSurface.copy(alpha = 0.6f),
                                        fontSize = 13.sp
                                    )
                                }
                            }
                            IconButton(onClick = { viewModel.deleteReading(reading.id) }) {
                                Icon(Icons.Default.Delete, "Delete", tint = Color(0xFFE53935))
                            }
                        }
                    }
                }
            }
        }
    }
}

// ── Field colors — adapts to dark/light ──────────────────────
@Composable
private fun trackerFieldColors(): TextFieldColors {
    val isDark         = com.sugarcare.app.ui.theme.LocalDarkTheme.current.value
    val textColor      = if (isDark) Color(0xFFE0F2F1) else Color(0xFF1A2B2B)
    val containerColor = if (isDark) Color(0xFF1A3333) else Color.White

    return OutlinedTextFieldDefaults.colors(
        focusedBorderColor        = TealPrimary,
        unfocusedBorderColor      = if (isDark) TealLight.copy(0.5f) else TealLight,
        focusedLabelColor         = TealPrimary,
        unfocusedLabelColor       = textColor.copy(0.6f),
        focusedTextColor          = textColor,
        unfocusedTextColor        = textColor,
        focusedContainerColor     = containerColor,
        unfocusedContainerColor   = containerColor,
        focusedPlaceholderColor   = textColor.copy(0.4f),
        unfocusedPlaceholderColor = textColor.copy(0.4f),
        focusedLeadingIconColor   = TealPrimary,
        unfocusedLeadingIconColor = TealPrimary,
        cursorColor               = TealPrimary
    )
}