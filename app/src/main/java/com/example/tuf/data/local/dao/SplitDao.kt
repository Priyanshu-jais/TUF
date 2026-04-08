package com.example.tuf.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.example.tuf.data.local.entity.SplitExpenseEntity
import com.example.tuf.data.local.entity.SplitGroupEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface SplitDao {

    @Query("SELECT * FROM split_groups ORDER BY createdAt DESC")
    fun getAllGroups(): Flow<List<SplitGroupEntity>>

    @Query("SELECT * FROM split_groups WHERE id = :groupId")
    suspend fun getGroupById(groupId: Long): SplitGroupEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertGroup(group: SplitGroupEntity): Long

    @Update
    suspend fun updateGroup(group: SplitGroupEntity)

    @Delete
    suspend fun deleteGroup(group: SplitGroupEntity)

    @Query("SELECT * FROM split_expenses WHERE groupId = :groupId ORDER BY date DESC")
    fun getExpensesForGroup(groupId: Long): Flow<List<SplitExpenseEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSplitExpense(expense: SplitExpenseEntity): Long

    @Delete
    suspend fun deleteSplitExpense(expense: SplitExpenseEntity)

    @Query("DELETE FROM split_expenses WHERE id = :expenseId")
    suspend fun deleteSplitExpenseById(expenseId: Long)
}
