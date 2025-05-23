package com.example.faunafinder.ui.post.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.faunafinder.ui.post.model.Post

@Composable
fun PostCard(post: Post, onCommentsClick: () -> Unit, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(8.dp)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text(text = post.content, style = MaterialTheme.typography.bodyLarge)
            Spacer(modifier = Modifier.height(8.dp))

            post.imageUrl?.let { url ->
                AsyncImage(
                    model = url,
                    contentDescription = "Imagen del post",
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                )
                Spacer(modifier = Modifier.height(8.dp))
            }

            Text(text = "Ubicación: lat=${post.latitude}, lon=${post.longitude}", style = MaterialTheme.typography.bodySmall)
            Spacer(modifier = Modifier.height(8.dp))

            LikeButton(postId = post.id, initialLikesCount = post.likesCount)

            Spacer(modifier = Modifier.height(8.dp))

            TextButton(onClick = onCommentsClick) {
                Text("Ver Comentarios (${post.commentsCount})")
            }
        }
    }
}
