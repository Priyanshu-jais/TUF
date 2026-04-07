package com.example.tuf.presentation.screens.dashboard

import androidx.lifecycle.viewModelScope
import com.example.tuf.core.base.BaseViewModel
import com.example.tuf.core.utils.DateUtils
import com.example.tuf.data.local.DataStoreManager
import com.example.tuf.domain.usecase.*
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.Calendar

/**
 * ViewModel for the Dashboard screen.
 * Manages monthly financial summary, recent transactions, budget progress, and insights.
 */
class DashboardViewModel(
    private val getMonthlySummaryUseCase: GetMonthlySummaryUseCase,
    private val getRecentTransactionsUseCase: GetRecentTransactionsUseCase,
    private val getBudgetProgressUseCase: GetBudgetProgressUseCase,
    private val getInsightsUseCase: GetInsightsUseCase,
    private val getFinancialHealthScoreUseCase: GetFinancialHealthScoreUseCase,
    private val dataStoreManager: DataStoreManager
) : BaseViewModel() {

    private val _uiState = MutableStateFlow(DashboardUiState())
    val uiState: StateFlow<DashboardUiState> = _uiState.asStateFlow()

    private val _events = Channel<DashboardEvent>(Channel.BUFFERED)
    val events = _events.receiveAsFlow()

    private val _selectedMonth = MutableStateFlow(
        Pair(DateUtils.currentMonth(), DateUtils.currentYear())
    )
    val selectedMonth: StateFlow<Pair<Int, Int>> = _selectedMonth.asStateFlow()

    init {
        observeSettings()
        observeMonthData()
        observeRecentTransactions()
    }

    private fun observeSettings() {
        safeLaunch {
            combine(
                dataStoreManager.currencySymbol,
                dataStoreManager.dailyLimit
            ) { symbol, limit -> Pair(symbol, limit) }.collect { (symbol, limit) ->
                _uiState.update { it.copy(currencySymbol = symbol, dailyLimit = limit) }
            }
        }
    }

    private fun observeMonthData() {
        safeLaunch {
            _selectedMonth.flatMapLatest { (month, year) ->
                combine(
                    getMonthlySummaryUseCase(month, year),
                    getBudgetProgressUseCase(month, year),
                    getInsightsUseCase(month, year),
                    getFinancialHealthScoreUseCase(month, year)
                ) { summary, budgetProgress, insights, healthScore ->
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            summary = summary,
                            budgetProgress = budgetProgress,
                            insights = insights,
                            healthScore = healthScore,
                            error = null
                        )
                    }
                }
            }.collect()
        }
    }

    private fun observeRecentTransactions() {
        safeLaunch {
            getRecentTransactionsUseCase(5).collect { transactions ->
                _uiState.update { it.copy(recentTransactions = transactions) }
            }
        }
    }

    fun onEvent(event: DashboardUiEvent) {
        when (event) {
            is DashboardUiEvent.MonthChanged -> {
                _selectedMonth.value = Pair(event.month, event.year)
                _uiState.update { it.copy(isLoading = true) }
            }
            is DashboardUiEvent.ToggleBalanceVisibility -> {
                _uiState.update { it.copy(isBalanceVisible = !it.isBalanceVisible) }
            }
            is DashboardUiEvent.AddTransactionClicked -> {
                safeLaunch {
                    _events.send(DashboardEvent.NavigateToAddTransaction(event.type))
                }
            }
            is DashboardUiEvent.SeeAllTransactionsClicked -> {
                safeLaunch { _events.send(DashboardEvent.NavigateToTransactions) }
            }
            is DashboardUiEvent.DismissError -> {
                _uiState.update { it.copy(error = null) }
            }
        }
    }

    override fun onError(throwable: Throwable) {
        _uiState.update {
            it.copy(isLoading = false, error = throwable.message ?: "An error occurred")
        }
    }

    /** Navigate to previous month. */
    fun previousMonth() {
        val (month, year) = _selectedMonth.value
        val cal = Calendar.getInstance()
        cal.set(Calendar.MONTH, month - 1)
        cal.set(Calendar.YEAR, year)
        cal.add(Calendar.MONTH, -1)
        _selectedMonth.value = Pair(cal.get(Calendar.MONTH) + 1, cal.get(Calendar.YEAR))
        _uiState.update { it.copy(isLoading = true) }
    }

    /** Navigate to next month. */
    fun nextMonth() {
        val (month, year) = _selectedMonth.value
        val cal = Calendar.getInstance()
        cal.set(Calendar.MONTH, month - 1)
        cal.set(Calendar.YEAR, year)
        cal.add(Calendar.MONTH, 1)
        _selectedMonth.value = Pair(cal.get(Calendar.MONTH) + 1, cal.get(Calendar.YEAR))
        _uiState.update { it.copy(isLoading = true) }
    }
}
