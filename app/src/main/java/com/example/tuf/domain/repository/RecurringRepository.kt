package com.example.tuf.domain.repository

import com.example.tuf.domain.model.RecurringTransaction
import kotlinx.coroutines.flow.Flow

/**
 * Repository interface for recurring transaction rule operations.
 */
interface RecurringRepository {

    fun getAllRecurring(): Flow<List<RecurringTransaction>>

    fun getActiveRecurring(): Flow<List<RecurringTransaction>>

    fun getRecurringDueBefore(date: Long): Flow<List<RecurringTransaction>>

    suspend fun getRecurringDueBeforeList(date: Long): List<RecurringTransaction>

    suspend fun insertRecurring(recurring: RecurringTransaction): Long

    suspend fun updateRecurring(recurring: RecurringTransaction)

    suspend fun deleteRecurring(recurring: RecurringTransaction)

    suspend fun deleteRecurringById(id: Long)
}
