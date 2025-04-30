package com.example.faunafinder.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.faunafinder.ui.auth.HomeScreen
import com.example.faunafinder.ui.auth.LoginScreen
import com.example.faunafinder.ui.auth.RegisterScreen
import com.example.faunafinder.ui.feed.FeedScreen
import com.example.faunafinder.ui.auth.CreatePostScreen

sealed class Screen(val route: String) {
    object Home : Screen("home")
    object Login : Screen("login")
    object Register : Screen("register")
    object Feed : Screen("feed")
    object Create : Screen("create")
}

@Composable
fun AppNavigation() {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = Screen.Home.route) {
        composable(Screen.Home.route) {
            HomeScreen(navController)
        }
        composable(Screen.Login.route) {
            LoginScreen(navController)
        }
        composable(Screen.Register.route) {
            RegisterScreen(navController)
        }
        composable(Screen.Feed.route) {
            FeedScreen(
                onNavigateToCreate = {
                    navController.navigate(Screen.Create.route)
                },
                onLogout = {
                    navController.navigate(Screen.Login.route) {
                        popUpTo(0) { inclusive = true }
                    }
                }
            )
        }
        composable(Screen.Create.route) {
            CreatePostScreen(
                onPostCreated = {
                    navController.popBackStack(Screen.Feed.route, inclusive = false)
                }
            )
        }
    }
}

