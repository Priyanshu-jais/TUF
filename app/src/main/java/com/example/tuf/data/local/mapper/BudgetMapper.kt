package com.example.tuf.data.local.mapper

import com.example.tuf.data.local.entity.BudgetEntity
import com.example.tuf.domain.model.Budget
import com.example.tuf.domain.model.Category

/**
 * Extension functions to map between [BudgetEntity] and [Budget] domain model.
 */

fun BudgetEntity.toDomain(category: Category): Budget = Budget(
    id = id,
    category = category,
    limitAmount = limitAmount,
    month = month,
    year = year
)

fun Budget.toEntity(): BudgetEntity = BudgetEntity(
    id = id,
    categoryId = category.id,
    limitAmount = limitAmount,
    month = month,
    year = year
)
