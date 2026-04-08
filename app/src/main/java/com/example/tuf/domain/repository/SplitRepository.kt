package com.example.tuf.domain.repository

import com.example.tuf.domain.model.SplitExpense
import com.example.tuf.domain.model.SplitGroup
import kotlinx.coroutines.flow.Flow

interface SplitRepository {
    fun getAllGroups(): Flow<List<SplitGroup>>
    suspend fun getGroupById(groupId: Long): SplitGroup?
    suspend fun insertGroup(group: SplitGroup): Long
    suspend fun updateGroup(group: SplitGroup)
    suspend fun deleteGroup(group: SplitGroup)

    fun getExpensesForGroup(groupId: Long): Flow<List<SplitExpense>>
    suspend fun insertSplitExpense(expense: SplitExpense): Long
    suspend fun deleteSplitExpense(expense: SplitExpense)
    suspend fun deleteSplitExpenseById(expenseId: Long)
}
