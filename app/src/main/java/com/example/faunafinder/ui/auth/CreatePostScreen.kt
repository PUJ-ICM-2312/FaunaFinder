package com.example.faunafinder.ui.auth

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.rememberAsyncImagePainter
import com.example.faunafinder.data.Post
import com.example.faunafinder.data.PostViewModel
import com.google.firebase.storage.ktx.storage
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import kotlinx.coroutines.tasks.await
import java.util.*

@Composable
fun CreatePostScreen(
    viewModel: PostViewModel = viewModel(),
    onPostCreated: () -> Unit = {}
) {
    val context = LocalContext.current
    var description by remember { mutableStateOf("") }
    var imageUri by remember { mutableStateOf<Uri?>(null) }
    var isUploading by remember { mutableStateOf(false) }

    // Lanzador de galería
    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        imageUri = uri
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text("Crear Publicación", style = MaterialTheme.typography.headlineSmall)

        OutlinedTextField(
            value = description,
            onValueChange = { description = it },
            label = { Text("Descripción") },
            modifier = Modifier.fillMaxWidth()
        )

        Button(onClick = { galleryLauncher.launch("image/*") }) {
            Text("Seleccionar imagen")
        }

        imageUri?.let {
            Image(
                painter = rememberAsyncImagePainter(it),
                contentDescription = "Imagen seleccionada",
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
            )
        }

        Button(
            onClick = {
                if (description.isBlank()) return@Button
                isUploading = true
                imageUri?.let { uri ->
                    uploadImageToFirebase(uri) { imageUrl ->
                        val post = Post(
                            userId = "fakeUser123",
                            userName = "Usuario Genérico",
                            description = description,
                            imageUrl = imageUrl
                        )
                        viewModel.createPost(post)
                        isUploading = false
                        description = ""
                        imageUri = null
                        onPostCreated()
                    }
                } ?: run {
                    val post = Post(
                        userId = "falseUser123",
                        userName = "Usuario Genérico de prueba",
                        description = description
                    )
                    viewModel.createPost(post)
                    isUploading = false
                    description = ""
                    imageUri = null
                    onPostCreated()
                }
            },
            enabled = !isUploading
        ) {
            Text(if (isUploading) "Publicando..." else "Publicar")
        }
    }
}

// Función para subir imagen a Firebase Storage
fun uploadImageToFirebase(uri: Uri, onComplete: (String?) -> Unit) {
    val storage = Firebase.storage
    val ref = storage.reference.child("post_images/${UUID.randomUUID()}.jpg")

    ref.putFile(uri)
        .continueWithTask { task ->
            if (!task.isSuccessful) {
                task.exception?.let { throw it }
            }
            ref.downloadUrl
        }
        .addOnSuccessListener { uri: Uri ->
            onComplete(uri.toString())
        }
        .addOnFailureListener { exception: Exception ->
            exception.printStackTrace()
            onComplete(null)
        }
}
