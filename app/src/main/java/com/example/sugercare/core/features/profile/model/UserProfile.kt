package com.example.sugercare.core.features.profile.model

enum class AuthProvider  {EMAIL,GOOGLE ,FACEBOOK}

data class UserProfile(
    val uid          : String = "",
    val fullName     : String = "",
    val phone        : String = "",
    val dob          : String = "",
    val age          : Int    = 0,
    val weight       : String = "",
    val gender       : String = "",
    val email        : String = "",
    val authProvider : AuthProvider = AuthProvider.EMAIL,
    val photoUrl     : String? = null,
    val createdAt    : Long = System.currentTimeMillis()
)

