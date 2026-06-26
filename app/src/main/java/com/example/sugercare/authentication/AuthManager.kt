package com.example.sugercare.authentication

import android.content.Context
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.firebase.Firebase
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.auth
import com.sugarcare.app.R
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.launch
import java.security.MessageDigest
import java.util.UUID

class AuthManager(val context: Context) {
    private val auth by lazy { Firebase.auth }

    // —————— Account Creation ————————————————

    fun createAccountWithEmail(email: String, password: String): Flow<AuthResponse> = callbackFlow {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    trySend(AuthResponse.Success)
                } else {
                    trySend(AuthResponse.Failure(task.exception?.message ?: "Auth Failure"))
                }
            }
        awaitClose { }
    }

    // —————— Account Login ————————————————
    // -------------------------------------------> replaced with view model

    fun loginWithEmail(email: String, password: String): Flow<AuthResponse> = callbackFlow {
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    trySend(AuthResponse.Success)
                } else {
                    trySend(AuthResponse.Failure(task.exception?.message ?: "Auth Failure"))
                }
            }
        awaitClose { }
    }

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

    // —————— Google login ————————————————

    fun signInWithGoogle(): Flow<AuthResponse> = callbackFlow {
        val job = launch {
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

                delay(250)
                val credentialManager = CredentialManager.create(context)
                val result = credentialManager.getCredential(
                    context = context,
                    request = request
                )
                
                val credential = result.credential
                if (credential is CustomCredential && 
                    credential.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL
                ) {
                    val googleIdTokenCredential = GoogleIdTokenCredential.createFrom(credential.data)
                    val firebaseCredential = GoogleAuthProvider.getCredential(googleIdTokenCredential.idToken, null)

                    // ---- Verify Google Credential with Firebase
                    auth.signInWithCredential(firebaseCredential)
                        .addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                trySend(AuthResponse.Success)
                            } else {
                                trySend(AuthResponse.Failure(task.exception?.message ?: "Firebase Error"))
                            }
                            close()
                        }
                } else {
                    trySend(AuthResponse.Failure("Unexpected credential type"))
                    close()
                }
            } catch (e: Exception) {
                trySend(AuthResponse.Failure(e.message ?: "Google Auth Failure"))
                close()
            }
        }

        awaitClose { job.cancel() }
    }
}
