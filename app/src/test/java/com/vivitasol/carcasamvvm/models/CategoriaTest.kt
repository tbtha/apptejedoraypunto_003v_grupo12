package com.vivitasol.carcasamvvm.models

import org.junit.Test
import org.junit.Assert.*

/**
 * Pruebas unitarias para el modelo Categoria
 */
class CategoriaTest {

    @Test
    fun `crear categoria con valores validos`() {
        val categoria = Categoria(
            id = 1,
            nombre = "Lana"
        )

        assertEquals(1, categoria.id)
        assertEquals("Lana", categoria.nombre)
    }

    @Test
    fun `categoria con nombre no vacio`() {
        val categoria = Categoria(
            id = 2,
            nombre = "Agujas"
        )

        assertTrue(categoria.nombre.isNotEmpty())
        assertEquals("Agujas", categoria.nombre)
    }

    @Test
    fun `categoria con id positivo`() {
        val categoria = Categoria(
            id = 5,
            nombre = "Test"
        )

        assertTrue(categoria.id > 0)
    }
}
