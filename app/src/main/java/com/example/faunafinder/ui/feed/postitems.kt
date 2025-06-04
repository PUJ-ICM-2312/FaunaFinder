package com.example.faunafinder.ui.post.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.faunafinder.ui.post.model.Post

@Composable
fun PostItem(
    post: Post,
    onClick: () -> Unit,
    onCommentsClick: () -> Unit,
    onLocationClick: (Double, Double) -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(8.dp)
            .clickable { onClick() }
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text(post.content)
            Spacer(modifier = Modifier.height(8.dp))

            AsyncImage(model = post.imageUrl, contentDescription = null)
            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Ubicación: ${post.latitude}, ${post.longitude}",
                modifier = Modifier.clickable { onLocationClick(post.latitude, post.longitude) },
                color = MaterialTheme.colorScheme.primary
            )

            Spacer(modifier = Modifier.height(8.dp))
            LikeButton(postId = post.id, initialLikesCount = post.likesCount)

            TextButton(onClick = onCommentsClick) {
                Text("Ver Comentarios (${post.commentsCount})")
            }
        }
    }
}