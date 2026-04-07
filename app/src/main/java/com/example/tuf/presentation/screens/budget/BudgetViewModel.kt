package com.example.tuf.presentation.screens.budget

import com.example.tuf.core.base.BaseViewModel
import com.example.tuf.core.utils.DateUtils
import com.example.tuf.data.local.DataStoreManager
import com.example.tuf.domain.model.Budget
import com.example.tuf.domain.model.BudgetProgress
import com.example.tuf.domain.model.Category
import com.example.tuf.domain.model.CategoryType
import com.example.tuf.domain.usecase.*
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import java.util.Calendar

data class BudgetUiState(
    val isLoading: Boolean = true,
    val budgetProgress: List<BudgetProgress> = emptyList(),
    val expenseCategories: List<Category> = emptyList(),
    val selectedMonth: Int = DateUtils.currentMonth(),
    val selectedYear: Int = DateUtils.currentYear(),
    val currencySymbol: String = "₹",
    val error: String? = null
)

sealed class BudgetEvent {
    data class ShowSnackbar(val message: String) : BudgetEvent()
}

class BudgetViewModel(
    private val getBudgetsUseCase: GetBudgetsUseCase,
    private val getBudgetProgressUseCase: GetBudgetProgressUseCase,
    private val addBudgetUseCase: AddBudgetUseCase,
    private val updateBudgetUseCase: UpdateBudgetUseCase,
    private val deleteBudgetUseCase: DeleteBudgetUseCase,
    private val getCategoriesUseCase: GetCategoriesUseCase,
    private val dataStoreManager: DataStoreManager
) : BaseViewModel() {

    private val _uiState = MutableStateFlow(BudgetUiState())
    val uiState: StateFlow<BudgetUiState> = _uiState.asStateFlow()

    private val _events = Channel<BudgetEvent>(Channel.BUFFERED)
    val events = _events.receiveAsFlow()

    init {
        observeCategories()
        observeBudgetProgress()
        observeCurrency()
    }

    private fun observeCurrency() {
        safeLaunch {
            dataStoreManager.currencySymbol.collect { symbol ->
                _uiState.update { it.copy(currencySymbol = symbol) }
            }
        }
    }

    private fun observeCategories() {
        safeLaunch {
            getCategoriesUseCase.byType(CategoryType.EXPENSE).collect { categories ->
                _uiState.update { it.copy(expenseCategories = categories) }
            }
        }
    }

    private fun observeBudgetProgress() {
        safeLaunch {
            _uiState.map { Pair(it.selectedMonth, it.selectedYear) }
                .distinctUntilChanged()
                .flatMapLatest { (month, year) ->
                    getBudgetProgressUseCase(month, year)
                }
                .collect { progress ->
                    _uiState.update { it.copy(isLoading = false, budgetProgress = progress) }
                }
        }
    }

    fun addOrUpdateBudget(categoryId: Long, limit: Double) {
        val (month, year) = _uiState.value.let { Pair(it.selectedMonth, it.selectedYear) }
        val category = _uiState.value.expenseCategories.find { it.id == categoryId } ?: return
        safeLaunch {
            val existing = _uiState.value.budgetProgress.find { it.budget.category.id == categoryId }
            if (existing != null) {
                updateBudgetUseCase(existing.budget.copy(limitAmount = limit))
            } else {
                addBudgetUseCase(Budget(id = 0, category = category, limitAmount = limit, month = month, year = year))
            }
            _events.send(BudgetEvent.ShowSnackbar("Budget saved!"))
        }
    }

    fun deleteBudget(budget: Budget) {
        safeLaunch {
            deleteBudgetUseCase(budget)
            _events.send(BudgetEvent.ShowSnackbar("Budget removed"))
        }
    }

    fun changeMonth(month: Int, year: Int) {
        _uiState.update { it.copy(selectedMonth = month, selectedYear = year, isLoading = true) }
    }

    fun previousMonth() {
        val cal = Calendar.getInstance()
        cal.set(Calendar.MONTH, _uiState.value.selectedMonth - 1)
        cal.set(Calendar.YEAR, _uiState.value.selectedYear)
        cal.add(Calendar.MONTH, -1)
        changeMonth(cal.get(Calendar.MONTH) + 1, cal.get(Calendar.YEAR))
    }

    fun nextMonth() {
        val cal = Calendar.getInstance()
        cal.set(Calendar.MONTH, _uiState.value.selectedMonth - 1)
        cal.set(Calendar.YEAR, _uiState.value.selectedYear)
        cal.add(Calendar.MONTH, 1)
        changeMonth(cal.get(Calendar.MONTH) + 1, cal.get(Calendar.YEAR))
    }

    override fun onError(throwable: Throwable) {
        _uiState.update { it.copy(isLoading = false, error = throwable.message) }
    }
}
