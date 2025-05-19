package com.example.examen2nativas.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.examen2nativas.viewmodel.UserViewModel

@Composable
fun UserScreen(navController: NavController, viewModel: UserViewModel) {
    val user = viewModel.currentUser.collectAsState().value

    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Perfil del Usuario", style = MaterialTheme.typography.headlineMedium)
        Spacer(modifier = Modifier.height(16.dp))

        Text("Nombre: ${user?.name}")
        Text("Correo: ${user?.email}")
        Text("Rol: ${user?.role}")
        Spacer(modifier = Modifier.height(16.dp))

        Button(onClick = { navController.navigate("edit_profile") }) {
            Text("Editar Perfil")
        }

        Spacer(modifier = Modifier.height(8.dp))

        Button(onClick = { navController.navigate("notification_history/${user?.uid}") }) {
            Text("Ver Historial de Notificaciones")
        }

        Spacer(modifier = Modifier.height(8.dp))

        Button(onClick = {
            viewModel.logout()
            navController.navigate("login") {
                popUpTo("user") { inclusive = true }
            }
        }) {
            Text("Cerrar sesión")
        }
    }
}
