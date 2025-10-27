package com.vivitasol.carcasamvvm.services

import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.json.Json
import io.ktor.client.engine.cio.*
import io.ktor.http.*
import com.vivitasol.carcasamvvm.models.dto.ProductoCreateDto
import com.vivitasol.carcasamvvm.models.dto.ProductoUpdateDto
import com.vivitasol.carcasamvvm.models.Categoria
import com.vivitasol.carcasamvvm.models.Producto

class InventarioServiceImpl : InventarioService {
    // Para emulador Android: usar 10.0.2.2 en lugar de localhost
    // Para dispositivo físico: usar la IP de tu PC (ej: 192.168.1.100:8082)
    private val BASE_URL = "http://10.0.2.2:8082/api"

    // Configuración del cliente Ktor para manejar JSON
    private val client = HttpClient(CIO) {
        install(ContentNegotiation) {
            json(Json {
                ignoreUnknownKeys = true
                coerceInputValues = true
            })
        }
        
        // Configuración de timeout
        engine {
            requestTimeout = 30_000 // 30 segundos
        }
    }

    override suspend fun getProductos(): List<Producto> {
        return try {
            println("🔄 Iniciando petición a: $BASE_URL/productos")
            val response = client.get("$BASE_URL/productos")
            println("✅ Respuesta recibida - Status: ${response.status}")
            
            // Log del JSON raw para debugging
            val jsonString = response.body<String>()
            println("🔍 JSON raw recibido: ${jsonString.take(200)}...")
            
            // Convertir JSON a lista de productos
            val data: List<Producto> = Json {
                ignoreUnknownKeys = true
                coerceInputValues = true
            }.decodeFromString(jsonString)
            
            println("📦 Productos obtenidos: ${data.size}")
            
            // Log de un producto ejemplo para verificar el campo activo
            if (data.isNotEmpty()) {
                val ejemplo = data.first()
                println("🔍 Producto ejemplo - activo: ${ejemplo.activo} (${ejemplo.activo::class.simpleName})")
                println("🔍 Producto ejemplo - isActivo: ${ejemplo.isActivo}")
            }
            
            // Aplicar la normalización de la imagen
            val normalizedData = data.map { p ->
                p.copy(imagen = p.imagen?.takeIf { it.isNotBlank() })
            }
            println("🔧 Productos normalizados: ${normalizedData.size}")
            normalizedData
        } catch (e: Exception) {
            println("❌ Error al obtener los productos: ${e.message}")
            println("🔍 Tipo de error: ${e.javaClass.simpleName}")
            e.printStackTrace()
            emptyList()
        }
    }

    override suspend fun getCategorias(): List<Categoria> {
        return try {
            println("🔄 Iniciando petición a: $BASE_URL/categorias")
            val response = client.get("$BASE_URL/categorias")
            println("✅ Respuesta recibida - Status: ${response.status}")
            val data: List<Categoria> = response.body()
            println("📁 Categorías obtenidas: ${data.size}")
            data
        } catch (e: Exception) {
            println("❌ Error al obtener categorías: ${e.message}")
            println("🔍 Tipo de error: ${e.javaClass.simpleName}")
            e.printStackTrace()
            emptyList()
        }
    }

    override suspend fun desactivarProducto(id: Int) {
        try {
            client.patch("$BASE_URL/productos/$id/desactivar")
        } catch (e: Exception) {
            println("Error al desactivar producto $id: ${e.message}")
            throw e
        }
    }

    override suspend fun activarProducto(id: Int) {
        try {
            client.patch("$BASE_URL/productos/$id/activar")
        } catch (e: Exception) {
            println("Error al activar producto $id: ${e.message}")
            throw e
        }
    }

    override suspend fun crearProducto(producto: ProductoCreateDto): Producto {
        return try {
            client.post("$BASE_URL/productos") {
                contentType(ContentType.Application.Json)
                setBody(producto)
            }.body<Producto>()
        } catch (e: Exception) {
            println("Error al crear producto: ${e.message}")
            throw e
        }
    }

    override suspend fun actualizarProducto(id: Int, producto: ProductoUpdateDto): Producto {
        return try {
            client.put("$BASE_URL/productos/$id") {
                contentType(ContentType.Application.Json)
                setBody(producto)
            }.body<Producto>()
        } catch (e: Exception) {
            println("Error al actualizar producto $id: ${e.message}")
            throw e
        }
    }

    override suspend fun eliminarProducto(id: Int) {
        try {
            client.delete("$BASE_URL/productos/$id")
        } catch (e: Exception) {
            println("Error al eliminar producto $id: ${e.message}")
            throw e
        }
    }

    override suspend fun crearCategoria(categoria: Categoria): Categoria {
        return try {
            client.post("$BASE_URL/categorias") {
                contentType(ContentType.Application.Json)
                setBody(categoria)
            }.body<Categoria>()
        } catch (e: Exception) {
            println("Error al crear categoría: ${e.message}")
            throw e
        }
    }

    override suspend fun actualizarCategoria(id: Int, categoria: Categoria): Categoria {
        return try {
            client.put("$BASE_URL/categorias/$id") {
                contentType(ContentType.Application.Json)
                setBody(categoria)
            }.body<Categoria>()
        } catch (e: Exception) {
            println("Error al actualizar categoría $id: ${e.message}")
            throw e
        }
    }

    override suspend fun eliminarCategoria(id: Int) {
        try {
            client.delete("$BASE_URL/categorias/$id")
        } catch (e: Exception) {
            println("Error al eliminar categoría $id: ${e.message}")
            throw e
        }
    }
}