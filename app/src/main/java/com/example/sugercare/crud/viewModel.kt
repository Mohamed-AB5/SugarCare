package com.example.sugercare.app

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.compose.runtime.mutableStateOf
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query

class SugarViewModel : ViewModel() {
    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    private val currentUserId: String
        get() = auth.currentUser?.uid ?: "test_user_123"

    var readingsList = mutableStateOf<List<SugarReading>>(emptyList())
        private set

    init { getReadings() }

    fun addReading(level: Int, note: String) {
        val reading = SugarReading(userId = currentUserId, glucoseLevel = level, note = note)
        db.collection("sugar_readings").document(reading.id).set(reading).addOnFailureListener { exception ->
            Log.d("FIRESTORE", "Firestore Error: ${exception.message}")
        }
    }

    fun getReadings() {
        db.collection("sugar_readings")
            .whereEqualTo("userId", currentUserId)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    Log.e("FIRESTORE", "Listen failed: ${error.message}")
                    return@addSnapshotListener
                }
                if (snapshot != null) {
                    readingsList.value = snapshot.toObjects(SugarReading::class.java)
                        .sortedBy { it.timestamp }
                }
            }
    }

    fun deleteReading(readingId: String) {
        db.collection("sugar_readings").document(readingId).delete()
    }
}


/*
package com.example.sugercare.crud

import android.widget.Toast
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.sugercare.viewModels.SugarTrackerViewModel
import com.example.sugercare.viewModels.TrackerState
import co.yml.charts.axis.AxisData
import co.yml.charts.common.model.Point
import co.yml.charts.ui.linechart.LineChart
import co.yml.charts.ui.linechart.model.LineChartData
import co.yml.charts.ui.linechart.model.LinePlotData
import co.yml.charts.ui.linechart.model.LineStyle

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SugarTrackerScreen(
    viewModel: SugarTrackerViewModel = androidx.lifecycle.viewmodel.compose.viewModel()
) {
    val context = LocalContext.current
    val trackerState = viewModel.trackerState.collectAsState().value
    val readings = viewModel.readingsList.collectAsState().value
    val amPmSelection = viewModel.amPmSelection.collectAsState().value

    LaunchedEffect(trackerState) {
        when (trackerState) {
            is TrackerState.Success -> {
                Toast.makeText(context, "Record Saved Successfully!", Toast.LENGTH_SHORT).show()
                viewModel.resetTrackerState()
            }
            is TrackerState.Error -> {
                Toast.makeText(context, trackerState.message, Toast.LENGTH_SHORT).show()
                viewModel.resetTrackerState()
            }
            else -> {}
        }
    }

    Scaffold(
        topBar = { TopAppBar(title = { Text("Glucose Tracker") }) }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            OutlinedTextField(
                value = viewModel.glucoseInput.collectAsState().value,
                onValueChange = { viewModel.updateGlucoseInput(it) },
                label = { Text("Glucose Level (mg/dL)") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = viewModel.mealRelation.collectAsState().value,
                onValueChange = { viewModel.updateMealRelation(it) },
                label = { Text("before/after the meal") },
                modifier = Modifier.fillMaxWidth()
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = viewModel.hourInput.collectAsState().value,
                    onValueChange = { if (it.length <= 2) viewModel.updateHourInput(it) },
                    label = { Text("HH") },
                    placeholder = { Text("") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.weight(1f)
                )
                Text(":", style = MaterialTheme.typography.titleLarge)
                OutlinedTextField(
                    value = viewModel.minuteInput.collectAsState().value,
                    onValueChange = { if (it.length <= 2) viewModel.updateMinuteInput(it) },
                    label = { Text("MM") },
                    placeholder = { Text("") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.weight(1f)
                )
                Button(
                    onClick = {
                        viewModel.updateAmPmSelection(if (amPmSelection == "AM") "PM" else "AM")
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.secondaryContainer,
                        contentColor = MaterialTheme.colorScheme.onSecondaryContainer
                    ),
                    modifier = Modifier.height(56.dp).padding(top = 8.dp)
                ) {
                    Text(text = amPmSelection, style = MaterialTheme.typography.bodyLarge)
                }
            }
            OutlinedTextField(
                value = viewModel.note.collectAsState().value,
                onValueChange = { viewModel.updateNote(it) },
                label = { Text("Notes") },
                modifier = Modifier.fillMaxWidth()
            )
            Button(
                onClick = { viewModel.saveSugarRecord() },
                modifier = Modifier.fillMaxWidth(),
                enabled = trackerState !is TrackerState.Loading
            ) {
                if (trackerState is TrackerState.Loading) {
                    CircularProgressIndicator(color = MaterialTheme.colorScheme.onPrimary, modifier = Modifier.size(24.dp))
                } else {
                    Text("Save Reading")
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
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
                LineChart(modifier = Modifier.height(180.dp).fillMaxWidth(), lineChartData = lineChartData)
            } else {
                Text("Enter at least 2 readings to view the graph", color = Color.Gray)
            }
            Spacer(modifier = Modifier.height(10.dp))
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
}*/
