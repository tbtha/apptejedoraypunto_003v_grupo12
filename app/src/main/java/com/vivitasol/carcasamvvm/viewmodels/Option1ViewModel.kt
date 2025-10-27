package com.vivitasol.carcasamvvm.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import com.vivitasol.carcasamvvm.models.Producto
import com.vivitasol.carcasamvvm.models.Categoria
import com.vivitasol.carcasamvvm.models.Inventario
import com.vivitasol.carcasamvvm.services.InventarioService
import com.vivitasol.carcasamvvm.services.InventarioServiceImpl

class Option1ViewModel(
    private val service: InventarioService = InventarioServiceImpl()
) : ViewModel() {
    
    private val _texto = MutableStateFlow("Dashboard de Productos")
    val texto: StateFlow<String> = _texto

    // StateFlow para productos
    private val _productos = MutableStateFlow<List<Producto>>(emptyList())
    val productos: StateFlow<List<Producto>> = _productos.asStateFlow()

    // StateFlow para categorías
    private val _categorias = MutableStateFlow<List<Categoria>>(emptyList())
    val categorias: StateFlow<List<Categoria>> = _categorias.asStateFlow()

    // StateFlow para el inventario completo
    private val _inventario = MutableStateFlow(Inventario())
    val inventario: StateFlow<Inventario> = _inventario.asStateFlow()

    // Estado de carga
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    // Estado de error
    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    init {
        cargarProductos()
        cargarCategorias()
    }

    private fun cargarProductos() {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            try {
                println("🔄 Option1ViewModel: Iniciando carga de productos...")
                val productos = service.getProductos()
                println("📦 Option1ViewModel: Productos recibidos: ${productos.size}")
                
                if (productos.isNotEmpty()) {
                    _productos.value = productos
                    actualizarInventario()
                    println("✅ Option1ViewModel: Productos cargados exitosamente")
                } else {
                    println("⚠️ Option1ViewModel: Lista de productos vacía")
                    _errorMessage.value = "No se pudieron cargar productos desde la API"
                }
            } catch (e: Exception) {
                println("❌ Option1ViewModel: Error al cargar productos: ${e.message}")
                e.printStackTrace()
                _errorMessage.value = "Error de conexión: Verifique que el servidor esté funcionando"
                
                // Mantener datos existentes si los hay
                if (_productos.value.isEmpty()) {
                    println("ℹ️ Option1ViewModel: No hay datos previos, usando lista vacía")
                }
            } finally {
                _isLoading.value = false
            }
        }
    }

    private fun cargarCategorias() {
        viewModelScope.launch {
            try {
                val categorias = service.getCategorias()
                _categorias.value = categorias
            } catch (e: Exception) {
                _errorMessage.value = "Error al cargar categorías: ${e.message}"
            }
        }
    }

    private fun actualizarInventario() {
        val productos = _productos.value
        val categorias = _categorias.value
        
        _inventario.value = Inventario(
            productos = productos,
            categorias = categorias,
            fechaActualizacion = null,
            totalProductos = productos.size,
            productosActivos = productos.count { it.isActivo },
            productosInactivos = productos.count { !it.isActivo },
            stockBajo = productos.count { it.stock < 5 },
            valorTotalInventario = productos.filter { it.isActivo }.sumOf { it.precio * it.stock }
        )
    }

    fun refreshProductos() {
        cargarProductos()
    }
}
