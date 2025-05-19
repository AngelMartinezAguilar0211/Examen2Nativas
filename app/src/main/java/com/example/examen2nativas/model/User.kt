package com.example.examen2nativas.model

data class User(
    val uid: String = "",
    val name: String = "",
    val email: String = "",
    val role: String = "normal",
    val photoUrl: String = "",
    val fcmToken: String = ""
)
