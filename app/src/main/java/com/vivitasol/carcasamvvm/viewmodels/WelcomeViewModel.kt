package com.vivitasol.carcasamvvm.viewmodels

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import com.vivitasol.carcasamvvm.R

class WelcomeViewModel : ViewModel() {
    private val _titulo = MutableStateFlow("")
    val titulo: StateFlow<String> = _titulo


    private val _logoResId = MutableStateFlow(R.drawable.logo_antiguo)
    val logoResId: StateFlow<Int> = _logoResId
}
