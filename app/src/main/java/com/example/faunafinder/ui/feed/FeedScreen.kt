package com.example.faunafinder.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.faunafinder.navigation.Screen
import com.example.faunafinder.ui.post.components.PostItem
import com.example.faunafinder.ui.post.model.Post
import com.example.faunafinder.ui.post.repository.PostsRepository
import com.example.faunafinder.navigation.BottomNavigationBar

import androidx.navigation.NavType
import androidx.navigation.compose.*

import androidx.compose.ui.Alignment


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FeedScreen(navController: NavController) {
    var posts by remember { mutableStateOf(listOf<Post>()) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var isLoading by remember { mutableStateOf(true) }

    LaunchedEffect(Unit) {
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
        bottomBar = { BottomNavigationBar(navController) },
        content = { padding ->
            Box(modifier = Modifier.padding(padding).fillMaxSize()) {
                when {
                    isLoading -> CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                    errorMessage != null -> Text(errorMessage ?: "", color = MaterialTheme.colorScheme.error, modifier = Modifier.align(Alignment.Center))
                    else -> LazyColumn {
                        items(posts) { post ->
                            PostItem(post = post, onClick = {}, onCommentsClick = {
                                navController.navigate(Screen.PostDetail.route + "/${post.id}")
                            })
                        }
                    }
                }
            }
        }
    )
}
