package com.example.tuf.data.local.dao

import androidx.room.*
import com.example.tuf.data.local.entity.TransactionEntity
import kotlinx.coroutines.flow.Flow

/**
 * Data Access Object for [TransactionEntity] operations.
 */
@Dao
interface TransactionDao {

    /** Observes all transactions ordered by date descending. */
    @Query("SELECT * FROM transactions ORDER BY date DESC")
    fun getAll(): Flow<List<TransactionEntity>>

    /** Observes transactions for a specific month and year, ordered by date descending. */
    @Query("""
        SELECT * FROM transactions
        WHERE strftime('%m', date/1000, 'unixepoch') = printf('%02d', :month)
        AND strftime('%Y', date/1000, 'unixepoch') = printf('%04d', :year)
        ORDER BY date DESC
    """)
    fun getByMonth(month: Int, year: Int): Flow<List<TransactionEntity>>

    /** Observes transactions for a specific category. */
    @Query("SELECT * FROM transactions WHERE categoryId = :categoryId ORDER BY date DESC")
    fun getByCategory(categoryId: Long): Flow<List<TransactionEntity>>

    /** Observes transactions within a date range (inclusive). */
    @Query("SELECT * FROM transactions WHERE date >= :start AND date <= :end ORDER BY date DESC")
    fun getByDateRange(start: Long, end: Long): Flow<List<TransactionEntity>>

    /** Observes the total amount for a specific type in a given month/year. */
    @Query("""
        SELECT SUM(amount) FROM transactions
        WHERE type = :type
        AND strftime('%m', date/1000, 'unixepoch') = printf('%02d', :month)
        AND strftime('%Y', date/1000, 'unixepoch') = printf('%04d', :year)
    """)
    fun getTotalByTypeAndMonth(type: String, month: Int, year: Int): Flow<Double?>

    /** Full-text search on note field. */
    @Query("SELECT * FROM transactions WHERE note LIKE '%' || :query || '%' ORDER BY date DESC")
    fun search(query: String): Flow<List<TransactionEntity>>

    /** Observes the most recent [limit] transactions. */
    @Query("SELECT * FROM transactions ORDER BY date DESC LIMIT :limit")
    fun getRecent(limit: Int): Flow<List<TransactionEntity>>

    /** Returns total expense for today. */
    @Query("SELECT SUM(amount) FROM transactions WHERE type = 'EXPENSE' AND date >= :startOfDay AND date <= :endOfDay")
    fun getTodayTotalExpense(startOfDay: Long, endOfDay: Long): Flow<Double?>

    /** Returns total spent per category in a date range for analytics. */
    @Query("""
        SELECT categoryId, SUM(amount) as total FROM transactions
        WHERE type = 'EXPENSE' AND date >= :start AND date <= :end
        GROUP BY categoryId
    """)
    fun getCategorySpending(start: Long, end: Long): Flow<List<CategorySpendingResult>>

    /** Gets a single transaction by its ID. */
    @Query("SELECT * FROM transactions WHERE id = :id")
    suspend fun getById(id: Long): TransactionEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(transaction: TransactionEntity): Long

    @Update
    suspend fun update(transaction: TransactionEntity)

    @Delete
    suspend fun delete(transaction: TransactionEntity)

    @Query("DELETE FROM transactions WHERE id = :id")
    suspend fun deleteById(id: Long)
}

/** Helper data class for aggregated category spending queries. */
data class CategorySpendingResult(
    val categoryId: Long,
    val total: Double
)
