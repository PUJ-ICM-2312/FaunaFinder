package com.example.faunafinder.ui.screens

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.app.ActivityCompat
import androidx.navigation.NavController
import com.example.faunafinder.navigation.BottomNavigationBar
import com.example.faunafinder.navigation.Screen
import com.example.faunafinder.sensors.AccelerometerSensorManager
import com.example.faunafinder.ui.post.components.PostItem
import com.example.faunafinder.ui.post.model.Post
import com.example.faunafinder.ui.post.repository.PostsRepository
import com.google.android.gms.location.LocationServices

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FeedScreen(navController: NavController) {
    val context = LocalContext.current
    var posts by remember { mutableStateOf<List<Post>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var refreshTrigger by remember { mutableStateOf(0) }

    // Acelerómetro para recargar
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

    LaunchedEffect(refreshTrigger) {
        isLoading = true
        PostsRepository.listenPosts(
            onChange = { fetchedPosts ->
                posts = fetchedPosts.sortedByDescending { it.timestamp }
                isLoading = false
            },
            onError = {
                errorMessage = it.message
                isLoading = false
            }
        )
    }

    Scaffold(
        topBar = { SmallTopAppBar(title = { Text("Feed") }) },
        bottomBar = { BottomNavigationBar(navController) }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
        ) {
            when {
                isLoading -> CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                errorMessage != null -> Text(
                    text = errorMessage ?: "Error desconocido",
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.align(Alignment.Center)
                )
                else -> {
                    LazyColumn(
                        contentPadding = PaddingValues(12.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        items(posts) { post ->
                            PostItem(
                                post = post,
                                onClick = {
                                    navController.navigate(Screen.PostDetail.route + "/${post.id}")
                                },
                                onCommentsClick = {
                                    navController.navigate(Screen.PostDetail.route + "/${post.id}")
                                },
                                onLocationClick = { lat, lon ->
                                    val fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)
                                    if (ActivityCompat.checkSelfPermission(
                                            context,
                                            Manifest.permission.ACCESS_FINE_LOCATION
                                        ) == PackageManager.PERMISSION_GRANTED
                                    ) {
                                        fusedLocationClient.lastLocation.addOnSuccessListener { location ->
                                            if (location != null) {
                                                val originLat = location.latitude
                                                val originLon = location.longitude
                                                val uri = Uri.parse(
                                                    "https://www.google.com/maps/dir/?api=1&origin=$originLat,$originLon&destination=$lat,$lon&travelmode=driving"
                                                )
                                                val intent = Intent(Intent.ACTION_VIEW, uri)
                                                intent.setPackage("com.google.android.apps.maps")
                                                context.startActivity(intent)
                                            } else {
                                                Toast.makeText(
                                                    context,
                                                    "No se pudo obtener tu ubicación actual",
                                                    Toast.LENGTH_SHORT
                                                ).show()
                                            }
                                        }
                                    } else {
                                        Toast.makeText(
                                            context,
                                            "Permiso de ubicación no concedido",
                                            Toast.LENGTH_LONG
                                        ).show()
                                    }
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}