package com.example.faunafinder.ui.feed


import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun NotificationsScreen() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
            text = "Notificaciones",
            style = MaterialTheme.typography.headlineMedium
        )

        Card {
            Text(
                text = "Sofi comentó tu publicación 🐾",
                modifier = Modifier.padding(16.dp)
            )
        }

        Card {
            Text(
                text = "Julian dio like a tu foto 📸",
                modifier = Modifier.padding(16.dp)
            )
        }

        Card {
            Text(
                text = "Tu publicación fue destacada 💡",
                modifier = Modifier.padding(16.dp)
            )
        }
    }
}
