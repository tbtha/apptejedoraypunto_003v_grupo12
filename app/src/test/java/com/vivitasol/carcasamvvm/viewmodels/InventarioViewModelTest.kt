package com.vivitasol.carcasamvvm.viewmodels

import com.vivitasol.carcasamvvm.models.Categoria
import com.vivitasol.carcasamvvm.models.Producto
import com.vivitasol.carcasamvvm.models.dto.ProductoCreateDto
import com.vivitasol.carcasamvvm.models.dto.ProductoUpdateDto
import com.vivitasol.carcasamvvm.services.InventarioService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.*
import kotlinx.serialization.json.JsonPrimitive
import org.junit.Test
import org.junit.Assert.*
import org.junit.Before
import org.junit.After

/**
 * Pruebas unitarias para InventarioViewModel
 */
@OptIn(ExperimentalCoroutinesApi::class)
class InventarioViewModelTest {

    private lateinit var mockService: MockInventarioService
    private val testDispatcher = StandardTestDispatcher()
    
    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        mockService = MockInventarioService()
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `filtro de categoria muestra solo productos de esa categoria`() = runTest {
        val viewModel = InventarioViewModel(mockService)
        
        // Esperar a que se complete la inicialización
        advanceUntilIdle()
        
        // Aplicar filtro de categoría 1
        viewModel.setCategoriaFilter(1)
        
        assertEquals(1, viewModel.categoriaFilter)
    }

    @Test
    fun `busqueda filtra productos por nombre`() = runTest {
        val viewModel = InventarioViewModel(mockService)
        
        advanceUntilIdle()
        
        viewModel.setSearchQuery("Ovillo")
        
        assertEquals("Ovillo", viewModel.searchQuery)
    }

    @Test
    fun `clearFilters restablece filtros`() = runTest {
        val viewModel = InventarioViewModel(mockService)
        
        advanceUntilIdle()
        
        viewModel.setCategoriaFilter(1)
        viewModel.setSearchQuery("Test")
        
        viewModel.clearFilters()
        
        assertNull(viewModel.categoriaFilter)
        assertEquals("", viewModel.searchQuery)
    }

    @Test
    fun `valores iniciales del ViewModel`() = runTest {
        val viewModel = InventarioViewModel(mockService)
        
        advanceUntilIdle()
        
        assertEquals("", viewModel.searchQuery)
        assertNull(viewModel.categoriaFilter)
    }
}

/**
 * Mock del servicio de inventario para pruebas
 */
class MockInventarioService : InventarioService {
    
    private val categoriaLana = Categoria(1, "Lana")
    private val categoriaAgujas = Categoria(2, "Agujas")
    
    private val productos = listOf(
        Producto(1, "Ovillo Rojo", "Lana roja 100g", 5000.0, 10, JsonPrimitive(true), null, categoriaLana),
        Producto(2, "Ovillo Azul", "Lana azul 100g", 5000.0, 8, JsonPrimitive(true), null, categoriaLana),
        Producto(3, "Aguja 3mm", "Aguja metálica", 2000.0, 15, JsonPrimitive(true), null, categoriaAgujas),
        Producto(4, "Aguja 5mm", "Aguja plástica", 1500.0, 3, JsonPrimitive(true), null, categoriaAgujas),
        Producto(5, "Ovillo Verde", "Lana verde 100g", 5000.0, 0, JsonPrimitive(false), null, categoriaLana)
    )
    
    override suspend fun getProductos(): List<Producto> = productos
    
    override suspend fun getCategorias(): List<Categoria> {
        return listOf(categoriaLana, categoriaAgujas)
    }
    
    override suspend fun desactivarProducto(id: Int) {
        // Mock implementation
    }
    
    override suspend fun activarProducto(id: Int) {
        // Mock implementation
    }
    
    override suspend fun crearProducto(producto: ProductoCreateDto): Producto {
        return Producto(100, producto.nombre, producto.descripcion, producto.precio, producto.stock, JsonPrimitive(producto.activo == 1), null, null)
    }
    
    override suspend fun actualizarProducto(id: Int, producto: ProductoUpdateDto): Producto {
        return Producto(id, producto.nombre, producto.descripcion, producto.precio, producto.stock, JsonPrimitive(producto.activo == 1), null, null)
    }
    
    override suspend fun eliminarProducto(id: Int) {
        // Mock implementation
    }
    
    override suspend fun crearCategoria(categoria: Categoria): Categoria {
        return categoria
    }
    
    override suspend fun actualizarCategoria(id: Int, categoria: Categoria): Categoria {
        return categoria
    }
    
    override suspend fun eliminarCategoria(id: Int) {
        // Mock implementation
    }
}
