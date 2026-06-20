package com.example.sugercare.app

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import co.yml.charts.axis.AxisData
import co.yml.charts.common.model.Point
import co.yml.charts.ui.linechart.LineChart
import co.yml.charts.ui.linechart.model.LineChartData
import co.yml.charts.ui.linechart.model.LinePlotData
import co.yml.charts.ui.linechart.model.LineStyle

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SugarTrackerScreen(viewModel: SugarViewModel = androidx.lifecycle.viewmodel.compose.viewModel()) {
    var glucoseInput by remember { mutableStateOf("") }
    var mealrelation by remember { mutableStateOf("") }
    var note by remember { mutableStateOf("") }
    var hourInput by remember { mutableStateOf("") }
    var minuteInput by remember { mutableStateOf("") }
    var amPmSelection by remember { mutableStateOf("AM") }
    val readings = viewModel.readingsList.value


    Scaffold(
        topBar = { TopAppBar(title = { Text("Glucose Tracker") }) }
    ) { paddingValues ->
        Column(
            modifier = Modifier.fillMaxSize().padding(paddingValues).padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            OutlinedTextField(
                value = glucoseInput,
                onValueChange = { glucoseInput = it },
                label = { Text("Glucose Level (mg/dL)") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(6.dp))

            OutlinedTextField(
                value = note,
                onValueChange = { note = it },
                label = { Text("Notes") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(6.dp))

            OutlinedTextField(
                value = mealrelation,
                onValueChange = { mealrelation = it },
                label = { Text("before/after the meal") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(6.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = hourInput,
                    onValueChange = { if (it.length <= 2) hourInput = it },
                    label = { Text("HH") },
                    placeholder = { Text("12") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.weight(1f)
                )
                Text(":", style = MaterialTheme.typography.titleLarge)
                OutlinedTextField(
                    value = minuteInput,
                    onValueChange = { if (it.length <= 2) minuteInput = it },
                    label = { Text("MM") },
                    placeholder = { Text("00") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.weight(1f)
                )
                Button(
                    onClick = { amPmSelection = if (amPmSelection == "AM") "PM" else "AM" },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.secondaryContainer,
                        contentColor = MaterialTheme.colorScheme.onSecondaryContainer
                    ),
                    modifier = Modifier.height(56.dp).padding(top = 8.dp)
                ) {
                    Text(amPmSelection, style = MaterialTheme.typography.bodyLarge)
                }
            }
            Spacer(modifier = Modifier.height(6.dp))

            Button(
                onClick = {
                    val level = glucoseInput.toIntOrNull() ?: 0
                    if (level > 0) {

                        val detailsBuilder = mutableListOf<String>()
                        if (mealrelation.isNotEmpty()) detailsBuilder.add(mealrelation)
                        if (note.isNotEmpty()) detailsBuilder.add("Note: $note")
                        val formattedTime = if (hourInput.isNotEmpty() && minuteInput.isNotEmpty()) {
                            "$hourInput:$minuteInput $amPmSelection"
                        } else {
                            ""
                        }

                        val fullDetails = detailsBuilder.joinToString(" | ")

                        viewModel.addReading(level, fullDetails)

                        glucoseInput = ""
                        mealrelation = ""
                        note = ""
                        hourInput = ""
                        minuteInput = ""
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) { Text("Save Reading") }

            Spacer(modifier = Modifier.height(24.dp))

            if (readings.size > 1) {
                val points = readings.mapIndexed { index, sugarReading ->
                    Point(index.toFloat(), sugarReading.glucoseLevel.toFloat())
                }
                val xAxisData = AxisData.Builder().axisStepSize(40.dp).steps(points.size - 1).labelData { i -> (i + 1).toString() }.build()
                val yAxisData = AxisData.Builder().steps(5).labelData { i -> (i * 50).toString() }.build()
                val lineChartData = LineChartData(
                    linePlotData = LinePlotData(lines = listOf(co.yml.charts.ui.linechart.model.Line(dataPoints = points, lineStyle = LineStyle(color = Color.Blue)))),
                    xAxisData = xAxisData, yAxisData = yAxisData
                )
                LineChart(modifier = Modifier.height(200.dp).fillMaxWidth(), lineChartData = lineChartData)
            } else {
                Text("Enter at least 2 readings to view the graph", color = Color.Gray)
            }

            Spacer(modifier = Modifier.height(16.dp))

            LazyColumn(modifier = Modifier.fillMaxSize()) {
                items(readings) { reading ->
                    Card(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
                        Row(modifier = Modifier.fillMaxWidth().padding(16.dp), horizontalArrangement = Arrangement.SpaceBetween) {
                            Column {
                                Text("Reading: ${reading.glucoseLevel} mg/dL")
                                if (reading.note.isNotEmpty()) {
                                    Text("Details: ${reading.note}", color = Color.Gray)
                                }
                            }
                            IconButton(onClick = { viewModel.deleteReading(reading.id) }) {
                                Icon(Icons.Default.Delete, contentDescription = "Delete", tint = Color.Red)
                            }
                        }
                    }
                }
            }
        }
    }
}