package com.example.sugercare.profileRepo

interface ProfileRepo {
    suspend fun getProfile(uid: String): Result<UserProfile>
    suspend fun saveProfile(profile: UserProfile): Result<Unit>
    suspend fun updateProfile(uid: String, updates: Map<String, Any>): Result<Unit>
}