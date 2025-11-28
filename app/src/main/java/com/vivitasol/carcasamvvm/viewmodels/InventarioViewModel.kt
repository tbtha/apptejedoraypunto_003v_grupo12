package com.vivitasol.carcasamvvm.viewmodels

import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import androidx.lifecycle.ViewModel // Asumiendo un entorno Android/Compose
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.lifecycle.viewModelScope
import com.vivitasol.carcasamvvm.models.Categoria
import com.vivitasol.carcasamvvm.models.Producto
import com.vivitasol.carcasamvvm.models.Inventario

// Importar el servicio desde su nueva ubicación
import com.vivitasol.carcasamvvm.services.InventarioService
import com.vivitasol.carcasamvvm.services.InventarioServiceImpl
import com.vivitasol.carcasamvvm.services.CurrencyConverter


// --- 3. ViewModel (State Management y Lógica) ---

class InventarioViewModel(
    private val service: InventarioService = InventarioServiceImpl()
) : ViewModel() { // ViewModel es la clase base para la gestión de ciclo de vida del estado

    // --- State (Reemplazo de useState) ---

    // StateFlow mantiene la lista de productos y categorías observable por la UI
    private val _productos = MutableStateFlow<List<Producto>>(emptyList())
    val productos: StateFlow<List<Producto>> = _productos.asStateFlow()

    private val _categorias = MutableStateFlow<List<Categoria>>(emptyList())
    val categorias: StateFlow<List<Categoria>> = _categorias.asStateFlow()

    // StateFlow para el estado completo del inventario
    private val _inventario = MutableStateFlow(Inventario())
    val inventario: StateFlow<Inventario> = _inventario.asStateFlow()

    // Estado de carga
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    // Estado de error
    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()
    
    // Tasas de cambio del día (1 CLP = X USD/EUR)
    private val _tasaUSD = MutableStateFlow<Double?>(null)
    val tasaUSD: StateFlow<Double?> = _tasaUSD.asStateFlow()
    
    private val _tasaEUR = MutableStateFlow<Double?>(null)
    val tasaEUR: StateFlow<Double?> = _tasaEUR.asStateFlow()

    // Filtros UI (usando Compose mutableStateOf para mayor reactividad en la UI)
    private var _categoriaFilter by mutableStateOf<Int?>(null) // null representa "Todas las categorías"
    val categoriaFilter: Int? get() = _categoriaFilter

    private var _searchQuery by mutableStateOf("")
    val searchQuery: String get() = _searchQuery

    // --- Derived State (Productos Filtrados) ---
    // Usamos 'combine' de Flow para crear una lista reactiva y filtrada,
    // equivalente al filtro en el render de React.
    val filteredProductos: StateFlow<List<Producto>> = combine(
        _productos,
        snapshotFlow { _categoriaFilter }, // Convierte mutableStateOf a Flow
        snapshotFlow { _searchQuery }      // Convierte mutableStateOf a Flow
    ) { list, catFilter, query ->
        list.filter { prod ->
            // 1. Filtrado por Categoría
            val categoryMatches = catFilter == null || prod.categoria?.id == catFilter

            // 2. Filtrado por Búsqueda (case-insensitive)
            val searchMatches = query.isBlank() || prod.nombre.contains(query, ignoreCase = true)

            categoryMatches && searchMatches
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )


    init {
        // Equivalente a useEffect con dependencia vacía ([]), se ejecuta al inicio.
        cargarProductos()
        cargarCategorias()
    }

    // --- Lógica de Negocio (Manejo de Eventos y API) ---

    fun cargarProductos() {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            try {
                println("🚀 InventarioViewModel: Iniciando carga de productos...")
                val productos = service.getProductos()
                _productos.value = productos
                println("✅ InventarioViewModel: Productos cargados: ${productos.size}")
                actualizarInventario()
                obtenerTasasCambio()
            } catch (e: Exception) {
                _errorMessage.value = "Error al cargar productos: ${e.message}"
                println("❌ InventarioViewModel: Error cargando productos: ${e.message}")
            } finally {
                _isLoading.value = false
            }
        }
    }
    
    private fun obtenerTasasCambio() {
        viewModelScope.launch {
            println("💱 Obteniendo tasas de cambio del día...")
            
            // Obtener tasa USD (1 CLP a USD)
            val tasaUSD = CurrencyConverter.getTasaUSD()
            if (tasaUSD != null) {
                _tasaUSD.value = tasaUSD
                println("✅ Tasa USD obtenida: 1 CLP = $${String.format("%.6f", tasaUSD)} USD")
            } else {
                println("❌ ERROR: No se pudo obtener la tasa USD")
            }
            
            // Obtener tasa EUR (1 CLP a EUR)  
            val tasaEUR = CurrencyConverter.getTasaEUR()
            if (tasaEUR != null) {
                _tasaEUR.value = tasaEUR
                println("✅ Tasa EUR obtenida: 1 CLP = €${String.format("%.6f", tasaEUR)} EUR")
            } else {
                println("❌ ERROR: No se pudo obtener la tasa EUR")
            }
            
            println("💱 Proceso de obtención de tasas completado")
        }
    }

    fun cargarCategorias() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                println("🚀 InventarioViewModel: Iniciando carga de categorías...")
                val categorias = service.getCategorias()
                _categorias.value = categorias
                println("✅ InventarioViewModel: Categorías cargadas: ${categorias.size}")
                actualizarInventario()
            } catch (e: Exception) {
                _errorMessage.value = "Error al cargar categorías: ${e.message}"
                println("❌ InventarioViewModel: Error cargando categorías: ${e.message}")
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun refreshData() {
        cargarProductos()
        cargarCategorias()
    }

    private fun actualizarInventario() {
        val productos = _productos.value
        val categorias = _categorias.value
        
        _inventario.value = Inventario(
            productos = productos,
            categorias = categorias,
            fechaActualizacion = java.time.LocalDateTime.now().toString()
        )
    }

    // Nota: El manejo de la confirmación (window.confirm) debe hacerse en la capa de UI.
    // Aquí solo se ejecuta la lógica si la UI confirma.
    fun handleDesactivar(id: Int, nombre: String) {
        viewModelScope.launch {
            try {
                service.desactivarProducto(id)
                println("Producto '$nombre' desactivado exitosamente.")
                cargarProductos() // Refresca la lista y actualiza el inventario
            } catch (e: Exception) {
                // Notificar error a la UI
                println("Error al desactivar el producto '$nombre': ${e.message}")
            }
        }
    }

    fun handleActivar(id: Int, nombre: String) {
        viewModelScope.launch {
            try {
                service.activarProducto(id)
                println("Producto '$nombre' activado exitosamente.")
                cargarProductos() // Refresca la lista y actualiza el inventario
            } catch (e: Exception) {
                // Notificar error a la UI
                println("Error al activar el producto '$nombre': ${e.message}")
            }
        }
    }

    // --- Handlers para Filtros ---

    fun setCategoriaFilter(categoryId: Int?) {
        _categoriaFilter = categoryId
    }

    fun setSearchQuery(query: String) {
        _searchQuery = query
    }

    fun clearFilters() {
        _searchQuery = ""
        _categoriaFilter = null
    }
}

// Nota: Para usar este ViewModel en una interfaz de usuario (por ejemplo, con Jetpack Compose):
// 1. Inicializar el ViewModel: val viewModel = viewModel<InventarioViewModel>()
// 2. Observar los datos:
//    val productosFiltrados by viewModel.filteredProductos.collectAsState()
//    val categorias by viewModel.categorias.collectAsState()
//    val filtroActual by viewModel.categoriaFilter
// 3. Llamar a los handlers desde los eventos de UI:
//    Select: viewModel.setCategoriaFilter(nuevoId)
//    Boton: viewModel.handleDesactivar(id, nombre)
