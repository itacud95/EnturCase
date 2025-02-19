package com.example.enturcase.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.enturcase.ui.screen.DeparturesScreen
import com.example.enturcase.ui.screen.HomeScreen
import com.example.enturcase.ui.viewmodel.DeparturesViewModel
import com.example.enturcase.ui.viewmodel.NearbyStopsViewModel

@Composable
fun NavGraph(
    navController: NavHostController,
    nearbyStopsViewModel: NearbyStopsViewModel,
    departuresViewModel: DeparturesViewModel,
) {
    NavHost(
        navController = navController,
        startDestination = Screen.Home.route
    ) {
        composable(Screen.Home.route) {
            HomeScreen(navController, nearbyStopsViewModel)
        }
        composable(
            route = Screen.Details.route,
            arguments = listOf(navArgument("stopPlaceId") { type = NavType.StringType })
        ) {
            DeparturesScreen(navController, departuresViewModel)
        }
    }
}