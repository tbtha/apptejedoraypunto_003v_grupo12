package com.vivitasol.carcasamvvm.services

import com.vivitasol.carcasamvvm.models.Categoria
import com.vivitasol.carcasamvvm.models.Producto
import com.vivitasol.carcasamvvm.models.dto.ProductoCreateDto
import com.vivitasol.carcasamvvm.models.dto.ProductoUpdateDto

interface InventarioService {
    suspend fun getProductos(): List<Producto>
    suspend fun getCategorias(): List<Categoria>
    suspend fun desactivarProducto(id: Int)
    suspend fun activarProducto(id: Int)
    suspend fun crearProducto(producto: ProductoCreateDto): Producto
    suspend fun actualizarProducto(id: Int, producto: ProductoUpdateDto): Producto
    suspend fun eliminarProducto(id: Int)
    suspend fun crearCategoria(categoria: Categoria): Categoria
    suspend fun actualizarCategoria(id: Int, categoria: Categoria): Categoria
    suspend fun eliminarCategoria(id: Int)
}