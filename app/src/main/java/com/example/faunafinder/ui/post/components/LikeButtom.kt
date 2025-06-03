package com.example.faunafinder.ui.post.components

import androidx.compose.material3.*
import androidx.compose.runtime.*
import com.example.faunafinder.ui.post.model.Like
import com.example.faunafinder.ui.post.repository.LikeRepository
import com.google.firebase.auth.FirebaseAuth

import androidx.compose.ui.Modifier
import androidx.compose.material3.*

@Composable
fun LikeButton(
    postId: String,
    initialLikesCount: Int,
    modifier: Modifier = Modifier
) {
    val currentUser = FirebaseAuth.getInstance().currentUser
    var userLike by remember { mutableStateOf<Like?>(null) }
    var likesCount by remember { mutableStateOf(initialLikesCount) }
    var isLoading by remember { mutableStateOf(false) }

    // Verifica si el usuario actual ya ha dado like al post
    LaunchedEffect(postId, currentUser?.uid) {
        currentUser?.uid?.let { uid ->
            LikeRepository.getUserLikeForPost(
                postId = postId,
                userId = uid,
                onSuccess = { userLike = it },
                onFailure = { /* manejo de error opcional */ }
            )
        }
    }

    Button(
        onClick = {
            if (currentUser == null || isLoading) return@Button
            isLoading = true
            val userId = currentUser.uid
            if (userLike != null) {
                LikeRepository.removeLike(
                    likeId = userLike!!.id,
                    onSuccess = {
                        userLike = null
                        likesCount -= 1
                        isLoading = false
                    },
                    onFailure = { isLoading = false }
                )
            } else {
                val like = Like(postId = postId, userId = userId)
                LikeRepository.addLike(
                    like = like,
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
