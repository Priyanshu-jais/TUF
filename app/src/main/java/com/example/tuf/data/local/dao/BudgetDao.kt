package com.example.tuf.data.local.dao

import androidx.room.*
import com.example.tuf.data.local.entity.BudgetEntity
import kotlinx.coroutines.flow.Flow

/**
 * Data Access Object for [BudgetEntity] operations.
 */
@Dao
interface BudgetDao {

    /** Observes all budgets for a specific month and year. */
    @Query("SELECT * FROM budgets WHERE month = :month AND year = :year")
    fun getByMonth(month: Int, year: Int): Flow<List<BudgetEntity>>

    /** Observes the budget for a specific category in a given month/year. Returns null if not set. */
    @Query("SELECT * FROM budgets WHERE categoryId = :categoryId AND month = :month AND year = :year LIMIT 1")
    fun getByCategoryAndMonth(categoryId: Long, month: Int, year: Int): Flow<BudgetEntity?>

    /** Returns a single budget by its ID. */
    @Query("SELECT * FROM budgets WHERE id = :id")
    suspend fun getById(id: Long): BudgetEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(budget: BudgetEntity): Long

    @Update
    suspend fun update(budget: BudgetEntity)

    @Delete
    suspend fun delete(budget: BudgetEntity)

    @Query("DELETE FROM budgets WHERE id = :id")
    suspend fun deleteById(id: Long)
}
