package com.example.faunafinder.ui.perfil

import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.faunafinder.navigation.BottomNavigationBar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PerfilScreen(navController: NavController) {
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
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
    var newPassword by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(true) }
    var isEditing by remember { mutableStateOf(false) }

    DisposableEffect(userId) {
        val profileRef = database.child("users").child(userId)
        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    userName = snapshot.child("name").getValue(String::class.java) ?: ""
                    userBio = snapshot.child("bio").getValue(String::class.java) ?: ""
                    userInterests = snapshot.child("interests").getValue(String::class.java) ?: ""
                    userEmail = snapshot.child("email").getValue(String::class.java) ?: ""
                }
                isLoading = false
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(context, "Error al cargar perfil", Toast.LENGTH_SHORT).show()
                isLoading = false
            }
        }

        profileRef.addValueEventListener(listener)
        onDispose { profileRef.removeEventListener(listener) }
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

            // Nombre
            if (isEditing) {
                ProfileSection(
                    title = "Nombre completo",
                    content = userName,
                    isEditing = true,
                    onValueChange = { userName = it }
                )
            } else {
                Box(
                    modifier = Modifier
                        .border(BorderStroke(3.dp, MaterialTheme.colorScheme.primary))
                        .background(MaterialTheme.colorScheme.surface, RoundedCornerShape(12.dp))
                        .padding(horizontal = 24.dp, vertical = 12.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = userName,
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Correo electrónico
            if (!isEditing) {
                Box(
                    modifier = Modifier
                        .border(BorderStroke(3.dp, MaterialTheme.colorScheme.primary))
                        .background(MaterialTheme.colorScheme.surface, RoundedCornerShape(12.dp))
                        .padding(horizontal = 24.dp, vertical = 12.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = userEmail,
                        fontSize = 18.sp,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f),
                        textAlign = TextAlign.Center
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            ProfileSection(
                title = "Biografía",
                content = userBio,
                isEditing = isEditing,
                onValueChange = { userBio = it }
            )

            Spacer(modifier = Modifier.height(16.dp))

            ProfileSection(
                title = "Intereses",
                content = userInterests,
                isEditing = isEditing,
                onValueChange = { userInterests = it }
            )

            Spacer(modifier = Modifier.height(24.dp))

            if (isEditing) {
                ProfileSection(
                    title = "Nueva Contraseña",
                    content = newPassword,
                    isEditing = true,
                    onValueChange = { newPassword = it },
                    isPassword = true
                )

                Spacer(modifier = Modifier.height(8.dp))

                ProfileSection(
                    title = "Confirmar Contraseña",
                    content = confirmPassword,
                    isEditing = true,
                    onValueChange = { confirmPassword = it },
                    isPassword = true
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            if (isEditing) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Button(
                        onClick = { isEditing = false },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primary
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
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF9CBB04)
                        )
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

            TextButton(
                onClick = {
                    auth.signOut()
                    navController.navigate("login") { popUpTo(0) }
                },
                colors = ButtonDefaults.textButtonColors(
                    contentColor = MaterialTheme.colorScheme.primary
                )
            ) {
                Text("Cerrar Sesión", fontWeight = FontWeight.SemiBold, fontSize = 18.sp)
            }
        }
    }
}

@Composable
private fun ProfileSection(
    title: String,
    content: String,
    isEditing: Boolean,
    onValueChange: (String) -> Unit,
    isPassword: Boolean = false
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
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.surface, RoundedCornerShape(8.dp)),
                singleLine = title != "Biografía",
                maxLines = if (title == "Biografía") 3 else 1,
                visualTransformation = if (isPassword) PasswordVisualTransformation() else VisualTransformation.None
            )
        } else {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(BorderStroke(3.dp, MaterialTheme.colorScheme.primary))
                    .background(MaterialTheme.colorScheme.surface, RoundedCornerShape(8.dp))
                    .padding(12.dp)
            ) {
                Text(
                    text = if (content.isNotBlank()) content else "No especificado",
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
    }
}
