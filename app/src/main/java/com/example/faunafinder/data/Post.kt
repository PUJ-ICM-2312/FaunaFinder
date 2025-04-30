package com.example.faunafinder.data

data class Post(
    val id: String = "",
    val userId: String = "",
    val userName: String = "",
    val description: String = "",
    val imageUrl: String? = null,
    val timestamp: Long = System.currentTimeMillis(),
    val likes: List<String> = emptyList(),
    val comments: List<Comment> = emptyList()
)
