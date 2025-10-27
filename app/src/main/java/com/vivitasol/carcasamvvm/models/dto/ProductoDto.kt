package com.vivitasol.carcasamvvm.models.dto

import kotlinx.serialization.Serializable

@Serializable
data class ProductoCreateDto(
    val nombre: String,
    val descripcion: String,
    val precio: Double,
    val stock: Int,
    val activo: Int = 1, // 0 = inactivo, 1 = activo
    val categoriaId: Int? = null
)

@Serializable
data class ProductoUpdateDto(
    val nombre: String,
    val descripcion: String,
    val precio: Double,
    val stock: Int,
    val activo: Int, // 0 = inactivo, 1 = activo
    val categoriaId: Int? = null
)