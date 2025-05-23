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
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PostDetailScreen(postId: String, onBack: () -> Unit) {
    val db = FirebaseFirestore.getInstance()
    var post by remember { mutableStateOf<Post?>(null) }
    var isLoadingPost by remember { mutableStateOf(true) }
    var errorPost by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(postId) {
        db.collection("posts").document(postId)
            .get()
            .addOnSuccessListener { snapshot ->
                val loadedPost = snapshot.toObject(Post::class.java)
                if (loadedPost != null) {
                    post = loadedPost
                } else {
                    errorPost = "Post no encontrado"
                }
                isLoadingPost = false
            }
            .addOnFailureListener { e ->
                errorPost = "Error cargando post: ${e.message}"
                isLoadingPost = false
            }
    }

    if (isLoadingPost) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
    } else if (errorPost != null) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
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

    var userLike by remember { mutableStateOf<Like?>(null) }
    var likesCount by remember { mutableStateOf(post.likesCount) }
    var isLiking by remember { mutableStateOf(false) }

    // Cargar comentarios
    LaunchedEffect(post.id) {
        isLoadingComments = true
        CommentRepository.getCommentsForPost(post.id,
            onSuccess = {
                comments = it
                isLoadingComments = false
            },
            onFailure = {
                errorMessage = "Error cargando comentarios: ${it.message}"
                isLoadingComments = false
            })
    }

    // Verificar si el usuario dio like
    LaunchedEffect(post.id, currentUser?.uid) {
        currentUser?.uid?.let { uid ->
            LikeRepository.getUserLikeForPost(post.id, uid, onSuccess = {
                userLike = it
            }, onFailure = {})
        }
    }

    Scaffold(
        topBar = {
            SmallTopAppBar(
                title = { Text("Detalle del Post") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Atrás")
                    }
                }
            )
        },
        content = { padding ->
            Column(modifier = Modifier
                .padding(padding)
                .padding(16.dp)
                .fillMaxSize()) {

                // Post contenido
                Text(post.content, style = MaterialTheme.typography.headlineSmall)
                Spacer(modifier = Modifier.height(8.dp))

                post.imageUrl?.let { url ->
                    AsyncImage(
                        model = url,
                        contentDescription = "Imagen del post",
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(250.dp)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                }

                Text(
                    text = "Ubicación: lat=${post.latitude}, lon=${post.longitude}",
                    style = MaterialTheme.typography.bodySmall
                )
                Text(
                    text = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault()).format(Date(post.timestamp)),
                    style = MaterialTheme.typography.bodySmall
                )
                Spacer(modifier = Modifier.height(16.dp))

                // Like button y contador
                Row {
                    Button(
                        onClick = {
                            if (currentUser == null) return@Button

                            if (isLiking) return@Button
                            isLiking = true

                            val userId = currentUser.uid

                            if (userLike != null) {
                                // Quitar like
                                LikeRepository.removeLike(userLike!!.id, onSuccess = {
                                    userLike = null
                                    likesCount -= 1
                                    isLiking = false
                                }, onFailure = {
                                    isLiking = false
                                    errorMessage = "Error quitando like"
                                })
                            } else {
                                // Agregar like
                                val like = Like(postId = post.id, userId = userId)
                                LikeRepository.addLike(like, onSuccess = {
                                    userLike = like
                                    likesCount += 1
                                    isLiking = false
                                }, onFailure = {
                                    isLiking = false
                                    errorMessage = "Error agregando like"
                                })
                            }
                        },
                        enabled = !isLiking
                    ) {
                        Text(text = if (userLike != null) "Quitar Like ($likesCount)" else "Dar Like ($likesCount)")
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Comentarios
                Text("Comentarios", style = MaterialTheme.typography.headlineSmall)
                Spacer(modifier = Modifier.height(8.dp))

                if (isLoadingComments) {
                    CircularProgressIndicator()
                } else {
                    if (comments.isEmpty()) {
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
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Nuevo comentario
                if (currentUser != null) {
                    OutlinedTextField(
                        value = newCommentText,
                        onValueChange = { newCommentText = it },
                        label = { Text("Escribe un comentario...") },
                        modifier = Modifier.fillMaxWidth(),
                        maxLines = 3
                    )
                    Spacer(modifier = Modifier.height(8.dp))
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

                            CommentRepository.addComment(comment, onSuccess = {
                                comments = comments + comment
                                newCommentText = ""
                            }, onFailure = {
                                errorMessage = "Error agregando comentario"
                            })
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Publicar comentario")
                    }
                } else {
                    Text("Inicia sesión para comentar.", style = MaterialTheme.typography.bodyMedium)
                }

                errorMessage?.let {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(it, color = MaterialTheme.colorScheme.error)
                }
            }
        }
    )
}

@Composable
fun CommentItem(comment: Comment) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
    ) {
        Column(modifier = Modifier.padding(8.dp)) {
            Text(comment.username, style = MaterialTheme.typography.bodyMedium)
            Spacer(modifier = Modifier.height(2.dp))
            Text(comment.content)
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault()).format(Date(comment.timestamp)),
                style = MaterialTheme.typography.bodySmall
            )
        }
    }
}
