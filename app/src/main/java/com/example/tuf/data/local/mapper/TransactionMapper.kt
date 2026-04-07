package com.example.tuf.data.local.mapper

import com.example.tuf.core.extensions.parseHashTags
import com.example.tuf.core.utils.Constants
import com.example.tuf.data.local.entity.CategoryEntity
import com.example.tuf.data.local.entity.TransactionEntity
import com.example.tuf.domain.model.Category
import com.example.tuf.domain.model.CategoryType
import com.example.tuf.domain.model.Transaction
import com.example.tuf.domain.model.TransactionType

/**
 * Extension functions to map between [TransactionEntity] and [Transaction] domain model.
 */

/**
 * Maps a [TransactionEntity] to a [Transaction] domain model.
 * Requires [category] to be resolved separately since entities don't embed relations.
 */
fun TransactionEntity.toDomain(category: Category): Transaction = Transaction(
    id = id,
    amount = amount,
    type = TransactionType.fromString(type),
    category = category,
    note = note,
    date = date,
    createdAt = createdAt,
    isRecurring = isRecurring,
    recurringId = recurringId,
    imageUri = imageUri,
    tags = note.parseHashTags()
)

/**
 * Maps a [Transaction] domain model to a [TransactionEntity] for persistence.
 */
fun Transaction.toEntity(): TransactionEntity = TransactionEntity(
    id = id,
    amount = amount,
    type = type.name,
    categoryId = category.id,
    note = note,
    date = date,
    createdAt = createdAt,
    isRecurring = isRecurring,
    recurringId = recurringId,
    imageUri = imageUri
)
