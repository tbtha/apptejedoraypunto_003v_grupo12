package com.vivitasol.carcasamvvm.repository

import com.vivitasol.carcasamvvm.models.Inventario
import com.vivitasol.carcasamvvm.models.Producto
import com.vivitasol.carcasamvvm.models.Categoria
import com.vivitasol.carcasamvvm.models.dto.ProductoCreateDto
import com.vivitasol.carcasamvvm.models.dto.ProductoUpdateDto
import com.vivitasol.carcasamvvm.services.InventarioService
import com.vivitasol.carcasamvvm.services.InventarioServiceImpl
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map

/**
 * Repository que centraliza el acceso a datos del inventario
 * Actúa como single source of truth y abstrae la fuente de datos
 */
class InventarioRepository(
    private val inventarioService: InventarioService = InventarioServiceImpl()
) {
    // Cache local de datos
    private val _productos = MutableStateFlow<List<Producto>>(emptyList())
    val productos: Flow<List<Producto>> = _productos.asStateFlow()

    private val _categorias = MutableStateFlow<List<Categoria>>(emptyList())
    val categorias: Flow<List<Categoria>> = _categorias.asStateFlow()

    // Estado completo del inventario
    val inventario: Flow<Inventario> = combine(
        _productos,
        _categorias
    ) { productos, categorias ->
        Inventario(
            productos = productos,
            categorias = categorias,
            fechaActualizacion = java.time.LocalDateTime.now().toString()
        )
    }

    // Cargar datos desde la API
    suspend fun refreshProductos(): Result<List<Producto>> {
        return try {
            val productos = inventarioService.getProductos()
            _productos.value = productos
            Result.success(productos)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun refreshCategorias(): Result<List<Categoria>> {
        return try {
            val categorias = inventarioService.getCategorias()
            _categorias.value = categorias
            Result.success(categorias)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun refreshAll(): Result<Inventario> {
        return try {
            refreshProductos().getOrThrow()
            refreshCategorias().getOrThrow()
            val currentInventario = Inventario(
                productos = _productos.value,
                categorias = _categorias.value,
                fechaActualizacion = java.time.LocalDateTime.now().toString()
            )
            Result.success(currentInventario)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // Operaciones sobre productos
    suspend fun activarProducto(id: Int): Result<Unit> {
        return try {
            inventarioService.activarProducto(id)
            refreshProductos()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun desactivarProducto(id: Int): Result<Unit> {
        return try {
            inventarioService.desactivarProducto(id)
            refreshProductos()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun crearProducto(productoDto: ProductoCreateDto): Result<Producto> {
        return try {
            val nuevoProducto = inventarioService.crearProducto(productoDto)
            refreshProductos()
            Result.success(nuevoProducto)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun actualizarProducto(id: Int, productoDto: ProductoUpdateDto): Result<Producto> {
        return try {
            val productoActualizado = inventarioService.actualizarProducto(id, productoDto)
            refreshProductos()
            Result.success(productoActualizado)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // Operaciones sobre categorías
    suspend fun crearCategoria(categoria: Categoria): Result<Categoria> {
        return try {
            val nuevaCategoria = inventarioService.crearCategoria(categoria)
            refreshCategorias()
            Result.success(nuevaCategoria)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun actualizarCategoria(id: Int, categoria: Categoria): Result<Categoria> {
        return try {
            val categoriaActualizada = inventarioService.actualizarCategoria(id, categoria)
            refreshCategorias()
            Result.success(categoriaActualizada)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // Búsquedas y filtros
    fun buscarProductos(query: String): Flow<List<Producto>> {
        return _productos.map { productos ->
            productos.filter { producto ->
                producto.nombre.contains(query, ignoreCase = true) ||
                producto.descripcion.contains(query, ignoreCase = true)
            }
        }
    }

    fun filtrarPorCategoria(categoriaId: Int): Flow<List<Producto>> {
        return _productos.map { productos ->
            productos.filter { it.categoria?.id == categoriaId }
        }
    }

    fun obtenerProductosBajoStock(umbral: Int = 5): Flow<List<Producto>> {
        return _productos.map { productos ->
            productos.filter { it.stock < umbral }
        }
    }
}