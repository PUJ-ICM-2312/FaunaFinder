package com.example.faunafinder.ui.perfil


import android.net.Uri
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.rememberImagePainter
import com.example.faunafinder.R
import com.example.faunafinder.navigation.BottomNavigationBar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage

import androidx.activity.compose.rememberLauncherForActivityResult

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PerfilScreen(navController: NavController) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Mi Perfil") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Atrás")
                    }
                },
                actions = {
                    IconButton(onClick = { /* Acción de editar */ }) {
                        Icon(Icons.Filled.Edit, contentDescription = "Editar")
                    }
                }
            )
        },
        bottomBar = { BottomNavigationBar(navController) }
    ) { paddingValues ->
        ProfileContent(navController, Modifier.padding(paddingValues))
    }
}

@Composable
private fun ProfileContent(navController: NavController, modifier: Modifier = Modifier) {
    val auth = FirebaseAuth.getInstance()
    val database = Firebase.database.reference
    val storage = FirebaseStorage.getInstance()
    val context = LocalContext.current

    val userId = auth.currentUser?.uid ?: run {
        Toast.makeText(context, "No hay usuario autenticado", Toast.LENGTH_SHORT).show()
        navController.navigate("login") { popUpTo(0) }
        return
    }

    var userName by remember { mutableStateOf("") }
    var userBio by remember { mutableStateOf("") }
    var userInterests by remember { mutableStateOf("") }
    var userEmail by remember { mutableStateOf("") }
    var userProfileImageUri by remember { mutableStateOf<Uri?>(null) }
    var newPassword by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(true) }
    var isEditing by remember { mutableStateOf(false) }

    // Cargar datos del perfil con DisposableEffect
    DisposableEffect(userId) {
        val profileRef = database.child("users").child(userId)
        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    userName = snapshot.child("name").getValue(String::class.java) ?: ""
                    userBio = snapshot.child("bio").getValue(String::class.java) ?: ""
                    userInterests = snapshot.child("interests").getValue(String::class.java) ?: ""
                    userEmail = snapshot.child("email").getValue(String::class.java) ?: ""
                    val profileImage = snapshot.child("profileImage").getValue(String::class.java)
                    userProfileImageUri = if (profileImage != null) Uri.parse(profileImage) else null
                }
                isLoading = false
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(context, "Error al cargar perfil", Toast.LENGTH_SHORT).show()
                isLoading = false
            }
        }

        profileRef.addValueEventListener(listener)

        onDispose {
            profileRef.removeEventListener(listener)
        }
    }

    val imagePickerLauncher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        uri?.let {
            userProfileImageUri = it
            // Subir la imagen a Firebase Storage y obtener la URL
            val storageRef = storage.reference.child("profile_images/${userId}.jpg")
            storageRef.putFile(uri)
                .addOnSuccessListener {
                    storageRef.downloadUrl.addOnSuccessListener { downloadUrl ->
                        database.child("users").child(userId).child("profileImage").setValue(downloadUrl.toString())
                    }
                }
                .addOnFailureListener {
                    Toast.makeText(context, "Error al subir la imagen", Toast.LENGTH_SHORT).show()
                }
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        if (isLoading) {
            CircularProgressIndicator(modifier = Modifier.padding(32.dp))
        } else {
            Spacer(modifier = Modifier.height(16.dp))

            // Foto de perfil
            Image(
                painter = rememberImagePainter(
                    userProfileImageUri ?: R.drawable.default_profile_image // Imagen predeterminada
                ),
                contentDescription = "Foto de perfil",
                modifier = Modifier
                    .size(120.dp)
                    .clip(CircleShape)
                    .clickable { imagePickerLauncher.launch("image/*") }
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Nombre de usuario
            if (isEditing) {
                OutlinedTextField(
                    value = userName,
                    onValueChange = { userName = it },
                    label = { Text("Nombre completo") },
                    modifier = Modifier.fillMaxWidth()
                )
            } else {
                Text(
                    text = userName,
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold
                )
            }

            // Email
            Text(
                text = userEmail,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                modifier = Modifier.padding(top = 4.dp)
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Sección de biografía
            ProfileSection(
                title = "Biografía",
                content = userBio,
                isEditing = isEditing,
                onValueChange = { userBio = it }
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Sección de intereses
            ProfileSection(
                title = "Intereses",
                content = userInterests,
                isEditing = isEditing,
                onValueChange = { userInterests = it }
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Botón para cambiar contraseña (con confirmación de contraseña)
            if (isEditing) {
                OutlinedTextField(
                    value = newPassword,
                    onValueChange = { newPassword = it },
                    label = { Text("Nueva Contraseña") },
                    modifier = Modifier.fillMaxWidth(),
                    visualTransformation = PasswordVisualTransformation(),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = confirmPassword,
                    onValueChange = { confirmPassword = it },
                    label = { Text("Confirmar Contraseña") },
                    modifier = Modifier.fillMaxWidth(),
                    visualTransformation = PasswordVisualTransformation(),
                    singleLine = true
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Botones de acción
            if (isEditing) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Button(
                        onClick = { isEditing = false },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.errorContainer
                        )
                    ) {
                        Text("Cancelar")
                    }

                    Button(
                        onClick = {
                            if (userName.isBlank()) {
                                Toast.makeText(context, "El nombre no puede estar vacío", Toast.LENGTH_SHORT).show()
                                return@Button
                            }

                            if (newPassword.isNotBlank() && newPassword != confirmPassword) {
                                Toast.makeText(context, "Las contraseñas no coinciden", Toast.LENGTH_SHORT).show()
                                return@Button
                            }

                            val updates = hashMapOf<String, Any>(
                                "name" to userName,
                                "bio" to userBio,
                                "interests" to userInterests
                            )

                            database.child("users").child(userId)
                                .updateChildren(updates)
                                .addOnSuccessListener {
                                    // Cambiar contraseña si se ha ingresado una nueva y coincidente
                                    if (newPassword.isNotBlank()) {
                                        auth.currentUser?.updatePassword(newPassword)
                                            ?.addOnCompleteListener { task ->
                                                if (task.isSuccessful) {
                                                    Toast.makeText(context, "Contraseña actualizada", Toast.LENGTH_SHORT).show()
                                                } else {
                                                    Toast.makeText(context, "Error al cambiar la contraseña", Toast.LENGTH_SHORT).show()
                                                }
                                            }
                                    }
                                    Toast.makeText(context, "Perfil actualizado", Toast.LENGTH_SHORT).show()
                                    isEditing = false
                                }
                                .addOnFailureListener {
                                    Toast.makeText(context, "Error al guardar cambios", Toast.LENGTH_SHORT).show()
                                }
                        },
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Guardar")
                    }
                }
            } else {
                Button(
                    onClick = { isEditing = true },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Editar Perfil")
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Botón de cerrar sesión
            TextButton(
                onClick = {
                    auth.signOut()
                    navController.navigate("login") {
                        popUpTo(0)
                    }
                },
                colors = ButtonDefaults.textButtonColors(
                    contentColor = MaterialTheme.colorScheme.error
                )
            ) {
                Text("Cerrar Sesión")
            }
        }
    }
}

@Composable
private fun ProfileSection(
    title: String,
    content: String,
    isEditing: Boolean,
    onValueChange: (String) -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.Start
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )

        Spacer(modifier = Modifier.height(8.dp))

        if (isEditing) {
            OutlinedTextField(
                value = content,
                onValueChange = onValueChange,
                modifier = Modifier.fillMaxWidth(),
                singleLine = title != "Biografía",
                maxLines = if (title == "Biografía") 3 else 1
            )
        } else {
            Text(
                text = if (content.isNotBlank()) content else "No especificado",
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
            )
        }
    }
}
