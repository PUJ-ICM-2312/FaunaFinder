package com.example.faunafinder.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.faunafinder.R
import com.example.faunafinder.navigation.Screen

@Composable
fun HomeScreen(navController: NavController) {
    Column(
        modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Image(
            painter = painterResource(id = R.drawable.logo),
            contentDescription = "Logo",
            modifier = Modifier.size(150.dp)
        )

        Spacer(modifier = Modifier.height(20.dp))

        Button(onClick = { navController.navigate(Screen.Login.route) },
               modifier = Modifier.width(220.dp).height(55.dp)
            ) {
            Text("Iniciar Sesión", fontSize = 18.sp)
        }

        TextButton(onClick = { navController.navigate(Screen.Register.route) }) {
            Text("Registrarse", fontWeight = FontWeight.Bold, fontSize = 22.sp)
        }
    }
}


