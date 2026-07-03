package com.example.sugercare.viewModels

import android.app.Activity
import android.app.Application
import android.content.Context
import android.content.Intent
import androidx.activity.ComponentActivity
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.sugercare.authentication.AuthDataStore
import com.facebook.CallbackManager
import com.facebook.FacebookCallback
import com.facebook.FacebookException
import com.facebook.login.LoginManager
import com.facebook.login.LoginResult
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.firebase.auth.FacebookAuthProvider
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

class AuthViewModel(application: Application) : AndroidViewModel(application) {
    private val auth: FirebaseAuth by lazy { FirebaseAuth.getInstance() }
    private val _authState = MutableStateFlow<AuthState>(AuthState.UnAuthenticated)
    private val prefsRepo = AuthDataStore(application)
    private var facebookCallbackManager = CallbackManager.Factory.create()

    init {
        checkAuthStatus()
        loadRememberedDetails()
    }

    val authState: StateFlow<AuthState> = _authState.asStateFlow()
    private val _email = MutableStateFlow("")
    val email: StateFlow<String> = _email.asStateFlow()

    private val _password = MutableStateFlow("")
    val password: StateFlow<String> = _password.asStateFlow()

    private val _fullName = MutableStateFlow("")
    val fullName: StateFlow<String> = _fullName.asStateFlow()

    private val _confirmPassword = MutableStateFlow("")
    val confirmPassword: StateFlow<String> = _confirmPassword.asStateFlow()

    private val _visiblePasswordFields = MutableStateFlow<Set<String>>(emptySet())
    val visiblePasswordFields: StateFlow<Set<String>> = _visiblePasswordFields.asStateFlow()

    private val _rememberMe = MutableStateFlow(false)
    val rememberMe: StateFlow<Boolean> = _rememberMe.asStateFlow()



    fun updateEmail(newEmail: String) {
        _email.value = newEmail
    }

    fun updateFullName(newFullName: String) {
        _fullName.value = newFullName
    }

    fun updatePassword(newPassword: String) {
        _password.value = newPassword
    }

    fun updateConfirmPassword(newPassword: String) {
        _confirmPassword.value = newPassword
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


    fun togglePasswordVisibility(fieldKey: String) {
        _visiblePasswordFields.value =
            if (fieldKey in _visiblePasswordFields.value)
                _visiblePasswordFields.value - fieldKey
            else
                _visiblePasswordFields.value + fieldKey
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

        when {
            email.isEmpty() -> {
                _authState.value =
                    AuthState.Error("Email is required")
            }

            !isValidEmail(email) -> {
                _authState.value =
                    AuthState.Error("Invalid email format")
            }

            password.isEmpty() -> {
                _authState.value =
                    AuthState.Error("Password is required")
            }

            password != _confirmPassword.value -> {
                _authState.value =
                    AuthState.Error("Two Passwords are mismatched")
            }

            password.length < 8 -> {
                _authState.value =
                    AuthState.Error("Password must be 8 characters at least")
            }

            else -> {
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
                        if (rememberMe.value) saveRememberMeDetails(email)
                        else clearRememberMeDetails()
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
                                viewModelScope.launch {
                                    prefsRepo.setRememberMe(true)
                                }
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

    // —————— Facebook Sign In ———————————————

    fun signInWithFacebook(activity: ComponentActivity) {
        viewModelScope.launch {
            _authState.value = AuthState.Loading

            LoginManager.getInstance().logInWithReadPermissions(
                activity,
                facebookCallbackManager,
                listOf("public_profile",)
            )

            LoginManager.getInstance().registerCallback(
                facebookCallbackManager,
                object : FacebookCallback<LoginResult> {
                    override fun onSuccess(result: LoginResult) {
                        val token = result.accessToken.token
                        val credential = FacebookAuthProvider.getCredential(token)

                        auth.signInWithCredential(credential)
                            .addOnCompleteListener { task ->
                                if (task.isSuccessful) {
                                    viewModelScope.launch {
                                        prefsRepo.setRememberMe(true)
                                    }
                                    _authState.value = AuthState.Authenticated
                                } else {
                                    _authState.value = AuthState.Error(
                                        task.exception?.message ?: "Facebook Auth Failed"
                                    )
                                }
                            }
                    }

                    override fun onCancel() {
                        _authState.value = AuthState.Error("Facebook login cancelled")
                    }

                    override fun onError(error: FacebookException) {
                        _authState.value = AuthState.Error(error.message ?: "Facebook Auth Failed")
                    }
                }
            )
        }
    }

    fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        facebookCallbackManager.onActivityResult(requestCode, resultCode, data)
    }

    fun logout() {
        auth.signOut()
        _password.value = ""
        _authState.value = AuthState.UnAuthenticated

        viewModelScope.launch {
            clearRememberMeDetails()
        }
    }
}

sealed class AuthState {
    object Authenticated : AuthState()
    object UnAuthenticated : AuthState()
    object Loading : AuthState()
    data class Error(val message: String) : AuthState()
}
