package com.example.faunafinder.ui.screens

import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.example.faunafinder.navigation.BottomNavigationBar
import com.example.faunafinder.sensors.AccelerometerSensorManager
import com.example.faunafinder.ui.post.model.Post
import com.example.faunafinder.ui.post.repository.PostsRepository
import com.example.faunafinder.ui.post.components.PostItem
import com.example.faunafinder.navigation.Screen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FeedScreen(navController: NavController) {
    val context = LocalContext.current
    var posts by remember { mutableStateOf(listOf<Post>()) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var isLoading by remember { mutableStateOf(true) }
    var refreshTrigger by remember { mutableStateOf(0) }

    // Acelerómetro para recargar
    val accelerometer = remember {
        AccelerometerSensorManager(context) {
            refreshTrigger++
            Toast.makeText(context, "Feed recargado 🔄", Toast.LENGTH_SHORT).show()
        }
    }

    DisposableEffect(Unit) {
        accelerometer.startListening()
        onDispose { accelerometer.stopListening() }
    }

    LaunchedEffect(refreshTrigger) {
        isLoading = true
        PostsRepository.listenPosts(
            onChange = {
                posts = it
                isLoading = false
            },
            onError = {
                errorMessage = it.message
                isLoading = false
            }
        )
    }

    Scaffold(
        topBar = { SmallTopAppBar(title = { Text("Feed") }) },
        bottomBar = { BottomNavigationBar(navController) }
    ) { padding ->
        Box(modifier = Modifier
            .padding(padding)
            .fillMaxSize()) {
            when {
                isLoading -> CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                errorMessage != null -> Text(errorMessage!!, color = MaterialTheme.colorScheme.error, modifier = Modifier.align(Alignment.Center))
                else -> LazyColumn(
                    contentPadding = PaddingValues(8.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    items(posts) { post ->
                        PostItem(
                            post = post,
                            onClick = { navController.navigate(Screen.PostDetail.route + "/${post.id}") },
                            onCommentsClick = { navController.navigate(Screen.PostDetail.route + "/${post.id}") }
                        )
                    }
                }
            }
        }
    }
}
