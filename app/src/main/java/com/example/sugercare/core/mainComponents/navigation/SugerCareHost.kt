package com.sugarcare.app.navigation

import android.annotation.SuppressLint
import android.app.Application
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.sugercare.MainActivity.Companion.openedFromNotification
import com.example.sugercare.app.SugarTrackerScreen
import com.example.sugercare.core.features.auth.AuthDataStore
import com.example.sugercare.core.features.auth.presentation.AuthState
import com.example.sugercare.core.features.auth.presentation.AuthViewModel
import com.example.sugercare.core.features.auth.presentation.SignInScreen
import com.example.sugercare.core.features.auth.presentation.SignUpScreen
import com.example.sugercare.core.features.chatBot.presentation.ChatScreen
import com.example.sugercare.core.features.chatBot.presentation.ChatViewModel
import com.example.sugercare.core.features.counter.presentation.CounterScreen
import com.example.sugercare.core.features.counter.presentation.CounterViewModel
import com.example.sugercare.core.features.emergency.EmergencyContactScreen
import com.example.sugercare.core.features.glucoseLogs.presentation.GlucoseViewModel
import com.example.sugercare.core.features.home.HomeScreen
import com.example.sugercare.core.features.meals.presentation.MealPlanScreen
import com.example.sugercare.core.features.profile.presentation.ProfileViewModel
import com.example.sugercare.core.features.auth.presentation.ForgotPasswordScreen
import com.example.sugercare.core.features.profile.presentation.ProfileViewModelFactory
import com.example.sugercare.core.features.profile.presentation.screens.ProfileScreen
import com.example.sugercare.core.mainComponents.notifications.presentation.NotificationsScreen
import com.sugarcare.app.ui.screens.MedicationsScreen
import com.sugarcare.app.ui.screens.SplashScreen
import com.sugarcare.app.ui.screens.WeeklyAnalyticsScreen
import com.sugarcare.app.ui.screens.WelcomeScreen
import okhttp3.internal.platform.android.AndroidSocketAdapter.Companion.factory


sealed class Screen(val route: String) {
    object Splash           : Screen("splash")
    object Welcome          : Screen("welcome")
    object SignIn           : Screen("sign_in")
    object SignUp           : Screen("sign_up")
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
    glucoseViewModel: GlucoseViewModel
) {
//  ———— TO check if user is logged in or not ————————————————

    val authState = authViewModel.authState.collectAsState()
    val rememberMe = authViewModel.rememberMe.collectAsState()

    Log.d("AUTH", "rememberMe = ${rememberMe.value}")
    Log.d("AUTH", "authState = ${authState.value}")

    LaunchedEffect(authState.value, ) {
        Log.d("NAV_TEST", "LaunchedEffect fired")

        if (openedFromNotification) {
            Log.d("NAV_TEST", "Opened from notification")
            openedFromNotification = false
            return@LaunchedEffect
        }
        if (authState.value is AuthState.Authenticated && rememberMe.value) {
            navController.navigate(Screen.Home.route) {
                popUpTo(Screen.Welcome.route) { inclusive = true }
            }
        }

    }
    val context = LocalContext.current

    // The Factory which is passed to @ProfileViewModel
    val profileViewModelFactory = remember {
        ProfileViewModelFactory(
            application   = context.applicationContext as Application,
            authDataStore = AuthDataStore(context)
        )
    }

    val profileViewModel: ProfileViewModel = viewModel(
        factory = profileViewModelFactory
    )

    val startDest = Screen.Splash.route

    NavHost(
        navController = navController,
        startDestination = startDest
    ) {
        composable(Screen.Splash.route) {
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
                    navController.navigate(Screen.Home.route) {
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


        composable(Screen.Home.route) {
            HomeScreen(navController = navController,
                profileViewModel = profileViewModel,
                counterViewModel = counterViewModel)
        }

        composable(Screen.Logs.route) {
            SugarTrackerScreen(navController,glucoseViewModel)
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
                onSaveSuccess = {}
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
