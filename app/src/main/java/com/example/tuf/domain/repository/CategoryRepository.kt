package com.example.tuf.domain.repository

import com.example.tuf.domain.model.Category
import com.example.tuf.domain.model.CategoryType
import kotlinx.coroutines.flow.Flow

/**
 * Repository interface for category data operations.
 */
interface CategoryRepository {

    fun getAllCategories(): Flow<List<Category>>

    fun getCategoriesByType(type: CategoryType): Flow<List<Category>>

    suspend fun getCategoryById(id: Long): Category?

    suspend fun insertCategory(category: Category): Long

    suspend fun updateCategory(category: Category)

    suspend fun deleteCategory(category: Category)
}
