package com.example.tuf.domain.model

/**
 * Domain model for a recurring transaction rule.
 *
 * @property id Unique identifier.
 * @property amount Transaction amount.
 * @property type [TransactionType.INCOME] or [TransactionType.EXPENSE].
 * @property category Associated category.
 * @property note Optional user note.
 * @property frequency Recurrence frequency.
 * @property startDate Epoch millis of the first occurrence.
 * @property nextDueDate Epoch millis of the next scheduled occurrence.
 * @property isActive Whether this rule is currently active.
 */
data class RecurringTransaction(
    val id: Long = 0,
    val amount: Double,
    val type: TransactionType,
    val category: Category,
    val note: String = "",
    val frequency: RecurringFrequency,
    val startDate: Long,
    val nextDueDate: Long,
    val isActive: Boolean = true
)

/** Enum for recurring transaction frequency. */
enum class RecurringFrequency {
    DAILY,
    WEEKLY,
    MONTHLY,
    YEARLY;

    val displayName: String get() = name.lowercase().replaceFirstChar { it.uppercase() }

    companion object {
        fun fromString(value: String): RecurringFrequency =
            entries.firstOrNull { it.name == value } ?: MONTHLY
    }
}
