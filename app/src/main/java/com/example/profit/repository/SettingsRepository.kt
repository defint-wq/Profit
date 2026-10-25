package com.example.profit.repository

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

val Context.settingsDataStore by preferencesDataStore(name = "settings")

object DisplayMode {
    const val BOTH = "both"
    const val INCOME = "income"
    const val EXPENSE = "expense"
}

class SettingsRepository(private val context: Context) {
    private val key = stringPreferencesKey("display_mode")

    val displayMode: Flow<String> = context.settingsDataStore.data.map { it[key] ?: DisplayMode.BOTH }

    suspend fun setDisplayMode(mode: String) {
        context.settingsDataStore.edit { it[key] = mode }
    }
}