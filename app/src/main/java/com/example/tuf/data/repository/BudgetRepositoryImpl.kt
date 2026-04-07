package com.example.tuf.data.repository

import com.example.tuf.data.local.dao.BudgetDao
import com.example.tuf.data.local.dao.CategoryDao
import com.example.tuf.data.local.mapper.toDomain
import com.example.tuf.data.local.mapper.toEntity
import com.example.tuf.domain.model.Budget
import com.example.tuf.domain.repository.BudgetRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map

/**
 * Concrete implementation of [BudgetRepository] using Room [BudgetDao] and [CategoryDao].
 */
class BudgetRepositoryImpl(
    private val budgetDao: BudgetDao,
    private val categoryDao: CategoryDao
) : BudgetRepository {

    override fun getBudgetsByMonth(month: Int, year: Int): Flow<List<Budget>> {
        return combine(budgetDao.getByMonth(month, year), categoryDao.getAll()) { budgets, categories ->
            val categoryMap = categories.associate { it.id to it.toDomain() }
            budgets.mapNotNull { entity ->
                categoryMap[entity.categoryId]?.let { entity.toDomain(it) }
            }
        }
    }

    override fun getBudgetByCategoryAndMonth(categoryId: Long, month: Int, year: Int): Flow<Budget?> {
        return combine(
            budgetDao.getByCategoryAndMonth(categoryId, month, year),
            categoryDao.getAll()
        ) { budget, categories ->
            val categoryMap = categories.associate { it.id to it.toDomain() }
            budget?.let { entity ->
                categoryMap[entity.categoryId]?.let { entity.toDomain(it) }
            }
        }
    }

    override suspend fun insertBudget(budget: Budget): Long {
        return budgetDao.insert(budget.toEntity())
    }

    override suspend fun updateBudget(budget: Budget) {
        budgetDao.update(budget.toEntity())
    }

    override suspend fun deleteBudget(budget: Budget) {
        budgetDao.delete(budget.toEntity())
    }

    override suspend fun deleteBudgetById(id: Long) {
        budgetDao.deleteById(id)
    }
}
