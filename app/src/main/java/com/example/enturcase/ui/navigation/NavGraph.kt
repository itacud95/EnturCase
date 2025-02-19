package com.example.enturcase.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.enturcase.GraphQLClient
import com.example.enturcase.data.repository.DeparturesRepository
import com.example.enturcase.data.repository.LocationRepository
import com.example.enturcase.data.repository.StopPlacesRepository
import com.example.enturcase.ui.screen.DeparturesScreen
import com.example.enturcase.ui.screen.HomeScreen
import com.example.enturcase.ui.screen.StopPlacesContent
import com.example.enturcase.ui.screen.StopPlacesScreen
import com.example.enturcase.ui.screen.UiEvent
import com.example.enturcase.ui.viewmodel.DeparturesViewModel
import com.example.enturcase.ui.viewmodel.DeparturesViewModelFactory
import com.example.enturcase.ui.viewmodel.NearbyStopsViewModel
import com.example.enturcase.ui.viewmodel.NearbyStopsViewModelFactory
import com.google.android.gms.location.LocationServices

@Composable
fun NavGraph(
    navController: NavHostController
) {
    val context = LocalContext.current

    NavHost(
        navController = navController,
        startDestination = Screen.Home.route
    ) {
        composable(Screen.Home.route) {
            HomeScreen(navController)
        }

        composable(Screen.StopPlaces.route) {
            val nearbyStopsViewModel: NearbyStopsViewModel = viewModel(
                factory = NearbyStopsViewModelFactory(
                    StopPlacesRepository(),
                    LocationRepository(
                        context,
                        LocationServices.getFusedLocationProviderClient(context)
                    )
                )
            )
            val stopPlaces by nearbyStopsViewModel.stopPlaces.collectAsState()
            val content = StopPlacesContent(
                stopPlaces
            )
            val onEvent: (UiEvent) -> Unit = { event ->
                when (event) {
                    is UiEvent.ReloadData -> nearbyStopsViewModel.refreshData()
                }
            }
            StopPlacesScreen(navController, content, onEvent)
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