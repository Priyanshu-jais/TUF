package com.example.tuf.data.repository

import com.example.tuf.data.local.dao.CategoryDao
import com.example.tuf.data.local.dao.RecurringDao
import com.example.tuf.data.local.mapper.toDomain
import com.example.tuf.data.local.mapper.toEntity
import com.example.tuf.domain.model.RecurringTransaction
import com.example.tuf.domain.repository.RecurringRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine

/**
 * Concrete implementation of [RecurringRepository] using Room [RecurringDao] and [CategoryDao].
 */
class RecurringRepositoryImpl(
    private val recurringDao: RecurringDao,
    private val categoryDao: CategoryDao
) : RecurringRepository {

    override fun getAllRecurring(): Flow<List<RecurringTransaction>> {
        return combine(recurringDao.getAll(), categoryDao.getAll()) { list, categories ->
            val categoryMap = categories.associate { it.id to it.toDomain() }
            list.mapNotNull { entity ->
                categoryMap[entity.categoryId]?.let { entity.toDomain(it) }
            }
        }
    }

    override fun getActiveRecurring(): Flow<List<RecurringTransaction>> {
        return combine(recurringDao.getActive(), categoryDao.getAll()) { list, categories ->
            val categoryMap = categories.associate { it.id to it.toDomain() }
            list.mapNotNull { entity ->
                categoryMap[entity.categoryId]?.let { entity.toDomain(it) }
            }
        }
    }

    override fun getRecurringDueBefore(date: Long): Flow<List<RecurringTransaction>> {
        return combine(recurringDao.getDueBefore(date), categoryDao.getAll()) { list, categories ->
            val categoryMap = categories.associate { it.id to it.toDomain() }
            list.mapNotNull { entity ->
                categoryMap[entity.categoryId]?.let { entity.toDomain(it) }
            }
        }
    }

    override suspend fun getRecurringDueBeforeList(date: Long): List<RecurringTransaction> {
        val categories = categoryDao.getAll()
        val dueList = recurringDao.getDueBeforeList(date)
        // Resolve categories synchronously
        return dueList.mapNotNull { entity ->
            categoryDao.getById(entity.categoryId)?.toDomain()?.let { entity.toDomain(it) }
        }
    }

    override suspend fun insertRecurring(recurring: RecurringTransaction): Long {
        return recurringDao.insert(recurring.toEntity())
    }

    override suspend fun updateRecurring(recurring: RecurringTransaction) {
        recurringDao.update(recurring.toEntity())
    }

    override suspend fun deleteRecurring(recurring: RecurringTransaction) {
        recurringDao.delete(recurring.toEntity())
    }

    override suspend fun deleteRecurringById(id: Long) {
        recurringDao.deleteById(id)
    }
}
