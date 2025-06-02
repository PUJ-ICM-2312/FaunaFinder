package com.example.faunafinder.ui.screens

import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.example.faunafinder.navigation.BottomNavigationBar
import com.example.faunafinder.sensors.AccelerometerSensorManager
import com.example.faunafinder.ui.map.PostRepository
import com.example.faunafinder.ui.publicar.LocalPost

@Composable
fun FeedScreen(navController: NavController) {
    val context = LocalContext.current
    var refreshTrigger by remember { mutableStateOf(0) }

    val accelerometer = remember {
        AccelerometerSensorManager(context) {
            refreshTrigger++
            Toast.makeText(context, "Feed recargado 🔄", Toast.LENGTH_SHORT).show()
        }
    }

    DisposableEffect(Unit) {
        accelerometer.startListening()
        onDispose { accelerometer.stopListening() }
    }

    val dynamicPosts = PostRepository.posts
    val allPosts = remember(refreshTrigger) { samplePosts + dynamicPosts }

    Scaffold(bottomBar = { BottomNavigationBar(navController) }) { padding ->
        Column(
            modifier = Modifier.padding(padding).fillMaxSize()
        ) {
            Text("Feed de Publicaciones", style = MaterialTheme.typography.headlineMedium, modifier = Modifier.padding(16.dp))

            LazyColumn(contentPadding = PaddingValues(8.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
                items(allPosts) { post ->
                    Card(
                        modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFF5F5F5))
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Image(
                                painter = rememberAsyncImagePainter(post.imageUri),
                                contentDescription = null,
                                modifier = Modifier.fillMaxWidth().height(250.dp)
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(post.description)

                            post.orientation?.let {
                                Text("Orientación al tomar la foto: $it", style = MaterialTheme.typography.bodySmall)
                            }

                            post.latitude?.let { lat ->
                                val lng = post.longitude ?: 0.0
                                //val geoUri = Uri.parse("geo:$lat,$lng?q=$lat,$lng")

                                Text(
                                    text = "Ubicación: $lat, $lng (Ver en mapa)",
                                    color = Color(0xFF1E88E5),
                                    modifier = Modifier.clickable {
                                        val directionsUri = Uri.parse("https://www.google.com/maps/dir/?api=1&destination=$lat,$lng&travelmode=walking")
                                        val intent = Intent(Intent.ACTION_VIEW, directionsUri)

                                        intent.setPackage("com.google.android.apps.maps")
                                        context.startActivity(intent)
                                    }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

val samplePosts = listOf(
    LocalPost(
        imageUri = Uri.parse("android.resource://com.example.faunafinder/drawable/animal"),
        description = "¿Saben de qué tipo es el chiwiro?",
        latitude = -12.0453,
        longitude = -77.0428,
        orientation = "Este"
    ),
    LocalPost(
        imageUri = Uri.parse("android.resource://com.example.faunafinder/drawable/danta"),
        description = "¿De qué raza es este perro?",
        latitude = -13.1628,
        longitude = -72.5450,
        orientation = "Noroeste"
    )
)
