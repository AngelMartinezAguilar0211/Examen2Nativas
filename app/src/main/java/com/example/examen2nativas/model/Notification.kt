package com.example.examen2nativas.model

import com.google.firebase.Timestamp

data class Notification(
    val title: String = "",
    val body: String = "",
    val timestamp: Timestamp? = null,
    val toUserId: String = ""
)
