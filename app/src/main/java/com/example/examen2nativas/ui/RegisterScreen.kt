package com.example.examen2nativas.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.examen2nativas.viewmodel.UserViewModel

@Composable
fun RegisterScreen(navController: NavController, viewModel: UserViewModel) {
    val context = LocalContext.current
    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var isAdmin by remember { mutableStateOf(false) }
    var masterPassword by remember { mutableStateOf("") }
    val registerError = viewModel.registerError.value

    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text("Registro", style = MaterialTheme.typography.headlineMedium)
        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(value = name, onValueChange = { name = it }, label = { Text("Nombre") })
        OutlinedTextField(value = email, onValueChange = { email = it }, label = { Text("Correo") })
        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Contraseña") },
            visualTransformation = PasswordVisualTransformation()
        )

        Row(verticalAlignment = Alignment.CenterVertically) {
            Checkbox(checked = isAdmin, onCheckedChange = { isAdmin = it })
            Text("¿Administrador?")
        }

        if (isAdmin) {
            OutlinedTextField(
                value = masterPassword,
                onValueChange = { masterPassword = it },
                label = { Text("Contraseña maestra") },
                visualTransformation = PasswordVisualTransformation()
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(onClick = {
            viewModel.register(
                name,
                email,
                password,
                isAdmin,
                masterPassword,
                context,
                navController
            )
        }) {
            Text("Registrarse")
        }

        Spacer(modifier = Modifier.height(8.dp))

        TextButton(onClick = { navController.navigate("login") }) {
            Text("¿Ya tienes cuenta? Inicia sesión")
        }

        if (!registerError.isNullOrEmpty()) {
            Spacer(modifier = Modifier.height(8.dp))
            Text(registerError, color = MaterialTheme.colorScheme.error)
        }
    }
}
