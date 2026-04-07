package com.example.tuf.domain.repository

import com.example.tuf.domain.model.Budget
import kotlinx.coroutines.flow.Flow

/**
 * Repository interface for budget data operations.
 */
interface BudgetRepository {

    fun getBudgetsByMonth(month: Int, year: Int): Flow<List<Budget>>

    fun getBudgetByCategoryAndMonth(categoryId: Long, month: Int, year: Int): Flow<Budget?>

    suspend fun insertBudget(budget: Budget): Long

    suspend fun updateBudget(budget: Budget)

    suspend fun deleteBudget(budget: Budget)

    suspend fun deleteBudgetById(id: Long)
}
