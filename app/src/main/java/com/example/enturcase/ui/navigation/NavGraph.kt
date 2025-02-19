package com.example.enturcase.ui.navigation

import androidx.activity.viewModels
import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.enturcase.GraphQLClient
import com.example.enturcase.data.repository.DeparturesRepository
import com.example.enturcase.ui.screen.DeparturesScreen
import com.example.enturcase.ui.screen.HomeScreen
import com.example.enturcase.ui.screen.StopPlacesScreen
import com.example.enturcase.ui.viewmodel.DeparturesViewModel
import com.example.enturcase.ui.viewmodel.DeparturesViewModelFactory
import com.example.enturcase.ui.viewmodel.LocationViewModel
import com.example.enturcase.ui.viewmodel.NearbyStopsViewModel

@Composable
fun NavGraph(
    navController: NavHostController,
    locationViewModel: LocationViewModel,
    nearbyStopsViewModel: NearbyStopsViewModel,
) {
    NavHost(
        navController = navController,
        startDestination = Screen.Home.route
    ) {
        composable(Screen.Home.route) {
            HomeScreen(navController, locationViewModel)
        }

        composable(Screen.StopPlaces.route) {
            StopPlacesScreen(navController, nearbyStopsViewModel)
        }
        composable(
            route = Screen.Departures.route,
            arguments = listOf(navArgument("stopPlaceId") { type = NavType.StringType })
        ) { backStackEntry ->
            val stopPlaceId = backStackEntry.arguments?.getString("stopPlaceId") ?: ""
            val departuresViewModel: DeparturesViewModel = viewModel(
                factory = DeparturesViewModelFactory(
                    DeparturesRepository(GraphQLClient),
                    stopPlaceId
                )
            )

            DeparturesScreen(navController, departuresViewModel)
        }
    }
}