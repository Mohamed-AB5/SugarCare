package com.example.sugercare.app

import android.icu.util.Calendar
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
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
import android.widget.Toast
import androidx.compose.ui.platform.LocalContext
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
import com.sugarcare.app.ui.components.SugarCareTextField
import com.sugarcare.app.ui.theme.GreenAccent
import com.sugarcare.app.ui.theme.GreenAccent2
import com.sugarcare.app.ui.theme.OrangeDrop
import com.sugarcare.app.ui.theme.OrangeDrop2
import com.sugarcare.app.ui.theme.TealDark
import com.sugarcare.app.ui.theme.TealLight
import com.sugarcare.app.ui.theme.TealPrimary
import com.sugarcare.app.ui.theme.TealPrimary2
import com.sugarcare.app.ui.theme.TextDark
import com.sugarcare.app.ui.theme.TextMedium
import com.sugarcare.app.ui.theme.TextLight
import com.sugarcare.app.ui.theme.White
import androidx.navigation.NavHostController
import com.sugarcare.app.navigation.Screen
import com.sugarcare.app.ui.screens.SugarCareBottomNavBar
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.ui.graphics.Color.Companion.White

fun parseReadingNote(noteStr: String): Triple<String?, String?, String?> {
    var meal: String? = null
    var time: String? = null
    var note: String? = null

    val parts = noteStr.split(" | ")
    for (part in parts) {
        if (part.startsWith("Meal: ")) {
            meal = part.substringAfter("Meal: ")
        } else if (part.startsWith("Time: ")) {
            time = part.substringAfter("Time: ")
        } else if (part.startsWith("Note: ")) {
            note = part.substringAfter("Note: ")
        } else {
            if (note == null) {
                note = part
            } else {
                note += " | $part"
            }
        }
    }
    return Triple(meal, time, note)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SugarTrackerScreen(
    navController: NavHostController,
    viewModel: SugarViewModel = androidx.lifecycle.viewmodel.compose.viewModel()
) {
    val context = LocalContext.current
    var glucoseInput by remember { mutableStateOf("") }
    var note by remember { mutableStateOf("") }
    var hourInput by remember { mutableStateOf("") }
    var minuteInput by remember { mutableStateOf("") }
    var amPmSelection by remember { mutableStateOf("AM") }
    val readings = viewModel.readingsList.value
    val mealOptions = listOf("Before", "After")
    var selectedMeal by remember { mutableStateOf("") }
    var showMealOptions by remember { mutableStateOf(false) }

    var selectedFilter by remember { mutableStateOf("All") }

    val filteredReadings = remember(readings, selectedFilter) {
        val now = System.currentTimeMillis()
        when (selectedFilter) {
            "Day" -> {
                val calendar = java.util.Calendar.getInstance()
                calendar.set(java.util.Calendar.HOUR_OF_DAY, 0)
                calendar.set(java.util.Calendar.MINUTE, 0)
                calendar.set(java.util.Calendar.SECOND, 0)
                calendar.set(java.util.Calendar.MILLISECOND, 0)
                val startOfToday = calendar.timeInMillis
                readings.filter { it.timestamp >= startOfToday }
            }
            "Week" -> {
                val sevenDaysAgo = now - (7L * 24 * 60 * 60 * 1000)
                readings.filter { it.timestamp >= sevenDaysAgo }
            }
            "Month" -> {
                val thirtyDaysAgo = now - (30L * 24 * 60 * 60 * 1000)
                readings.filter { it.timestamp >= thirtyDaysAgo }
            }
            else -> readings
        }
    }




    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Glucose Tracker") },
                actions = {
                    IconButton(onClick = { navController.navigate(Screen.WeeklyAnalytics.route) }) {
                        Icon(
                            imageVector = Icons.Default.BarChart,
                            contentDescription = "Weekly Analytics",
                            tint = TealPrimary
                        )
                    }
                }
            )
        },
        bottomBar = {
            SugarCareBottomNavBar(navController, Screen.Logs.route)
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Form & inputs
            item {
                Spacer(modifier = Modifier.height(8.dp))
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    SugarCareTextField(
                        value = glucoseInput,
                        onValueChange = { glucoseInput = it },
                        label = "Glucose Level (mg/dL)",
                        keyboardType = KeyboardType.Number,
                        modifier = Modifier.fillMaxWidth(),
                        colors = trackerFieldColors()
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    SugarCareTextField(
                        value = note,
                        onValueChange = { note = it },
                        label = "Notes",
                        modifier = Modifier.fillMaxWidth(),
                        colors = trackerFieldColors()
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    ExposedDropdownMenuBox(
                        expanded = showMealOptions,
                        onExpandedChange = { showMealOptions = !showMealOptions }
                    ) {
                        OutlinedTextField(
                            value = selectedMeal,
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("Before / After Meal") },
                            placeholder = { Text("Select") },
                            trailingIcon = {
                                ExposedDropdownMenuDefaults.TrailingIcon(expanded = showMealOptions)
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .menuAnchor(),
                            shape = RoundedCornerShape(14.dp),
                            colors = trackerFieldColors()
                        )

                        ExposedDropdownMenu(
                            expanded = showMealOptions,
                            onDismissRequest = { showMealOptions = false },
                            modifier = Modifier.background(Color.White)
                        ) {
                            mealOptions.forEach { option ->
                                DropdownMenuItem(
                                    text = {
                                        Text(
                                            text = option,
                                            color = TextDark,
                                            fontWeight = if (option == selectedMeal)
                                                FontWeight.SemiBold
                                            else
                                                FontWeight.Normal
                                        )
                                    },
                                    onClick = {
                                        selectedMeal = option
                                        showMealOptions = false
                                    },
                                    modifier = Modifier.background(Color.White)
                                )
                            }
                        }
                    }

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
                            placeholder = { Text("") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(16.dp),
                            colors = trackerFieldColors()
                        )
                        Text(":", style = MaterialTheme.typography.titleLarge)
                        OutlinedTextField(
                            value = minuteInput,
                            onValueChange = { if (it.length <= 2) minuteInput = it },
                            label = { Text("MM") },
                            placeholder = { Text("") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(16.dp),
                            colors = trackerFieldColors()
                        )

                        Box(
                            modifier = Modifier
                                .height(56.dp)
                                .width(72.dp)
                                .clip(RoundedCornerShape(16.dp))
                                .background(
                                    Brush.horizontalGradient(
                                        listOf(
                                            GreenAccent,
                                            GreenAccent2
                                        )
                                    )
                                )
                                .clickable {
                                    amPmSelection =
                                        if (amPmSelection == "AM") "PM"
                                        else "AM"
                                },
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = amPmSelection,
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(6.dp))

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp)
                            .clip(RoundedCornerShape(28.dp))
                            .background(
                                Brush.horizontalGradient(
                                    listOf(TealDark, TealPrimary2)
                                )
                            )
                            .clickable {
                                val level = glucoseInput.toIntOrNull()
                                if (level == null || level <= 0) {
                                    Toast.makeText(context, "Please enter a valid glucose level", Toast.LENGTH_SHORT).show()
                                    return@clickable
                                }

                                val detailsBuilder = mutableListOf<String>()

                                if (selectedMeal.isNotBlank()) {
                                    detailsBuilder.add("Meal: $selectedMeal")
                                }

                                if (hourInput.isNotBlank() && minuteInput.isNotBlank()) {
                                    detailsBuilder.add("Time: $hourInput:$minuteInput $amPmSelection")
                                }

                                if (note.isNotBlank()) {
                                    detailsBuilder.add("Note: $note")
                                }

                                val fullDetails = detailsBuilder.joinToString(" | ")

                                viewModel.addReading(
                                    level = level,
                                    note = fullDetails,
                                )

                                // Reset fields
                                glucoseInput = ""
                                selectedMeal = ""
                                note = ""
                                hourInput = ""
                                minuteInput = ""
                                amPmSelection = "AM"
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Icon(
                                Icons.Default.Add,
                                contentDescription = null,
                                tint = Color.White
                            )

                            Spacer(Modifier.width(8.dp))

                            Text(
                                text = "Add Reading",
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 18.sp
                            )
                        }
                    }
                }
            }

            // Chart Section
            item {
                Spacer(modifier = Modifier.height(16.dp))
                if (filteredReadings.size > 1) {
                    val points = filteredReadings.mapIndexed { index, sugarReading ->
                        Point(index.toFloat(), sugarReading.glucoseLevel.toFloat())
                    }
                    val xAxisData =
                        AxisData.Builder().axisStepSize(40.dp).steps(points.size - 1)
                            .labelData { i -> (i + 1).toString() }.build()
                    val yAxisData =
                        AxisData.Builder().steps(5).labelData { i -> (i * 50).toString() }
                            .build()
                    val lineChartData = LineChartData(
                        linePlotData = LinePlotData(
                            lines = listOf(
                                co.yml.charts.ui.linechart.model.Line(
                                    dataPoints = points,
                                    lineStyle = LineStyle(color = Color.Blue)
                                )
                            )
                        ),
                        xAxisData = xAxisData, yAxisData = yAxisData
                    )
                    LineChart(
                        modifier = Modifier
                            .height(200.dp)
                            .fillMaxWidth(),
                        lineChartData = lineChartData
                    )
                } else {
                    Box(
                        modifier = Modifier.fillMaxWidth().padding(vertical = 16.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("Enter at least 2 readings to view the graph", color = Color.Gray)
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
            }

            // Readings header & filter chips
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp)
                ) {
                    Text(
                        text = "History of Readings",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = TealDark
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        listOf("All", "Day", "Week", "Month").forEach { filter ->
                            val isSelected = selectedFilter == filter
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(if (isSelected) TealPrimary else TealLight.copy(alpha = 0.3f))
                                    .clickable { selectedFilter = filter }
                                    .padding(horizontal = 12.dp, vertical = 6.dp)
                            ) {
                                Text(
                                    text = filter,
                                    fontSize = 12.sp,
                                    color = if (isSelected) Color.White else TealDark,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                        }
                    }
                }
            }

            // Readings list
            items(filteredReadings) { reading ->
                val (meal, time, parsedNote) = remember(reading.note) { parseReadingNote(reading.note) }

                val formattedDate = remember(reading.timestamp) {
                    SimpleDateFormat("dd MMM yyyy", Locale.getDefault()).format(Date(reading.timestamp))
                }
                val formattedTimeFromTimestamp = remember(reading.timestamp) {
                    SimpleDateFormat("hh:mm a", Locale.getDefault()).format(Date(reading.timestamp))
                }

                // Color code depending on glucose level
                val (badgeBgColor, badgeTextColor) = when {
                    reading.glucoseLevel < 70 -> Pair(Color(0xFFFFEBEE), Color(0xFFD32F2F)) // Light Red / Dark Red
                    reading.glucoseLevel > 140 -> Pair(Color(0xFFFFF3E0), OrangeDrop) // Light Orange / Orange
                    else -> Pair(Color(0xFFE8F5E9), GreenAccent) // Light Green / Green
                }

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Glucose Badge
                        Box(
                            modifier = Modifier
                                .size(64.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(badgeBgColor),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(
                                    text = reading.glucoseLevel.toString(),
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 18.sp,
                                    color = badgeTextColor
                                )
                                Text(
                                    text = "mg/dL",
                                    fontSize = 10.sp,
                                    color = badgeTextColor.copy(alpha = 0.8f)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.width(12.dp))

                        // Time & Date Column (stacked vertically)
                        val timeToDisplay = when {
                            !time.isNullOrBlank() -> time
                            reading.glucoseLevel < 70 || reading.glucoseLevel > 140 -> formattedTimeFromTimestamp
                            else -> null
                        }

                        Column(
                            modifier = Modifier.width(IntrinsicSize.Max),
                            verticalArrangement = Arrangement.Center
                        ) {
                            if (timeToDisplay != null) {
                                Text(
                                    text = timeToDisplay,
                                    fontSize = 12.sp,
                                    color = TextDark,
                                    fontWeight = FontWeight.Bold
                                )
                            }

                            Text(
                                text = formattedDate,
                                fontSize = 10.sp,
                                color = TextLight,
                                fontWeight = FontWeight.Normal
                            )
                        }

                        Spacer(modifier = Modifier.width(12.dp))

                        // Details Column (Meal badge & Notes)
                        Column(modifier = Modifier.weight(1f)) {
                            // Meal tag (if exists)
                            if (!meal.isNullOrBlank()) {
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(TealLight.copy(alpha = 0.3f))
                                        .padding(horizontal = 8.dp, vertical = 2.dp)
                                ) {
                                    Text(
                                        text = "$meal Meal",
                                        fontSize = 10.sp,
                                        color = TealDark,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                }
                                Spacer(modifier = Modifier.height(4.dp))
                            }

                            // Note
                            if (!parsedNote.isNullOrBlank()) {
                                Text(
                                    text = parsedNote,
                                    fontSize = 13.sp,
                                    color = TextMedium,
                                    fontWeight = FontWeight.Normal
                                )
                            } else {
                                Text(
                                    text = "No notes added",
                                    fontSize = 12.sp,
                                    color = TextLight
                                )
                            }
                        }

                        // Delete button
                        IconButton(onClick = { viewModel.deleteReading(reading.id) }) {
                            Icon(
                                imageVector = Icons.Default.Delete,
                                contentDescription = "Delete",
                                tint = Color(0xFFEF5350)
                            )
                        }
                    }
                }
            }
            item {
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}


@Composable
private fun trackerFieldColors() = OutlinedTextFieldDefaults.colors(
    focusedBorderColor = TealPrimary,
    unfocusedBorderColor = TealPrimary.copy(alpha = 0.5f),

    focusedLabelColor = TealPrimary,
    unfocusedLabelColor = TextDark.copy(alpha = 0.7f),

    focusedTextColor = TextDark,
    unfocusedTextColor = TextDark,

    focusedPlaceholderColor = TextDark.copy(alpha = 0.4f),
    unfocusedPlaceholderColor = TextDark.copy(alpha = 0.4f),

    cursorColor = TealPrimary
)

/*
package com.example.sugercare.viewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.sugercare.app.SugarReading
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

private val savedReadingsList = MutableStateFlow<List<SugarReading>>(emptyList())
class SugarTrackerViewModel : ViewModel() {
    private val _trackerState = MutableStateFlow<TrackerState>(TrackerState.Idle)
    val trackerState: StateFlow<TrackerState> = _trackerState.asStateFlow()
    val readingsList: StateFlow<List<SugarReading>> = savedReadingsList.asStateFlow()
    private val _glucoseInput = MutableStateFlow("")
    val glucoseInput: StateFlow<String> = _glucoseInput.asStateFlow()
    private val _mealRelation = MutableStateFlow("")
    val mealRelation: StateFlow<String> = _mealRelation.asStateFlow()
    private val _note = MutableStateFlow("")
    val note: StateFlow<String> = _note.asStateFlow()
    private val _hourInput = MutableStateFlow("")
    val hourInput: StateFlow<String> = _hourInput.asStateFlow()
    private val _minuteInput = MutableStateFlow("")
    val minuteInput: StateFlow<String> = _minuteInput.asStateFlow()
    private val _amPmSelection = MutableStateFlow("AM")
    val amPmSelection: StateFlow<String> = _amPmSelection.asStateFlow()

    fun updateGlucoseInput(newValue: String) { _glucoseInput.value = newValue }
    fun updateMealRelation(newValue: String) { _mealRelation.value = newValue }
    fun updateNote(newValue: String) { _note.value = newValue }
    fun updateHourInput(newValue: String) { _hourInput.value = newValue }
    fun updateMinuteInput(newValue: String) { _minuteInput.value = newValue }
    fun updateAmPmSelection(newValue: String) { _amPmSelection.value = newValue }
    fun resetTrackerState() { _trackerState.value = TrackerState.Idle }
    fun saveSugarRecord() {
        val glucose = _glucoseInput.value
        val meal = _mealRelation.value
        val hour = _hourInput.value
        val minute = _minuteInput.value
        val amPm = _amPmSelection.value

        when {
            glucose.isEmpty() -> { _trackerState.value = TrackerState.Error("Glucose reading is required") }
            meal.isEmpty() -> { _trackerState.value = TrackerState.Error("Meal relation is required") }
            hour.isEmpty() || minute.isEmpty() -> {
                _trackerState.value = TrackerState.Error("Time is required")
            }
            else -> {
                val level = glucose.toIntOrNull() ?: 0
                if (level > 0) {
                    val detailsBuilder = mutableListOf<String>()
                    detailsBuilder.add("Meal: $meal")
                    if (_note.value.isNotEmpty()) detailsBuilder.add("Note: ${_note.value}")
                    detailsBuilder.add("Time: $hour:$minute $amPm")

                    val fullDetails = detailsBuilder.joinToString(" | ")
                    performSaveRecord(level, fullDetails)
                } else {
                    _trackerState.value = TrackerState.Error("Invalid glucose level")
                }
            }
        }
    }
    private fun performSaveRecord(level: Int, details: String) {
        _trackerState.value = TrackerState.Loading
        viewModelScope.launch {
            try {
                val newReading = SugarReading(
                    glucoseLevel = level,
                    note = details
                )
                savedReadingsList.value = savedReadingsList.value + newReading
                _trackerState.value = TrackerState.Success
                clearForm()
            } catch (e: Exception) {
                _trackerState.value = TrackerState.Error(e.message ?: "Save failed")
            }
        }
    }
    fun deleteReading(id: String) {
        savedReadingsList.value = savedReadingsList.value.filter { it.id != id }
    }
    private fun clearForm() {
        _glucoseInput.value = ""
        _mealRelation.value = ""
        _note.value = ""
        _hourInput.value = ""
        _minuteInput.value = ""
        _amPmSelection.value = "AM"
    }
}
sealed class TrackerState {
    object Success : TrackerState()
    object Idle : TrackerState()
    object Loading : TrackerState()
    data class Error(val message: String) : TrackerState()
}*/
