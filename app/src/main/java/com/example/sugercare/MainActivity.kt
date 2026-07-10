package com.sugarcare.app

import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.sugercare.viewModels.AuthViewModel
import com.example.sugercare.viewModels.ProfileViewModel
import com.sugarcare.app.navigation.SugarCareNavHost
import com.sugarcare.app.ui.theme.SugarCareTheme
import java.lang.String
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException
import kotlin.io.encoding.Base64

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            SugarCareTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    val authViewModel: AuthViewModel = viewModel()
                    val profileViewModel: ProfileViewModel = viewModel()
                    SugarCareNavHost(
                        authViewModel = authViewModel,
                        profileViewModel = profileViewModel
                    )
                }
            }

        }

        try {
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
        } catch (e: PackageManager.NameNotFoundException) {
            Log.e("HashKey_Meta", "Package not found")
        } catch (e: NoSuchAlgorithmException) {
            Log.e("HashKey_Meta", "Algorithm not found")
        }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        val authViewModel: AuthViewModel = ViewModelProvider(this)[AuthViewModel::class.java]
        authViewModel.onActivityResult(requestCode, resultCode, data)
    }

}


