package com.example.sugercare.viewModels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.sugercare.data.meal.MealEntity
import com.example.sugercare.data.meal.MealRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

// ─────────────────────────────────────────────────────────────
//  UI State
// ─────────────────────────────────────────────────────────────

/**
 * Immutable snapshot of everything the My Meals tab needs to render.
 *
 * Using a single sealed state object means the UI always sees a
 * consistent picture — no partial updates between individual StateFlows.
 */
sealed class MealCrudUiState {
    object Loading : MealCrudUiState()
    data class Success(val meals: List<MealEntity>) : MealCrudUiState()
}

/** One-shot events sent from the ViewModel to the UI via SharedFlow. */
sealed class MealCrudEvent {
    data class ShowSnackbar(val message: String) : MealCrudEvent()
}

// ─────────────────────────────────────────────────────────────
//  Sort options
// ─────────────────────────────────────────────────────────────

enum class MealSortOrder(val label: String) {
    NEWEST("Newest"),
    OLDEST("Oldest"),
    TIME("Time")
}

// ─────────────────────────────────────────────────────────────
//  ViewModel
// ─────────────────────────────────────────────────────────────

/**
 * ViewModel for the My Meals CRUD tab.
 *
 * Separated from the existing [MealViewModel] deliberately:
 * Room requires [AndroidViewModel] for the Application context,
 * but changing [MealViewModel]'s constructor would break the
 * existing Doctor Plan and Suggestions tabs.
 *
 * ── Firestore upgrade path ──────────────────────────────────────
 * Replace [MealRepository] with a Firestore-backed repository.
 * This ViewModel and all StateFlows stay identical.
 * ───────────────────────────────────────────────────────────────
 */
@OptIn(ExperimentalCoroutinesApi::class, FlowPreview::class)
class MealCrudViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = MealRepository(application)

    // ── Search ────────────────────────────────────────────────
    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    // ── Filter ────────────────────────────────────────────────
    private val _selectedFilter = MutableStateFlow("All")
    val selectedFilter: StateFlow<String> = _selectedFilter.asStateFlow()

    val filterOptions = listOf("All", "Breakfast", "Lunch", "Dinner", "Snack")

    // ── Sort ──────────────────────────────────────────────────
    private val _sortOrder = MutableStateFlow(MealSortOrder.NEWEST)
    val sortOrder: StateFlow<MealSortOrder> = _sortOrder.asStateFlow()

    // ── UI State ──────────────────────────────────────────────

    /**
     * Combines search query, filter, and sort into a single Flow.
     * Emits a new [MealCrudUiState] whenever any of the three change
     * or the underlying database changes.
     */
    val uiState: StateFlow<MealCrudUiState> = combine(
        _searchQuery
            .debounce(300L)         // avoid querying on every keystroke
            .distinctUntilChanged(),
        _selectedFilter,
        _sortOrder
    ) { query, filter, sort ->
        Triple(query, filter, sort)
    }.flatMapLatest { (query, filter, sort) ->
        val source: Flow<List<MealEntity>> = when {
            query.isNotBlank()  -> repository.searchMeals(query)
            filter != "All"     -> repository.getMealsByType(filter)
            else                -> repository.getAllMeals()
        }
        source.map { list ->
            val sorted = when (sort) {
                MealSortOrder.NEWEST -> list.sortedByDescending { it.createdAt }
                MealSortOrder.OLDEST -> list.sortedBy { it.createdAt }
                MealSortOrder.TIME   -> list.sortedBy { it.mealTime }
            }
            MealCrudUiState.Success(sorted) as MealCrudUiState
        }
    }.stateIn(
        scope         = viewModelScope,
        started       = SharingStarted.WhileSubscribed(5_000),
        initialValue  = MealCrudUiState.Loading
    )

    // ── One-shot events (Snackbar) ─────────────────────────────
    private val _events = MutableSharedFlow<MealCrudEvent>()
    val events: SharedFlow<MealCrudEvent> = _events.asSharedFlow()

    // ── Search / Filter / Sort actions ────────────────────────

    fun updateSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun updateFilter(filter: String) {
        _selectedFilter.value = filter
    }

    fun updateSortOrder(order: MealSortOrder) {
        _sortOrder.value = order
    }

    // ── CRUD actions ──────────────────────────────────────────

    fun addMeal(
        mealName: String,
        mealType: String,
        description: String,
        calories: Int?,
        mealTime: String
    ) {
        viewModelScope.launch {
            repository.insertMeal(
                MealEntity(
                    mealName    = mealName,
                    mealType    = mealType,
                    description = description,
                    calories    = calories,
                    mealTime    = mealTime
                )
            )
            _events.emit(MealCrudEvent.ShowSnackbar("✓ Meal added"))
        }
    }

    fun updateMeal(meal: MealEntity) {
        viewModelScope.launch {
            repository.updateMeal(meal)
            _events.emit(MealCrudEvent.ShowSnackbar("✓ Meal updated"))
        }
    }

    fun deleteMeal(meal: MealEntity) {
        viewModelScope.launch {
            repository.deleteMeal(meal)
            _events.emit(MealCrudEvent.ShowSnackbar("Meal deleted"))
        }
    }
}
