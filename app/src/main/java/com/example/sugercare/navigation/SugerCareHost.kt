package com.sugarcare.app.navigation

import android.annotation.SuppressLint
import android.util.Log
import com.example.sugercare.ui.theme.screens.HomeScreen
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.sugercare1.CompleteProfileScreen
import com.example.sugercare1.ForgotPasswordCodeScreen
import com.example.sugercare1.ForgotPasswordScreen
import com.example.sugercare1.NotificationsScreen
import com.example.sugercare.viewModels.AuthState
import com.example.sugercare.viewModels.AuthViewModel
import com.sugarcare.app.ui.screens.*

sealed class Screen(val route: String) {
    object Welcome : Screen("welcome")
    object SignIn : Screen("sign_in")
    object SignUp : Screen("sign_up")
    object HealthInfo : Screen("health_info")
    object Home : Screen("home")
    object Logs : Screen("logs")
    object MealPlan : Screen("meal_plan")
    object Medications : Screen("medications")
    object WeeklyAnalytics : Screen("weekly_analytics")
    object Profile : Screen("profile")
    object Notifications : Screen("notifications")
    object ForgotPassword : Screen("forgot_password")
    object ForgotPasswordCode : Screen("forgot_password_code")
}

@SuppressLint("StateFlowValueCalledInComposition")
@Composable
fun SugarCareNavHost(
    navController: NavHostController = rememberNavController(),
    authViewModel: AuthViewModel
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


//    TODO -> We need to add splash screen instead
    val startDest = Screen.Welcome.route

    NavHost(
        navController = navController,
        startDestination = startDest
    ) {
        composable(Screen.Welcome.route) {
            WelcomeScreen(
                onSignIn = {
                    authViewModel.resetAuthState()
                    navController.navigate(Screen.SignIn.route)
                },
                onSignUp = {
                    authViewModel.resetAuthState()
                    navController.navigate(Screen.SignUp.route)
                }
            )
        }

        composable(Screen.SignIn.route) {
            SignInScreen(
                onSignInSuccess = {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Welcome.route) { inclusive = true }
                    }
                },
                onNavigateToSignUp = { navController.navigate(Screen.SignUp.route) },
                onForgotPassword = { navController.navigate(Screen.ForgotPassword.route) },
                authViewModel = authViewModel
            )
        }

        composable(Screen.SignUp.route) {
            SignUpScreen(
                onSignUpSuccess = {
                    navController.navigate(Screen.HealthInfo.route) {
                        popUpTo(Screen.Welcome.route) { inclusive = true }
                    }
                },
                onNavigateToSignIn = { navController.navigate(Screen.SignIn.route) },
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
            HomeScreen(navController = navController)
        }
        composable(Screen.Logs.route) {
            LogsScreen(navController = navController)
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
            CompleteProfileScreen(navController = navController, authViewModel = authViewModel)
        }
        composable(Screen.Notifications.route) {
            NotificationsScreen(navController = navController)
        }
        composable(Screen.ForgotPassword.route) {
            ForgotPasswordScreen(navController = navController)
        }
        composable(Screen.ForgotPasswordCode.route) {
            ForgotPasswordCodeScreen(navController = navController)
        }
    }
}
