package com.example.tuf.data.local.mapper

import com.example.tuf.data.local.entity.SplitExpenseEntity
import com.example.tuf.data.local.entity.SplitGroupEntity
import com.example.tuf.domain.model.SplitExpense
import com.example.tuf.domain.model.SplitGroup
import org.json.JSONObject

fun SplitGroupEntity.toDomainModel(): SplitGroup {
    val membersList = this.members.split(",").filter { it.isNotBlank() }
    return SplitGroup(
        id = this.id,
        name = this.name,
        createdAt = this.createdAt,
        members = membersList
    )
}

fun SplitGroup.toEntity(): SplitGroupEntity {
    return SplitGroupEntity(
        id = this.id,
        name = this.name,
        createdAt = this.createdAt,
        members = this.members.joinToString(",")
    )
}

fun SplitExpenseEntity.toDomainModel(): SplitExpense {
    val map = mutableMapOf<String, Double>()
    try {
        val obj = JSONObject(this.splitsJson)
        val keys = obj.keys()
        while (keys.hasNext()) {
            val key = keys.next()
            map[key] = obj.getDouble(key)
        }
    } catch (e: Exception) {
        // Fallback or ignore
    }
    return SplitExpense(
        id = this.id,
        groupId = this.groupId,
        description = this.description,
        totalAmount = this.totalAmount,
        paidBy = this.paidBy,
        splitType = this.splitType,
        splits = map,
        date = this.date
    )
}

fun SplitExpense.toEntity(): SplitExpenseEntity {
    val obj = JSONObject()
    try {
        this.splits.forEach { (k, v) -> obj.put(k, v) }
    } catch (e: Exception) {
        // Ignore
    }
    return SplitExpenseEntity(
        id = this.id,
        groupId = this.groupId,
        description = this.description,
        totalAmount = this.totalAmount,
        paidBy = this.paidBy,
        splitType = this.splitType,
        splitsJson = obj.toString(),
        date = this.date
    )
}
