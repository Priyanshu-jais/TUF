package com.example.tuf.domain.repository

import com.example.tuf.domain.model.Transaction
import com.example.tuf.domain.model.TransactionType
import kotlinx.coroutines.flow.Flow

/**
 * Repository interface for transaction data operations.
 * All implementations must reside in the data layer.
 */
interface TransactionRepository {

    fun getAllTransactions(): Flow<List<Transaction>>

    fun getTransactionsByMonth(month: Int, year: Int): Flow<List<Transaction>>

    fun getTransactionsByCategory(categoryId: Long): Flow<List<Transaction>>

    fun getTransactionsByDateRange(start: Long, end: Long): Flow<List<Transaction>>

    fun getRecentTransactions(limit: Int): Flow<List<Transaction>>

    fun getTotalByTypeAndMonth(type: TransactionType, month: Int, year: Int): Flow<Double?>

    fun getTodayTotalExpense(startOfDay: Long, endOfDay: Long): Flow<Double?>

    fun searchTransactions(query: String): Flow<List<Transaction>>

    suspend fun getTransactionById(id: Long): Transaction?

    suspend fun insertTransaction(transaction: Transaction): Long

    suspend fun updateTransaction(transaction: Transaction)

    suspend fun deleteTransaction(transaction: Transaction)

    suspend fun deleteTransactionById(id: Long)
}
