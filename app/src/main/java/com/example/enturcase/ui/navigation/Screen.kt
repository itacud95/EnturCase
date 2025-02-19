package com.example.enturcase.ui.navigation

sealed class Screen(val route: String) {
    object Home : Screen("home")
    object StopPlaces : Screen("stopPlaces")
    object Departures : Screen("details/{stopPlaceId}/{stopPlaceName}") {
        fun createRoute(itemId: String, stopPlaceName: String) = "details/$itemId/$stopPlaceName"
    }
}