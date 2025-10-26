package com.vivitasol.carcasamvvm.models

import kotlinx.serialization.Serializable

/**
 * Modelo que representa el estado general del inventario
 * Incluye listas de productos y categorías, así como métricas útiles
 */
@Serializable
data class Inventario(
    val productos: List<Producto> = emptyList(),
    val categorias: List<Categoria> = emptyList(),
    val fechaActualizacion: String? = null,
    val totalProductos: Int = productos.size,
    val productosActivos: Int = productos.count { it.activo },
    val productosInactivos: Int = productos.count { !it.activo },
    val stockBajo: Int = productos.count { it.stock < 5 },
    val valorTotalInventario: Double = productos.filter { it.activo }.sumOf { it.precio * it.stock }
)