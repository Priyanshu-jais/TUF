package com.example.tuf.data.repository

import com.example.tuf.data.local.dao.CategoryDao
import com.example.tuf.data.local.dao.TransactionDao
import com.example.tuf.data.local.mapper.toDomain
import com.example.tuf.data.local.mapper.toEntity
import com.example.tuf.domain.model.Transaction
import com.example.tuf.domain.model.TransactionType
import com.example.tuf.domain.repository.TransactionRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map

/**
 * Concrete implementation of [TransactionRepository] using Room DAOs.
 * Resolves category entities and maps to domain models before returning.
 */
class TransactionRepositoryImpl(
    private val transactionDao: TransactionDao,
    private val categoryDao: CategoryDao
) : TransactionRepository {

    override fun getAllTransactions(): Flow<List<Transaction>> {
        return combine(transactionDao.getAll(), categoryDao.getAll()) { transactions, categories ->
            val categoryMap = categories.associate { it.id to it.toDomain() }
            transactions.mapNotNull { entity ->
                categoryMap[entity.categoryId]?.let { entity.toDomain(it) }
            }
        }
    }

    override fun getTransactionsByMonth(month: Int, year: Int): Flow<List<Transaction>> {
        return combine(transactionDao.getByMonth(month, year), categoryDao.getAll()) { transactions, categories ->
            val categoryMap = categories.associate { it.id to it.toDomain() }
            transactions.mapNotNull { entity ->
                categoryMap[entity.categoryId]?.let { entity.toDomain(it) }
            }
        }
    }

    override fun getTransactionsByCategory(categoryId: Long): Flow<List<Transaction>> {
        return combine(transactionDao.getByCategory(categoryId), categoryDao.getAll()) { transactions, categories ->
            val categoryMap = categories.associate { it.id to it.toDomain() }
            transactions.mapNotNull { entity ->
                categoryMap[entity.categoryId]?.let { entity.toDomain(it) }
            }
        }
    }

    override fun getTransactionsByDateRange(start: Long, end: Long): Flow<List<Transaction>> {
        return combine(transactionDao.getByDateRange(start, end), categoryDao.getAll()) { transactions, categories ->
            val categoryMap = categories.associate { it.id to it.toDomain() }
            transactions.mapNotNull { entity ->
                categoryMap[entity.categoryId]?.let { entity.toDomain(it) }
            }
        }
    }

    override fun getRecentTransactions(limit: Int): Flow<List<Transaction>> {
        return combine(transactionDao.getRecent(limit), categoryDao.getAll()) { transactions, categories ->
            val categoryMap = categories.associate { it.id to it.toDomain() }
            transactions.mapNotNull { entity ->
                categoryMap[entity.categoryId]?.let { entity.toDomain(it) }
            }
        }
    }

    override fun getTotalByTypeAndMonth(type: TransactionType, month: Int, year: Int): Flow<Double?> {
        return transactionDao.getTotalByTypeAndMonth(type.name, month, year)
    }

    override fun getTodayTotalExpense(startOfDay: Long, endOfDay: Long): Flow<Double?> {
        return transactionDao.getTodayTotalExpense(startOfDay, endOfDay)
    }

    override fun searchTransactions(query: String): Flow<List<Transaction>> {
        return combine(transactionDao.search(query), categoryDao.getAll()) { transactions, categories ->
            val categoryMap = categories.associate { it.id to it.toDomain() }
            transactions.mapNotNull { entity ->
                categoryMap[entity.categoryId]?.let { entity.toDomain(it) }
            }
        }
    }

    override suspend fun getTransactionById(id: Long): Transaction? {
        val entity = transactionDao.getById(id) ?: return null
        val categoryEntity = categoryDao.getById(entity.categoryId) ?: return null
        return entity.toDomain(categoryEntity.toDomain())
    }

    override suspend fun insertTransaction(transaction: Transaction): Long {
        return transactionDao.insert(transaction.toEntity())
    }

    override suspend fun updateTransaction(transaction: Transaction) {
        transactionDao.update(transaction.toEntity())
    }

    override suspend fun deleteTransaction(transaction: Transaction) {
        transactionDao.delete(transaction.toEntity())
    }

    override suspend fun deleteTransactionById(id: Long) {
        transactionDao.deleteById(id)
    }
}
