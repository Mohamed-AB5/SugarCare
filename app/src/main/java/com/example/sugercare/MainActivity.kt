package com.sugarcare.app


import android.content.Intent
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.navigation.compose.rememberNavController
import com.sugarcare.app.navigation.Screen
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.sugercare.viewModels.AuthViewModel
import com.sugarcare.app.navigation.SugarCareNavHost
import com.sugarcare.app.ui.theme.SugarCareTheme

class MainActivity : ComponentActivity() {
    private var pendingNavigationRoute by mutableStateOf<String?>(null)
    companion object {
        var openedFromNotification = false
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        pendingNavigationRoute = resolveNotificationRoute(intent)
        openedFromNotification = pendingNavigationRoute != null
        enableEdgeToEdge()
        setContent {
            SugarCareTheme {
                Surface(modifier = Modifier.fillMaxSize()) {

                    val authViewModel: AuthViewModel = viewModel()

                    val navController = rememberNavController()

                    SugarCareNavHost(
                        navController = navController,
                        authViewModel = authViewModel
                    )

                    LaunchedEffect(pendingNavigationRoute) {

                        val route = pendingNavigationRoute ?: return@LaunchedEffect

                        navController.navigate(route) {
                            launchSingleTop = true
                        }

                        pendingNavigationRoute = null
                    }
                }
            }
        }
    }
    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        pendingNavigationRoute = resolveNotificationRoute(intent)
    }

    private fun resolveNotificationRoute(intent: Intent): String? {
        return when (intent.getStringExtra("open_screen")) {
            "medications" -> Screen.Medications.route
            else -> null
        }
    }
}
