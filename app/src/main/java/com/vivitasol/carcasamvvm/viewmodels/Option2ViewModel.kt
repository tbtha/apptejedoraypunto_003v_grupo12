package com.vivitasol.carcasamvvm.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import com.vivitasol.carcasamvvm.models.Producto
import com.vivitasol.carcasamvvm.models.Categoria
import com.vivitasol.carcasamvvm.services.InventarioService
import com.vivitasol.carcasamvvm.services.InventarioServiceImpl

class Option2ViewModel(
    private val service: InventarioService = InventarioServiceImpl()
) : ViewModel() {
    
    // StateFlow para productos
    private val _productos = MutableStateFlow<List<Producto>>(emptyList())
    val productos: StateFlow<List<Producto>> = _productos.asStateFlow()

    // StateFlow para categorías
    private val _categorias = MutableStateFlow<List<Categoria>>(emptyList())
    val categorias: StateFlow<List<Categoria>> = _categorias.asStateFlow()

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
                println("🔄 Option2ViewModel: Iniciando carga de productos...")
                val productos = service.getProductos()
                println("📦 Option2ViewModel: Productos recibidos: ${productos.size}")
                _productos.value = productos
                println("✅ Option2ViewModel: Productos cargados exitosamente")
            } catch (e: Exception) {
                println("❌ Option2ViewModel: Error al cargar productos: ${e.message}")
                e.printStackTrace()
                _errorMessage.value = "Error de conexión: Verifique que el servidor esté funcionando"
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

    fun refreshProductos() {
        cargarProductos()
    }
}
