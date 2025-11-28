package com.vivitasol.carcasamvvm.models

import org.junit.Test
import org.junit.Assert.*

/**
 * Pruebas unitarias para el modelo Inventario
 */
class InventarioTest {

    @Test
    fun `inventario con valores por defecto`() {
        val inventario = Inventario()

        assertEquals(0, inventario.totalProductos)
        assertEquals(0, inventario.productosActivos)
        assertEquals(0, inventario.productosInactivos)
        assertEquals(0, inventario.stockBajo)
        assertNull(inventario.fechaActualizacion)
    }

    @Test
    fun `inventario con valores personalizados`() {
        val inventario = Inventario(
            totalProductos = 50,
            productosActivos = 45,
            productosInactivos = 5,
            stockBajo = 3,
            fechaActualizacion = "2025-11-27"
        )

        assertEquals(50, inventario.totalProductos)
        assertEquals(45, inventario.productosActivos)
        assertEquals(5, inventario.productosInactivos)
        assertEquals(3, inventario.stockBajo)
        assertEquals("2025-11-27", inventario.fechaActualizacion)
    }

    @Test
    fun `suma de activos e inactivos igual a total`() {
        val inventario = Inventario(
            totalProductos = 100,
            productosActivos = 80,
            productosInactivos = 20
        )

        assertEquals(
            inventario.totalProductos,
            inventario.productosActivos + inventario.productosInactivos
        )
    }

    @Test
    fun `stockBajo no puede ser mayor que total`() {
        val inventario = Inventario(
            totalProductos = 10,
            stockBajo = 3
        )

        assertTrue(inventario.stockBajo <= inventario.totalProductos)
    }
}
