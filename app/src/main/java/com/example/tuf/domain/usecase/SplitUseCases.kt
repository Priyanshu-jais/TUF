package com.example.tuf.domain.usecase.split

import com.example.tuf.domain.model.SplitExpense
import com.example.tuf.domain.model.SplitGroup
import com.example.tuf.domain.repository.SplitRepository
import kotlinx.coroutines.flow.Flow

class GetGroupsUseCase(private val repository: SplitRepository) {
    operator fun invoke(): Flow<List<SplitGroup>> = repository.getAllGroups()
}

class AddGroupUseCase(private val repository: SplitRepository) {
    suspend operator fun invoke(group: SplitGroup): Long = repository.insertGroup(group)
}

class DeleteGroupUseCase(private val repository: SplitRepository) {
    suspend operator fun invoke(group: SplitGroup) = repository.deleteGroup(group)
}

class GetGroupExpensesUseCase(private val repository: SplitRepository) {
    operator fun invoke(groupId: Long): Flow<List<SplitExpense>> = repository.getExpensesForGroup(groupId)
}

class AddSplitExpenseUseCase(private val repository: SplitRepository) {
    suspend operator fun invoke(expense: SplitExpense): Long = repository.insertSplitExpense(expense)
}

class DeleteSplitExpenseUseCase(private val repository: SplitRepository) {
    suspend operator fun invoke(expenseId: Long) = repository.deleteSplitExpenseById(expenseId)
}
