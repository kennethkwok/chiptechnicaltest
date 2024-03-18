package com.kennethkwok.chiptechnicaltest.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import coil.ImageLoader
import com.kennethkwok.chiptechnicaltest.feature.images.DogBreedImagesScreen
import com.kennethkwok.chiptechnicaltest.feature.images.DogBreedImagesViewModel
import com.kennethkwok.chiptechnicaltest.feature.home.HomeScreen
import com.kennethkwok.chiptechnicaltest.feature.home.HomeViewModel

@Composable
fun AppNavHost(
    modifier: Modifier = Modifier,
    navController: NavHostController,
    imageLoader: ImageLoader,
    startDestination: String = NavigationItem.Home.route,
) {
    NavHost(
        modifier = modifier,
        navController = navController,
        startDestination = startDestination
    ) {
        composable(NavigationItem.Home.route) { navBackStackEntry ->
            val viewModel: HomeViewModel = hiltViewModel(navBackStackEntry)
            HomeScreen(
                viewModel = viewModel,
                navigateToBreedImages = {
                    navController.navigate("${NavigationItem.Details.route}?breed=$it")
                },
                navigateToSubBreedImages = { breed, subBreed ->
                    navController.navigate("${NavigationItem.Details.route}?breed=$breed&subBreed=$subBreed")
                }
            )
        }
        composable(
            "${NavigationItem.Details.route}?breed={breed}&subBreed={subBreed}",
            arguments = listOf(
                navArgument("breed") {
                    type = NavType.StringType
                },
                navArgument("subBreed") {
                    nullable = true
                    defaultValue = null
                    type = NavType.StringType
                }
            )
        ) { navBackStackEntry ->
            val viewModel: DogBreedImagesViewModel = hiltViewModel(navBackStackEntry)
            DogBreedImagesScreen(viewModel, imageLoader) {
                navController.popBackStack()
            }
        }
    }
}
