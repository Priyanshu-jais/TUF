package com.example.tuf.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Room entity representing a recurring transaction rule.
 *
 * @property id Auto-generated primary key.
 * @property amount Transaction amount.
 * @property type "INCOME" or "EXPENSE".
 * @property categoryId Foreign key referencing [CategoryEntity].
 * @property note Optional user note.
 * @property frequency "DAILY", "WEEKLY", "MONTHLY", or "YEARLY".
 * @property startDate Epoch millis of the first occurrence.
 * @property nextDueDate Epoch millis of the next scheduled occurrence.
 * @property isActive Whether this recurring rule is active.
 */
@Entity(tableName = "recurring_transactions")
data class RecurringTransactionEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val amount: Double,
    val type: String,
    val categoryId: Long,
    val note: String = "",
    val frequency: String,
    val startDate: Long,
    val nextDueDate: Long,
    val isActive: Boolean = true
)
