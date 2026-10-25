package com.example.profit.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface ProfitDao {

    @Insert
    suspend fun addProfit(profitEntity: ProfitEntity)

    @Delete
    suspend fun deleteProfit(profitEntity: ProfitEntity)

    @Update
    suspend fun updateProfit(profitEntity: ProfitEntity)

    @Query("SELECT * FROM profit ORDER BY DATE DESC")
    fun getAllProfits(): Flow<List<ProfitEntity>>

    @Query("SELECT * FROM profit WHERE type = 'income' ORDER BY DATE DESC")
    fun getAllIncomes(): Flow<List<ProfitEntity>>

    @Query("SELECT * FROM profit WHERE type = 'expense' ORDER BY DATE DESC")
    fun getAllExpenses(): Flow<List<ProfitEntity>>

}