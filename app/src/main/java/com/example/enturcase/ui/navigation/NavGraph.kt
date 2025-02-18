package com.example.enturcase.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.enturcase.ui.screen.DetailsScreen
import com.example.enturcase.ui.screen.HomeScreen
import com.example.enturcase.ui.viewmodel.MainViewModel

@Composable
fun NavGraph(
    navController: NavHostController,
    myViewModel: MainViewModel,
) {
    NavHost(
        navController = navController,
        startDestination = Screen.Home.route
    ) {
        composable(Screen.Home.route) {
            HomeScreen(navController, myViewModel)
        }
        composable(
            route = Screen.Details.route,
            arguments = listOf(navArgument("itemId") { type = NavType.StringType })
        ) { backStackEntry ->
            val itemId = backStackEntry.arguments?.getString("itemId")
            DetailsScreen(navController, itemId)
        }
    }
}