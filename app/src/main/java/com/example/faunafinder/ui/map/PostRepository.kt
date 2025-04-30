package com.example.faunafinder.ui.map



import androidx.compose.runtime.mutableStateListOf
import com.example.faunafinder.ui.publicar.LocalPost

object PostRepository {
    val posts = mutableStateListOf<LocalPost>()

    fun addPost(post: LocalPost) {
        posts.add(0, post) // al principio de la lista
    }
}
