package com.example.faunafinder.ui.post.model

data class Post(
    val id: String = "",
    val content: String = "",
    val imageUrl: String? = null,  // nueva propiedad para imagen
    val latitude: Double = 0.0,
    val longitude: Double = 0.0,
    val timestamp: Long = 0L,
    val likesCount: Int = 0,
    val commentsCount: Int = 0


)

