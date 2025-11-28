package com.vivitasol.carcasamvvm.models

import org.junit.Test
import org.junit.Assert.*

/**
 * Pruebas unitarias para el modelo FrankfurterResponse (API de monedas)
 */
class FrankfurterResponseTest {

    @Test
    fun `crear respuesta con tasas USD y EUR`() {
        val response = FrankfurterResponse(
            amount = 1.0,
            base = "CLP",
            date = "2025-11-27",
            rates = mapOf("USD" to 0.001054, "EUR" to 0.000968)
        )

        assertEquals(1.0, response.amount, 0.0001)
        assertEquals("CLP", response.base)
        assertEquals("2025-11-27", response.date)
        assertEquals(0.001054, response.rates["USD"]!!, 0.000001)
        assertEquals(0.000968, response.rates["EUR"]!!, 0.000001)
    }

    @Test
    fun `obtener tasa USD del mapa rates`() {
        val response = FrankfurterResponse(
            amount = 1.0,
            base = "CLP",
            date = "2025-11-27",
            rates = mapOf("USD" to 0.001054)
        )

        assertNotNull(response.rates["USD"])
        assertTrue(response.rates["USD"]!! > 0)
    }

    @Test
    fun `rates puede contener multiples monedas`() {
        val response = FrankfurterResponse(
            amount = 1.0,
            base = "CLP",
            date = "2025-11-27",
            rates = mapOf(
                "USD" to 0.001054,
                "EUR" to 0.000968,
                "GBP" to 0.000832
            )
        )

        assertEquals(3, response.rates.size)
        assertTrue(response.rates.containsKey("USD"))
        assertTrue(response.rates.containsKey("EUR"))
        assertTrue(response.rates.containsKey("GBP"))
    }
}
