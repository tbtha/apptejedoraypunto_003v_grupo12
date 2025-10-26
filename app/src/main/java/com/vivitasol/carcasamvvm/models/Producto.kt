package com.vivitasol.carcasamvvm.models

import kotlinx.serialization.Serializable

@Serializable
data class Producto(
    val id: Int,
    val nombre: String,
    val descripcion: String,
    val precio: Double,
    val stock: Int,
    val activo: Boolean,
    val imagen: String? = null,
    val categoria: Categoria? = null
)