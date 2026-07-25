package com.example.sugercare.core.features.profile.model

sealed class ProfileUiState {
    object Loading : ProfileUiState()
    object Saving : ProfileUiState()
    object Saved : ProfileUiState()
    data class Success(
        val profile: UserProfile,
        val isEditable: Boolean = true
    ) : ProfileUiState()
    data class Error(val message: String) : ProfileUiState()
}