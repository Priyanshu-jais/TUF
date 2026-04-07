package com.example.tuf.data.local.mapper

import com.example.tuf.core.utils.ColorUtils
import com.example.tuf.data.local.entity.CategoryEntity
import com.example.tuf.domain.model.Category
import com.example.tuf.domain.model.CategoryType

/**
 * Extension functions to map between [CategoryEntity] and [Category] domain model.
 */

fun CategoryEntity.toDomain(): Category = Category(
    id = id,
    name = name,
    iconName = iconName,
    colorHex = colorHex,
    color = ColorUtils.hexToColor(colorHex),
    type = CategoryType.fromString(type),
    isCustom = isCustom
)

fun Category.toEntity(): CategoryEntity = CategoryEntity(
    id = id,
    name = name,
    iconName = iconName,
    colorHex = colorHex,
    type = type.name,
    isCustom = isCustom
)
