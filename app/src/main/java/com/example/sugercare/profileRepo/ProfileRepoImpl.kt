package com.example.sugercare.profileRepo

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import kotlinx.coroutines.tasks.await

class ProfileRepoImpl : ProfileRepo {

    private val firestore = FirebaseFirestore.getInstance()
    private val usersCollection = firestore.collection("users")

    // —————— Getting profile data  ———————————————

    override suspend fun getProfile(uid: String): Result<UserProfile> {
        return try {
            val doc = usersCollection.document(uid).get().await()
            if (doc.exists()) {
                val profile = UserProfile(
                    uid          = uid,
                    fullName     = doc.getString("fullName") ?: "",
                    phone        = doc.getString("phone") ?: "",
                    dob          = doc.getString("dob") ?: "",
                    gender       = doc.getString("gender") ?: "",
                    email        = doc.getString("email") ?: "",
                    authProvider = AuthProvider.valueOf(
                        doc.getString("authProvider") ?: "EMAIL"
                    ),
                    photoUrl     = doc.getString("photoUrl"),
                    createdAt     = doc.getLong("createAt") ?: System.currentTimeMillis()
                )
                Result.success(profile) // The created profile above ↑ ↑
            }else {
                Result.success(UserProfile()) // Empty profile --> new user
            }
        }
        catch (e: Exception) {
            Result.failure(e)
        }
    }


    // —————— Saving profile data  ———————————————

    override suspend fun saveProfile(profile: UserProfile): Result<Unit> {
        return try {
            val data = mapOf(
                "uid"          to profile.uid,
                "fullName"     to profile.fullName,
                "phone"        to profile.phone,
                "dob"          to profile.dob,
                "gender"       to profile.gender,
                "email"        to profile.email,
                "authProvider" to profile.authProvider.name,
                "photoUrl"     to profile.photoUrl,
                "createdAt"    to profile.createdAt
            )
            usersCollection.document(profile.uid).set(data).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // —————— Updating profile data  ———————————————

    override suspend fun updateProfile(uid: String, updates: Map<String, Any>): Result<Unit> {
        return try {
            usersCollection.document(uid)
                .set(updates, SetOptions.merge())
                .await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
