package com.example.faunafinder.ui.notification


import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.faunafinder.navigation.BottomNavigationBar
import com.example.faunafinder.ui.notification.Notification
import com.example.faunafinder.ui.notification.NotificationRepository
import com.google.firebase.auth.FirebaseAuth

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotificationScreen(navController: NavController) {
    val currentUser = FirebaseAuth.getInstance().currentUser
    var notifications by remember { mutableStateOf(listOf<Notification>()) }
    var error by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(currentUser?.uid) {
        currentUser?.uid?.let { uid ->
            NotificationRepository.getNotificationsForUser(
                userId = uid,
                onSuccess = { notifications = it },
                onFailure = { error = it.message }
            )
        }
    }

    Scaffold(
        topBar = { SmallTopAppBar(title = { Text("Notificaciones") }) },
        bottomBar = { BottomNavigationBar(navController) }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp)
                .fillMaxSize()
        ) {
            if (error != null) {
                Text("Error: $error", color = MaterialTheme.colorScheme.error)
            } else if (notifications.isEmpty()) {
                Text("No tienes notificaciones todavía.")
            } else {
                notifications.forEach { notification ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp)
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Text(
                                text = notification.message,
                                style = MaterialTheme.typography.bodyLarge
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = formatTimestamp(notification.timestamp),
                                style = MaterialTheme.typography.bodySmall
                            )
                        }
                    }
                }
            }
        }
    }
}

fun formatTimestamp(timestamp: Long): String {
    return java.text.SimpleDateFormat(
        "dd/MM/yyyy HH:mm",
        java.util.Locale.getDefault()
    ).format(java.util.Date(timestamp))
}
