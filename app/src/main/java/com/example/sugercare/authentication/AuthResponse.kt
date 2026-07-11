package com.example.sugercare.authentication

interface AuthResponse {
    data object Success : AuthResponse
    data class Failure (val message: String) : AuthResponse
}