package com.vivitasol.carcasamvvm.views

import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.material3.Divider
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.vivitasol.carcasamvvm.viewmodels.Option1ViewModel
import com.vivitasol.carcasamvvm.models.Producto
import com.vivitasol.carcasamvvm.models.Inventario
import com.vivitasol.carcasamvvm.R

/**
 * Dashboard de Productos - Vista con métricas y productos destacados
 * Consume productos de la misma manera que inventario pero con estructura de Option1
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Option1View(
    vm: Option1ViewModel = viewModel(),
    @DrawableRes imageRes: Int? = R.drawable.servel
) {
    val titulo by vm.texto.collectAsState()
    val productos by vm.productos.collectAsState()
    val inventario by vm.inventario.collectAsState()
    val isLoading by vm.isLoading.collectAsState()
    val errorMessage by vm.errorMessage.collectAsState()

    // Ejemplo de estado local para el contador de clicks (mantener funcionalidad original)
    var contador by rememberSaveable { mutableStateOf(0) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(titulo, fontWeight = FontWeight.Bold) },
                actions = {
                    IconButton(
                        onClick = { vm.refreshProductos() }
                    ) {
                        Icon(
                            imageVector = Icons.Default.Refresh,
                            contentDescription = "Actualizar datos"
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        
        when {
            isLoading -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }
            
            !errorMessage.isNullOrEmpty() -> {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.errorContainer
                        )
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(
                                imageVector = Icons.Default.Warning,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.onErrorContainer
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "Error",
                                style = MaterialTheme.typography.titleMedium,
                                color = MaterialTheme.colorScheme.onErrorContainer
                            )
                            Text(
                                text = errorMessage ?: "",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onErrorContainer,
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }
            }
            
            else -> {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    
                    // Dashboard con métricas del inventario
                    item {
                        DashboardMetricas(inventario)
                    }
                    
                    // Divider
                    item {
                        Divider()
                    }
                    
                    // Productos con stock bajo (si los hay)
                    if (productos.any { it.stock < 5 }) {
                        item {
                            ProductosStockBajo(productos.filter { it.stock < 5 })
                        }
                    }
                    
                    // Productos destacados (últimos productos activos)
                    item {
                        ProductosDestacados(productos.filter { it.isActivo }.take(5))
                    }
                    
                    // Sección interactiva original (mantener para demostración)
//                    item {
//                        SeccionInteractiva(
//                            contador = contador,
//                            onIncrementarContador = { contador++ },
//                            imageRes = imageRes
//                        )
//                    }
                }
            }
        }
    }
}

@Composable
private fun DashboardMetricas(inventario: Inventario) {
    Column {
        Text(
            text = "Métricas del Inventario",
            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
            modifier = Modifier.padding(bottom = 12.dp)
        )
        
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                MetricaCard(
                    MetricaItem(
                        icon = Icons.Default.Settings,
                        titulo = "Total Productos",
                        valor = inventario.totalProductos.toString(),
                        color = MaterialTheme.colorScheme.primary
                    )
                )
            }
            item {
                MetricaCard(
                    MetricaItem(
                        icon = Icons.Default.Done,
                        titulo = "Activos",
                        valor = inventario.productosActivos.toString(),
                        color = Color(0xFF4CAF50)
                    )
                )
            }
            item {
                MetricaCard(
                    MetricaItem(
                        icon = Icons.Default.Clear,
                        titulo = "Inactivos",
                        valor = inventario.productosInactivos.toString(),
                        color = Color(0xFFF44336)
                    )
                )
            }
            item {
                MetricaCard(
                    MetricaItem(
                        icon = Icons.Default.Warning,
                        titulo = "Stock Bajo",
                        valor = inventario.stockBajo.toString(),
                        color = Color(0xFFFF9800)
                    )
                )
            }
            item {
                MetricaCard(
                    MetricaItem(
                        icon = Icons.Default.Star,
                        titulo = "Valor Total",
                        valor = "$${String.format("%.0f", inventario.valorTotalInventario)}",
                        color = MaterialTheme.colorScheme.tertiary
                    )
                )
            }
        }
    }
}

@Composable
private fun MetricaCard(metrica: MetricaItem) {
    Card(
        modifier = Modifier
            .width(140.dp)
            .height(100.dp),
        colors = CardDefaults.cardColors(containerColor = metrica.color.copy(alpha = 0.1f))
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Icon(
                imageVector = metrica.icon,
                contentDescription = null,
                tint = metrica.color,
                modifier = Modifier.size(24.dp)
            )
            
            Column {
                Text(
                    text = metrica.valor,
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                    color = metrica.color
                )
                Text(
                    text = metrica.titulo,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}

@Composable
private fun ProductosStockBajo(productos: List<Producto>) {
    Column {
        Text(
            text = "⚠️ Productos con Stock Bajo",
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
            color = Color(0xFFFF9800),
            modifier = Modifier.padding(bottom = 8.dp)
        )
        
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(productos) { producto ->
                ProductoCard(producto, showStock = true)
            }
        }
    }
}

@Composable
private fun ProductosDestacados(productos: List<Producto>) {
    Column {
        Text(
            text = "Productos Destacados",
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
            modifier = Modifier.padding(bottom = 8.dp)
        )
        
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(productos) { producto ->
                ProductoCard(producto, showStock = false)
            }
        }
    }
}

@Composable
private fun ProductoCard(producto: Producto, showStock: Boolean) {
    Card(
        modifier = Modifier
            .width(160.dp)
            .height(120.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = producto.nombre,
                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
            
            Column {
                Text(
                    text = "$${String.format("%.2f", producto.precio)}",
                    style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.primary
                )
                
                if (showStock) {
                    Text(
                        text = "Stock: ${producto.stock}",
                        style = MaterialTheme.typography.bodySmall,
                        color = if (producto.stock < 5) Color.Red else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                
                if (producto.categoria != null) {
                    Text(
                        text = producto.categoria.nombre,
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.tertiary,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
        }
    }
}

@Composable
private fun SeccionInteractiva(
    contador: Int,
    onIncrementarContador: () -> Unit,
    imageRes: Int?
) {
    Column {
        Text(
            text = "Sección Interactiva",
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
            modifier = Modifier.padding(bottom = 8.dp)
        )
        
        Text(
            text = "Esta sección mantiene la funcionalidad original de demostración.",
            style = MaterialTheme.typography.bodyMedium,
            textAlign = TextAlign.Center
        )
        
        Spacer(Modifier.height(16.dp))
        
        // Card original con imagen
        Card(
            modifier = Modifier.fillMaxWidth(),
            elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
            shape = RoundedCornerShape(12.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                if (imageRes != null) {
                    Image(
                        painter = painterResource(id = imageRes),
                        contentDescription = "Imagen de demostración",
                        modifier = Modifier
                            .size(100.dp)
                            .clip(RoundedCornerShape(8.dp)),
                        contentScale = ContentScale.Crop
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                }
                
                Text(
                    text = "Demostración de Componentes",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                Text(
                    text = "Has hecho clic $contador veces",
                    style = MaterialTheme.typography.bodyMedium
                )
                
                Spacer(modifier = Modifier.height(12.dp))
                
                Button(
                    onClick = onIncrementarContador,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(
                        imageVector = Icons.Default.Info,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Incrementar contador")
                }
            }
        }
    }
}

// Data class para las métricas
private data class MetricaItem(
    val icon: ImageVector,
    val titulo: String,
    val valor: String,
    val color: Color
)
