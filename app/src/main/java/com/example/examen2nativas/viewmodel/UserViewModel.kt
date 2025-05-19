package com.example.examen2nativas.viewmodel

import android.content.Context
import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import com.example.examen2nativas.model.User
import com.example.examen2nativas.utils.FirebaseUtil
import com.google.firebase.messaging.FirebaseMessaging
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class UserViewModel : ViewModel() {

    private val _currentUser = MutableStateFlow<User?>(null)
    val currentUser: StateFlow<User?> = _currentUser

    val loginError = mutableStateOf<String?>(null)
    val registerError = mutableStateOf<String?>(null)

    private val masterPassword = "claveSecreta123"

    fun login(email: String, password: String, context: Context, navController: NavController) {
        FirebaseUtil.auth.signInWithEmailAndPassword(email, password)
            .addOnSuccessListener {
                loadUser(
                    onSuccess = {
                        val user = _currentUser.value
                        if (user != null) {
                            Log.d("LOGIN_FLOW", "Navegando como ${user.role}")
                            navController.navigate(if (user.role == "admin") "admin" else "user") {
                                popUpTo("login") { inclusive = true }
                            }
                        } else {
                            Log.e("LOGIN_FLOW", "Usuario nulo luego de carga")
                        }
                    },
                    onError = {
                        loginError.value = it
                    }
                )
            }
            .addOnFailureListener { loginError.value = it.message }
    }


    fun register(
        name: String,
        email: String,
        password: String,
        isAdmin: Boolean,
        masterPass: String,
        context: Context,
        navController: NavController
    ) {
        if (isAdmin && masterPass != masterPassword) {
            registerError.value = "Contraseña maestra incorrecta"
            return
        }

        FirebaseUtil.auth.createUserWithEmailAndPassword(email, password)
            .addOnSuccessListener {
                val uid = FirebaseUtil.auth.currentUser?.uid ?: return@addOnSuccessListener
                FirebaseMessaging.getInstance().token.addOnSuccessListener { token ->
                    val newUser = User(
                        uid = uid,
                        name = name,
                        email = email,
                        role = if (isAdmin) "admin" else "normal",
                        fcmToken = token
                    )
                    FirebaseUtil.db.collection("users").document(uid).set(newUser)
                        .addOnSuccessListener {
                            _currentUser.value = newUser
                            navController.navigate("login") {
                                popUpTo("register") { inclusive = true }
                            }
                        }
                        .addOnFailureListener { registerError.value = it.message }
                }
            }
            .addOnFailureListener { registerError.value = it.message }
    }

    fun loadUser(onSuccess: () -> Unit = {}, onError: (String) -> Unit = {}) {
        val uid = FirebaseUtil.auth.currentUser?.uid ?: return
        Log.d("LOAD_USER", "Intentando cargar UID: $uid")

        FirebaseUtil.db.collection("users").document(uid).get()
            .addOnSuccessListener { doc ->
                Log.d("LOAD_USER", "Documento obtenido: ${doc.data}")

                doc.toObject(User::class.java)?.let {
                    _currentUser.value = it
                    Log.d("LOAD_USER", "Usuario cargado correctamente: ${it.email}, rol=${it.role}")
                    updateFcmTokenIfNeeded(it)
                    onSuccess()
                } ?: onError("Usuario no encontrado o mal formateado")
            }
            .addOnFailureListener { onError(it.message ?: "Error al obtener datos") }
    }


    private fun updateFcmTokenIfNeeded(user: User) {
        FirebaseMessaging.getInstance().token.addOnSuccessListener { newToken ->
            if (user.fcmToken != newToken) {
                FirebaseUtil.db.collection("users").document(user.uid)
                    .update("fcmToken", newToken)
            }
        }
    }

    fun updateProfile(name: String, photoUrl: String, onSuccess: () -> Unit) {
        val uid = FirebaseUtil.auth.currentUser?.uid ?: return
        val updates = mapOf("name" to name, "photoUrl" to photoUrl)
        FirebaseUtil.db.collection("users").document(uid)
            .update(updates)
            .addOnSuccessListener {
                _currentUser.value = _currentUser.value?.copy(name = name, photoUrl = photoUrl)
                onSuccess()
            }
    }

    fun logout() {
        FirebaseUtil.auth.signOut()
        _currentUser.value = null
    }
}
