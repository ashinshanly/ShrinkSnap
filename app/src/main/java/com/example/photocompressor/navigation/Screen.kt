package com.example.photocompressor.navigation

sealed class Screen(val route: String) {
    object Home : Screen("home")
    object Compress : Screen("compress")
    object Results : Screen("results")
    object Settings : Screen("settings")
} 