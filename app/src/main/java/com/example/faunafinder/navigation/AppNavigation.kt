package com.example.faunafinder.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.faunafinder.ui.notification.NotificationScreen
import com.example.faunafinder.ui.perfil.PerfilScreen
import com.example.faunafinder.ui.screens.*
import com.example.faunafinder.ui.feed.*

sealed class Screen(val route: String) {
    object Home : Screen("home")
    object Login : Screen("login")
    object Register : Screen("register")
    object Feed : Screen("feed")
    object CreatePost : Screen("create_post")
    object Perfil : Screen("perfil")
    object Notifications : Screen("notifications")
    object PostDetail : Screen("post_detail")
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
        composable(Screen.Perfil.route) { PerfilScreen(navController) }
        composable(Screen.Notifications.route) {  NotificationScreen(navController) }

        // Post detail con parámetro postId
        composable(
            route = Screen.PostDetail.route + "/{postId}",
            arguments = listOf(navArgument("postId") { type = NavType.StringType })
        ) { backStackEntry ->
            val postId = backStackEntry.arguments?.getString("postId") ?: ""
            PostDetailScreen(postId = postId, onBack = { navController.popBackStack() })
        }
    }
}
