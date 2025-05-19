package com.example.examen2nativas.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.examen2nativas.utils.FirebaseUtil
import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.Timestamp

data class Notification(
    val title: String = "",
    val body: String = "",
    val timestamp: Timestamp? = null
)

@Composable
fun NotificationHistoryScreen(userId: String) {
    var notifications by remember { mutableStateOf(listOf<Notification>()) }

    LaunchedEffect(Unit) {
        FirebaseMessaging.getInstance().token.addOnSuccessListener { token ->
            FirebaseUtil.db.collection("notifications")
                .whereEqualTo("toUserId", token)
                .get()
                .addOnSuccessListener { result ->
                    notifications = result.documents.mapNotNull { it.toObject(Notification::class.java) }
                }
        }
    }


    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text("Historial de Notificaciones", style = MaterialTheme.typography.headlineMedium)
        Spacer(modifier = Modifier.height(16.dp))

        LazyColumn {
            items(notifications) { notif ->
                Card(modifier = Modifier.fillMaxWidth().padding(4.dp)) {
                    Column(modifier = Modifier.padding(8.dp)) {
                        Text(text = notif.title, style = MaterialTheme.typography.titleMedium)
                        Text(text = notif.body)
                        Text(notif.timestamp?.toDate().toString(), style = MaterialTheme.typography.labelSmall)

                    }
                }
            }
        }
    }
}
