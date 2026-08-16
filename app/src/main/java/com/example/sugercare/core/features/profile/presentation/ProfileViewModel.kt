package com.example.sugercare.core.features.profile.presentation

import android.R.attr.password
import android.app.Application
import android.icu.util.Calendar
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.sugercare.core.features.profile.model.AuthProvider
import com.example.sugercare.core.features.profile.model.ProfileUiState
import com.example.sugercare.core.features.profile.model.UserProfile
import com.example.sugercare.core.features.profile.repository.ProfileRepo
import com.example.sugercare.core.features.profile.repository.ProfileRepoImpl
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class ProfileViewModel(application: Application) :
    AndroidViewModel(application) {

    private val auth = FirebaseAuth.getInstance()
    private val repo: ProfileRepo = ProfileRepoImpl()

    private val _profileState = MutableStateFlow<ProfileUiState>(ProfileUiState.Loading)
    val profileState: StateFlow<ProfileUiState> = _profileState.asStateFlow()

    private val _editableProfile = MutableStateFlow(UserProfile())
    val editableProfile: StateFlow<UserProfile> = _editableProfile.asStateFlow()

    private val _fieldErrors = MutableStateFlow<Map<String, String>>(emptyMap())
    val fieldErrors: StateFlow<Map<String, String>> = _fieldErrors.asStateFlow()

    init {
        loadProfile()
    }

    // ── Load ────────────────────

    fun loadProfile() {
        viewModelScope.launch {
            _profileState.value = ProfileUiState.Loading
            val user = auth.currentUser

            if (user == null) {
                _profileState.value = ProfileUiState.Error("User not authenticated")
                return@launch
            }
            Log.d("PHOTO_TEST", "1. Firebase Auth URL: ${user.photoUrl}")
            val provider = detectAuthProvider(user)
            val result = repo.getProfile(user.uid)

            result.fold(
                onSuccess = { profile ->
                    Log.d("PHOTO_TEST", "2. Firestore Saved URL: ${profile.photoUrl}")
                    val finalProfile = if (profile.uid.isEmpty()) {
                        initializeProfile(user, provider)

                    } else {
                        if (profile.photoUrl.isNullOrEmpty() && user.photoUrl != null) {
                            val livePhotoUrl = if (provider == AuthProvider.GOOGLE) {
                                user.photoUrl.toString().replace("s96-c", "s400-c")
                            } else {
                                user.photoUrl.toString()
                            }
                            profile.copy(photoUrl = livePhotoUrl)
                        } else {
                            profile
                        }
                    }
                    _editableProfile.value = finalProfile
                    _profileState.value = ProfileUiState.Success(
                        profile = finalProfile,
                        isEditable = true
                    )
                    Log.d("PHOTO_TEST", "3. Final URL for UI: ${finalProfile.photoUrl}")
                },
                onFailure = {
                    _profileState.value = ProfileUiState.Error(
                        it.message ?: "Failed to load profile"
                    )
                }
            )

        }
    }

    // ── Detect Auth Provider ──────────────────────────────

    private fun detectAuthProvider(user: FirebaseUser): AuthProvider {
        val providerId = user.providerData
            .map { it.providerId }
            .firstOrNull { it != "firebase" }
        return when (providerId) {
            "google.com" -> AuthProvider.GOOGLE
            "facebook.com" -> AuthProvider.FACEBOOK
            else -> AuthProvider.EMAIL
        }
    }

    // ── Initialize Profile for new Social users ───────────

    private fun initializeProfile(
        user: FirebaseUser, provider: AuthProvider
    ): UserProfile {
        var photoUrl = user.photoUrl?.toString()

        when (provider) {
            AuthProvider.FACEBOOK -> {
                val facebookUid = user.providerData
                    .firstOrNull { it.providerId == "facebook.com" }?.uid

                if (facebookUid != null) {
                    photoUrl = "https://graph.facebook.com/$facebookUid/picture?type=large"
                }
            }

            AuthProvider.GOOGLE -> {
                photoUrl = photoUrl?.replace("s96-c", "s400-c") // trick to get high quality photos
            }

            else -> {}

        }

        return UserProfile(
            uid = user.uid,
            fullName = user.displayName ?: "",
            email = user.email ?: "",
            phone = "",
            dob = "",
            age = 0,
            weight = 0,
            gender = "",
            authProvider = provider,
            photoUrl = photoUrl ?: ""
        )
    }

    // ── Edit Fields ───────────────────────
    fun updateFullName(value: String) {
        _editableProfile.value = _editableProfile.value.copy(fullName = value)
    }

    fun updatePhoneNumber(value: String) {
        _editableProfile.value = _editableProfile.value.copy(phone = value)
    }

    fun updateDateOfBirth(value: String) {
        _editableProfile.value = _editableProfile.value.copy(
            dob = value,
            age = calculateAgeFromDOB(value)
        )
    }

    fun updateGender(value: String) {
        _editableProfile.value = _editableProfile.value.copy(gender = value)
    }

    /*    fun updateAge(value: String) {
            return try {
                val age = value.toInt()
                _editableProfile.value = _editableProfile.value.copy(
                    age = age,
                    dob = calculateDOBFromAge(age)
                )
            } catch (e: Exception) { }
        }*/
    fun updateWeight(value: String) {
        val weight = value.toInt()
        _editableProfile.value = _editableProfile.value.copy(weight = weight)
    }


// ── Validation ──────────────────────────────

    private fun validate(profile: UserProfile): Boolean {
        val errors = mutableMapOf<String, String>()

        if (profile.fullName.isBlank())
            errors["fullName"] = "Full name is required"

        if (profile.phone.length < 10)
            errors["phone"] = "Enter a valid phone number"

        if (profile.dob.isNotBlank()) {
            val parts = profile.dob.split("/")
            if (parts.size != 3)
                errors["dob"] = "Use format DD/MM/YYYY"
        }

        /*  if(profile.age !in 1..126)
          {
              errors["age"] = "Enter a valid age"
          }
  */
        if (profile.weight !in 1..300) {
            errors["weight"] = "Enter a valid weight"
        }

        _fieldErrors.value = errors
        return errors.isEmpty()
    }

    // ── Calculate age from DOB ──────────────────────────────────
    private fun calculateAgeFromDOB(dob: String): Int {

        return try {
            val parts = dob.split("/")
            if (parts.size != 3) return 0
            val day = parts[0].toInt()
            val month = parts[1].toInt()
            val year = parts[2].toInt()

            val today = Calendar.getInstance()
            val birthDate = Calendar.getInstance().apply {
                set(year, month - 1, day)
            }

            var age = today.get(Calendar.YEAR) - birthDate.get(Calendar.YEAR)
            // if the birthday has not yet passed this year, decrement the age
            if (today.get(Calendar.DAY_OF_YEAR) < birthDate.get(Calendar.DAY_OF_YEAR)) {
                age--
            }
            age
        } catch (e: Exception) {
            0
        }

    }

    // ── Calculate DOB from age ──────────────────────────────────
    /*   private fun calculateDOBFromAge(age: Int): String {

           val today     = Calendar.getInstance()
           val birthYear = today.get(Calendar.YEAR) - age
           val month     = today.get(Calendar.MONTH) + 1
           val day       = today.get(Calendar.DAY_OF_MONTH)

           val formattedDay = day.toString().padStart(2, '0')
           val formattedMonth = month.toString().padStart(2, '0')

           return "$formattedDay/$formattedMonth/$birthYear"
       }*/


//  ── Saving Profile Data  ────────────────────────────

    fun saveProfile() {
        viewModelScope.launch {
            val profile = _editableProfile.value
            Log.d("PROFILE", "1. saveProfile called")
            Log.d("PROFILE", "2. profile = $profile")
            Log.d("PROFILE", "uid = ${profile.uid}")

            if (!validate(profile)) {
                Log.d("PROFILE", "3. Validation failed: ${_fieldErrors.value}")
                return@launch
            }

            Log.d("PROFILE", "4. Validation passed")
            _profileState.value = ProfileUiState.Saving

            val result = repo.updateProfile(
                profile.uid,
                mapOf(
                    "fullName" to profile.fullName,
                    "phone" to profile.phone,
                    "dob" to profile.dob,
                    "age" to profile.age,
                    "weight" to profile.weight,
                    "gender" to profile.gender,
                    "email" to profile.email,
                    "authProvider" to profile.authProvider.name,
                    "createdAt" to profile.createdAt,
                    "photoUrl" to (profile.photoUrl ?: "")
                )
            )

            Log.d("PROFILE", "5. Result = $result")

            result.fold(
                onSuccess = {
                    Log.d("PROFILE", "6. Save SUCCESS")
                    _profileState.value = ProfileUiState.SaveSuccess
                },
                onFailure = {
                    Log.d("PROFILE", "6. Save FAILED: ${it.message}")
                    _profileState.value = ProfileUiState.Error(
                        it.message ?: "Failed to save profile"
                    )
                }
            )
        }
    }

   /* fun deleteAccount(deletePassword: String) {
        viewModelScope.launch {
            val user = auth.currentUser
            if (user == null) {
                _profileState.value = ProfileUiState.Error("No user logged in")
                return@launch
            }


            *//*_profileState.value = ProfileUiState.Saving TODO -> i need to find out a feedback like this*//*

            try {

                Log.d("PROFILE", "user.email = '${user?.email}'")
                Log.d("PROFILE", "password length = ${password.toString().length}")
                Log.d("PROFILE", "password = '$password'")  // ← Check for spaces
                val provider = user.providerData.map { it.providerId }


                when {
                    "password" in provider -> {
                        val credential = EmailAuthProvider.getCredential(user.email ?: "",
                            (password ?: "").toString()
                        )
                        user.reauthenticate(credential).await()
                    }
                    "google.com" in provider -> {
                        // Google re-auth → handled separately (needs GoogleSignIn flow)
                        // For now just attempt delete
                    }
                    "facebook.com" in provider -> {
                        // Facebook re-auth → handled separately
                    }
                }
                FirebaseFirestore.getInstance()
                    .collection("users")
                    .document(user.uid)
                    .delete()
                    .await()

                user.delete().await()

                Log.d("PROFILE", "Account deleted successfully")
                _profileState.value = ProfileUiState.AccountDeleted

            } catch (e: Exception) {
                Log.e("PROFILE", "Delete failed: ${e.message}")
                _profileState.value = ProfileUiState.Error(
                    e.message ?: "Failed to delete account"
                )
            }


        }
    }
*/

    fun deleteAccount(password: String? = null) {
        viewModelScope.launch {
            val user = auth.currentUser ?: return@launch
            _profileState.value = ProfileUiState.Saving

            try {
                // ✅ Detect provider
                val providers = user.providerData.map { it.providerId }
                Log.d("PROFILE", "providers = $providers")  // ← Check this

                when {
                    "google.com" in providers -> {
                        // ✅ Google accounts don't need re-auth for delete
                        // Just delete directly
                    }
                    "facebook.com" in providers -> {
                        // ✅ Same for Facebook
                    }
                    "password" in providers -> {
                        // ✅ Only Email/Password needs re-auth
                        val credential = EmailAuthProvider.getCredential(
                            user.email?.trim() ?: "",
                            password?.trim() ?: ""
                        )
                        user.reauthenticate(credential).await()
                    }
                }

                // ✅ Delete Firestore doc
                FirebaseFirestore.getInstance()
                    .collection("users")
                    .document(user.uid)
                    .delete()
                    .await()

                // ✅ Delete Auth account
                user.delete().await()

                _profileState.value = ProfileUiState.AccountDeleted

            } catch (e: Exception) {
                Log.e("PROFILE", "Delete failed: ${e.message}")
                _profileState.value = ProfileUiState.Error(
                    e.message ?: "Failed to delete account"
                )
            }
        }
    }
    fun resetState() {
        _profileState.value = ProfileUiState.Success(
            profile = _editableProfile.value,
            isEditable = true
        )
    }

    // ── TO Clear Errors  ─────────────────────────────
    fun clearFieldError(field: String) {
        _fieldErrors.value = _fieldErrors.value.toMutableMap().also {
            it.remove(field)
        }
    }

    // ── Clear State on Logout ──────────────────────────────
    fun clearData() {
        _profileState.value = ProfileUiState.Loading
        _editableProfile.value = UserProfile()
        _fieldErrors.value = emptyMap()
    }

}