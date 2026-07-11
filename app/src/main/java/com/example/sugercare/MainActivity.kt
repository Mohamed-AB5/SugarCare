package com.example.sugercare

import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Base64
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import com.example.sugercare.app.SugarViewModel
import com.example.sugercare.viewModels.AuthViewModel
import com.example.sugercare.viewModels.ChatViewModel
import com.example.sugercare.viewModels.CounterViewModel
import com.example.sugercare.viewModels.ProfileViewModel
import com.sugarcare.app.navigation.SugarCareNavHost
import com.sugarcare.app.ui.theme.LocalDarkTheme
import com.sugarcare.app.ui.theme.SugarCareTheme
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException

class MainActivity : ComponentActivity() {

    private val authViewModel   : AuthViewModel    by viewModels()
    private val profileViewModel: ProfileViewModel by viewModels()
    private val chatViewModel   : ChatViewModel    by viewModels()
    private val counterViewModel   : CounterViewModel    by viewModels()
    private val sugarViewModel   : SugarViewModel    by viewModels()



    @RequiresApi(Build.VERSION_CODES.VANILLA_ICE_CREAM)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // ── SHA Key Hash for Facebook Login ──────────────────
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                val info = packageManager.getPackageInfo(
                    packageName,
                    PackageManager.GET_SIGNING_CERTIFICATES
                )
                val signatures = info.signingInfo?.apkContentsSigners
                for (signature in signatures!!) {
                    val md = MessageDigest.getInstance("SHA")
                    md.update(signature.toByteArray())
                    val hashKey = String(Base64.encode(md.digest(), 0))
                    Log.d("HashKey_Meta", "Your Key Hash is: $hashKey")
                }
            } else {
                val info = packageManager.getPackageInfo(
                    packageName,
                    PackageManager.GET_SIGNATURES
                )
                for (signature in info.signatures!!) {
                    val md = MessageDigest.getInstance("SHA")
                    md.update(signature.toByteArray())
                    val hashKey = String(Base64.encode(md.digest(), 0))
                    Log.d("HashKey_Meta", "Your Key Hash is: $hashKey")
                }
            }
        } catch (e: PackageManager.NameNotFoundException) {
            Log.e("HashKey_Meta", "Package not found")
        } catch (e: NoSuchAlgorithmException) {
            Log.e("HashKey_Meta", "Algorithm not found")
        }

        enableEdgeToEdge()

        setContent {
            // ── Global dark mode state ────────────────────────
            val darkState = remember { mutableStateOf(false) }

            CompositionLocalProvider(LocalDarkTheme provides darkState) {
                SugarCareTheme(darkTheme = darkState.value) {
                    Surface(modifier = Modifier.fillMaxSize()) {
                        SugarCareNavHost(
                            authViewModel    = authViewModel,
                            profileViewModel = profileViewModel,
                            chatViewModel    = chatViewModel,
                            counterViewModel = counterViewModel,
                            sugarViewModel   = sugarViewModel
                        )
                    }
                }
            }
        }
    }

    // ── Facebook Login result handler ─────────────────────────
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        val authViewModel: AuthViewModel by viewModels()
        authViewModel.onActivityResult(requestCode, resultCode, data)
    }
}