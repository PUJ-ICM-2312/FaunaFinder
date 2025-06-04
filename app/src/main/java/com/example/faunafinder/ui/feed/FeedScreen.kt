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
        topBar = {
            SmallTopAppBar(
                title = { Text("FaunaFeed", style = MaterialTheme.typography.titleLarge) }
            )
        },
        bottomBar = {
            BottomNavigationBar(navController)
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            when {
                isLoading -> {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                }

                errorMessage != null -> {
                    Text(
                        text = errorMessage ?: "Error desconocido",
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier.align(Alignment.Center)
                    )
                }

                else -> {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 8.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(posts, key = { it.id }) { post ->
                            PostItem(
                                post = post,
                                modifier = Modifier
                                    .fillMaxWidth(),
                                onClick = {},
                                onCommentsClick = {
                                    navController.navigate(Screen.PostDetail.route + "/${post.id}")
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}
