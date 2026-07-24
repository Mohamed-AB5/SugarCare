package com.example.sugercare.app

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
import androidx.compose.material.icons.automirrored.filled.Assignment
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
import com.sugarcare.app.ui.theme.GreenAccent
import com.sugarcare.app.ui.theme.GreenAccent2
import com.sugarcare.app.ui.theme.OrangeDrop
import com.sugarcare.app.ui.theme.TealDark
import com.sugarcare.app.ui.theme.TealLight
import com.sugarcare.app.ui.theme.TealPrimary
import com.sugarcare.app.ui.theme.TealPrimary2
import com.sugarcare.app.ui.theme.TextDark
import com.sugarcare.app.ui.theme.TextMedium
import com.sugarcare.app.ui.theme.TextLight
import androidx.navigation.NavHostController
import com.sugarcare.app.navigation.Screen
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Restaurant
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.sugercare.viewModels.SugarViewModel
import com.sugarcare.app.ui.theme.BackgroundDark
import com.sugarcare.app.ui.theme.BackgroundLight
import com.sugarcare.app.ui.theme.LocalDarkTheme
import com.sugarcare.app.ui.theme.SurfaceDark

// it was in crud/Tracker
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

    val isDark = LocalDarkTheme.current.value
    val bgColor = if (isDark) BackgroundDark else BackgroundLight
    val cardColor = if (isDark) SurfaceDark else Color.White
    val textColor = if (isDark) Color(0xFFE0F2F1) else TextDark
    val subColor = if (isDark) Color(0xFF80CBC4) else TextMedium
    val navColor = if (isDark) SurfaceDark else Color.White
    val navText = if (isDark) Color(0xFF80CBC4) else TextMedium

    val currentRoute = navController.currentBackStackEntryAsState().value?.destination?.route




    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Glucose Tracker", color = textColor) },
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
            NavigationBar(containerColor = navColor, tonalElevation = 8.dp) {
                listOf(
                    Triple("Home", Icons.Filled.Home, Screen.Home.route),
                    Triple("Logs", Icons.AutoMirrored.Filled.Assignment, Screen.Logs.route),
                    Triple("Meals", Icons.Filled.Restaurant, Screen.MealPlan.route),
                    Triple("Profile", Icons.Filled.Person, Screen.Profile.route)
                ).forEach { (label, icon, route) ->
                    NavigationBarItem(
                        selected = route == currentRoute,
                        onClick = {
                            if (route != Screen.Logs.route) navController.navigate(route) {
                                launchSingleTop = true
                            }
                        },
                        icon = { Icon(icon, contentDescription = label) },
                        label = { Text(label, fontSize = 11.sp, color = textColor) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = TealPrimary,
                            unselectedIconColor = navText,
                            indicatorColor = TealLight
                        )
                    )
                }
            }
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
                    // it was SugarCareTextField
                    OutlinedTextField(
                        value = glucoseInput,
                        onValueChange = { glucoseInput = it },
                        label = { Text("Glucose Level (mg/dL)", color = textColor) },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = trackerFieldColors()
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    // it was SugarCareTextField
                    OutlinedTextField(
                        value = note,
                        onValueChange = { note = it },
                        label = { Text("Notes", color = textColor) },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
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
                            label = { Text("Before / After Meal", color = textColor) },
                            placeholder = { Text("Select",color = textColor) },
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
                            label = { Text("HH", color = textColor) },
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
                            label = { Text("MM", color = textColor) },
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
                                    Toast.makeText(
                                        context,
                                        "Please enter a valid glucose level",
                                        Toast.LENGTH_SHORT
                                    ).show()
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
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 16.dp),
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
                                    .background(
                                        if (isSelected) TealPrimary else TealLight.copy(
                                            alpha = 0.3f
                                        )
                                    )
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
                    SimpleDateFormat(
                        "dd MMM yyyy",
                        Locale.getDefault()
                    ).format(Date(reading.timestamp))
                }
                val formattedTimeFromTimestamp = remember(reading.timestamp) {
                    SimpleDateFormat("hh:mm a", Locale.getDefault()).format(Date(reading.timestamp))
                }

                // Color code depending on glucose level
                val (badgeBgColor, badgeTextColor) = when {
                    reading.glucoseLevel < 70 -> Pair(
                        Color(0xFFFFEBEE),
                        Color(0xFFD32F2F)
                    ) // Light Red / Dark Red
                    reading.glucoseLevel > 140 -> Pair(
                        Color(0xFFFFF3E0),
                        OrangeDrop
                    ) // Light Orange / Orange
                    else -> Pair(Color(0xFFE8F5E9), GreenAccent) // Light Green / Green
                }

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = cardColor),
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
                                    color = textColor,
                                    fontWeight = FontWeight.Bold
                                )
                            }

                            Text(
                                text = formattedDate,
                                fontSize = 10.sp,
                                color = textColor,
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
                                        color = textColor,
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
                                    color = subColor,
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

