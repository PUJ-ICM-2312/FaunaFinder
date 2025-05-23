package com.example.faunafinder.ui.post.components

import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import com.example.faunafinder.ui.post.model.Like
import com.example.faunafinder.ui.post.repository.LikeRepository
import com.google.firebase.auth.FirebaseAuth

@Composable
fun LikeButton(postId: String, initialLikesCount: Int, modifier: androidx.compose.ui.Modifier = androidx.compose.ui.Modifier) {
    val currentUser = FirebaseAuth.getInstance().currentUser
    var userLike by remember { mutableStateOf<Like?>(null) }
    var likesCount by remember { mutableStateOf(initialLikesCount) }
    var isLoading by remember { mutableStateOf(false) }

    // Consultar si el usuario ya dio like
    LaunchedEffect(postId, currentUser?.uid) {
        currentUser?.uid?.let { uid ->
            LikeRepository.getUserLikeForPost(postId, uid,
                onSuccess = { like -> userLike = like },
                onFailure = { /* manejo error opcional */ })
        }
    }

    Button(
        onClick = {
            if (currentUser == null || isLoading) return@Button
            isLoading = true
            val userId = currentUser.uid
            if (userLike != null) {
                LikeRepository.removeLike(userLike!!.id,
                    onSuccess = {
                        userLike = null
                        likesCount -= 1
                        isLoading = false
                    },
                    onFailure = { isLoading = false }
                )
            } else {
                val like = Like(postId = postId, userId = userId)
                LikeRepository.addLike(like,
                    onSuccess = {
                        userLike = like
                        likesCount += 1
                        isLoading = false
                    },
                    onFailure = { isLoading = false }
                )
            }
        },
        modifier = modifier,
        enabled = !isLoading
    ) {
        Text(text = if (userLike != null) "Quitar Like ($likesCount)" else "Dar Like ($likesCount)")
    }
}

