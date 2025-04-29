package com.example.faunafinder.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

@Composable
fun BottomNavigationBar() {
    NavigationBar(
        containerColor = Color.White
    ) {
        NavigationBarItem(
            selected = true,
            onClick = { /* Navegar a Home */ },
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
            onClick = { /* Navegar a Agregar Post */ },
            icon = {
                Icon(
                    imageVector = Icons.Filled.Add,
                    contentDescription = "Add Post",
                    tint = Color(0xFFFCB900)
                )
            }
        )
        NavigationBarItem(
            selected = false,
            onClick = { /* Navegar a Notificaciones */ },
            icon = {
                Icon(
                    imageVector = Icons.Filled.Notifications,
                    contentDescription = "Notifications",
                    tint = Color(0xFFFCB900)
                )
            }
        )
        NavigationBarItem(
            selected = false,
            onClick = { /* Navegar a Perfil */ },
            icon = {
                Icon(
                    imageVector = Icons.Filled.Person,
                    contentDescription = "Profile",
                    tint = Color(0xFFFCB900)
                )
            }
        )
    }
}
