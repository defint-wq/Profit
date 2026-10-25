package com.example.profit.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.profit.data.ProfitEntity
import com.example.profit.repository.ProfitRepository
import kotlinx.coroutines.launch

class ProfitViewModel(val repository: ProfitRepository): ViewModel() {

    fun addProfit(profitEntity: ProfitEntity) {
        viewModelScope.launch {
            repository.addProfitToRoom(profitEntity)
        }
    }

    val profits = repository.getAllProfitsFromRoom()

    val incomes = repository.getIncomesFromRoom()

    val expenses = repository.getExpensesFromRoom()

    fun deleteProfit(profitEntity: ProfitEntity) {
        viewModelScope.launch {
            repository.deleteProfitFromRoom(profitEntity)
        }
    }
    fun updateProfit(profitEntity: ProfitEntity) {
        viewModelScope.launch {
            repository.updateProfitFromRoom(profitEntity)
        }
    }
}