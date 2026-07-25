package com.example.sugercare.core.features.profile.repository

import com.example.sugercare.core.features.profile.model.UserProfile

interface ProfileRepo {
    suspend fun getProfile(uid: String): Result<UserProfile>
    suspend fun saveProfile(profile: UserProfile): Result<Unit>
    suspend fun updateProfile(uid: String, updates: Map<String, Any>): Result<Unit>
}