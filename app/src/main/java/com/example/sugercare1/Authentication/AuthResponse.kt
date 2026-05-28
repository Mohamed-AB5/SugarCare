package com.example.sugercare1.Authentication

import com.google.android.gms.auth.api.Auth

interface AuthResponse {
    data object Success : AuthResponse
    data class Failure (val message: String) : AuthResponse
}