package com.sugarcare.app.navigation
import com.example.sugercare.ui.theme.screens.HomeScreen
import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.sugercare.CompleteProfileScreen
import com.example.sugercare.ForgotPasswordCodeScreen
import com.example.sugercare.ForgotPasswordScreen
import com.example.sugercare.NotificationsScreen
import com.example.sugercare.crud.SugarTrackerScreen
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

@Composable
fun SugarCareNavHost(
    navController: NavHostController = rememberNavController()
) {
    val authViewModel: AuthViewModel = viewModel()
    NavHost(
        navController = navController,
        startDestination = Screen.Welcome.route
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
            SugarTrackerScreen()
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
            CompleteProfileScreen(navController = navController)
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
