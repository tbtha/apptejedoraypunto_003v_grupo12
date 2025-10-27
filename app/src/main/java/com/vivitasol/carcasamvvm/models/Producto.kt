package com.vivitasol.carcasamvvm.models

import kotlinx.serialization.Serializable
import kotlinx.serialization.SerialName
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.boolean
import kotlinx.serialization.json.booleanOrNull
import kotlinx.serialization.json.intOrNull

@Serializable
data class Producto(
    val id: Int,
    val nombre: String,
    val descripcion: String,
    val precio: Double,
    val stock: Int,
    @SerialName("activo")
    private val _activo: JsonElement, // Recibe como JsonElement para manejar múltiples tipos
    val imagen: String? = null,
    val categoria: Categoria? = null
) {
    // Convertir el JsonElement a Int (0 o 1)
    val activo: Int
        get() = when {
            _activo is JsonPrimitive && _activo.booleanOrNull != null -> 
                if (_activo.boolean) 1 else 0
            _activo is JsonPrimitive && _activo.intOrNull != null -> 
                if (_activo.intOrNull == 1) 1 else 0
            else -> 1 // Por defecto activo
        }
    
    // Función de conveniencia para trabajar con boolean en la UI
    val isActivo: Boolean get() = activo == 1
}