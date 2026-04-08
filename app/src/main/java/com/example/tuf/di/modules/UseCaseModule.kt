package com.example.tuf.di.modules

import com.example.tuf.domain.usecase.*
import com.example.tuf.domain.usecase.split.*
import org.koin.dsl.module

/**
 * Koin module providing all use cases as factories (new instance per injection).
 */
val useCaseModule = module {
    // Transaction use cases
    factory { GetTransactionsUseCase(get()) }
    factory { GetTransactionsByMonthUseCase(get()) }
    factory { GetRecentTransactionsUseCase(get()) }
    factory { AddTransactionUseCase(get()) }
    factory { UpdateTransactionUseCase(get()) }
    factory { DeleteTransactionUseCase(get()) }
    factory { SearchTransactionsUseCase(get()) }

    // Summary
    factory { GetMonthlySummaryUseCase(get()) }

    // Category use cases
    factory { GetCategoriesUseCase(get()) }
    factory { AddCategoryUseCase(get()) }
    factory { UpdateCategoryUseCase(get()) }
    factory { DeleteCategoryUseCase(get()) }

    // Budget use cases
    factory { GetBudgetsUseCase(get()) }
    factory { AddBudgetUseCase(get()) }
    factory { UpdateBudgetUseCase(get()) }
    factory { DeleteBudgetUseCase(get()) }
    factory { GetBudgetProgressUseCase(get(), get()) }

    // Analytics use cases
    factory { GetCategoryWiseSpendingUseCase(get()) }
    factory { GetWeeklySpendingUseCase(get()) }
    factory { GetMonthlyTrendUseCase(get()) }

    // Recurring use cases
    factory { GetRecurringTransactionsUseCase(get()) }
    factory { AddRecurringTransactionUseCase(get()) }
    factory { UpdateRecurringUseCase(get()) }
    factory { DeleteRecurringUseCase(get()) }
    factory { ProcessDueRecurringUseCase(get(), get()) }

    // Split Group Use cases
    factory { GetGroupsUseCase(get()) }
    factory { AddGroupUseCase(get()) }
    factory { DeleteGroupUseCase(get()) }
    factory { GetGroupExpensesUseCase(get()) }
    factory { AddSplitExpenseUseCase(get()) }
    factory { DeleteSplitExpenseUseCase(get()) }

    // Export
    factory { ExportTransactionsUseCase(get()) }

    // Insights & Health Score
    factory { GetInsightsUseCase(get()) }
    factory { GetFinancialHealthScoreUseCase(get(), get()) }
}
