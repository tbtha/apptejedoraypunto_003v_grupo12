package com.vivitasol.carcasamvvm.data

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import java.io.IOException

private val KEY_DARK_THEME = booleanPreferencesKey("dark_theme")

object ThemePrefsRepo {
    fun darkThemeFlow(context: Context): Flow<Boolean> =
        context.dataStore.data
            .catch { e -> if (e is IOException) emit(emptyPreferences()) else throw e }
            .map { it[KEY_DARK_THEME] ?: false }

    suspend fun setDarkTheme(context: Context, isDark: Boolean) {
        context.dataStore.edit { it[KEY_DARK_THEME] = isDark }
    }
}
