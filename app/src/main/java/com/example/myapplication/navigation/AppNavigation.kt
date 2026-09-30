package com.example.myapplication.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.myapplication.data.GameMode
import com.example.myapplication.data.PlayerManager
import com.example.myapplication.ui.screens.CategorySelectionScreen
import com.example.myapplication.ui.screens.ConfigScreen
import com.example.myapplication.ui.screens.CreditsScreen
import com.example.myapplication.ui.screens.CustomWordInputScreen
import com.example.myapplication.ui.screens.GameModeSelectionScreen
import com.example.myapplication.ui.screens.GameScreen
import com.example.myapplication.ui.screens.HomeScreen
import com.example.myapplication.ui.screens.InstructionsScreen
import com.example.myapplication.ui.screens.ScoreScreen
import com.example.myapplication.ui.screens.SplashScreen

@Composable
fun AppNavigation() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = Screen.Splash.route
    ) {
        composable(Screen.Splash.route) {
            SplashScreen(
                onTimeout = {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Splash.route) { inclusive = true }
                    }
                }
            )
        }

        composable(Screen.Home.route) {
            HomeScreen(
                onNavigateToGameModeSelection = {
                    navController.navigate(Screen.GameModeSelection.route)
                },
                onNavigateToInstructions = {
                    navController.navigate(Screen.Instructions.route)
                },
                onNavigateToScore = {
                    navController.navigate(Screen.Score.route)
                },
                onNavigateToConfig = {
                    navController.navigate(Screen.Config.route)
                },
                onNavigateToCredits = {
                    navController.navigate(Screen.Credits.route)
                }
            )
        }

        composable(Screen.GameModeSelection.route) {
            GameModeSelectionScreen(
                onModeSelected = {
                    navController.navigate(Screen.CategorySelection.route)
                },
                onBack = {
                    navController.popBackStack()
                }
            )
        }

        composable(Screen.CategorySelection.route) {
            CategorySelectionScreen(
                onCategorySelected = { category ->
                    if (PlayerManager.currentGameMode == GameMode.VS_FRIEND) {
                        navController.navigate(Screen.CustomWordInput.createRoute(category))
                    } else {
                        navController.navigate(Screen.Game.createRoute(category))
                    }
                },
                onBack = {
                    navController.popBackStack()
                }
            )
        }

        composable(
            route = Screen.CustomWordInput.route,
            arguments = listOf(
                navArgument("category") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val category = backStackEntry.arguments?.getString("category") ?: "General"
            CustomWordInputScreen(
                category = category,
                onStartGame = { customWord ->
                    navController.navigate(Screen.Game.createRoute(category, customWord))
                },
                onBack = {
                    navController.popBackStack()
                }
            )
        }

        composable(
            route = Screen.Game.route,
            arguments = listOf(
                navArgument("category") { type = NavType.StringType },
                navArgument("customWord") {
                    type = NavType.StringType
                    nullable = true
                    defaultValue = null
                }
            )
        ) { backStackEntry ->
            val category = backStackEntry.arguments?.getString("category") ?: "General"
            val customWord = backStackEntry.arguments?.getString("customWord")
            GameScreen(
                category = category,
                customWord = customWord,
                onBack = {
                    navController.popBackStack(Screen.Home.route, false)
                }
            )
        }

        composable(Screen.Instructions.route) {
            InstructionsScreen(
                onBack = {
                    navController.popBackStack()
                }
            )
        }

        composable(Screen.Score.route) {
            ScoreScreen(
                onBack = {
                    navController.popBackStack()
                }
            )
        }

        composable(Screen.Config.route) {
            ConfigScreen(
                onBack = {
                    navController.popBackStack()
                }
            )
        }

        composable(Screen.Credits.route) {
            CreditsScreen(
                onBack = {
                    navController.popBackStack()
                }
            )
        }
    }
}
