package com.example.tuf.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "split_expenses",
    foreignKeys = [
        ForeignKey(
            entity = SplitGroupEntity::class,
            parentColumns = ["id"],
            childColumns = ["groupId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index(value = ["groupId"])]
)
data class SplitExpenseEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val groupId: Long,
    val description: String,
    val totalAmount: Double,
    val paidBy: String, // Which member paid (e.g. "You" or "John")
    val splitType: String, // "EQUAL" or "UNEQUAL"
    // JSON mapping of member -> amount owed (e.g., {"You": 0.0, "John": 500.0})
    val splitsJson: String,
    val date: Long
)
