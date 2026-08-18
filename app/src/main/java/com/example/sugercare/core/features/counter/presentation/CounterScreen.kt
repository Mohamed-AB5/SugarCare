package com.example.sugercare.core.features.counter.presentation

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.drawscope.clipRect
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.sugercare.core.features.counter.model.HistoryEntry
import com.sugarcare.app.R
import com.sugarcare.app.ui.components.GradientButton
import com.sugarcare.app.ui.components.SugarCareBackground
import com.sugarcare.app.ui.theme.BackgroundLight
import com.sugarcare.app.ui.theme.FireIcon
import com.sugarcare.app.ui.theme.OrangeDrop
import com.sugarcare.app.ui.theme.OrangeDrop2
import com.sugarcare.app.ui.theme.TealLight
import com.sugarcare.app.ui.theme.TealPrimary
import com.sugarcare.app.ui.theme.TealPrimary2
import com.sugarcare.app.ui.theme.TextGray
import kotlinx.coroutines.delay
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.concurrent.TimeUnit


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CounterScreen(
    viewModel: CounterViewModel = viewModel()
) {
    val state = viewModel.uiState.collectAsState()
    var now by remember { mutableLongStateOf(System.currentTimeMillis()) }
    var showHistory by remember { mutableStateOf(false) }
    var showEditDate by remember { mutableStateOf(false) }

    LaunchedEffect(state.value.isRunning) {
        while (state.value.isRunning) {
            now = System.currentTimeMillis()
            viewModel.checkAndUpdateBestStreak()
            delay(1000)
        }
    }

    val elapsedMillis =
        if (state.value.isRunning) (now - state.value.startDate).coerceAtLeast(0) else 0L
    val elapsedDays =
        TimeUnit.MILLISECONDS.toDays(elapsedMillis).toInt().coerceAtMost(state.value.totalDays)
    val progress =
        if (state.value.totalDays > 0) elapsedDays / state.value.totalDays.toFloat() else 0f
    val elapsedSecondsToday = TimeUnit.MILLISECONDS.toSeconds(elapsedMillis) % 86400
    val hours = elapsedSecondsToday / 3600
    val minutes = (elapsedSecondsToday % 3600) / 60
    val seconds = elapsedSecondsToday % 60
    SugarCareBackground {
        Column(modifier = Modifier.fillMaxSize()) {

            // Header
            Text(
                "90 Day Challenge",
                style = MaterialTheme.typography.headlineMedium,
                modifier = Modifier.padding(start = 24.dp, top = 48.dp, bottom = 8.dp),
                color = TealPrimary, fontWeight = FontWeight.Bold
            )

            // Screen content
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                // ── Top Row: streak (left) | actions (right) ─────────
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            Icons.Filled.LocalFireDepartment,
                            contentDescription = "Best streak",
                            tint = FireIcon
                        )
                        Spacer(Modifier.width(4.dp))
                        Text(
                            "${state.value.bestStreak} days",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = FireIcon
                        )
                    }
                    Row {
                        IconButton(onClick = { showEditDate = true }) {
                            Icon(
                                Icons.Filled.Edit,
                                contentDescription = "Edit Start Date",
                                tint = TealPrimary2
                            )
                        }
                        IconButton(onClick = { showHistory = true }) {
                            Icon(
                                Icons.Filled.History,
                                contentDescription = "History",
                                tint = TealPrimary2
                            )
                        }
                    }
                }

                Spacer(Modifier.height(16.dp))

                ShapeProgressFill(
                    painter = painterResource(R.drawable.ic_90_counter),
                    progress = progress,
                    filledColor = OrangeDrop2,
                    emptyColor = TealLight,
                    modifier = Modifier
                        .fillMaxWidth(0.75f)
                        .height(180.dp)
                )

                Spacer(Modifier.height(16.dp))

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth(0.8f)
                ) {
                    HorizontalDivider(
                        modifier = Modifier.weight(1f),
                        color = TealLight,
                        thickness = 2.dp
                    )
                    Text(
                        "Day",
                        color = TextGray,
                        fontSize = 14.sp,
                        modifier = Modifier.padding(horizontal = 12.dp)
                    )
                    HorizontalDivider(
                        modifier = Modifier.weight(1f),
                        color = TealLight,
                        thickness = 2.dp
                    )
                }

                Spacer(Modifier.height(16.dp))

                Text(
                    "$elapsedDays/${state.value.totalDays}",
                    fontSize = 34.sp,
                    fontWeight = FontWeight.Bold,
                    color = TealPrimary2
                )

                Spacer(Modifier.height(20.dp))

                Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                    TimeUnitBox(value = hours.toInt(), label = "Hour")
                    TimeUnitBox(value = minutes.toInt(), label = "Minute")
                    TimeUnitBox(value = seconds.toInt(), label = "Second")
                }

                Spacer(Modifier.height(32.dp))
                // —— Start & Reset buttons ——————
                if (!state.value.isRunning) {
                    GradientButton(
                        text = "Start Counter",
                        textSize = 14.sp,
                        onClick = { viewModel.startCounter() },
                        color1 = TealPrimary2,
                        color2 = TealPrimary,
                        modifier = Modifier
                            .fillMaxWidth(0.7f)
                            .height(50.dp)
                    ) {
                        Image(
                            painter = painterResource(R.drawable.ic_play),
                            contentDescription = null,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                } else {
                    GradientButton(
                        text = "Reset Counter",
                        textSize = 14.sp,
                        onClick = { viewModel.resetCounter() },
                        color1 = OrangeDrop2,
                        color2 = OrangeDrop,
                        modifier = Modifier
                            .fillMaxWidth(0.7f)
                            .height(50.dp)
                    ) {
                        Image(
                            painter = painterResource(R.drawable.ic_reload),
                            contentDescription = null,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }
            }

        }



        if (showEditDate) {
            EditStartDateDialog(
                currentStartDate = state.value.startDate,
                onDismiss = { showEditDate = false },
                onConfirm = { newDate ->
                    viewModel.updateStartDate(newDate)
                    showEditDate = false
                }
            )
        }

        if (showHistory) {
            HistoryDialog(
                history = state.value.history.sortedByDescending { it.date },
                onDismiss = { showHistory = false }
            )
        }
    }
}


@Composable
fun TimeUnitBox(value: Int, label: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Box(
            modifier = Modifier
                .size(width = 64.dp, height = 56.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(TealLight),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = value.toString().padStart(2, '0'),
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF2B2F33)
            )
        }
        Spacer(Modifier.height(4.dp))
        Text(text = label, fontSize = 12.sp, color = TextGray)
    }
}

// ─────────────────────────────────────────────────────────────
//  Edit Start Date Dialog
// ─────────────────────────────────────────────────────────────
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditStartDateDialog(
    currentStartDate: Long,
    onDismiss: () -> Unit,
    onConfirm: (newDate: Long) -> Unit
) {
    val calendar = remember { Calendar.getInstance().apply { timeInMillis = currentStartDate } }
    val dateFormatter = remember { SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()) }
    var selectedDateText by remember { mutableStateOf(dateFormatter.format(Date(currentStartDate))) }
    var showDatePicker by remember { mutableStateOf(false) }

    if (showDatePicker) {
        val datePickerState = rememberDatePickerState(initialSelectedDateMillis = currentStartDate)
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    datePickerState.selectedDateMillis?.let {
                        selectedDateText = dateFormatter.format(Date(it))
                        calendar.timeInMillis = it
                    }
                    showDatePicker = false
                }) { Text("OK", color = TealPrimary2) }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) {
                    Text("Cancel", color = TealPrimary2)
                }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(20.dp),
            color = Color.White,
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(24.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Edit Start Date", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Filled.Close, contentDescription = "Close")
                    }
                }

                Spacer(Modifier.height(16.dp))

                OutlinedTextField(
                    value = selectedDateText,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Start Date") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { showDatePicker = true },
                    trailingIcon = {
                        IconButton(onClick = { showDatePicker = true }) {
                            Icon(Icons.Filled.Edit, contentDescription = null, tint = TealPrimary2)
                        }
                    },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = TealPrimary2,
                        focusedLabelColor = TealPrimary2
                    )
                )

                Spacer(Modifier.height(12.dp))


                Spacer(Modifier.height(20.dp))

                Button(
                    onClick = {
                        onConfirm(calendar.timeInMillis)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp),
                    shape = RoundedCornerShape(24.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = TealPrimary2)
                ) {
                    Text("Save", color = Color.White, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

// ─────────────────────────────────────────────────────────────
//  History Dialog
// ─────────────────────────────────────────────────────────────
@Composable
fun HistoryDialog(
    history: List<HistoryEntry>,
    onDismiss: () -> Unit
) {
    val formatter = remember { SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault()) }

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(20.dp),
            color = Color.White,
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(max = 480.dp)
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("History Log", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Filled.Close, contentDescription = "Close")
                    }
                }

                Spacer(Modifier.height(12.dp))

                if (history.isEmpty()) {
                    Text("No history yet.", color = TextGray, modifier = Modifier.padding(16.dp))
                } else {
                    LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        items(history) { entry ->
                            HistoryRow(entry = entry, formatter = formatter)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun HistoryRow(entry: HistoryEntry, formatter: SimpleDateFormat) {
    val (label, color) = when (entry.action) {
        "START" -> "Start" to TealLight
        "SET" -> "Set" to TealPrimary2
        "RESET" -> "Reset" to OrangeDrop2
        else -> "Unknown" to Color.Gray
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(BackgroundLight)
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(10.dp)
                .clip(CircleShape)
                .background(color)
        )
        Spacer(Modifier.width(10.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = "$label • ${entry.totalDays} days",
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp
            )
            Text(
                text = "Start: ${formatter.format(Date(entry.startDate))}",
                fontSize = 12.sp,
                color = TextGray
            )
        }
        Text(
            text = formatter.format(Date(entry.date)),
            fontSize = 11.sp,
            color = TextGray
        )
    }
}


@Composable
fun ShapeProgressFill(
    painter: Painter,
    progress: Float,
    filledColor: Color,
    emptyColor: Color,
    modifier: Modifier = Modifier
) {
    Box(modifier = modifier) {
        // Empty shape
        Image(
            painter = painter,
            contentDescription = null,
            colorFilter = ColorFilter.tint(emptyColor),
            modifier = Modifier.matchParentSize()
        )
        // Filled shape
        Image(
            painter = painter,
            contentDescription = null,
            colorFilter = ColorFilter.tint(filledColor),
            modifier = Modifier
                .matchParentSize()
                .drawWithContent {
                    clipRect(top = size.height * (1f - progress)) {
                        this@drawWithContent.drawContent()
                    }
                }
        )
    }
}