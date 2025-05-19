package com.example.examen2nativas.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.examen2nativas.viewmodel.UserViewModel

@Composable
fun UserProfileScreen(navController: NavController, viewModel: UserViewModel) {
    val user by viewModel.currentUser.collectAsState()
    var name by remember { mutableStateOf(user?.name ?: "") }
    var photoUrl by remember { mutableStateOf(user?.photoUrl ?: "") }
    var message by remember { mutableStateOf("") }

    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Editar Perfil", style = MaterialTheme.typography.headlineMedium)
        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(value = name, onValueChange = { name = it }, label = { Text("Nombre") })
        OutlinedTextField(value = photoUrl, onValueChange = { photoUrl = it }, label = { Text("URL de Foto (opcional)") })

        Spacer(modifier = Modifier.height(16.dp))

        Button(onClick = {
            viewModel.updateProfile(name, photoUrl) {
                message = "Perfil actualizado correctamente"
            }
        }) {
            Text("Guardar Cambios")
        }

        Spacer(modifier = Modifier.height(8.dp))
        TextButton(onClick = { navController.popBackStack() }) {
            Text("Volver")
        }

        if (message.isNotEmpty()) {
            Spacer(modifier = Modifier.height(8.dp))
            Text(message, color = MaterialTheme.colorScheme.primary)
        }
    }
}
