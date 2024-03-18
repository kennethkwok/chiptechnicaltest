package com.kennethkwok.chiptechnicaltest.ui.navigation

/**
 * Represents the navigable routes within this app
 */
sealed class NavigationItem(val route: String) {

    /**
     * Home screen containing the list of dog breeds
     */
    data object Home : NavigationItem("home")

    /**
     * Dog breed details screen containing images of the selected dog breed
     */
    data object Details : NavigationItem("details")
}
