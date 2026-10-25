package com.example.profit.viewModel

import android.app.Application
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.profit.data.ProfitDataBase
import com.example.profit.repository.ProfitRepository
import com.example.profit.repository.SettingsRepository

object AppViewModelProvider {
    val Factory = viewModelFactory {
        initializer {
            val application =
                this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as Application
            val db = ProfitDataBase.getInstance(application)
            ProfitViewModel(ProfitRepository(db))
        }
        initializer {
            val application = this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as Application
            SettingsViewModel(SettingsRepository(application))
        }
    }
}