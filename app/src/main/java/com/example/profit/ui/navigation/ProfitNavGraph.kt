package com.example.profit.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.profit.ui.screens.home.HomeDestination
import com.example.profit.ui.screens.profit.ProfitEntryDestination
import com.example.profit.ui.screens.profit.ProfitEntryScreen
import com.example.profit.ui.screens.home.HomeScreen
import androidx.navigation.NavType
import com.example.profit.ui.screens.settings.SettingsDestination
import com.example.profit.ui.screens.settings.SettingsScreen

@Composable
fun ProfitNavHost(
    navController: NavHostController,
    modifier: Modifier = Modifier
) {
    NavHost(
        navController = navController,
        startDestination = HomeDestination.route,
        modifier = modifier
    ) {
        composable(route = HomeDestination.route) {
            HomeScreen(
                navigateToProfitEntry = { type ->
                    navController.navigate("${ProfitEntryDestination.route}/$type")
                },
                navigateToSettings = { navController.navigate(SettingsDestination.route) }
            )
        }

        composable(
            route = ProfitEntryDestination.routeWithArgs,
            arguments = listOf(navArgument(ProfitEntryDestination.typeArg) { type = NavType.StringType })
        ) { entry ->
            ProfitEntryScreen(
                navigateBack = { navController.popBackStack() },
                onNavigateUp = { navController.navigateUp() },
                initialType = entry.arguments?.getString(ProfitEntryDestination.typeArg) ?: "income"
            )
        }

        composable(route = SettingsDestination.route) {
            SettingsScreen(navigateBack = { navController.popBackStack() })
        }
    }
}