package com.example.tuf.data.local.dao

import androidx.room.*
import com.example.tuf.data.local.entity.RecurringTransactionEntity
import kotlinx.coroutines.flow.Flow

/**
 * Data Access Object for [RecurringTransactionEntity] operations.
 */
@Dao
interface RecurringDao {

    /** Observes all recurring transactions ordered by next due date. */
    @Query("SELECT * FROM recurring_transactions ORDER BY nextDueDate ASC")
    fun getAll(): Flow<List<RecurringTransactionEntity>>

    /** Observes all active recurring transactions. */
    @Query("SELECT * FROM recurring_transactions WHERE isActive = 1 ORDER BY nextDueDate ASC")
    fun getActive(): Flow<List<RecurringTransactionEntity>>

    /** Observes recurring transactions whose next due date is before or on [date]. */
    @Query("SELECT * FROM recurring_transactions WHERE nextDueDate <= :date AND isActive = 1")
    fun getDueBefore(date: Long): Flow<List<RecurringTransactionEntity>>

    /** Returns all overdue recurring transactions as a one-shot list. */
    @Query("SELECT * FROM recurring_transactions WHERE nextDueDate <= :date AND isActive = 1")
    suspend fun getDueBeforeList(date: Long): List<RecurringTransactionEntity>

    /** Returns a single recurring transaction by its ID. */
    @Query("SELECT * FROM recurring_transactions WHERE id = :id")
    suspend fun getById(id: Long): RecurringTransactionEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(recurring: RecurringTransactionEntity): Long

    @Update
    suspend fun update(recurring: RecurringTransactionEntity)

    @Delete
    suspend fun delete(recurring: RecurringTransactionEntity)

    @Query("DELETE FROM recurring_transactions WHERE id = :id")
    suspend fun deleteById(id: Long)
}
