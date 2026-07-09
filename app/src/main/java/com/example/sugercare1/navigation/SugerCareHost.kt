package com.example.sugercare1.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.sugercare1.ui.theme.screens.SignInScreen
import com.sugarcare.app.ui.screens.*

@Composable
fun SugarCareNavHost(
    navController: NavHostController = rememberNavController(),
    isDark: Boolean,           
    onToggleTheme: () -> Unit  
) {
    NavHost(
        navController    = navController,
        startDestination = Screen.Splash.route  
    ) {

        composable(Screen.Splash.route) {
            SplashScreen(navController = navController)
        }

        composable(Screen.Welcome.route) {
            WelcomeScreen(
                onSignIn = { navController.navigate(Screen.SignIn.route) },
                onSignUp = { navController.navigate(Screen.SignUp.route) }
            )
        }

        composable(Screen.SignIn.route) {
            SignInScreen(
                onSignInSuccess    = {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Welcome.route) { inclusive = true }
                    }
                },
                onNavigateToSignUp = { navController.navigate(Screen.SignUp.route) },
                onForgotPassword   = { navController.navigate(Screen.ForgotPassword.route) }
            )
        }

        composable(Screen.SignUp.route) {
            SignUpScreen(
                onSignUpSuccess    = {
                    navController.navigate(Screen.HealthInfo.route) {
                        popUpTo(Screen.Welcome.route) { inclusive = true }
                    }
                },
                onNavigateToSignIn = { navController.navigate(Screen.SignIn.route) }
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

        composable(Screen.Home.route) { HomeScreen(navController = navController) }
        composable(Screen.Logs.route) { LogsScreen(navController = navController) }
        composable(Screen.MealPlan.route) { MealPlanScreen(navController = navController) }
        composable(Screen.Medications.route) { MedicationsScreen(navController = navController) }
        composable(Screen.WeeklyAnalytics.route) { WeeklyAnalyticsScreen(navController = navController) }
        
        composable(Screen.Profile.route) { 
            CompleteProfileScreen(
                navController = navController,
                isDark = isDark,
                onToggleTheme = onToggleTheme
            ) 
        }
        
        composable(Screen.Notifications.route) { NotificationsScreen(navController = navController) }
        composable(Screen.ForgotPassword.route) { ForgotPasswordScreen(navController = navController) }
        composable(Screen.ForgotPasswordCode.route) { ForgotPasswordCodeScreen(navController = navController) }
    }
}
