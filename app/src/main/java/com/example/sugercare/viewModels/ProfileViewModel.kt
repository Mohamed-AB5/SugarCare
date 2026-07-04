package com.example.sugercare.viewModels

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.sugercare.profileRepo.AuthProvider
import com.example.sugercare.profileRepo.ProfileRepo
import com.example.sugercare.profileRepo.ProfileRepoImpl
import com.example.sugercare.profileRepo.ProfileUiState
import com.example.sugercare.profileRepo.UserProfile
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ProfileViewModel(application: Application) : AndroidViewModel(application) {

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
        _editableProfile.value = _editableProfile.value.copy(dob = value)
    }

    fun updateGender(value: String) {
        _editableProfile.value = _editableProfile.value.copy(gender = value)
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

        _fieldErrors.value = errors
        return errors.isEmpty()
    }

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
                        "fullName"     to profile.fullName,
                        "phone"        to profile.phone,
                        "dob"          to profile.dob,
                        "gender"       to profile.gender,
                        "email"        to profile.email,
                        "authProvider" to profile.authProvider.name,
                        "createdAt"    to profile.createdAt,
                        "photoUrl"     to (profile.photoUrl ?:"")
                    )
                )

                Log.d("PROFILE", "5. Result = $result")

                result.fold(
                    onSuccess = {
                        Log.d("PROFILE", "6. Save SUCCESS")
                        _profileState.value = ProfileUiState.Saved
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











