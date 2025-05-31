package com.example.faunafinder.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.faunafinder.ui.post.model.Comment
import com.example.faunafinder.ui.post.model.Like
import com.example.faunafinder.ui.post.model.Post
import com.example.faunafinder.ui.post.repository.CommentRepository
import com.example.faunafinder.ui.post.repository.LikeRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.example.faunafinder.ui.post.components.CommentItem
import com.example.faunafinder.ui.post.repository.PostsRepository
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun PostDetailScreen(postId: String, onBack: () -> Unit) {
    val db = FirebaseDatabase.getInstance().reference.child("posts").child(postId)
    var post by remember { mutableStateOf<Post?>(null) }
    var isLoadingPost by remember { mutableStateOf(true) }
    var errorPost by remember { mutableStateOf<String?>(null) }

    // Escuchar post en tiempo real
    LaunchedEffect(postId) {
        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val loadedPost = snapshot.getValue(Post::class.java)
                if (loadedPost != null) {
                    post = loadedPost
                    errorPost = null
                } else {
                    errorPost = "Post no encontrado"
                }
                isLoadingPost = false
            }

            override fun onCancelled(error: DatabaseError) {
                errorPost = "Error cargando post: ${error.message}"
                isLoadingPost = false
            }
        }
        db.addValueEventListener(listener)
    }

    if (isLoadingPost) {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { CircularProgressIndicator() }
    } else if (errorPost != null) {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text(text = errorPost ?: "", color = MaterialTheme.colorScheme.error)
        }
    } else if (post != null) {
        PostDetailContent(post = post!!, onBack = onBack)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PostDetailContent(post: Post, onBack: () -> Unit) {
    val currentUser = FirebaseAuth.getInstance().currentUser
    var comments by remember { mutableStateOf(listOf<Comment>()) }
    var newCommentText by remember { mutableStateOf("") }
    var isLoadingComments by remember { mutableStateOf(true) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    // Escuchar comentarios en tiempo real
    LaunchedEffect(post.id) {
        CommentRepository.listenCommentsForPost(post.id,
            onChange = {
                comments = it
                isLoadingComments = false
            },
            onError = {
                errorMessage = "Error cargando comentarios: ${it.message}"
                isLoadingComments = false
            })
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Detalle del Post") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Atrás")
                    }
                }
            )
        },
        content = { padding ->
            Column(
                modifier = Modifier
                    .padding(padding)
                    .padding(16.dp)
                    .fillMaxSize()
            ) {
                Text(post.content, style = MaterialTheme.typography.headlineSmall)
                Spacer(Modifier.height(8.dp))

                post.imageUrl?.let { url ->
                    AsyncImage(
                        model = url,
                        contentDescription = "Imagen del post",
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(250.dp)
                    )
                    Spacer(Modifier.height(8.dp))
                }

                // Ubicación y fecha...
                Text(
                    "Ubicación: lat=${post.latitude}, lon=${post.longitude}",
                    style = MaterialTheme.typography.bodySmall
                )
                Text(
                    SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
                        .format(Date(post.timestamp)),
                    style = MaterialTheme.typography.bodySmall
                )

                Spacer(Modifier.height(16.dp))

                // Sección de comentarios
                Text("Comentarios", style = MaterialTheme.typography.headlineSmall)
                Spacer(Modifier.height(8.dp))

                if (isLoadingComments) {
                    CircularProgressIndicator()
                } else if (comments.isEmpty()) {
                    Text("Sin comentarios aún.")
                } else {
                    LazyColumn(
                        modifier = Modifier.weight(1f),
                        contentPadding = PaddingValues(vertical = 4.dp)
                    ) {
                        items(comments) { comment ->
                            CommentItem(comment)
                        }
                    }
                }

                Spacer(Modifier.height(8.dp))

                if (currentUser != null) {
                    OutlinedTextField(
                        value = newCommentText,
                        onValueChange = { newCommentText = it },
                        label = { Text("Escribe un comentario...") },
                        modifier = Modifier.fillMaxWidth(),
                        maxLines = 3
                    )
                    Spacer(Modifier.height(8.dp))
                    Button(
                        onClick = {
                            if (newCommentText.isBlank()) return@Button
                            val userId = currentUser.uid
                            val username = currentUser.email ?: "Anonimo"

                            val comment = Comment(
                                postId = post.id,
                                userId = userId,
                                username = username,
                                content = newCommentText.trim(),
                                timestamp = System.currentTimeMillis()
                            )

                            CommentRepository.addComment(comment,
                                onSuccess = {
                                    newCommentText = ""
                                    PostsRepository.incrementCommentsCount(post.id)
                                },
                                onFailure = {
                                    errorMessage = "Error agregando comentario"
                                }
                            )
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Publicar comentario")
                    }
                } else {
                    Text("Inicia sesión para comentar.", style = MaterialTheme.typography.bodyMedium)
                }

                errorMessage?.let {
                    Spacer(Modifier.height(8.dp))
                    Text(it, color = MaterialTheme.colorScheme.error)
                }
            }
        }
    )
}