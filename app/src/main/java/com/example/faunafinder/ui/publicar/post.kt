package com.example.faunafinder.ui.publicar

import androidx.compose.ui.graphics.Color

data class Post(
    val username: String,
    val profileColor: Color,
    val question: String,
    val imageRes: Int // ID de drawable
)
