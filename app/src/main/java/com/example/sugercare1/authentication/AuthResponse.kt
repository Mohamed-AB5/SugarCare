package com.example.sugercare1.authentication

interface AuthResponse {
    data object Success : AuthResponse
    data class Failure (val message: String) : AuthResponse
}