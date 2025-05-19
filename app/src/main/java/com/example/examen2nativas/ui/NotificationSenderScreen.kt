package com.example.examen2nativas.ui

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
import com.example.examen2nativas.model.User
import com.example.examen2nativas.utils.FirebaseUtil
import com.google.firebase.functions.FirebaseFunctions

@Composable
fun NotificationSenderScreen(currentUser: User, onBack: () -> Unit) {
    val context = LocalContext.current
    var title by remember { mutableStateOf("") }
    var body by remember { mutableStateOf("") }
    var users by remember { mutableStateOf(listOf<User>()) }
    var selectedUser by remember { mutableStateOf<User?>(null) }

    val functions = FirebaseFunctions.getInstance()

    LaunchedEffect(Unit) {
        FirebaseUtil.db.collection("users").get()
            .addOnSuccessListener { result ->
                users = result.documents.mapNotNull { it.toObject(User::class.java) }
            }
    }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text("Enviar Notificación", style = MaterialTheme.typography.headlineMedium)
        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(value = title, onValueChange = { title = it }, label = { Text("Título") })
        OutlinedTextField(value = body, onValueChange = { body = it }, label = { Text("Mensaje") })

        Spacer(modifier = Modifier.height(8.dp))
        Text("Selecciona destinatario:")

        LazyColumn {
            items(users) { user ->
                Row(verticalAlignment = Alignment.CenterVertically) {
                    RadioButton(
                        selected = selectedUser == user,
                        onClick = { selectedUser = user }
                    )
                    Text(user.name)
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))
        Button(onClick = {
            if (title.isBlank() || body.isBlank()) {
                Toast.makeText(context, "Completa el título y mensaje", Toast.LENGTH_SHORT).show()
                return@Button
            }

            val token = selectedUser?.fcmToken
            if (token.isNullOrBlank()) {
                Toast.makeText(context, "Selecciona un usuario con token válido", Toast.LENGTH_SHORT).show()
                return@Button
            }

            val data = mapOf(
                "title" to title,
                "body" to body,
                "token" to token
            )

            functions.getHttpsCallable("sendNotification").call(data)
                .addOnSuccessListener {
                    Toast.makeText(context, "Notificación enviada", Toast.LENGTH_SHORT).show()
                    title = ""
                    body = ""
                    selectedUser = null
                }
                .addOnFailureListener {
                    Toast.makeText(context, "Error: ${it.message}", Toast.LENGTH_LONG).show()
                }
        }) {
            Text("Enviar Notificación")
        }

        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = onBack) {
            Text("Volver")
        }
    }
}
