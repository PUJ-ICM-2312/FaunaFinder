package com.example.faunafinder.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.navigation.NavController

@Composable
fun BottomNavigationBar(navController: NavController) {
    NavigationBar(
        containerColor = Color.White
    ) {
        NavigationBarItem(
            selected = false,
            onClick = { navController.navigate(Screen.Feed.route) },
            icon = {
                Icon(
                    imageVector = Icons.Filled.Home,
                    contentDescription = "Home",
                    tint = Color(0xFFFCB900)
                )
            }
        )
        NavigationBarItem(
            selected = false,
            onClick = { navController.navigate(Screen.CreatePost.route) }, // 👈 ¡Aquí va el +!
            icon = {
                Icon(
                    imageVector = Icons.Filled.Add,
                    contentDescription = "Crear publicación",
                    tint = Color(0xFFFCB900)
                )
            }
        )
        NavigationBarItem(
            selected = false,
            onClick = { /* Notificaciones */ },
            icon = {
                Icon(
                    imageVector = Icons.Filled.Notifications,
                    contentDescription = "Notificaciones",
                    tint = Color(0xFFFCB900)
                )
            }
        )
        NavigationBarItem(
            selected = false,
            onClick = { /* Perfil */ },
            icon = {
                Icon(
                    imageVector = Icons.Filled.Person,
                    contentDescription = "Perfil",
                    tint = Color(0xFFFCB900)
                )
            }
        )
    }
}
