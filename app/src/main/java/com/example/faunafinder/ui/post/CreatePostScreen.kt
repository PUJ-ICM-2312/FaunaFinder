package com.example.faunafinder.ui.screens

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import coil.compose.AsyncImage
import com.example.faunafinder.ui.post.model.Post
import com.example.faunafinder.ui.post.repository.PostsRepository
import com.google.android.gms.location.LocationServices
import com.google.firebase.storage.FirebaseStorage
import java.io.File
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("MissingPermission")
@Composable
fun CreatePostScreen(navController: androidx.navigation.NavController) {
    val context = LocalContext.current

    var content by remember { mutableStateOf("") }
    var imageUri by remember { mutableStateOf<Uri?>(null) }
    var imageUrl by remember { mutableStateOf<String?>(null) }
    var latitude by remember { mutableStateOf(0.0) }
    var longitude by remember { mutableStateOf(0.0) }
    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    val locationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { granted ->
            if (granted) {
                getCurrentLocation(
                    context = context,
                    onSuccess = { latLng ->
                        latitude = latLng.first
                        longitude = latLng.second
                    },
                    onFailure = { message ->
                        errorMessage = message
                    }
                )
            } else {
                errorMessage = "Permiso de ubicación denegado"
            }
        }
    )

    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            imageUri = it
            getCurrentLocation(
                context = context,
                onSuccess = { latLng ->
                    latitude = latLng.first
                    longitude = latLng.second
                },
                onFailure = { message ->
                    errorMessage = message
                }
            )
        }
    }

    val cameraImageUri = remember { mutableStateOf<Uri?>(null) }

    fun createImageUri(context: Context): Uri {
        val imageFile = File(context.cacheDir, "camera_photo_${UUID.randomUUID()}.jpg")
        return androidx.core.content.FileProvider.getUriForFile(
            context,
            context.packageName + ".fileprovider",
            imageFile
        )
    }

    val takePictureLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture()
    ) { success ->
        if (success) {
            cameraImageUri.value?.let { uri ->
                imageUri = uri
                getCurrentLocation(
                    context = context,
                    onSuccess = { latLng ->
                        latitude = latLng.first
                        longitude = latLng.second
                    },
                    onFailure = { message ->
                        errorMessage = message
                    }
                )
            }
        } else {
            errorMessage = "No se tomó la foto"
        }
    }

    fun uploadImage(uri: Uri, onSuccess: (String) -> Unit, onFailure: (Exception) -> Unit) {
        val storageRef = FirebaseStorage.getInstance().reference
        val imageRef = storageRef.child("posts/${UUID.randomUUID()}.jpg")

        val uploadTask = imageRef.putFile(uri)
        uploadTask.addOnSuccessListener {
            imageRef.downloadUrl.addOnSuccessListener { url ->
                onSuccess(url.toString())
            }.addOnFailureListener { e -> onFailure(e) }
        }.addOnFailureListener { e -> onFailure(e) }
    }

    LaunchedEffect(Unit) {
        getCurrentLocation(
            context = context,
            onSuccess = { latLng ->
                latitude = latLng.first
                longitude = latLng.second
            },
            onFailure = { message ->
                errorMessage = message
            }
        )
    }

    Column(modifier = Modifier.padding(16.dp)) {
        Text("Crear Post con imagen y ubicación", style = MaterialTheme.typography.headlineMedium)

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = content,
            onValueChange = { content = it },
            label = { Text("Descripción del post") },
            modifier = Modifier.fillMaxWidth(),
            maxLines = 5
        )

        Spacer(modifier = Modifier.height(16.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Button(onClick = { imagePickerLauncher.launch("image/*") }) {
                Text("Seleccionar imagen")
            }
            Button(onClick = {
                cameraImageUri.value = createImageUri(context)
                cameraImageUri.value?.let { uri ->
                    takePictureLauncher.launch(uri)
                }
            }) {
                Text("Tomar foto")
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        imageUri?.let { uri ->
            AsyncImage(
                model = uri,
                contentDescription = "Imagen seleccionada o tomada",
                modifier = Modifier.fillMaxWidth().height(200.dp)
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            "Ubicación actual: lat=$latitude, lon=$longitude",
            style = MaterialTheme.typography.bodySmall
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                if (content.isBlank()) {
                    errorMessage = "El contenido no puede estar vacío"
                    return@Button
                }

                isLoading = true
                errorMessage = null

                val uploadAndPost = {
                    val post = Post(
                        content = content.trim(),
                        imageUrl = imageUrl,
                        latitude = latitude,
                        longitude = longitude,
                        timestamp = System.currentTimeMillis()
                    )
                    PostsRepository.addPost(
                        post,
                        onSuccess = {
                            isLoading = false
                            navController.popBackStack()
                        },
                        onFailure = { e ->
                            isLoading = false
                            errorMessage = "Error guardando post: ${e.message}"
                        }
                    )
                }

                imageUri?.let { uri ->
                    uploadImage(
                        uri,
                        onSuccess = { url ->
                            imageUrl = url
                            uploadAndPost()
                        },
                        onFailure = { e ->
                            isLoading = false
                            errorMessage = "Error subiendo imagen: ${e.message}"
                        }
                    )
                } ?: uploadAndPost()
            },
            enabled = !isLoading,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(if (isLoading) "Publicando..." else "Publicar")
        }

        errorMessage?.let {
            Spacer(modifier = Modifier.height(8.dp))
            Text(it, color = MaterialTheme.colorScheme.error)
        }
    }
}

@SuppressLint("MissingPermission")
private fun getCurrentLocation(
    context: Context,
    onSuccess: (Pair<Double, Double>) -> Unit,
    onFailure: (String) -> Unit = { _ -> }
) {
    val fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)
    if (ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
        fusedLocationClient.lastLocation.addOnSuccessListener { location ->
            if (location != null) {
                onSuccess(Pair(location.latitude, location.longitude))
            } else {
                onFailure("No se pudo obtener la ubicación")
            }
        }.addOnFailureListener { e ->
            onFailure("Error al obtener ubicación: ${e.message}")
        }
    } else {
        onFailure("Permiso de ubicación no concedido")
    }
}
