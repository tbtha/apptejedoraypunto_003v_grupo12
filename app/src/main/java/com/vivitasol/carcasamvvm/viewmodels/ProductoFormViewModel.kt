package com.vivitasol.carcasamvvm.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import com.vivitasol.carcasamvvm.models.Categoria
import com.vivitasol.carcasamvvm.models.Producto
import com.vivitasol.carcasamvvm.models.dto.ProductoCreateDto
import com.vivitasol.carcasamvvm.models.dto.ProductoUpdateDto
import com.vivitasol.carcasamvvm.services.InventarioService
import com.vivitasol.carcasamvvm.services.InventarioServiceImpl

class ProductoFormViewModel(
    private val service: InventarioService = InventarioServiceImpl()
) : ViewModel() {

    // StateFlow para categorías
    private val _categorias = MutableStateFlow<List<Categoria>>(emptyList())
    val categorias: StateFlow<List<Categoria>> = _categorias.asStateFlow()

    // Estado de carga
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    // Estado de error
    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    // Estado de éxito
    private val _successMessage = MutableStateFlow<String?>(null)
    val successMessage: StateFlow<String?> = _successMessage.asStateFlow()

    init {
        cargarCategorias()
    }

    fun cargarCategorias() {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                println("🔄 Cargando categorías...")
                
                val categorias = service.getCategorias()
                _categorias.value = categorias
                
                println("✅ Categorías cargadas: ${categorias.size}")
            } catch (e: Exception) {
                println("❌ Error al cargar categorías: ${e.message}")
                _errorMessage.value = "Error al cargar categorías: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun cargarProducto(id: Int, onProductoCargado: (Producto) -> Unit) {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                println("🔄 Cargando producto con ID: $id")
                
                val productos = service.getProductos()
                val producto = productos.find { it.id == id }
                
                if (producto != null) {
                    println("✅ Producto encontrado: ${producto.nombre}")
                    onProductoCargado(producto)
                } else {
                    println("❌ Producto no encontrado con ID: $id")
                    _errorMessage.value = "Producto no encontrado"
                }
            } catch (e: Exception) {
                println("❌ Error al cargar producto: ${e.message}")
                _errorMessage.value = "Error al cargar producto: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun crearProducto(
        nombre: String,
        descripcion: String,
        precio: Double,
        stock: Int,
        activo: Boolean,
        categoriaId: Int?
    ) {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                _errorMessage.value = null
                
                println("🔄 Creando producto: $nombre")
                
                val productoDto = ProductoCreateDto(
                    nombre = nombre,
                    descripcion = descripcion,
                    precio = precio,
                    stock = stock,
                    activo = if (activo) 1 else 0,
                    categoriaId = categoriaId
                )
                
                // Llamar al servicio real para crear el producto
                val productoCreado = service.crearProducto(productoDto)
                
                println("✅ Producto creado con ID: ${productoCreado.id}")
                _successMessage.value = "Producto '${productoCreado.nombre}' creado exitosamente"
                
            } catch (e: Exception) {
                println("❌ Error al crear producto: ${e.message}")
                _errorMessage.value = "Error al crear producto: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun actualizarProducto(
        id: Int,
        nombre: String,
        descripcion: String,
        precio: Double,
        stock: Int,
        activo: Boolean,
        categoriaId: Int?
    ) {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                _errorMessage.value = null
                
                println("🔄 Actualizando producto: $nombre")
                
                val productoDto = ProductoUpdateDto(
                    nombre = nombre,
                    descripcion = descripcion,
                    precio = precio,
                    stock = stock,
                    activo = if (activo) 1 else 0,
                    categoriaId = categoriaId
                )
                
                // Llamar al servicio real para actualizar el producto
                val productoActualizado = service.actualizarProducto(id, productoDto)
                
                println("✅ Producto actualizado: ${productoActualizado.nombre}")
                _successMessage.value = "Producto '${productoActualizado.nombre}' actualizado exitosamente"
                
            } catch (e: Exception) {
                println("❌ Error al actualizar producto: ${e.message}")
                _errorMessage.value = "Error al actualizar producto: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun setError(message: String) {
        _errorMessage.value = message
    }

    fun clearMessages() {
        _errorMessage.value = null
        _successMessage.value = null
    }

}
