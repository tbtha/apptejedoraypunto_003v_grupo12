package com.vivitasol.carcasamvvm.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.vivitasol.carcasamvvm.data.ThemePrefsRepo
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ThemeViewModel(application: Application) : AndroidViewModel(application) {
    
    private val _isDarkTheme = MutableStateFlow(false)
    val isDarkTheme: StateFlow<Boolean> = _isDarkTheme.asStateFlow()

    init {
        // Cargar preferencia guardada
        viewModelScope.launch {
            ThemePrefsRepo.darkThemeFlow(getApplication()).collect { isDark ->
                _isDarkTheme.value = isDark
            }
        }
    }

    fun toggleTheme() {
        viewModelScope.launch {
            val newValue = !_isDarkTheme.value
            _isDarkTheme.value = newValue
            ThemePrefsRepo.setDarkTheme(getApplication(), newValue)
        }
    }

    fun setTheme(isDark: Boolean) {
        viewModelScope.launch {
            _isDarkTheme.value = isDark
            ThemePrefsRepo.setDarkTheme(getApplication(), isDark)
        }
    }
}
