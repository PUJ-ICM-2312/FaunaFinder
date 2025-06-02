package com.example.faunafinder.ui.screens

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.net.Uri
import android.os.Environment
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.example.faunafinder.ui.map.PostRepository
import com.example.faunafinder.ui.publicar.LocalPost
import com.example.faunafinder.sensors.ProximitySensorManager
import com.example.faunafinder.sensors.CompassSensorManager
import com.google.android.gms.location.LocationServices
import java.io.File
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.roundToInt

@Composable
fun CreatePostScreen(navController: NavController) {
    val context = LocalContext.current

    val proximitySensor = remember { ProximitySensorManager(context) }
    DisposableEffect(Unit) {
        proximitySensor.startListening()
        onDispose { proximitySensor.stopListening() }
    }
    val isNear = proximitySensor.isNear.value

    val compassSensor = remember { CompassSensorManager(context) }
    DisposableEffect(Unit) {
        compassSensor.startListening()
        onDispose { compassSensor.stopListening() }
    }
    val azimuth = compassSensor.azimuth.value

    var imageUri by remember { mutableStateOf<Uri?>(null) }
    var description by remember { mutableStateOf(TextFieldValue("")) }

    var hasCameraPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_GRANTED
        )
    }

    var hasLocationPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        )
    }

    var location by remember { mutableStateOf<Location?>(null) }

    val cameraPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { granted -> hasCameraPermission = granted }

    val locationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { granted -> hasLocationPermission = granted }

    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture()
    ) { success -> if (!success) imageUri = null }

    fun createImageFile(context: Context): File {
        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(Date())
        val storageDir = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        return File.createTempFile("JPEG_${timeStamp}_", ".jpg", storageDir)
    }

    fun azimuthToDirection(azimuth: Float): String {
        val directions = listOf("Norte", "Noreste", "Este", "Sureste", "Sur", "Suroeste", "Oeste", "Noroeste")
        val index = ((azimuth + 22.5f) % 360 / 45).toInt()
        return directions[index]
    }

    LaunchedEffect(hasLocationPermission) {
        if (hasLocationPermission) {
            val fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)
            if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                fusedLocationClient.lastLocation.addOnSuccessListener { loc ->
                    location = loc
                }
            }
        } else {
            locationPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        }
    }

    Column(
        modifier = Modifier.fillMaxSize().padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text("Crear publicación", style = MaterialTheme.typography.headlineMedium)

        if (isNear) {
            Text("Sensor de proximidad activado: aleja el teléfono para continuar 📵", color = Color.Red)
        }

        Button(
            onClick = {
                if (!hasCameraPermission) {
                    cameraPermissionLauncher.launch(Manifest.permission.CAMERA)
                } else {
                    val photoFile = createImageFile(context)
                    val photoUri = FileProvider.getUriForFile(context, "${context.packageName}.provider", photoFile)
                    imageUri = photoUri
                    cameraLauncher.launch(photoUri)
                }
            },
            modifier = Modifier.fillMaxWidth(),
            enabled = !isNear
        ) {
            Text("Tomar foto")
        }

        val direction = azimuthToDirection(azimuth)
        Text("Orientación: ${azimuth.roundToInt()}° ($direction)", style = MaterialTheme.typography.bodySmall)

        imageUri?.let {
            Image(
                painter = rememberAsyncImagePainter(it),
                contentDescription = "Foto tomada",
                modifier = Modifier.fillMaxWidth().height(300.dp)
            )

            OutlinedTextField(
                value = description,
                onValueChange = { description = it },
                label = { Text("¿Qué especie encontraste?") },
                modifier = Modifier.fillMaxWidth()
            )

            location?.let {
                Text("Ubicación: ${it.latitude}, ${it.longitude}", style = MaterialTheme.typography.bodySmall)
            } ?: Text("Obteniendo ubicación...")

            Button(
                onClick = {
                    val post = LocalPost(
                        imageUri = imageUri!!,
                        description = description.text,
                        latitude = location?.latitude,
                        longitude = location?.longitude,
                        orientation = direction
                    )
                    PostRepository.addPost(post)
                    Toast.makeText(context, "Publicado!", Toast.LENGTH_SHORT).show()
                    navController.navigate("feed")
                },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFCB900))
            ) {
                Text("Publicar")
            }
        } ?: Text("Toma una foto para crear tu publicación.")
    }
}