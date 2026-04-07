package com.example.tuf.domain.usecase

import com.example.tuf.domain.model.Category
import com.example.tuf.domain.model.CategoryType
import com.example.tuf.domain.repository.CategoryRepository
import kotlinx.coroutines.flow.Flow

class GetCategoriesUseCase(private val repository: CategoryRepository) {
    operator fun invoke(): Flow<List<Category>> = repository.getAllCategories()
    fun byType(type: CategoryType): Flow<List<Category>> = repository.getCategoriesByType(type)
}

class AddCategoryUseCase(private val repository: CategoryRepository) {
    suspend operator fun invoke(category: Category): Long = repository.insertCategory(category)
}

class UpdateCategoryUseCase(private val repository: CategoryRepository) {
    suspend operator fun invoke(category: Category) = repository.updateCategory(category)
}

class DeleteCategoryUseCase(private val repository: CategoryRepository) {
    suspend operator fun invoke(category: Category) = repository.deleteCategory(category)
}
