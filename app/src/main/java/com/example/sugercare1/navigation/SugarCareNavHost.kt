package com.sugarcare.app.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.sugercare1.ui.theme.screens.HomeScreen
import com.sugarcare.app.ui.screens.*

sealed class Screen(val route: String) {
    object Welcome        : Screen("welcome")
    object SignIn         : Screen("sign_in")
    object SignUp         : Screen("sign_up")
    object HealthInfo     : Screen("health_info")
    object Home           : Screen("home")
    object Logs           : Screen("logs")
    object MealPlan       : Screen("meal_plan")
    object Medications    : Screen("medications")
    object WeeklyAnalytics: Screen("weekly_analytics")
    object Profile        : Screen("profile")
}

@Composable
fun SugarCareNavHost(
    navController: NavHostController = rememberNavController()
) {
    NavHost(
        navController  = navController,
        startDestination = Screen.Welcome.route
    ) {
        composable(Screen.Welcome.route) {
            WelcomeScreen(
                onSignIn = { navController.navigate(Screen.SignIn.route) },
                onSignUp = { navController.navigate(Screen.SignUp.route) }
            )
        }
        composable(Screen.SignIn.route) {
            SignInScreen(
                onSignInSuccess = { navController.navigate(Screen.Home.route) {
                    popUpTo(Screen.Welcome.route) { inclusive = true }
                }},
                onNavigateToSignUp = { navController.navigate(Screen.SignUp.route) }
            )
        }
        composable(Screen.SignUp.route) {
            SignUpScreen(
                onSignUpSuccess = { navController.navigate(Screen.HealthInfo.route) {
                    popUpTo(Screen.Welcome.route) { inclusive = true }
                }},
                onNavigateToSignIn = { navController.navigate(Screen.SignIn.route) }
            )
        }
        composable(Screen.HealthInfo.route) {
            HealthInfoScreen(
                onSaved = { navController.navigate(Screen.Home.route) {
                    popUpTo(Screen.HealthInfo.route) { inclusive = true }
                }}
            )
        }
        composable(Screen.Home.route) {
            HomeScreen(navController = navController)
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
            ProfileScreen(navController = navController)
        }
    }
}
