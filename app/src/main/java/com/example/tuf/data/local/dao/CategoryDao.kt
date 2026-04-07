package com.example.tuf.data.local.dao

import androidx.room.*
import com.example.tuf.data.local.entity.CategoryEntity
import kotlinx.coroutines.flow.Flow

/**
 * Data Access Object for [CategoryEntity] operations.
 */
@Dao
interface CategoryDao {

    /** Observes all categories ordered by name. */
    @Query("SELECT * FROM categories ORDER BY isCustom ASC, name ASC")
    fun getAll(): Flow<List<CategoryEntity>>

    /** Observes categories filtered by type ("INCOME", "EXPENSE", or "BOTH"). */
    @Query("SELECT * FROM categories WHERE type = :type OR type = 'BOTH' ORDER BY name ASC")
    fun getByType(type: String): Flow<List<CategoryEntity>>

    /** Returns a single category by its ID. */
    @Query("SELECT * FROM categories WHERE id = :id")
    suspend fun getById(id: Long): CategoryEntity?

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(category: CategoryEntity): Long

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertAll(categories: List<CategoryEntity>)

    @Update
    suspend fun update(category: CategoryEntity)

    @Delete
    suspend fun delete(category: CategoryEntity)

    /** Returns count of all categories — used to detect first launch seeding. */
    @Query("SELECT COUNT(*) FROM categories")
    suspend fun count(): Int
}
