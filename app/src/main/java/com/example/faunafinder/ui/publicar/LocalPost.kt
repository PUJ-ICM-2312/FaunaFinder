package com.example.faunafinder.ui.publicar


import android.net.Uri

data class LocalPost(
    val imageUri: Uri,
    val description: String,
    val latitude: Double?,
    val longitude: Double?
)
