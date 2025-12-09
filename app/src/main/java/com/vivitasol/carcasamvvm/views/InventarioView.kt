package com.vivitasol.carcasamvvm.views

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.foundation.background
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.HorizontalDivider
import com.vivitasol.carcasamvvm.models.Categoria
import com.vivitasol.carcasamvvm.models.Producto
import com.vivitasol.carcasamvvm.models.Inventario
import com.vivitasol.carcasamvvm.viewmodels.InventarioViewModel
import com.vivitasol.carcasamvvm.navigation.Route
import com.vivitasol.carcasamvvm.R

// ---------------------------------------------------------------------
// Composable Principal (La Vista con el ViewModel real)
// ---------------------------------------------------------------------

@Composable
fun InventarioView(
    navController: androidx.navigation.NavController? = null,
    viewModel: InventarioViewModel = viewModel()
) {
    // Observación de StateFlows del ViewModel real
    val filteredProductos by viewModel.filteredProductos.collectAsState()
    val categorias by viewModel.categorias.collectAsState()
    val inventario by viewModel.inventario.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()
    val categoriaFilter by remember { derivedStateOf { viewModel.categoriaFilter } }
    val searchQuery by remember { derivedStateOf { viewModel.searchQuery } }
    val tasaUSD by viewModel.tasaUSD.collectAsState()
    val tasaEUR by viewModel.tasaEUR.collectAsState()

    // Estado para el diálogo de confirmación (reemplaza window.confirm)
    var dialogState by remember { mutableStateOf<Pair<Producto, Boolean>?>(null) }

    // Detectar tamaño de pantalla para la adaptabilidad
    val configuration = LocalConfiguration.current
    val isLargeScreen = configuration.screenWidthDp > 800

    // DIALOGO DE CONFIRMACIÓN
    if (dialogState != null) {
        val (prod, activating) = dialogState!!
        AlertDialog(
            onDismissRequest = { dialogState = null },
            title = { Text(if (activating) "Confirmar Activación" else "Confirmar Desactivación") },
            text = { Text("¿Estás seguro de ${if (activating) "activar" else "desactivar"} el producto \"${prod.nombre}\"?") },
            confirmButton = {
                Button(
                    onClick = {
                        if (activating) {
                            viewModel.handleActivar(prod.id, prod.nombre)
                        } else {
                            viewModel.handleDesactivar(prod.id, prod.nombre)
                        }
                        dialogState = null
                    }
                ) { Text(if (activating) "Activar" else "Desactivar") }
            },
            dismissButton = {
                OutlinedButton(onClick = { dialogState = null }) {
                    Text("Cancelar")
                }
            }
        )
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = { TopAppBarInventario(viewModel, navController) }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp, vertical = 8.dp)
        ) {
            // Dashboard con métricas del inventario
            InventarioDashboard(inventario)
            
            // Mostrar tasas de cambio del día
            TasasCambioCard(tasaUSD, tasaEUR)

            // Mostrar estado de carga o error
            if (isLoading) {
                Box(
                    modifier = Modifier.fillMaxWidth().padding(16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        CircularProgressIndicator(modifier = Modifier.size(24.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Cargando productos...")
                    }
                }
            }

            errorMessage?.let { error ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = Color.Red.copy(alpha = 0.1f))
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("⚠️ Error de Conexión", fontWeight = FontWeight.Bold, color = Color.Red)
                        Text(error, color = Color.Red)
                        Button(
                            onClick = { 
                                viewModel.cargarProductos()
                                viewModel.cargarCategorias()
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = Color.Red)
                        ) {
                            Text("Reintentar", color = Color.White)
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Controles de Filtrado
            FiltrosInventario(
                categorias = categorias,
                categoriaFilter = categoriaFilter,
                searchQuery = searchQuery,
                onCategoryChange = viewModel::setCategoriaFilter,
                onSearchChange = viewModel::setSearchQuery,
                onClearFilters = viewModel::clearFilters
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Lógica de Layout Responsivo (Tabla vs. Tarjetas)
            if (isLargeScreen) {
                DesktopInventarioLayout(
                    productos = filteredProductos,
                    onActionClick = { prod, activate -> dialogState = prod to activate }
                )
            } else {
                MobileInventarioLayout(
                    productos = filteredProductos,
                    onActionClick = { prod, activate -> dialogState = prod to activate }
                )
            }
        }
    }
}

// ---------------------------------------------------------------------
// Dashboard con métricas del inventario
// ---------------------------------------------------------------------

@Composable
fun InventarioDashboard(inventario: Inventario) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                text = "Resumen del Inventario",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
            
            Spacer(modifier = Modifier.height(12.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                DashboardMetric("Total Productos", inventario.totalProductos.toString())
                DashboardMetric("Activos", inventario.productosActivos.toString(), Color.Green)
                DashboardMetric("Inactivos", inventario.productosInactivos.toString(), Color.Red)
                DashboardMetric("Stock Bajo", inventario.stockBajo.toString(), Color(0xFFFF9800))
            }
            
            if (inventario.fechaActualizacion != null) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Última actualización: ${inventario.fechaActualizacion}",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Gray
                )
            }
        }
    }
}

// ---------------------------------------------------------------------
// Card con tasas de cambio del día
// ---------------------------------------------------------------------

@Composable
fun TasasCambioCard(tasaUSD: Double?, tasaEUR: Double?) {
    // DEBUG: Siempre mostrar algo para verificar
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (tasaUSD != null || tasaEUR != null) 
                Color(0xFF1976D2).copy(alpha = 0.1f)
            else 
                Color(0xFFFF9800).copy(alpha = 0.1f)
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp)
        ) {
            // DEBUG: Mostrar estado
            // Text(
            //     "DEBUG: tasaUSD=${tasaUSD} tasaEUR=${tasaEUR}",
            //     fontSize = 10.sp,
            //     color = Color.Red,
            //     fontWeight = FontWeight.Bold
            // )
            
            if (tasaUSD != null || tasaEUR != null) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        "💱 Tasas del Día:",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF1565C0)
                    )
                    
                    if (tasaUSD != null) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Text("💵", fontSize = 18.sp)
                            Text(
                                "1 CLP = $${String.format("%.6f", tasaUSD)} USD",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Medium,
                                color = Color(0xFF2E7D32)
                            )
                        }
                    }
                    
                    if (tasaEUR != null) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Text("💶", fontSize = 18.sp)
                            Text(
                                "1 CLP = €${String.format("%.6f", tasaEUR)} EUR",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Medium,
                                color = Color(0xFF1565C0)
                            )
                        }
                    }
                }
            } else {
                Text(
                    "⏳ Cargando tasas de cambio...",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFFFF9800)
                )
            }
        }
    }
    Spacer(modifier = Modifier.height(12.dp))
}

@Composable
fun DashboardMetric(label: String, value: String, color: Color = MaterialTheme.colorScheme.onSurface) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = value,
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            color = color
        )
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = Color.Gray
        )
    }
}

// ---------------------------------------------------------------------
// Componentes Reutilizables de la UI
// ---------------------------------------------------------------------

// AppBar con botones de acción
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopAppBarInventario(
    viewModel: InventarioViewModel,
    navController: androidx.navigation.NavController? = null
) {
    TopAppBar(
        title = { Text("", fontWeight = FontWeight.Bold, fontSize = 20.sp) },
        actions = {
            Button(
                onClick = { println("Navegando a Crear Categoría") },
                modifier = Modifier.padding(horizontal = 4.dp),
                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF6200EE))
            ) {
                Text("Crear Categoría", color = Color.White)
            }

            IconButton(
                onClick = { viewModel.refreshData() }
            ) {
                Icon(
                    imageVector = Icons.Default.Refresh,
                    contentDescription = "Actualizar productos"
                )
            }

            Button(
                onClick = { 
                    navController?.navigate(Route.ProductoForm.route) ?: println("NavController no disponible")
                },
                modifier = Modifier.padding(end = 8.dp),
                shape = RoundedCornerShape(8.dp)
            ) {
                Icon(Icons.Filled.Add, contentDescription = "Crear Producto")
                Spacer(Modifier.width(4.dp))
                Text("Crear Producto")
            }
        }
    )
}

// Componente para los controles de filtro
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FiltrosInventario(
    categorias: List<Categoria>,
    categoriaFilter: Int?,
    searchQuery: String,
    onCategoryChange: (Int?) -> Unit,
    onSearchChange: (String) -> Unit,
    onClearFilters: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Dropdown de Categoría
        var expanded by remember { mutableStateOf(false) }

        OutlinedButton(onClick = { expanded = true }) {
            Text(categorias.find { it.id == categoriaFilter }?.nombre ?: "Todas las categorías")
            Spacer(Modifier.width(8.dp))
            Icon(Icons.Filled.ArrowDropDown, contentDescription = "Seleccionar Categoría")
        }
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            DropdownMenuItem(
                text = { Text("Todas las categorías") },
                onClick = { onCategoryChange(null); expanded = false }
            )
            categorias.forEach { cat ->
                DropdownMenuItem(
                    text = { Text(cat.nombre) },
                    onClick = { onCategoryChange(cat.id); expanded = false }
                )
            }
        }

        // Campo de Búsqueda
        OutlinedTextField(
            value = searchQuery,
            onValueChange = onSearchChange,
            label = { Text("Buscar por nombre...") },
            modifier = Modifier.weight(1f),
            singleLine = true,
            trailingIcon = {
                if (searchQuery.isNotEmpty() || categoriaFilter != null) {
                    IconButton(onClick = onClearFilters) {
                        Icon(Icons.Filled.Close, contentDescription = "Limpiar filtros")
                    }
                }
            }
        )
    }
}

// Layout para pantallas grandes (Tabla)
@Composable
fun DesktopInventarioLayout(
    productos: List<Producto>, 
    onActionClick: (Producto, Boolean) -> Unit
) {
    // Cabecera de la tabla
    Row(
        Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surfaceVariant)
            .padding(vertical = 8.dp, horizontal = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        TableHeaderCell("Id", 0.05f)
        TableHeaderCell("Imagen", 0.1f)
        TableHeaderCell("Nombre", 0.2f)
        TableHeaderCell("Precio", 0.15f)
        TableHeaderCell("Stock", 0.1f)
        TableHeaderCell("Categoría", 0.15f)
        TableHeaderCell("Acciones", 0.3f)
    }

    // Contenido de la tabla
    LazyColumn {
        items(productos, key = { it.id }) { prod ->
            DesktopInventarioRow(prod, onActionClick)
            HorizontalDivider()
        }
    }
}

@Composable
fun RowScope.TableHeaderCell(text: String, weight: Float) {
    Text(
        text = text,
        modifier = Modifier.weight(weight),
        fontWeight = FontWeight.Bold,
        style = MaterialTheme.typography.labelMedium
    )
}

@Composable
fun DesktopInventarioRow(
    prod: Producto, 
    onActionClick: (Producto, Boolean) -> Unit
) {
    val rowColor = if (!prod.isActivo) Color.LightGray.copy(alpha = 0.3f) else MaterialTheme.colorScheme.surface

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(rowColor)
            .padding(vertical = 8.dp, horizontal = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(prod.id.toString(), modifier = Modifier.weight(0.05f))

        // Imagen
        Box(modifier = Modifier.weight(0.1f)) {
            if (prod.imagen.isNullOrBlank()) {
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(RoundedCornerShape(4.dp))
                        .background(Color.Gray.copy(alpha = 0.5f)),
                    contentAlignment = Alignment.Center
                ) {
                    Text("N/A", color = Color.White, fontSize = 10.sp)
                }
            } else {
                // Placeholder para imagen
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(RoundedCornerShape(4.dp))
                        .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.2f)),
                    contentAlignment = Alignment.Center
                ) {
                    Text("IMG", fontSize = 10.sp)
                }
            }
        }

        Column(modifier = Modifier.weight(0.2f)) {
            Text(prod.nombre, fontWeight = FontWeight.Medium)
            Text(prod.descripcion, style = MaterialTheme.typography.bodySmall, maxLines = 1)
        }

        Column(modifier = Modifier.weight(0.15f)) {
            Text("CLP $${String.format("%.0f", prod.precio)}", fontWeight = FontWeight.SemiBold, fontSize = 12.sp)
        }

        Box(modifier = Modifier.weight(0.1f)) {
            if (prod.stock < 5) {
                // Animación pulsante MÁS NOTORIA para stock bajo
                val infiniteTransition = rememberInfiniteTransition(label = "pulse")
                val scale by infiniteTransition.animateFloat(
                    initialValue = 1f,
                    targetValue = 1.3f, // Más grande (antes 1.1)
                    animationSpec = infiniteRepeatable(
                        animation = tween(600, easing = FastOutSlowInEasing), // Más rápido (antes 800ms)
                        repeatMode = RepeatMode.Reverse
                    ),
                    label = "scale"
                )
                val alpha by infiniteTransition.animateFloat(
                    initialValue = 1f,
                    targetValue = 0.6f,
                    animationSpec = infiniteRepeatable(
                        animation = tween(600, easing = FastOutSlowInEasing),
                        repeatMode = RepeatMode.Reverse
                    ),
                    label = "alpha"
                )
                
                Badge(
                    containerColor = Color(0xFFFFCC00),
                    contentColor = Color.Black,
                    modifier = Modifier
                        .scale(scale)
                        .graphicsLayer { this.alpha = alpha }
                ) {
                    Text("${prod.stock} (Bajo)", fontWeight = FontWeight.Bold)
                }
            } else {
                Text(prod.stock.toString())
            }
        }

        Text(prod.categoria?.nombre ?: "Sin categoría", modifier = Modifier.weight(0.15f))

        Row(modifier = Modifier.weight(0.3f), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Button(
                onClick = { println("Navegando a /editar-producto/${prod.id}") },
                enabled = prod.isActivo,
                modifier = Modifier.width(80.dp),
                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary.copy(alpha = if (prod.isActivo) 1f else 0.5f)
                )
            ) { Text("Editar", fontSize = 12.sp) }

            Button(
                onClick = { onActionClick(prod, !prod.isActivo) },
                modifier = Modifier.width(100.dp),
                shape = RoundedCornerShape(8.dp),
                colors = if (prod.isActivo) ButtonDefaults.buttonColors(containerColor = Color.Red.copy(alpha = 0.8f))
                else ButtonDefaults.buttonColors(containerColor = Color.Green.copy(alpha = 0.8f))
            ) {
                Text(if (prod.isActivo) "Desactivar" else "Activar", fontSize = 12.sp)
            }
        }
    }
}

// Layout para pantallas pequeñas (Tarjetas)
@Composable
fun MobileInventarioLayout(
    productos: List<Producto>, 
    onActionClick: (Producto, Boolean) -> Unit
) {
    LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        itemsIndexed(productos, key = { _, prod -> prod.id }) { index, prod ->
            // Animación de fade-in MÁS NOTORIA para cada tarjeta
            var visible by remember { mutableStateOf(false) }
            LaunchedEffect(Unit) {
                kotlinx.coroutines.delay(index * 100L) // Más delay (antes 50ms)
                visible = true
            }
            
            AnimatedVisibility(
                visible = visible,
                enter = fadeIn(animationSpec = tween(600)) + // Más lento (antes 400ms)
                        slideInVertically(
                            animationSpec = spring(
                                dampingRatio = Spring.DampingRatioMediumBouncy,
                                stiffness = Spring.StiffnessLow
                            )
                        ) { it / 2 } + // Desliza desde más arriba (antes it/4)
                        scaleIn(
                            initialScale = 0.8f,
                            animationSpec = spring(
                                dampingRatio = Spring.DampingRatioMediumBouncy,
                                stiffness = Spring.StiffnessLow
                            )
                        )
            ) {
                ProductInventarioCard(prod, onActionClick)
            }
        }
    }
}

@Composable
fun ProductInventarioCard(
    prod: Producto, 
    onActionClick: (Producto, Boolean) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (!prod.isActivo) Color.LightGray.copy(alpha = 0.15f) else MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Text(
                    text = prod.nombre,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = if (!prod.isActivo) Color.Gray else Color.Black
                )
                if (!prod.isActivo) {
                    AssistChip(onClick = { }, label = { Text("INACTIVO") })
                }
            }

            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = prod.descripcion,
                style = MaterialTheme.typography.bodySmall,
                color = Color.DarkGray
            )

            Spacer(modifier = Modifier.height(12.dp))
            
            
            Row(modifier = Modifier.fillMaxWidth()) {
                DetailColumn("ID", prod.id.toString(), Modifier.weight(1f))
                Column(modifier = Modifier.weight(1f)) {
                    Text("Precio", style = MaterialTheme.typography.labelSmall, color = Color.Gray)
                    Spacer(Modifier.height(2.dp))
                    Text("CLP $${String.format("%.0f", prod.precio)}", 
                        style = MaterialTheme.typography.bodyMedium, 
                        fontWeight = FontWeight.SemiBold)
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
            Row(modifier = Modifier.fillMaxWidth()) {
                DetailColumn("Stock", prod.stock.toString(), Modifier.weight(1f), isLow = prod.stock < 5)
                DetailColumn("Categoría", prod.categoria?.nombre ?: "N/A", Modifier.weight(1f))
            }

            Spacer(modifier = Modifier.height(16.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Button(
                    onClick = { println("Navegando a /editar-producto/${prod.id}") },
                    enabled = prod.isActivo,
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary.copy(alpha = if (prod.isActivo) 1f else 0.5f)
                    )
                ) { Text("Editar") }

                Button(
                    onClick = { onActionClick(prod, !prod.isActivo) },
                    modifier = Modifier.weight(1f),
                    colors = if (prod.isActivo) ButtonDefaults.buttonColors(containerColor = Color.Red.copy(alpha = 0.8f))
                    else ButtonDefaults.buttonColors(containerColor = Color.Green.copy(alpha = 0.8f))
                ) {
                    Text(if (prod.isActivo) "Desactivar" else "Activar")
                }
            }
        }
    }
}

@Composable
fun DetailColumn(label: String, value: String, modifier: Modifier, isLow: Boolean = false) {
    Column(modifier = modifier) {
        Text(label, style = MaterialTheme.typography.labelSmall, color = Color.Gray)
        Spacer(Modifier.height(2.dp))
        if (isLow) {
            // Animación pulsante MÁS NOTORIA para stock bajo
            val infiniteTransition = rememberInfiniteTransition(label = "pulse")
            val scale by infiniteTransition.animateFloat(
                initialValue = 1f,
                targetValue = 1.3f, // Más grande (antes 1.1)
                animationSpec = infiniteRepeatable(
                    animation = tween(600, easing = FastOutSlowInEasing), // Más rápido (antes 800ms)
                    repeatMode = RepeatMode.Reverse
                ),
                label = "scale"
            )
            val alpha by infiniteTransition.animateFloat(
                initialValue = 1f,
                targetValue = 0.6f,
                animationSpec = infiniteRepeatable(
                    animation = tween(600, easing = FastOutSlowInEasing),
                    repeatMode = RepeatMode.Reverse
                ),
                label = "alpha"
            )
            
            Badge(
                containerColor = Color(0xFFFFCC00),
                contentColor = Color.Black,
                modifier = Modifier
                    .scale(scale)
                    .graphicsLayer { this.alpha = alpha }
            ) {
                Text("$value (Bajo)", fontWeight = FontWeight.Bold)
            }
        } else {
            Text(value, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.SemiBold)
        }
    }
}
