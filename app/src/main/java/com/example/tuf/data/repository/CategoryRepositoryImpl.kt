package com.example.tuf.data.repository

import com.example.tuf.data.local.dao.CategoryDao
import com.example.tuf.data.local.mapper.toDomain
import com.example.tuf.data.local.mapper.toEntity
import com.example.tuf.domain.model.Category
import com.example.tuf.domain.model.CategoryType
import com.example.tuf.domain.repository.CategoryRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

/**
 * Concrete implementation of [CategoryRepository] using Room [CategoryDao].
 */
class CategoryRepositoryImpl(
    private val categoryDao: CategoryDao
) : CategoryRepository {

    override fun getAllCategories(): Flow<List<Category>> {
        return categoryDao.getAll().map { entities -> entities.map { it.toDomain() } }
    }

    override fun getCategoriesByType(type: CategoryType): Flow<List<Category>> {
        return categoryDao.getByType(type.name).map { entities -> entities.map { it.toDomain() } }
    }

    override suspend fun getCategoryById(id: Long): Category? {
        return categoryDao.getById(id)?.toDomain()
    }

    override suspend fun insertCategory(category: Category): Long {
        return categoryDao.insert(category.toEntity())
    }

    override suspend fun updateCategory(category: Category) {
        categoryDao.update(category.toEntity())
    }

    override suspend fun deleteCategory(category: Category) {
        categoryDao.delete(category.toEntity())
    }
}
