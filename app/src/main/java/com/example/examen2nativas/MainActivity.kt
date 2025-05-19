package com.example.examen2nativas

import android.os.Bundle
import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.collectAsState
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.*
import androidx.navigation.navArgument
import com.example.examen2nativas.ui.*
import com.example.examen2nativas.ui.theme.Examen2NativasTheme
import com.example.examen2nativas.viewmodel.UserViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            val permissionCheck = ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS)
            if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
                registerForActivityResult(ActivityResultContracts.RequestPermission()) {}.launch(
                    Manifest.permission.POST_NOTIFICATIONS
                )
            }
        }

        setContent {
            Examen2NativasTheme {
                val navController = rememberNavController()
                val userViewModel: UserViewModel = viewModel()

                NavHost(navController = navController, startDestination = "login") {
                    composable("login") {
                        LoginScreen(navController, userViewModel)
                    }
                    composable("register") {
                        RegisterScreen(navController, userViewModel)
                    }
                    composable("user") {
                        UserScreen(navController, userViewModel)
                    }
                    composable("admin") {
                        AdminScreen(navController, userViewModel)
                    }
                    composable("edit_profile") {
                        UserProfileScreen(navController, userViewModel)
                    }
                    composable(
                        "notification_history/{userId}",
                        arguments = listOf(navArgument("userId") { type = NavType.StringType })
                    ) { backStackEntry ->
                        val userId = backStackEntry.arguments?.getString("userId") ?: ""
                        NotificationHistoryScreen(userId)
                    }
                    composable("send_notification") {
                        val user = userViewModel.currentUser.collectAsState().value
                        if (user != null) {
                            NotificationSenderScreen(currentUser = user) {
                                navController.popBackStack()
                            }
                        }
                    }
                }
            }
        }
    }
}
