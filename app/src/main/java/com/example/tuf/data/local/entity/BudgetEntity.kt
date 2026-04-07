package com.example.tuf.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Room entity representing a monthly budget for a specific category.
 *
 * @property id Auto-generated primary key.
 * @property categoryId Foreign key referencing [CategoryEntity].
 * @property limitAmount The spending limit for this budget.
 * @property month Month (1-12).
 * @property year Full year (e.g. 2025).
 */
@Entity(tableName = "budgets")
data class BudgetEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val categoryId: Long,
    val limitAmount: Double,
    val month: Int,
    val year: Int
)
