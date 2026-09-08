package com.example.sugercare.core.mainComponents.theme

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

val Context.themeDataStore by preferencesDataStore("theme_prefs")

class ThemeDataStore(private val context: Context) {

    companion object {
        val KEY_DARK_MODE = booleanPreferencesKey("dark_mode")
    }

    // ── Read saved dark mode (defaults to false / light) ──────
    val isDarkModeFlow: Flow<Boolean> =
        context.themeDataStore.data.map { it[KEY_DARK_MODE] ?: false }

    // ── Save dark mode whenever the user toggles the switch ───
    suspend fun setDarkMode(enabled: Boolean) {
        context.themeDataStore.edit { prefs ->
            prefs[KEY_DARK_MODE] = enabled
        }
    }
}
