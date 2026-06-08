package com.example.sugercare.Authentication

interface AuthResponse {
    data object Success : AuthResponse
    data class Failure (val message: String) : AuthResponse
}