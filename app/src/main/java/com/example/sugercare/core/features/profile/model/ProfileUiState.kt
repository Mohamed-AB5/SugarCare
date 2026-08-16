package com.example.sugercare.core.features.profile.model

sealed class ProfileUiState {
    object Loading : ProfileUiState()
    object Saving : ProfileUiState()
    object SaveSuccess : ProfileUiState()
    object AccountDeleted : ProfileUiState()
    data class Success(
        val profile: UserProfile,
        val isEditable: Boolean = true
    ) : ProfileUiState()
    data class Error(val message: String) : ProfileUiState()
}