package com.vivitasol.carcasamvvm.models

import kotlinx.serialization.Serializable

@Serializable
data class Categoria(
    val id: Int,
    val nombre: String
)