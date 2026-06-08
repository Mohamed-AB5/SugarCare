package com.example.sugercare1.viewModels

import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class AuthViewModel : ViewModel() {
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val _authState = MutableStateFlow<AuthState>(AuthState.UnAuthenticated)
//    val authState: StateFlow<AuthState> =
//        _authState.asStateFlow()// to access across project as a getter
//
//    init {
//        checkAuthStatus()
//    }
val authState : StateFlow<AuthState> = _authState.asStateFlow()

    // —————— Check current login status  ————————————————
    fun checkAuthStatus() {
        _authState.value =
            if (auth.currentUser == null) {
                AuthState.UnAuthenticated
            } else {
                AuthState.Authenticated
            }
    }

    // —————— Reset Auth State ———————————————
    fun resetAuthState() {
        _authState.value = AuthState.UnAuthenticated
    }

    // —————— Creating the Account ———————————————

    fun signUp(email: String, password: String) {

        if (email.isEmpty() || password.isEmpty()) {
            _authState.value = AuthState.Error("Email or password cannot be empty")
            return
        }

        _authState.value = AuthState.Loading
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    _authState.value = AuthState.Authenticated
                } else {
                    _authState.value =
                        AuthState.Error(task.exception?.message ?: "Authentication failed")
                }
            }
    }

    fun signIn(email: String, password: String) {

        if (email.isEmpty() || password.isEmpty()) {
            _authState.value = AuthState.Error("Email or password cannot be empty")
            return
        }
        _authState.value = AuthState.Loading
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    _authState.value = AuthState.Authenticated
                } else {
                    _authState.value =
                        AuthState.Error(task.exception?.message ?: "Authentication failed")
                }
            }
    }


    fun logout() {
        auth.signOut()
        _authState.value = AuthState.UnAuthenticated
    }
}


/**
 * TODO: modify it with [AuthResponse] interface later
 */
sealed class AuthState {
    object Authenticated : AuthState()
    object UnAuthenticated : AuthState()
    object Loading : AuthState()
    data class Error(val message: String) : AuthState()
}