package com.example.tuf.domain.usecase

import com.example.tuf.core.utils.DateUtils
import com.example.tuf.domain.model.Transaction
import com.example.tuf.domain.model.TransactionType
import com.example.tuf.domain.repository.RecurringRepository
import com.example.tuf.domain.repository.TransactionRepository

/**
 * Processes all recurring transactions that are due before or on the current date.
 * For each due item, inserts a corresponding [Transaction] and advances the [nextDueDate].
 */
class ProcessDueRecurringUseCase(
    private val recurringRepository: RecurringRepository,
    private val transactionRepository: TransactionRepository
) {
    suspend operator fun invoke() {
        val now = DateUtils.now()
        val dueItems = recurringRepository.getRecurringDueBeforeList(now)

        dueItems.forEach { recurring ->
            // Insert the transaction for this due date
            val transaction = Transaction(
                amount = recurring.amount,
                type = recurring.type,
                category = recurring.category,
                note = recurring.note,
                date = recurring.nextDueDate,
                createdAt = now,
                isRecurring = true,
                recurringId = recurring.id
            )
            transactionRepository.insertTransaction(transaction)

            // Advance the next due date
            val nextDue = DateUtils.nextDueDate(recurring.nextDueDate, recurring.frequency.name)
            recurringRepository.updateRecurring(recurring.copy(nextDueDate = nextDue))
        }
    }
}
