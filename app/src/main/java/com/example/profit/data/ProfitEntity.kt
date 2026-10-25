package com.example.profit.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "profit")
data class ProfitEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val type: String,
    val category: String,
    val amount: Float,
    val date: Long,
)