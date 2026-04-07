package com.example.tuf.domain.model

import androidx.compose.ui.graphics.Color

/**
 * Domain model representing a transaction category.
 *
 * @property id Unique identifier.
 * @property name Display name (e.g. "Food & Dining").
 * @property iconName Material icon name string.
 * @property colorHex Hex color string (e.g. "#FF6584").
 * @property color Compose [Color] resolved from [colorHex].
 * @property type Category type — income, expense, or both.
 * @property isCustom Whether created by the user.
 */
data class Category(
    val id: Long = 0,
    val name: String,
    val iconName: String,
    val colorHex: String,
    val color: Color = Color.Gray,
    val type: CategoryType,
    val isCustom: Boolean = false
)

/** Enum for category applicability. */
enum class CategoryType {
    INCOME,
    EXPENSE,
    BOTH;

    companion object {
        fun fromString(value: String): CategoryType =
            entries.firstOrNull { it.name == value } ?: EXPENSE
    }
}
