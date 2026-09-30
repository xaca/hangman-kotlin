package com.example.myapplication.navigation

sealed class Screen(val route: String) {
    object Splash : Screen("splash")
    object Home : Screen("home")
    object GameModeSelection : Screen("game_mode_selection")
    object CategorySelection : Screen("category_selection")
    object CustomWordInput : Screen("custom_word_input/{category}") {
        fun createRoute(category: String) = "custom_word_input/$category"
    }
    object Game : Screen("game/{category}?customWord={customWord}") {
        fun createRoute(category: String, customWord: String? = null): String {
            return if (!customWord.isNullOrEmpty()) {
                "game/$category?customWord=$customWord"
            } else {
                "game/$category"
            }
        }
    }
    object Instructions : Screen("instructions")
    object Score : Screen("score")
    object Config : Screen("config")
    object Credits : Screen("credits")
}
