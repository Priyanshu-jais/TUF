package com.example.tuf.domain.model

/**
 * Domain model for a monthly budget limit for a specific category.
 *
 * @property id Unique identifier.
 * @property category The category this budget applies to.
 * @property limitAmount The maximum spending allowed.
 * @property month Month (1-12).
 * @property year Full year (e.g. 2025).
 */
data class Budget(
    val id: Long = 0,
    val category: Category,
    val limitAmount: Double,
    val month: Int,
    val year: Int
)

/**
 * Represents the spending progress against a budget for a category.
 *
 * @property budget The budget this progress is for.
 * @property spent The amount spent so far in this category.
 * @property percentage Spending as a fraction of limit (0.0–1.0+).
 * @property isOverBudget Whether spending has exceeded the limit.
 */
data class BudgetProgress(
    val budget: Budget,
    val spent: Double,
    val percentage: Float,
    val isOverBudget: Boolean
)
