package com.example.enturcase.ui.navigation

sealed class Screen(val route: String) {
    object Home : Screen("home")
    object StopPlaces : Screen("stopPlaces")
    object Departures : Screen("details/{stopPlaceId}") { // todo: rename
        fun createRoute(itemId: String) = "details/$itemId"
    }
}