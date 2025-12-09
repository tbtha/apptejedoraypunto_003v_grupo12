package com.vivitasol.carcasamvvm.models

import org.junit.Test
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue

/**
 * Pruebas unitarias para los modelos de API Cambista.cl
 * API chilena de tasas de cambio
 */
class CambistaResponseTest {

    @Test
    fun `crear respuesta Cambista con tasas USD y EUR`() {
        val response = CambistaResponse(
            meta = CambistaMeta(
                tz = "America/Santiago",
                codes = listOf("USD", "EUR"),
                carried_forward = true,
                date = "2025-08-31"
            ),
            data = listOf(
                CambistaData(
                    date = "2025-08-31",
                    rates = mapOf("USD" to 967.48, "EUR" to 1130.12)
                )
            )
        )

        assertEquals("America/Santiago", response.meta.tz)
        assertEquals(2, response.meta.codes.size)
        assertEquals("2025-08-31", response.meta.date)
        assertEquals(967.48, response.data[0].rates["USD"]!!, 0.01)
        assertEquals(1130.12, response.data[0].rates["EUR"]!!, 0.01)
    }

    @Test
    fun `obtener tasa USD del mapa rates de Cambista`() {
        val response = CambistaResponse(
            meta = CambistaMeta(
                tz = "America/Santiago",
                codes = listOf("USD"),
                carried_forward = false,
                date = "2025-08-31"
            ),
            data = listOf(
                CambistaData(
                    date = "2025-08-31",
                    rates = mapOf("USD" to 967.48)
                )
            )
        )

        val tasaUSD = response.data.firstOrNull()?.rates?.get("USD")
        assertNotNull(tasaUSD)
        assertTrue(tasaUSD!! > 0)
    }

    @Test
    fun `rates de Cambista puede contener multiples monedas`() {
        val response = CambistaResponse(
            meta = CambistaMeta(
                tz = "America/Santiago",
                codes = listOf("USD", "EUR", "GBP"),
                carried_forward = false,
                date = "2025-08-31"
            ),
            data = listOf(
                CambistaData(
                    date = "2025-08-31",
                    rates = mapOf(
                        "USD" to 967.48,
                        "EUR" to 1130.12,
                        "GBP" to 1320.50
                    )
                )
            )
        )

        val rates = response.data.firstOrNull()?.rates
        assertNotNull(rates)
        assertEquals(3, rates!!.size)
        assertTrue(rates.containsKey("USD"))
        assertTrue(rates.containsKey("EUR"))
        assertTrue(rates.containsKey("GBP"))
    }
}
