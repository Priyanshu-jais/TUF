package com.example.tuf.data.repository

import com.example.tuf.data.local.dao.SplitDao
import com.example.tuf.data.local.mapper.toDomainModel
import com.example.tuf.data.local.mapper.toEntity
import com.example.tuf.domain.model.SplitExpense
import com.example.tuf.domain.model.SplitGroup
import com.example.tuf.domain.repository.SplitRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class SplitRepositoryImpl(private val dao: SplitDao) : SplitRepository {
    override fun getAllGroups(): Flow<List<SplitGroup>> {
        return dao.getAllGroups().map { list -> list.map { it.toDomainModel() } }
    }

    override suspend fun getGroupById(groupId: Long): SplitGroup? {
        return dao.getGroupById(groupId)?.toDomainModel()
    }

    override suspend fun insertGroup(group: SplitGroup): Long {
        return dao.insertGroup(group.toEntity())
    }

    override suspend fun updateGroup(group: SplitGroup) {
        dao.updateGroup(group.toEntity())
    }

    override suspend fun deleteGroup(group: SplitGroup) {
        dao.deleteGroup(group.toEntity())
    }

    override fun getExpensesForGroup(groupId: Long): Flow<List<SplitExpense>> {
        return dao.getExpensesForGroup(groupId).map { list -> list.map { it.toDomainModel() } }
    }

    override suspend fun insertSplitExpense(expense: SplitExpense): Long {
        return dao.insertSplitExpense(expense.toEntity())
    }

    override suspend fun deleteSplitExpense(expense: SplitExpense) {
        dao.deleteSplitExpense(expense.toEntity())
    }

    override suspend fun deleteSplitExpenseById(expenseId: Long) {
        dao.deleteSplitExpenseById(expenseId)
    }
}
