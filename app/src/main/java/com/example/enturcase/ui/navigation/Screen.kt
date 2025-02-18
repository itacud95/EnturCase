package com.example.enturcase.ui.navigation

sealed class Screen(val route: String) {
    object Home : Screen("home")
    object Details : Screen("details/{itemId}") {
        fun createRoute(itemId: String) = "details/$itemId"
    }
}