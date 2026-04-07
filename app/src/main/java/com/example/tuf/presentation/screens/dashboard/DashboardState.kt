package com.example.tuf.presentation.screens.dashboard

import com.example.tuf.domain.model.BudgetProgress
import com.example.tuf.domain.model.FinancialHealthScore
import com.example.tuf.domain.model.InsightModel
import com.example.tuf.domain.model.MonthlySummary
import com.example.tuf.domain.model.Transaction

/** UI state for the Dashboard screen. */
data class DashboardUiState(
    val isLoading: Boolean = true,
    val summary: MonthlySummary? = null,
    val recentTransactions: List<Transaction> = emptyList(),
    val budgetProgress: List<BudgetProgress> = emptyList(),
    val insights: List<InsightModel> = emptyList(),
    val healthScore: FinancialHealthScore? = null,
    val isBalanceVisible: Boolean = true,
    val todayExpense: Double = 0.0,
    val dailyLimit: Double = 0.0,
    val currencySymbol: String = "₹",
    val error: String? = null
)

/** One-time events emitted from DashboardViewModel. */
sealed class DashboardEvent {
    data class ShowSnackbar(val message: String) : DashboardEvent()
    data class NavigateToAddTransaction(val type: String) : DashboardEvent()
    object NavigateToTransactions : DashboardEvent()
    object NavigateToAnalytics : DashboardEvent()
    object NavigateToBudget : DashboardEvent()
}

/** User actions that trigger state changes in DashboardViewModel. */
sealed class DashboardUiEvent {
    data class MonthChanged(val month: Int, val year: Int) : DashboardUiEvent()
    object ToggleBalanceVisibility : DashboardUiEvent()
    data class AddTransactionClicked(val type: String) : DashboardUiEvent()
    object SeeAllTransactionsClicked : DashboardUiEvent()
    object DismissError : DashboardUiEvent()
}
