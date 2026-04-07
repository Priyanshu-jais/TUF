package com.example.tuf.domain.usecase

import com.example.tuf.domain.model.Transaction
import com.example.tuf.domain.repository.TransactionRepository
import kotlinx.coroutines.flow.Flow

class GetTransactionsUseCase(private val repository: TransactionRepository) {
    operator fun invoke(): Flow<List<Transaction>> = repository.getAllTransactions()
}

class GetTransactionsByMonthUseCase(private val repository: TransactionRepository) {
    operator fun invoke(month: Int, year: Int): Flow<List<Transaction>> =
        repository.getTransactionsByMonth(month, year)
}

class GetRecentTransactionsUseCase(private val repository: TransactionRepository) {
    operator fun invoke(limit: Int = 5): Flow<List<Transaction>> =
        repository.getRecentTransactions(limit)
}

class AddTransactionUseCase(private val repository: TransactionRepository) {
    suspend operator fun invoke(transaction: Transaction): Long =
        repository.insertTransaction(transaction)
}

class UpdateTransactionUseCase(private val repository: TransactionRepository) {
    suspend operator fun invoke(transaction: Transaction) =
        repository.updateTransaction(transaction)
}

class DeleteTransactionUseCase(private val repository: TransactionRepository) {
    suspend operator fun invoke(transaction: Transaction) =
        repository.deleteTransaction(transaction)

    suspend fun byId(id: Long) = repository.deleteTransactionById(id)
}

class SearchTransactionsUseCase(private val repository: TransactionRepository) {
    operator fun invoke(query: String): Flow<List<Transaction>> =
        repository.searchTransactions(query)
}
