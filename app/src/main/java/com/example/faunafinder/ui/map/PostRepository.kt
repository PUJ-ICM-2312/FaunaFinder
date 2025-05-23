package com.example.faunafinder.ui.map

import androidx.compose.runtime.mutableStateListOf
import com.example.faunafinder.ui.post.model.Post


object PostRepository {
    val posts = mutableStateListOf<Post>()

    fun addPost(post: Post) {
        posts.add(0, post) // al principio de la lista
    }
}
