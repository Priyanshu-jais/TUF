package com.example.tuf.domain.usecase

import com.example.tuf.domain.model.Budget
import com.example.tuf.domain.model.BudgetProgress
import com.example.tuf.domain.model.TransactionType
import com.example.tuf.domain.repository.BudgetRepository
import com.example.tuf.domain.repository.TransactionRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine

class GetBudgetsUseCase(private val repository: BudgetRepository) {
    operator fun invoke(month: Int, year: Int): Flow<List<Budget>> =
        repository.getBudgetsByMonth(month, year)
}

class AddBudgetUseCase(private val repository: BudgetRepository) {
    suspend operator fun invoke(budget: Budget): Long = repository.insertBudget(budget)
}

class UpdateBudgetUseCase(private val repository: BudgetRepository) {
    suspend operator fun invoke(budget: Budget) = repository.updateBudget(budget)
}

class DeleteBudgetUseCase(private val repository: BudgetRepository) {
    suspend operator fun invoke(budget: Budget) = repository.deleteBudget(budget)
}

/**
 * Computes [BudgetProgress] for each budget by joining with actual transaction spending.
 */
class GetBudgetProgressUseCase(
    private val budgetRepository: BudgetRepository,
    private val transactionRepository: TransactionRepository
) {
    operator fun invoke(month: Int, year: Int): Flow<List<BudgetProgress>> {
        val budgetsFlow = budgetRepository.getBudgetsByMonth(month, year)
        val transactionsFlow = transactionRepository.getTransactionsByMonth(month, year)

        return combine(budgetsFlow, transactionsFlow) { budgets, transactions ->
            val expenseTransactions = transactions.filter { it.type == TransactionType.EXPENSE }

            budgets.map { budget ->
                val spent = expenseTransactions
                    .filter { it.category.id == budget.category.id }
                    .sumOf { it.amount }

                val percentage = if (budget.limitAmount > 0) {
                    (spent / budget.limitAmount).toFloat()
                } else 0f

                BudgetProgress(
                    budget = budget,
                    spent = spent,
                    percentage = percentage,
                    isOverBudget = spent > budget.limitAmount
                )
            }
        }
    }
}
