package com.example.faunafinder.ui.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.faunafinder.ui.post.components.CommentItem
import com.example.faunafinder.ui.post.model.Comment
import com.example.faunafinder.ui.post.model.Post
import com.example.faunafinder.ui.post.repository.CommentRepository
import com.example.faunafinder.ui.post.repository.PostsRepository
import androidx.compose.material3.ExperimentalMaterial3Api


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PostDetailScreen(postId: String, onBack: () -> Unit) {
    var post by remember { mutableStateOf<Post?>(null) }
    var comments by remember { mutableStateOf<List<Comment>>(emptyList()) }
    var newComment by remember { mutableStateOf("") }

    LaunchedEffect(postId) {
        PostsRepository.getPostById(postId,
            onSuccess = { post = it },
            onFailure = { /* manejar error */ }
        )
        CommentRepository.listenCommentsForPost(postId,
            onChange = { comments = it },
            onError = { /* manejar error */ }
        )
    }

    post?.let {
        Scaffold(
            topBar = {
                TopAppBar(title = { Text("Detalle del Post") }, navigationIcon = {
                    IconButton(onClick = onBack) { Icon(Icons.Default.ArrowBack, contentDescription = "Atrás") }
                })
            }
        ) { padding ->
            Column(modifier = Modifier.padding(padding).padding(16.dp)) {
                Text(text = it.content, style = MaterialTheme.typography.titleLarge)
                Spacer(modifier = Modifier.height(8.dp))
                AsyncImage(
                    model = it.imageUrl,
                    contentDescription = "Imagen del post",
                    modifier = Modifier.fillMaxWidth().height(200.dp)
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text("Ubicación: ${it.latitude}, ${it.longitude}", style = MaterialTheme.typography.bodySmall)

                Spacer(modifier = Modifier.height(16.dp))
                OutlinedTextField(
                    value = newComment,
                    onValueChange = { newComment = it },
                    label = { Text("Añadir comentario") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))
                Button(
                    onClick = {
                        val comment = Comment(
                            postId = postId,
                            userId = "anon", // reemplazar por ID de usuario real
                            username = "Invitado",
                            content = newComment,
                            timestamp = System.currentTimeMillis()
                        )
                        CommentRepository.addComment(
                            comment,
                            onSuccess = { newComment = "" },
                            onFailure = { /* manejar error */ }
                        )
                    },
                    enabled = newComment.isNotBlank()
                ) {
                    Text("Comentar")
                }

                Spacer(modifier = Modifier.height(16.dp))
                LazyColumn {
                    items(comments) { comment ->
                        CommentItem(comment)
                    }
                }
            }
        }
    } ?: Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        CircularProgressIndicator()
    }
}
