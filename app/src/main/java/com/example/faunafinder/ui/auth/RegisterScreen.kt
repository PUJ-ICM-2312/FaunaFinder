package com.example.faunafinder.ui.screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.faunafinder.navigation.Screen
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.example.faunafinder.ui.components.FloatingLabel

@Composable
fun RegisterScreen(navController: NavController) {
    val context = LocalContext.current
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var passwordVisible by remember { mutableStateOf(false) }

    val emailFocus = remember { mutableStateOf(false) }
    val passwordFocus = remember { mutableStateOf(false) }

    val auth = FirebaseAuth.getInstance()
    val db = FirebaseFirestore.getInstance()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text("Regístrate en Fauna Finder!", style = MaterialTheme.typography.headlineMedium, color = MaterialTheme.colorScheme.onBackground)

        Spacer(modifier = Modifier.height(20.dp))

        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = {
                FloatingLabel("Correo", isActive = emailFocus.value || email.isNotBlank())
            },
            singleLine = true,
            modifier = Modifier
                .fillMaxWidth()
                .onFocusChanged { emailFocus.value = it.isFocused },
            shape = RectangleShape,
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = MaterialTheme.colorScheme.surface,
                unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                focusedTextColor = MaterialTheme.colorScheme.onSurface,
                unfocusedTextColor = MaterialTheme.colorScheme.onSurface,
                focusedBorderColor = MaterialTheme.colorScheme.primary,
                unfocusedBorderColor = MaterialTheme.colorScheme.outline
            )
        )

        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = {
                FloatingLabel("Contraseña", isActive = passwordFocus.value || password.isNotBlank())
            },
            singleLine = true,
            visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
            trailingIcon = {
                val icon = if (passwordVisible) Icons.Filled.VisibilityOff else Icons.Filled.Visibility
                val description = if (passwordVisible) "Ocultar contraseña" else "Mostrar contraseña"

                IconButton(onClick = { passwordVisible = !passwordVisible }) {
                    Icon(imageVector = icon, contentDescription = description, tint = MaterialTheme.colorScheme.onSurface)
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .onFocusChanged { passwordFocus.value = it.isFocused },
            shape = RectangleShape,
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = MaterialTheme.colorScheme.surface,
                unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                focusedTextColor = MaterialTheme.colorScheme.onSurface,
                unfocusedTextColor = MaterialTheme.colorScheme.onSurface,
                focusedBorderColor = MaterialTheme.colorScheme.primary,
                unfocusedBorderColor = MaterialTheme.colorScheme.outline
            )
        )

        Spacer(modifier = Modifier.height(10.dp))

        Button(
            onClick = {
                if (email.isBlank() || password.isBlank()) {
                    errorMessage = "Todos los campos son obligatorios"
                    return@Button
                }

                isLoading = true
                errorMessage = null

                auth.createUserWithEmailAndPassword(email.trim(), password.trim())
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            val user = auth.currentUser
                            user?.let {
                                val userData = hashMapOf(
                                    "email" to it.email,
                                    "uid" to it.uid
                                )

                                db.collection("users")
                                    .document(it.uid)
                                    .set(userData)
                                    .addOnSuccessListener {
                                        isLoading = false
                                        navController.navigate(Screen.Feed.route) {
                                            popUpTo(Screen.Register.route) { inclusive = true }
                                        }
                                    }
                                    .addOnFailureListener { e ->
                                        isLoading = false
                                        errorMessage = "Error guardando datos: ${e.message}"
                                    }
                            }
                        } else {
                            isLoading = false
                            errorMessage = task.exception?.message ?: "Error al crear cuenta"
                        }
                    }
            },
            enabled = !isLoading,
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary
            )
        ) {
            Text(text = if (isLoading) "Registrando..." else "Crear Cuenta", fontWeight = FontWeight.Bold, fontSize = 20.sp)
        }

        errorMessage?.let { message ->
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = message, color = MaterialTheme.colorScheme.error)
        }

        Spacer(modifier = Modifier.height(10.dp))

        TextButton(
            onClick = { navController.navigate(Screen.Login.route) },
            colors = ButtonDefaults.textButtonColors(
                contentColor = MaterialTheme.colorScheme.primary
            )
        ) {
            Text("¿Ya tienes cuenta? Inicia sesión", fontWeight = FontWeight.Bold, fontSize = 20.sp)
        }
    }
}
