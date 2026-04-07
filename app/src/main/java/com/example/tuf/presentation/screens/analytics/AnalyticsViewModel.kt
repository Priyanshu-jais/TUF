package com.example.tuf.presentation.screens.analytics

import com.example.tuf.core.base.BaseViewModel
import com.example.tuf.core.utils.DateUtils
import com.example.tuf.domain.model.CategorySpending
import com.example.tuf.domain.model.DailySpending
import com.example.tuf.domain.model.MonthlyTrend
import com.example.tuf.domain.model.MonthlySummary
import com.example.tuf.domain.usecase.GetCategoryWiseSpendingUseCase
import com.example.tuf.domain.usecase.GetMonthlyTrendUseCase
import com.example.tuf.domain.usecase.GetMonthlySummaryUseCase
import com.example.tuf.domain.usecase.GetWeeklySpendingUseCase
import kotlinx.coroutines.flow.*
import java.util.Calendar

data class AnalyticsUiState(
    val isLoading: Boolean = true,
    val summary: MonthlySummary? = null,
    val categorySpending: List<CategorySpending> = emptyList(),
    val weeklySpending: List<DailySpending> = emptyList(),
    val monthlyTrend: List<MonthlyTrend> = emptyList(),
    val selectedPeriod: AnalyticsPeriod = AnalyticsPeriod.MONTH,
    val selectedCategoryIndex: Int = -1,
    val error: String? = null
)

enum class AnalyticsPeriod(val label: String) {
    WEEK("Week"), MONTH("Month"), THREE_MONTHS("3M"),
    SIX_MONTHS("6M"), YEAR("Year")
}

class AnalyticsViewModel(
    private val getMonthlySummaryUseCase: GetMonthlySummaryUseCase,
    private val getCategoryWiseSpendingUseCase: GetCategoryWiseSpendingUseCase,
    private val getWeeklySpendingUseCase: GetWeeklySpendingUseCase,
    private val getMonthlyTrendUseCase: GetMonthlyTrendUseCase
) : BaseViewModel() {

    private val _uiState = MutableStateFlow(AnalyticsUiState())
    val uiState: StateFlow<AnalyticsUiState> = _uiState.asStateFlow()

    init {
        val month = DateUtils.currentMonth()
        val year = DateUtils.currentYear()
        loadData(month, year)
    }

    private fun loadData(month: Int, year: Int) {
        safeLaunch {
            combine(
                getMonthlySummaryUseCase(month, year),
                getCategoryWiseSpendingUseCase(month, year),
                getWeeklySpendingUseCase(),
                getMonthlyTrendUseCase()
            ) { summary, categorySpending, weeklySpending, trend ->
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        summary = summary,
                        categorySpending = categorySpending,
                        weeklySpending = weeklySpending,
                        monthlyTrend = trend
                    )
                }
            }.collect()
        }
    }

    fun selectCategoryIndex(index: Int) {
        _uiState.update { it.copy(selectedCategoryIndex = index) }
    }

    fun selectPeriod(period: AnalyticsPeriod) {
        _uiState.update { it.copy(selectedPeriod = period) }
    }

    override fun onError(throwable: Throwable) {
        _uiState.update { it.copy(isLoading = false, error = throwable.message) }
    }
}
