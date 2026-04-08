package com.example.tuf.domain.model

data class SplitGroup(
    val id: Long = 0,
    val name: String,
    val createdAt: Long,
    val members: List<String>
)

data class SplitExpense(
    val id: Long = 0,
    val groupId: Long,
    val description: String,
    val totalAmount: Double,
    val paidBy: String,
    val splitType: String,
    val splits: Map<String, Double>,
    val date: Long
)
