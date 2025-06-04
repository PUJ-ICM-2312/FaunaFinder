package com.example.faunafinder.ui.notification

data class Notification(
    val id: String = "",
    val userId: String = "", // A quién va dirigida
    val message: String = "",
    val timestamp: Long = 0L,
    val type: String = "" // "like", "comment", etc.
)
