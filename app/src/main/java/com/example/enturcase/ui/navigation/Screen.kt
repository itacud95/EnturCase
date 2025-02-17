package com.example.enturcase.ui.navigation

sealed class Screen(val route: String) {
    data object Home : Screen("home")
    data object StopPlaces : Screen("stopPlaces")
    data object Departures : Screen("details/{stopPlaceId}/{stopPlaceName}") {
        fun createRoute(itemId: String, stopPlaceName: String) = "details/$itemId/$stopPlaceName"
    }
}