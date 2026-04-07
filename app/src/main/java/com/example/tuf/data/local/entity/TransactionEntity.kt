package com.example.tuf.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Room entity representing a single financial transaction.
 *
 * @property id Auto-generated primary key.
 * @property amount The transaction amount (always positive).
 * @property type "INCOME" or "EXPENSE".
 * @property categoryId Foreign key referencing [CategoryEntity].
 * @property note Optional user note; may contain #tags.
 * @property date Epoch millis of the transaction date.
 * @property createdAt Epoch millis when the record was inserted.
 * @property isRecurring Whether this transaction was auto-created by a recurring rule.
 * @property recurringId Optional reference to [RecurringTransactionEntity].
 * @property imageUri Optional URI to a receipt photo stored locally.
 */
@Entity(tableName = "transactions")
data class TransactionEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val amount: Double,
    val type: String,
    val categoryId: Long,
    val note: String = "",
    val date: Long,
    val createdAt: Long,
    val isRecurring: Boolean = false,
    val recurringId: Long? = null,
    val imageUri: String? = null
)
