package com.vivitasol.carcasamvvm.services

import com.vivitasol.carcasamvvm.models.FrankfurterResponse
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.json.Json

object CurrencyConverter {
    
    private val client = HttpClient(CIO) {
        install(ContentNegotiation) {
            json(Json {
                ignoreUnknownKeys = true
                isLenient = true
            })
        }
    }
    
    // Frankfurter API - Gratuita, sin API key necesaria
    private const val BASE_URL = "https://api.frankfurter.app"
    
    /**
     * Obtiene la tasa de cambio de CLP a USD (1 CLP = X USD)
     */
    suspend fun getTasaUSD(): Double? {
        return try {
            val url = "$BASE_URL/latest?amount=1&from=CLP&to=USD"
            println("🌐 Obteniendo tasa USD: $url")
            
            val response: FrankfurterResponse = client.get(url).body()
            val tasa = response.rates["USD"]
            println("🌐 Tasa USD obtenida: $tasa")
            tasa
        } catch (e: Exception) {
            println("❌ Error obteniendo tasa USD: ${e.message}")
            e.printStackTrace()
            null
        }
    }
    
    /**
     * Obtiene la tasa de cambio de CLP a EUR (1 CLP = X EUR)
     */
    suspend fun getTasaEUR(): Double? {
        return try {
            val url = "$BASE_URL/latest?amount=1&from=CLP&to=EUR"
            println("🌐 Obteniendo tasa EUR: $url")
            
            val response: FrankfurterResponse = client.get(url).body()
            val tasa = response.rates["EUR"]
            println("🌐 Tasa EUR obtenida: $tasa")
            tasa
        } catch (e: Exception) {
            println("❌ Error obteniendo tasa EUR: ${e.message}")
            e.printStackTrace()
            null
        }
    }
}
