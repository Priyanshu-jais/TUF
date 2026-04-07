package com.example.tuf.domain.usecase

import com.example.tuf.domain.model.RecurringTransaction
import com.example.tuf.domain.repository.RecurringRepository
import kotlinx.coroutines.flow.Flow

class GetRecurringTransactionsUseCase(private val repository: RecurringRepository) {
    operator fun invoke(): Flow<List<RecurringTransaction>> = repository.getAllRecurring()
    fun active(): Flow<List<RecurringTransaction>> = repository.getActiveRecurring()
}

class AddRecurringTransactionUseCase(private val repository: RecurringRepository) {
    suspend operator fun invoke(recurring: RecurringTransaction): Long =
        repository.insertRecurring(recurring)
}

class UpdateRecurringUseCase(private val repository: RecurringRepository) {
    suspend operator fun invoke(recurring: RecurringTransaction) =
        repository.updateRecurring(recurring)
}

class DeleteRecurringUseCase(private val repository: RecurringRepository) {
    suspend operator fun invoke(recurring: RecurringTransaction) =
        repository.deleteRecurring(recurring)

    suspend fun byId(id: Long) = repository.deleteRecurringById(id)
}
