package com.example.faunafinder.ui.feed

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.faunafinder.R
import com.example.faunafinder.ui.publicar.Post
import com.example.faunafinder.ui.feed.PostItem
import androidx.navigation.NavController
import com.example.faunafinder.navigation.BottomNavigationBar


@Composable
fun FeedScreen(navController: NavController) {
    Scaffold(
        bottomBar = { BottomNavigationBar() }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            Text(
                text = "Inicio",
                style = MaterialTheme.typography.headlineMedium,
                modifier = Modifier.padding(16.dp)
            )

            LazyColumn(
                contentPadding = PaddingValues(8.dp)
            ) {
                items(samplePosts) { post ->
                    PostItem(post = post)
                }
            }
        }
    }
}

val samplePosts = listOf(
    Post(
        username = "Camil_777",
        profileColor = Color.Red,
        question = "¿Saben de que tipo es el chiwiro?",
        imageRes = R.drawable.animal // Asegurate de tener esta imagen en `res/drawable`
    ),
    Post(
        username = "Pau_rodr",
        profileColor = Color.Yellow,
        question = "¿de que raza es este perro?",
        imageRes = R.drawable.danta // Asegurate de tener esta imagen en `res/drawable`
    )
)