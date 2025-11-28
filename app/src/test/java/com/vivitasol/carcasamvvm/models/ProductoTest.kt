package com.vivitasol.carcasamvvm.models

import org.junit.Test
import org.junit.Assert.*
import kotlinx.serialization.json.JsonPrimitive

/**
 * Pruebas unitarias para el modelo Producto
 */
class ProductoTest {

    @Test
    fun `crear producto con valores validos`() {
        val categoria = Categoria(id = 1, nombre = "Lana")
        val producto = Producto(
            id = 1,
            nombre = "Ovillo Rojo",
            descripcion = "Lana roja 100g",
            precio = 5000.0,
            stock = 10,
            _activo = JsonPrimitive(true),
            imagen = null,
            categoria = categoria
        )

        assertEquals(1, producto.id)
        assertEquals("Ovillo Rojo", producto.nombre)
        assertEquals(5000.0, producto.precio, 0.01)
        assertEquals(10, producto.stock)
        assertTrue(producto.isActivo)
        assertEquals(categoria, producto.categoria)
    }

    @Test
    fun `producto inactivo tiene isActivo en false`() {
        val producto = Producto(
            id = 2,
            nombre = "Test",
            descripcion = "Test",
            precio = 1000.0,
            stock = 5,
            _activo = JsonPrimitive(false),
            imagen = null,
            categoria = null
        )

        assertFalse(producto.isActivo)
    }

    @Test
    fun `producto con stock cero`() {
        val producto = Producto(
            id = 3,
            nombre = "Sin Stock",
            descripcion = "Test",
            precio = 2000.0,
            stock = 0,
            _activo = JsonPrimitive(true),
            imagen = null,
            categoria = null
        )

        assertEquals(0, producto.stock)
    }

    @Test
    fun `producto puede tener categoria null`() {
        val producto = Producto(
            id = 4,
            nombre = "Sin Categoria",
            descripcion = "Test",
            precio = 3000.0,
            stock = 5,
            _activo = JsonPrimitive(true),
            imagen = null,
            categoria = null
        )

        assertNull(producto.categoria)
    }

    @Test
    fun `precio debe ser positivo`() {
        val producto = Producto(
            id = 5,
            nombre = "Producto",
            descripcion = "Test",
            precio = 15000.0,
            stock = 3,
            _activo = JsonPrimitive(true),
            imagen = null,
            categoria = null
        )

        assertTrue(producto.precio > 0)
    }
}
