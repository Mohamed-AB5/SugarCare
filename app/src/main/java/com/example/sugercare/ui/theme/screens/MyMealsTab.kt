package com.example.sugercare.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.sugercare.viewModels.MealCrudEvent
import com.example.sugercare.viewModels.MealCrudUiState
import com.example.sugercare.viewModels.MealCrudViewModel
import com.example.sugercare.viewModels.MealSortOrder
import com.example.sugercare.data.meal.MealEntity
import com.sugarcare.app.ui.components.SugarCareCard
import com.example.sugercare.ui.theme.*
import com.sugarcare.app.ui.theme.BackgroundLight
import com.sugarcare.app.ui.theme.GreenAccent
import com.sugarcare.app.ui.theme.SurfaceWhite
import com.sugarcare.app.ui.theme.TealDark
import com.sugarcare.app.ui.theme.TealLight
import com.sugarcare.app.ui.theme.TealPrimary
import com.sugarcare.app.ui.theme.TextDark
import com.sugarcare.app.ui.theme.TextLight
import com.sugarcare.app.ui.theme.TextMedium
import java.text.SimpleDateFormat
import java.util.*

/**
 * "My Meals" tab — full CRUD UI.
 *
 * Rendered inside MealPlanScreen as Tab 2.
 * All logic lives in [MealCrudViewModel].
 * All persistence lives in MealRepository → MealDao → Room.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyMealsTab(viewModel: MealCrudViewModel) {

    val uiState      by viewModel.uiState.collectAsState()
    val searchQuery  by viewModel.searchQuery.collectAsState()
    val selectedFilter by viewModel.selectedFilter.collectAsState()
    val sortOrder    by viewModel.sortOrder.collectAsState()

    // ── Dialog state ──────────────────────────────────────────
    var showAddDialog    by remember { mutableStateOf(false) }
    var editingMeal      by remember { mutableStateOf<MealEntity?>(null) }
    var deletingMeal     by remember { mutableStateOf<MealEntity?>(null) }

    // ── Sort dropdown state ───────────────────────────────────
    var sortDropdownExpanded by remember { mutableStateOf(false) }

    // ── Snackbar ──────────────────────────────────────────────
    val snackbarHostState = remember { SnackbarHostState() }
    LaunchedEffect(Unit) {
        viewModel.events.collect { event ->
            when (event) {
                is MealCrudEvent.ShowSnackbar ->
                    snackbarHostState.showSnackbar(event.message)
            }
        }
    }

    // ── Dialogs ───────────────────────────────────────────────
    if (showAddDialog) {
        MealDialog(
            existingMeal = null,
            onDismiss    = { showAddDialog = false },
            onSave       = { name, type, desc, cal, time ->
                viewModel.addMeal(name, type, desc, cal, time)
                showAddDialog = false
            }
        )
    }

    editingMeal?.let { meal ->
        MealDialog(
            existingMeal = meal,
            onDismiss    = { editingMeal = null },
            onSave       = { name, type, desc, cal, time ->
                viewModel.updateMeal(
                    meal.copy(
                        mealName    = name,
                        mealType    = type,
                        description = desc,
                        calories    = cal,
                        mealTime    = time
                    )
                )
                editingMeal = null
            }
        )
    }

    deletingMeal?.let { meal ->
        DeleteMealDialog(
            mealName  = meal.mealName,
            onDismiss = { deletingMeal = null },
            onConfirm = {
                viewModel.deleteMeal(meal)
                deletingMeal = null
            }
        )
    }

    // ── Layout ────────────────────────────────────────────────
    Scaffold(
        containerColor = Color.Transparent,
        snackbarHost   = { SnackbarHost(snackbarHostState) },
        floatingActionButton = {
            FloatingActionButton(
                onClick           = { showAddDialog = true },
                containerColor    = TealPrimary,
                contentColor      = Color.White,
                shape             = RoundedCornerShape(16.dp)
            ) {
                Icon(Icons.Filled.Add, contentDescription = "Add meal")
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // ── Search bar ────────────────────────────────────
            OutlinedTextField(
                value         = searchQuery,
                onValueChange = viewModel::updateSearchQuery,
                placeholder   = { Text("Search meals…", color = TextLight) },
                leadingIcon   = {
                    Icon(Icons.Filled.Search, null, tint = TealPrimary)
                },
                trailingIcon  = if (searchQuery.isNotEmpty()) {
                    {
                        IconButton(onClick = { viewModel.updateSearchQuery("") }) {
                            Icon(Icons.Filled.Clear, null, tint = TextMedium)
                        }
                    }
                } else null,
                singleLine    = true,
                shape         = RoundedCornerShape(12.dp),
                colors        = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor   = TealPrimary,
                    unfocusedBorderColor = TealLight,
                    focusedLabelColor    = TealPrimary,
                    cursorColor          = TealPrimary
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            )

            // ── Filter chips + Sort button ────────────────────
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState())
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment     = Alignment.CenterVertically
            ) {
                viewModel.filterOptions.forEach { filter ->
                    FilterChip(
                        selected = selectedFilter == filter,
                        onClick  = { viewModel.updateFilter(filter) },
                        label    = { Text(filter, fontSize = 12.sp) },
                        colors   = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = TealPrimary,
                            selectedLabelColor     = Color.White,
                            containerColor         = BackgroundLight,
                            labelColor             = TextMedium
                        ),
                        border = FilterChipDefaults.filterChipBorder(
                            enabled             = true,
                            selected            = selectedFilter == filter,
                            borderColor         = TealLight,
                            selectedBorderColor = TealPrimary
                        )
                    )
                }

                // Sort dropdown
                Box {
                    AssistChip(
                        onClick = { sortDropdownExpanded = true },
                        label   = { Text(sortOrder.label, fontSize = 12.sp) },
                        leadingIcon = {
                            Icon(Icons.Filled.Sort, null,
                                tint     = TealPrimary,
                                modifier = Modifier.size(16.dp))
                        },
                        border = AssistChipDefaults.assistChipBorder(
                            enabled     = true,
                            borderColor = TealLight
                        ),
                        colors = AssistChipDefaults.assistChipColors(
                            labelColor = TextMedium
                        )
                    )
                    DropdownMenu(
                        expanded        = sortDropdownExpanded,
                        onDismissRequest = { sortDropdownExpanded = false },
                        containerColor  = SurfaceWhite
                    ) {
                        MealSortOrder.entries.forEach { order ->
                            DropdownMenuItem(
                                text    = { Text(order.label, color = TextDark) },
                                onClick = {
                                    viewModel.updateSortOrder(order)
                                    sortDropdownExpanded = false
                                }
                            )
                        }
                    }
                }
            }

            Spacer(Modifier.height(8.dp))

            // ── Content ───────────────────────────────────────
            when (val state = uiState) {
                is MealCrudUiState.Loading -> {
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(color = TealPrimary)
                    }
                }
                is MealCrudUiState.Success -> {
                    if (state.meals.isEmpty()) {
                        EmptyMealsState(onAddClick = { showAddDialog = true })
                    } else {
                        LazyColumn(
                            modifier            = Modifier.fillMaxSize(),
                            contentPadding      = PaddingValues(
                                start  = 16.dp,
                                end    = 16.dp,
                                bottom = 88.dp   // space for FAB
                            ),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            items(
                                items = state.meals,
                                key   = { it.id }
                            ) { meal ->
                                AnimatedVisibility(
                                    visible = true,
                                    enter   = fadeIn() + expandVertically(),
                                    exit    = fadeOut() + shrinkVertically()
                                ) {
                                    MealCrudCard(
                                        meal     = meal,
                                        onEdit   = { editingMeal = meal },
                                        onDelete = { deletingMeal = meal }
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

// ─────────────────────────────────────────────────────────────
//  Meal Card
// ─────────────────────────────────────────────────────────────

@Composable
private fun MealCrudCard(
    meal: MealEntity,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    SugarCareCard {
        // ── Top row: type chip + time ─────────────────────────
        Row(
            modifier              = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment     = Alignment.CenterVertically
        ) {
            Surface(
                shape = RoundedCornerShape(50),
                color = mealTypeColor(meal.mealType)
            ) {
                Text(
                    text       = meal.mealType,
                    color      = Color.White,
                    fontSize   = 11.sp,
                    fontWeight = FontWeight.Bold,
                    modifier   = Modifier.padding(horizontal = 10.dp, vertical = 3.dp)
                )
            }
            if (meal.mealTime.isNotBlank()) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        Icons.Filled.Schedule,
                        contentDescription = null,
                        tint     = TextMedium,
                        modifier = Modifier.size(13.dp)
                    )
                    Spacer(Modifier.width(3.dp))
                    Text(meal.mealTime, fontSize = 12.sp, color = TextMedium)
                }
            }
        }

        Spacer(Modifier.height(6.dp))

        // ── Meal name ─────────────────────────────────────────
        Text(
            text       = meal.mealName,
            fontWeight = FontWeight.Bold,
            fontSize   = 16.sp,
            color      = TextDark,
            maxLines   = 1,
            overflow   = TextOverflow.Ellipsis
        )

        // ── Description ───────────────────────────────────────
        if (meal.description.isNotBlank()) {
            Spacer(Modifier.height(4.dp))
            Text(
                text     = meal.description,
                fontSize = 13.sp,
                color    = TextMedium,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
        }

        // ── Calories ──────────────────────────────────────────
        meal.calories?.let { cal ->
            Spacer(Modifier.height(4.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    Icons.Filled.LocalFireDepartment,
                    contentDescription = null,
                    tint     = GreenAccent,
                    modifier = Modifier.size(14.dp)
                )
                Spacer(Modifier.width(3.dp))
                Text(
                    text       = "$cal kcal",
                    fontSize   = 12.sp,
                    color      = GreenAccent,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }

        // ── Created date ──────────────────────────────────────
        Spacer(Modifier.height(4.dp))
        Text(
            text     = "Added ${formatDate(meal.createdAt)}",
            fontSize = 11.sp,
            color    = TextLight
        )

        Spacer(Modifier.height(8.dp))
        HorizontalDivider(color = TealLight.copy(alpha = 0.4f))
        Spacer(Modifier.height(6.dp))

        // ── Edit / Delete buttons ─────────────────────────────
        Row(
            modifier              = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.End,
            verticalAlignment     = Alignment.CenterVertically
        ) {
            TextButton(
                onClick = onEdit,
                colors  = ButtonDefaults.textButtonColors(contentColor = TealPrimary)
            ) {
                Icon(Icons.Filled.Edit, null, modifier = Modifier.size(16.dp))
                Spacer(Modifier.width(4.dp))
                Text("Edit", fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
            }
            Spacer(Modifier.width(4.dp))
            TextButton(
                onClick = onDelete,
                colors  = ButtonDefaults.textButtonColors(
                    contentColor = MaterialTheme.colorScheme.error
                )
            ) {
                Icon(Icons.Filled.Delete, null, modifier = Modifier.size(16.dp))
                Spacer(Modifier.width(4.dp))
                Text("Delete", fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
            }
        }
    }
}

// ─────────────────────────────────────────────────────────────
//  Empty state
// ─────────────────────────────────────────────────────────────

@Composable
private fun EmptyMealsState(onAddClick: () -> Unit) {
    Column(
        modifier            = Modifier
            .fillMaxSize()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            Icons.Filled.Restaurant,
            contentDescription = null,
            tint     = TealLight,
            modifier = Modifier.size(80.dp)
        )
        Spacer(Modifier.height(16.dp))
        Text(
            text       = "No meals added yet",
            fontSize   = 18.sp,
            fontWeight = FontWeight.Bold,
            color      = TextDark
        )
        Spacer(Modifier.height(8.dp))
        Text(
            text     = "Start tracking your meals by adding your first one.",
            fontSize = 14.sp,
            color    = TextMedium
        )
        Spacer(Modifier.height(24.dp))
        Button(
            onClick = onAddClick,
            shape   = RoundedCornerShape(24.dp),
            colors  = ButtonDefaults.buttonColors(containerColor = TealPrimary)
        ) {
            Icon(Icons.Filled.Add, null)
            Spacer(Modifier.width(6.dp))
            Text("Add your first meal", fontWeight = FontWeight.Bold)
        }
    }
}

// ─────────────────────────────────────────────────────────────
//  Helpers
// ─────────────────────────────────────────────────────────────

/** Returns a distinct color per meal type so cards are easy to scan. */
@Composable
private fun mealTypeColor(type: String): androidx.compose.ui.graphics.Color = when (type) {
    "Breakfast" -> TealPrimary
    "Lunch"     -> GreenAccent
    "Dinner"    -> TealDark
    "Snack"     -> TextMedium
    else        -> TealPrimary
}

private fun formatDate(epochMillis: Long): String =
    SimpleDateFormat("dd MMM yyyy", Locale.getDefault()).format(Date(epochMillis))
