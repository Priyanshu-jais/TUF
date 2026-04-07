package com.example.tuf.domain.usecase

import com.example.tuf.core.utils.Constants
import com.example.tuf.core.utils.DateUtils
import com.example.tuf.domain.model.MonthlySummary
import com.example.tuf.domain.model.TransactionType
import com.example.tuf.domain.repository.TransactionRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine

/**
 * Returns the complete monthly financial summary including income, expense, balance, and savings rate.
 */
class GetMonthlySummaryUseCase(private val repository: TransactionRepository) {

    operator fun invoke(month: Int, year: Int): Flow<MonthlySummary> {
        val incomeFlow = repository.getTotalByTypeAndMonth(TransactionType.INCOME, month, year)
        val expenseFlow = repository.getTotalByTypeAndMonth(TransactionType.EXPENSE, month, year)

        return combine(incomeFlow, expenseFlow) { income, expense ->
            MonthlySummary(
                month = month,
                year = year,
                totalIncome = income ?: 0.0,
                totalExpense = expense ?: 0.0
            )
        }
    }
}
