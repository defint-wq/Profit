package com.example.profit

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.profit.ui.navigation.ProfitNavHost

@Composable
fun ProfitApp(navController: NavHostController = rememberNavController()) {
    ProfitNavHost(navController = navController)
}