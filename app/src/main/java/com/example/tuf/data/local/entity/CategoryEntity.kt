package com.example.tuf.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Room entity representing a transaction category.
 *
 * @property id Auto-generated primary key.
 * @property name Display name (e.g. "Food & Dining").
 * @property iconName Material icon name string (e.g. "restaurant").
 * @property colorHex Hex color string (e.g. "#FF6584").
 * @property type "INCOME", "EXPENSE", or "BOTH".
 * @property isCustom Whether this category was created by the user.
 */
@Entity(tableName = "categories")
data class CategoryEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val iconName: String,
    val colorHex: String,
    val type: String,
    val isCustom: Boolean = false
)
