package com.example.tuf.data.local.mapper

import com.example.tuf.data.local.entity.RecurringTransactionEntity
import com.example.tuf.domain.model.Category
import com.example.tuf.domain.model.RecurringFrequency
import com.example.tuf.domain.model.RecurringTransaction
import com.example.tuf.domain.model.TransactionType

/**
 * Extension functions to map between [RecurringTransactionEntity] and [RecurringTransaction].
 */

fun RecurringTransactionEntity.toDomain(category: Category): RecurringTransaction = RecurringTransaction(
    id = id,
    amount = amount,
    type = TransactionType.fromString(type),
    category = category,
    note = note,
    frequency = RecurringFrequency.fromString(frequency),
    startDate = startDate,
    nextDueDate = nextDueDate,
    isActive = isActive
)

fun RecurringTransaction.toEntity(): RecurringTransactionEntity = RecurringTransactionEntity(
    id = id,
    amount = amount,
    type = type.name,
    categoryId = category.id,
    note = note,
    frequency = frequency.name,
    startDate = startDate,
    nextDueDate = nextDueDate,
    isActive = isActive
)
