package com.example.tuf.domain.usecase

import com.example.tuf.core.extensions.toFormattedDate
import com.example.tuf.domain.model.Transaction
import com.example.tuf.domain.model.TransactionType
import com.example.tuf.domain.repository.TransactionRepository
import kotlinx.coroutines.flow.first

/**
 * Generates a CSV string from all transactions in the repository.
 * Format: Date, Type, Category, Amount, Note
 */
class ExportTransactionsUseCase(private val repository: TransactionRepository) {

    suspend operator fun invoke(): String {
        val transactions = repository.getAllTransactions().first()
        return buildCsvString(transactions)
    }

    suspend fun invokeForDateRange(start: Long, end: Long): String {
        val transactions = repository.getTransactionsByDateRange(start, end).first()
        return buildCsvString(transactions)
    }

    private fun buildCsvString(transactions: List<Transaction>): String {
        val header = "Date,Type,Category,Amount,Note\n"
        val rows = transactions.joinToString("\n") { tx ->
            "${tx.date.toFormattedDate()}," +
                    "${tx.type.name}," +
                    "\"${tx.category.name}\"," +
                    "${tx.amount}," +
                    "\"${tx.note.replace("\"", "\"\"")}\""
        }
        return header + rows
    }
}
