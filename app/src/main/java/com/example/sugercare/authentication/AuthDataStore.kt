package com.example.sugercare.authentication

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

val Context.dataStore by preferencesDataStore("auth_prefs")

class AuthDataStore(private val context: Context) {
    companion object {
        val KEY_EMAIL = stringPreferencesKey("remember_email")
        val KEY_REMEMBER_ME = booleanPreferencesKey("remember_me")
    }

    val emailFlow: Flow<String> = context.dataStore.data.map { it[KEY_EMAIL] ?: "" }
    val rememberMeFlow: Flow<Boolean> = context.dataStore.data.map { it[KEY_REMEMBER_ME] ?: false }

    suspend fun saveDetails(email: String, rememberMe: Boolean) {
        context.dataStore.edit { prefs ->
            prefs[KEY_EMAIL] = if (rememberMe) email else ""
            prefs[KEY_REMEMBER_ME] = rememberMe
        }
    }
        suspend fun clearDetails() {
            context.dataStore.edit { it.clear() }
        }
}


