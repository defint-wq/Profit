package com.example.profit.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.profit.repository.DisplayMode
import com.example.profit.repository.SettingsRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class SettingsViewModel(private val repository: SettingsRepository) : ViewModel() {

    val displayMode = repository.displayMode
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), DisplayMode.BOTH)

    fun save(mode: String) {
        viewModelScope.launch { repository.setDisplayMode(mode) }
    }
}