package com.example.profit.ui.screens.settings

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.profit.R
import com.example.profit.ui.navigation.NavigationDestination
import com.example.profit.ui.screens.ProfitTopBar
import com.example.profit.viewModel.AppViewModelProvider
import com.example.profit.viewModel.SettingsViewModel
import com.example.profit.repository.DisplayMode

object SettingsDestination : NavigationDestination {
    override val route = "settings"
    override val titleRes = R.string.settings_title
}

@OptIn(markerClass = [androidx.compose.material3.ExperimentalMaterial3Api::class])
@Composable
fun SettingsScreen(
    navigateBack: () -> Unit,
    viewModel: SettingsViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {
    val stored by viewModel.displayMode.collectAsState()
    var selected by remember(stored) { mutableStateOf(stored) }

    val options = listOf(
        DisplayMode.BOTH to "Орлого, зарлагыг хоёуланг нь харуулах",
        DisplayMode.INCOME to "Зөвхөн орлогыг харуулах",
        DisplayMode.EXPENSE to "Зөвхөн зарлагыг харуулах"
    )

    Scaffold(
        topBar = {
            ProfitTopBar(
                title = "Тохиргоо",
                canNavigateBack = true,
                navigateUp = navigateBack
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier.padding(innerPadding).padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Column(Modifier.selectableGroup()) {
                options.forEach { (mode, label) ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .selectable(
                                selected = selected == mode,
                                onClick = { selected = mode },
                                role = Role.RadioButton
                            )
                            .padding(vertical = 12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(selected = selected == mode, onClick = null)
                        Spacer(Modifier.width(12.dp))
                        Text(label)
                    }
                }
            }
            Button(
                onClick = {
                    viewModel.save(selected)
                    navigateBack()
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("ХАДГАЛАХ")
            }
        }
    }
}