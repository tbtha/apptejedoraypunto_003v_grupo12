package com.vivitasol.carcasamvvm.services

import com.vivitasol.carcasamvvm.models.CambistaResponse
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.double
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import java.time.LocalDate
import java.time.format.DateTimeFormatter

object CurrencyConverter {
    
    private val client = HttpClient(CIO) {
        install(ContentNegotiation) {
            json(Json {
                ignoreUnknownKeys = true
                isLenient = true
            })
        }
    }
    
    // Cambista.cl API - API de tasas de cambio chilena
    private const val BASE_URL = "https://cambista.cl/api/rates_day.php"
    
    // ExchangeRate-API como fallback
    private const val FALLBACK_API = "https://api.exchangerate-api.com/v4/latest/CLP"
    
    /**
     * Obtiene tasas desde la API alternativa (ExchangeRate-API)
     */
    private suspend fun obtenerTasaFallbackAPI(codigo: String): Double? {
        return try {
            println("🔄 Intentando con API alternativa para $codigo")
            val response: JsonObject = client.get(FALLBACK_API).body()
            
            val rates = response["rates"]?.jsonObject
            val tasa = when(codigo) {
                "USD" -> rates?.get("USD")?.jsonPrimitive?.double
                "EUR" -> rates?.get("EUR")?.jsonPrimitive?.double
                else -> null
            }
            
            if (tasa != null) {
                println("✅ Tasa $codigo obtenida desde API alternativa: $tasa")
            } else {
                println("⚠️ No se encontró $codigo en API alternativa")
            }
            
            tasa
        } catch (e: Exception) {
            println("❌ Error en API alternativa para $codigo: ${e.message}")
            null
        }
    }
    
    /**
     * Función auxiliar para obtener tasa con fallback a días anteriores
     */
    private suspend fun obtenerTasaConFallback(codigo: String): Double? {
        // Intento 1: Probar con Cambista.cl (últimos 7 días)
        for (diasAtras in 0..7) {
            try {
                val fecha = LocalDate.now().minusDays(diasAtras.toLong())
                    .format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))
                val url = "$BASE_URL?codes=$codigo&date=$fecha"
                
                if (diasAtras == 0) {
                    println("🌐 Obteniendo tasa $codigo: $url")
                } else {
                    println("🔄 Reintentando con fecha anterior ($diasAtras días atrás): $url")
                }
                
                val httpResponse: HttpResponse = client.get(url)
                println("📡 Status HTTP $codigo: ${httpResponse.status}")
                
                val bodyText = httpResponse.bodyAsText()
                println("📄 Respuesta cruda (primeros 200 chars): ${bodyText.take(200)}")
                
                val response: CambistaResponse = Json.decodeFromString(bodyText)
                println("📦 Respuesta $codigo: meta.codes=${response.meta.codes}, data.size=${response.data.size}")
                
                val tasa = response.data.firstOrNull()?.rates?.get(codigo)
                
                if (tasa != null && tasa > 0) {
                    val tasaInvertida = 1.0 / tasa
                    println("✅ Tasa $codigo obtenida: $tasa CLP/$codigo (invertida: $tasaInvertida $codigo/CLP)")
                    return tasaInvertida
                } else {
                    println("⚠️ No se encontró tasa $codigo para fecha $fecha (tasa=$tasa)")
                }
            } catch (e: Exception) {
                println("❌ Error obteniendo tasa $codigo (día -$diasAtras): ${e.message}")
                println("   Tipo: ${e.javaClass.simpleName}")
                if (diasAtras == 7) {
                    e.printStackTrace()
                }
            }
        }
        
        // Intento 2: Si Cambista.cl falló, usar API alternativa
        println("⚠️ Cambista.cl no devolvió datos para $codigo. Intentando API alternativa...")
        return obtenerTasaFallbackAPI(codigo)
    }
    
    /**
     * Obtiene la tasa de cambio de USD (cuántos CLP equivalen a 1 USD)
     * Nota: La API Cambista retorna CLP/USD, que es el inverso de Frankfurter
     */
    suspend fun getTasaUSD(): Double? {
        return obtenerTasaConFallback("USD")
    }
    
    /**
     * Obtiene la tasa de cambio de EUR (cuántos CLP equivalen a 1 EUR)
     * Nota: La API Cambista retorna CLP/EUR, que es el inverso de Frankfurter
     */
    suspend fun getTasaEUR(): Double? {
        return obtenerTasaConFallback("EUR")
    }
}
