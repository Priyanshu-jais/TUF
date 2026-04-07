package com.example.tuf.di.modules

import com.example.tuf.presentation.screens.analytics.AnalyticsViewModel
import com.example.tuf.presentation.screens.budget.BudgetViewModel
import com.example.tuf.presentation.screens.categories.CategoryViewModel
import com.example.tuf.presentation.screens.dashboard.DashboardViewModel
import com.example.tuf.presentation.screens.onboarding.OnboardingViewModel
import com.example.tuf.presentation.screens.recurring.RecurringViewModel
import com.example.tuf.presentation.screens.search.SearchViewModel
import com.example.tuf.presentation.screens.settings.SettingsViewModel
import com.example.tuf.presentation.screens.transactions.TransactionViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

/**
 * Koin module providing all ViewModels.
 */
val viewModelModule = module {
    viewModel {
        DashboardViewModel(
            getMonthlySummaryUseCase = get(),
            getRecentTransactionsUseCase = get(),
            getBudgetProgressUseCase = get(),
            getInsightsUseCase = get(),
            getFinancialHealthScoreUseCase = get(),
            dataStoreManager = get()
        )
    }
    viewModel {
        TransactionViewModel(
            getTransactionsByMonthUseCase = get(),
            addTransactionUseCase = get(),
            updateTransactionUseCase = get(),
            deleteTransactionUseCase = get(),
            getCategoriesUseCase = get(),
            searchTransactionsUseCase = get(),
            dataStoreManager = get()
        )
    }
    viewModel {
        AnalyticsViewModel(
            getMonthlySummaryUseCase = get(),
            getCategoryWiseSpendingUseCase = get(),
            getWeeklySpendingUseCase = get(),
            getMonthlyTrendUseCase = get()
        )
    }
    viewModel {
        BudgetViewModel(
            getBudgetsUseCase = get(),
            getBudgetProgressUseCase = get(),
            addBudgetUseCase = get(),
            updateBudgetUseCase = get(),
            deleteBudgetUseCase = get(),
            getCategoriesUseCase = get(),
            dataStoreManager = get()
        )
    }
    viewModel {
        RecurringViewModel(
            getRecurringTransactionsUseCase = get(),
            addRecurringTransactionUseCase = get(),
            updateRecurringUseCase = get(),
            deleteRecurringUseCase = get(),
            getCategoriesUseCase = get()
        )
    }
    viewModel {
        SettingsViewModel(dataStoreManager = get(), exportTransactionsUseCase = get())
    }
    viewModel {
        SearchViewModel(searchTransactionsUseCase = get(), getCategoriesUseCase = get())
    }
    viewModel {
        CategoryViewModel(
            getCategoriesUseCase = get(),
            addCategoryUseCase = get(),
            updateCategoryUseCase = get(),
            deleteCategoryUseCase = get()
        )
    }
    viewModel { OnboardingViewModel(dataStoreManager = get()) }
}
