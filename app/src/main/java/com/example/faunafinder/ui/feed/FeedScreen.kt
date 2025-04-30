package com.example.faunafinder.ui.feed

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.outlined.ExitToApp
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.rememberAsyncImagePainter
import com.example.faunafinder.data.Comment
import com.example.faunafinder.data.Post
import com.example.faunafinder.data.PostViewModel
import com.google.firebase.auth.FirebaseAuth

@Composable
fun FeedScreen(
    viewModel: PostViewModel = viewModel(),
    onNavigateToCreate: () -> Unit,
    onLogout: () -> Unit
) {
    val context = LocalContext.current
    val posts by viewModel.posts.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.loadPosts()
    }

    Box(modifier = Modifier.fillMaxSize()) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(posts) { post ->
                PostCard(
                    post = post,
                    currentUserId = "fakeUser123",
                    onLike = { viewModel.toggleLike(post.id, "fakeUser123") },
                    onLocationClick = {
                        val uri = "geo:${it.first},${it.second}?q=${Uri.encode(it.third)}"
                        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(uri))
                        intent.setPackage("com.google.android.apps.maps")
                        context.startActivity(intent)
                    },
                    onCommentSubmit = { commentText ->
                        viewModel.addComment(
                            postId = post.id,
                            comment = Comment(
                                userId = "fakeUser123",
                                userName = "Usuario Genérico",
                                content = commentText
                            )
                        )
                    }
                )
            }
        }

        // FAB: Crear publicación
        FloatingActionButton(
            onClick = onNavigateToCreate,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(16.dp)
        ) {
            Text("+")
        }

        // FAB: Cerrar sesión
        FloatingActionButton(
            onClick = {
                FirebaseAuth.getInstance().signOut()
                onLogout()
            },
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(16.dp),
            containerColor = Color.Red
        ) {
            Icon(Icons.Outlined.ExitToApp, contentDescription = "Cerrar sesión", tint = Color.White)
        }
    }
}

@Composable
fun PostCard(
    post: Post,
    currentUserId: String,
    onLike: () -> Unit,
    onLocationClick: (Triple<Double, Double, String>) -> Unit,
    onCommentSubmit: (String) -> Unit
) {
    var newComment by remember { mutableStateOf(TextFieldValue("")) }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(10.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            post.imageUrl?.let {
                Image(
                    painter = rememberAsyncImagePainter(it),
                    contentDescription = "Imagen del animal",
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                        .clip(RoundedCornerShape(10.dp)),
                    contentScale = ContentScale.Crop
                )
                Spacer(modifier = Modifier.height(8.dp))
            }

            val locationName = "Parque Nacional, Colombia"
            val latitude = 4.623978
            val longitude = -74.064417

            Text(
                text = buildAnnotatedString {
                    append("📍 ")
                    withStyle(
                        androidx.compose.ui.text.SpanStyle(
                            color = Color.Blue,
                            textDecoration = TextDecoration.Underline
                        )
                    ) {
                        append(locationName)
                    }
                },
                modifier = Modifier.clickable {
                    onLocationClick(Triple(latitude, longitude, locationName))
                }
            )

            Spacer(modifier = Modifier.height(4.dp))
            Text(text = post.description, style = MaterialTheme.typography.bodyMedium)

            Spacer(modifier = Modifier.height(8.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = onLike) {
                    if (currentUserId in post.likes) {
                        Icon(Icons.Filled.Favorite, contentDescription = "Quitar like", tint = Color.Red)
                    } else {
                        Icon(Icons.Outlined.FavoriteBorder, contentDescription = "Dar like")
                    }
                }
                Text("${post.likes.size} Me gusta", style = MaterialTheme.typography.bodySmall)
            }

            Divider(modifier = Modifier.padding(vertical = 8.dp))

            Text("Comentarios:", style = MaterialTheme.typography.bodySmall)
            post.comments.takeLast(3).forEach {
                Text("- ${it.userName}: ${it.content}", style = MaterialTheme.typography.bodySmall)
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                OutlinedTextField(
                    value = newComment,
                    onValueChange = { newComment = it },
                    placeholder = { Text("Escribe un comentario...") },
                    modifier = Modifier.weight(1f)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Button(onClick = {
                    if (newComment.text.isNotBlank()) {
                        onCommentSubmit(newComment.text)
                        newComment = TextFieldValue("")
                    }
                }) {
                    Text("Enviar")
                }
            }
        }
    }
}
