package com.example.sugercare.viewModels

import android.content.Context
import android.util.Log
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.sugercare.authentication.AuthDataStore
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.google.firebase.auth.GoogleAuthProvider
import com.sugarcare.app.R
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.security.MessageDigest
import java.util.UUID

class AuthViewModel(context: Context) : ViewModel() {
    private val auth: FirebaseAuth by lazy { FirebaseAuth.getInstance() }
    private val _authState = MutableStateFlow<AuthState>(AuthState.UnAuthenticated)
    private val prefsRepo = AuthDataStore(context)

    init {
        checkAuthStatus()
        loadRememberedDetails()
    }

    val authState: StateFlow<AuthState> = _authState.asStateFlow()
    private val _email = MutableStateFlow("")
    val email: StateFlow<String> = _email.asStateFlow()

    private val _password = MutableStateFlow("")
    val password: StateFlow<String> = _password.asStateFlow()

    private val _showPass = MutableStateFlow(false)
    val showPass: StateFlow<Boolean> = _showPass.asStateFlow()

    private val _rememberMe = MutableStateFlow(false)
    val rememberMe: StateFlow<Boolean> = _rememberMe.asStateFlow()

    fun updateEmail(newEmail: String) {
        _email.value = newEmail
    }

    fun updatePassword(newPassword: String) {
        _password.value = newPassword
    }

    fun toggleShowPass() {
        _showPass.value = !_showPass.value
    }

    fun toggleRememberMe() {
        _rememberMe.value = !_rememberMe.value
    }

    // —————— For Remember Me  ————————————————
    private fun loadRememberedDetails() {
        viewModelScope.launch {
            prefsRepo.emailFlow.collectLatest { savedEmail ->
                _email.value = savedEmail
            }
        }
        viewModelScope.launch {
            prefsRepo.rememberMeFlow.collectLatest { isRemembered ->
                _rememberMe.value = isRemembered
            }
        }
    }

    fun saveRememberMeDetails(email: String) {
        viewModelScope.launch {
            prefsRepo.saveDetails(email, rememberMe = true)
        }
    }

    fun clearRememberMeDetails() {
        viewModelScope.launch {
            prefsRepo.clearDetails()
        }
    }

    // —————— Check current login status  ————————————————
    fun checkAuthStatus() {
        viewModelScope.launch {
            val isRemembered = prefsRepo.rememberMeFlow.first()
            _rememberMe.value = isRemembered

            if (auth.currentUser != null && !isRemembered) {
                auth.signOut()
                _authState.value = AuthState.UnAuthenticated
            } else if (auth.currentUser != null) {
                _authState.value = AuthState.Authenticated
            } else {
                _authState.value = AuthState.UnAuthenticated
            }
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
        when {
            email.isEmpty() -> {
                _authState.value = AuthState.Error("Email is required")
            }

            !isValidEmail(email) -> {
                _authState.value = AuthState.Error("Invalid email format")
            }

            password.isEmpty() -> {
                _authState.value = AuthState.Error("Password is required")
            }

            password.length < 8 -> {
                _authState.value = AuthState.Error("Password must be 8 characters at least")
            }

            else -> performSignIn(email, password)
        }
    }

    private fun isValidEmail(email: String): Boolean {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }

    private fun performSignIn(email: String, password: String) {
        _authState.value = AuthState.Loading
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    viewModelScope.launch {
                        prefsRepo.saveDetails(email, _rememberMe.value)
                    }
                    _authState.value = AuthState.Authenticated
                } else {
                    _authState.value = AuthState.Error(
                        when (task.exception) {
                            is FirebaseAuthInvalidUserException -> "Email not found"
                            is FirebaseAuthInvalidCredentialsException -> "Wrong Email or Password"
                            else -> task.exception?.message ?: "Sign in failed"
                        }
                    )
                }
            }
    }
    //  —————————————————— Google SignIn ————————————————————————————
    // —————— Nonce(Encryption Algorithm) ————————————————

    private fun createNonce(): String {
        val rawNonce = UUID.randomUUID().toString()
        val bytes = rawNonce.toByteArray()
        val md = MessageDigest.getInstance("SHA-256")
        val digest = md.digest(bytes)

        return digest.fold("") { str, it ->
            str + "%02x".format(it)
        }
    }

    fun signInWithGoogle(context: Context) {
        viewModelScope.launch {
            _authState.value = AuthState.Loading
            _rememberMe.value = true

            try {
                // ---- Google id Option
                val googleIdOption = GetGoogleIdOption.Builder()
                    .setFilterByAuthorizedAccounts(false)
                    .setServerClientId(context.getString(R.string.web_client_id))
                    .setAutoSelectEnabled(true)
                    .setNonce(createNonce())
                    .build()

                // ---- Verify Google Credential with Firebase
                val request = GetCredentialRequest.Builder()
                    .addCredentialOption(googleIdOption)
                    .build()

                delay(100)
                val credentialManager = CredentialManager.create(context)
                val result = credentialManager.getCredential(
                    context = context,
                    request = request
                )

                val credential = result.credential
                if (credential is CustomCredential &&
                    credential.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL
                ) {
                    val googleIdTokenCredential =
                        GoogleIdTokenCredential.createFrom(credential.data)

                    val firebaseCredential =
                        GoogleAuthProvider.getCredential(googleIdTokenCredential.idToken, null)

                    // ---- Verify Google Credential with Firebase
                    auth.signInWithCredential(firebaseCredential)
                        .addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                viewModelScope.launch { prefsRepo.saveDetails(email.value, true) }
                                _authState.value = AuthState.Authenticated
                            } else {
                                _authState.value = AuthState.Error(
                                    task.exception?.message ?: "Firebase Error"
                                )
                            }

                        }
                } else {
                    _authState.value = AuthState.Error("Unexpected credential type")
                }
            } catch (e: Exception) {
                _authState.value = AuthState.UnAuthenticated
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