package com.example.profit.repository

import com.example.profit.data.ProfitDataBase
import com.example.profit.data.ProfitEntity

class ProfitRepository (val profitDataBase: ProfitDataBase) {
    suspend fun addProfitToRoom(profitEntity: ProfitEntity) {
        profitDataBase.profitDao().addProfit(profitEntity)
    }
    suspend fun deleteProfitFromRoom(profitEntity: ProfitEntity) {
        profitDataBase.profitDao().deleteProfit(profitEntity)
    }
    suspend fun updateProfitFromRoom(profitEntity: ProfitEntity) {
        profitDataBase.profitDao().updateProfit(profitEntity)
    }

    fun getAllProfitsFromRoom() = profitDataBase.profitDao().getAllProfits()
    fun getIncomesFromRoom() = profitDataBase.profitDao().getAllIncomes()
    fun getExpensesFromRoom() = profitDataBase.profitDao().getAllExpenses()
 }