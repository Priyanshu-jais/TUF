package com.example.tuf.domain.model

/**
 * Domain model representing a financial transaction.
 *
 * @property id Unique identifier.
 * @property amount Transaction amount (always positive).
 * @property type [TransactionType.INCOME] or [TransactionType.EXPENSE].
 * @property category The associated category.
 * @property note User note, may contain #tags.
 * @property date Epoch millis of the transaction date.
 * @property createdAt Epoch millis of record creation.
 * @property isRecurring Whether generated from a recurring rule.
 * @property recurringId ID of the parent recurring rule if applicable.
 * @property imageUri URI to a locally stored receipt photo.
 * @property tags Parsed #tags from the note field.
 */
data class Transaction(
    val id: Long = 0,
    val amount: Double,
    val type: TransactionType,
    val category: Category,
    val note: String = "",
    val date: Long,
    val createdAt: Long,
    val isRecurring: Boolean = false,
    val recurringId: Long? = null,
    val imageUri: String? = null,
    val tags: List<String> = emptyList()
)

/** Enum representing whether a transaction is income or expense. */
enum class TransactionType {
    INCOME,
    EXPENSE;

    companion object {
        fun fromString(value: String): TransactionType =
            entries.firstOrNull { it.name == value } ?: EXPENSE
    }
}
