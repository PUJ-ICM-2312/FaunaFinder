package com.example.faunafinder.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHost
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.faunafinder.ui.screens.HomeScreen
import com.example.faunafinder.ui.screens.LoginScreen
import com.example.faunafinder.ui.screens.RegisterScreen
import com.example.faunafinder.ui.screens.CreatePostScreen
import com.example.faunafinder.ui.screens.FeedScreen

sealed class Screen(val route: String) {
    object Home : Screen("home")
    object Login : Screen("login")
    object Register : Screen("register")
    object Feed : Screen("feed")
    object CreatePost : Screen("create_post")

}

@Composable
fun AppNavigation() {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = Screen.Home.route) {
        composable(Screen.Home.route) { HomeScreen(navController) }
        composable(Screen.Login.route) { LoginScreen(navController) }
        composable(Screen.Register.route) { RegisterScreen(navController) }
        composable(Screen.Feed.route) { FeedScreen(navController) }
        composable(Screen.CreatePost.route) { CreatePostScreen(navController) }
    }
}
