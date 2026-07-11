package com.sugarcare.app.navigation

import android.annotation.SuppressLint
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import com.example.sugercare.ui.theme.screens.HomeScreen
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.sugercare.app.SugarTrackerScreen
import com.example.sugercare.app.SugarViewModel
import com.example.sugercare.ui.theme.screens.CounterScreen
import com.example.sugercare.ui.theme.screens.ChatScreen
import com.example.sugercare.ui.theme.screens.EmergencyContactScreen
import com.example.sugercare.ui.theme.screens.ForgotPasswordScreen
import com.example.sugercare.ui.theme.screens.MealPlanScreen
import com.example.sugercare.ui.theme.screens.MedicationsScreen
import com.example.sugercare.ui.theme.screens.NotificationsScreen
import com.example.sugercare.ui.theme.screens.ProfileScreen
import com.example.sugercare.ui.theme.screens.SignInScreen
import com.example.sugercare.ui.theme.screens.SignUpScreen
import com.example.sugercare.viewModels.AuthState
import com.example.sugercare.viewModels.AuthViewModel
import com.example.sugercare.viewModels.ChatViewModel
import com.example.sugercare.viewModels.CounterViewModel
import com.sugarcare.app.ui.screens.*
import com.example.sugercare.viewModels.ProfileViewModel


sealed class Screen(val route: String) {
    object Splash           : Screen("splash")
    object Welcome          : Screen("welcome")
    object SignIn           : Screen("sign_in")
    object SignUp           : Screen("sign_up")
    object HealthInfo       : Screen("health_info")
    object Home             : Screen("home")
    object Logs             : Screen("logs")
    object MealPlan         : Screen("meal_plan")
    object Medications      : Screen("medications")
    object WeeklyAnalytics  : Screen("weekly_analytics")
    object Profile          : Screen("profile")
    object Notifications    : Screen("notifications")
    object ForgotPassword   : Screen("forgot_password")
    object ChatScreen       : Screen("chat_screen")
    object CounterScreen    : Screen("countdown_timer_screen")
    object EmergencyContact : Screen("emergency_contact")
}

@RequiresApi(Build.VERSION_CODES.VANILLA_ICE_CREAM)
@SuppressLint("StateFlowValueCalledInComposition")
@Composable
fun SugarCareNavHost(
    navController: NavHostController = rememberNavController(),
    authViewModel: AuthViewModel,
    profileViewModel: ProfileViewModel,
    chatViewModel: ChatViewModel,
    counterViewModel: CounterViewModel,
    sugarViewModel: SugarViewModel
) {
//  ———— TO check if user is logged in or not ————————————————

    val authState = authViewModel.authState.collectAsState()
    val rememberMe = authViewModel.rememberMe.collectAsState()

    Log.d("AUTH", "rememberMe = ${rememberMe.value}")
    Log.d("AUTH", "authState = ${authState.value}")

    LaunchedEffect(authState.value, ) {
        if (authState.value is AuthState.Authenticated && rememberMe.value) {
            navController.navigate(Screen.Home.route) {
                popUpTo(Screen.Welcome.route) { inclusive = true }
            }
        }

    }


    val startDest = Screen.Splash.route

    NavHost(
        navController = navController,
        startDestination = startDest
    ) {
        composable(Screen.Splash.route) {
//            android.window.SplashScreen(navController)
            SplashScreen(navController)
        }
        composable(Screen.Welcome.route) {
            WelcomeScreen(
                onSignIn = {
                    authViewModel.clearFields()
                    authViewModel.resetAuthState()
                    navController.navigate(Screen.SignIn.route)
                },
                onSignUp = {
                    authViewModel.clearFields()
                    authViewModel.resetAuthState()
                    navController.navigate(Screen.SignUp.route)
                }
            )
        }

        composable(Screen.SignIn.route) {
            SignInScreen(
                onSignInSuccess    = {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Welcome.route) { inclusive = true }
                    }
                },
                onNavigateToSignUp = {
                    authViewModel.clearFields()
                    navController.navigate(Screen.SignUp.route)
                },
                onForgotPassword   = { navController.navigate(Screen.ForgotPassword.route) },
                authViewModel      = authViewModel
            )
        }

        composable(Screen.SignUp.route) {
            SignUpScreen(
                onSignUpSuccess    = {
                    navController.navigate(Screen.HealthInfo.route) {
                        popUpTo(Screen.Welcome.route) { inclusive = true }
                    }
                },
                onNavigateToSignIn = {
                    authViewModel.clearFields()
                    navController.navigate(Screen.SignIn.route)
                },
                authViewModel = authViewModel
            )
        }

        composable(Screen.HealthInfo.route) {
            HealthInfoScreen(
                onSaved = {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.HealthInfo.route) { inclusive = true }
                    }
                }
            )
        }

        composable(Screen.Home.route) {
            HomeScreen(navController = navController,
                profileViewModel = profileViewModel,
                counterViewModel = counterViewModel)
        }

        composable(Screen.Logs.route) {
            SugarTrackerScreen(navController,sugarViewModel)
        }

        composable(Screen.MealPlan.route) {
            MealPlanScreen(navController = navController)
        }

        composable(Screen.Medications.route) {
            MedicationsScreen(navController = navController)
        }

        composable(Screen.WeeklyAnalytics.route) {
            WeeklyAnalyticsScreen(navController = navController)
        }

        composable(Screen.Profile.route) {
            ProfileScreen(
                navController    = navController,
                authViewModel    = authViewModel,
                profileViewModel = profileViewModel,
                onSaveSuccess    = {}
            )
        }

        composable(Screen.Notifications.route) {
            NotificationsScreen(navController = navController)
        }

        composable(Screen.ForgotPassword.route) {
            ForgotPasswordScreen(
                navController = navController,
                authViewModel = authViewModel
            )
        }

        composable(Screen.ChatScreen.route) {
            ChatScreen(chatViewModel = chatViewModel)
        }

        composable(Screen.CounterScreen.route) {
            CounterScreen()
        }

        composable(Screen.EmergencyContact.route) {
            EmergencyContactScreen(navController = navController)
        }
    }
}
