package com.vivitasol.carcasamvvm.views

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.vivitasol.carcasamvvm.models.Categoria
import com.vivitasol.carcasamvvm.models.Producto
import com.vivitasol.carcasamvvm.viewmodels.ProductoFormViewModel

/**
 * Vista del formulario para crear/editar productos
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductoFormView(
    onNavigateBack: () -> Unit,
    productoId: Int? = null, // null para crear, id para editar
    viewModel: ProductoFormViewModel = viewModel()
) {
    val categorias by viewModel.categorias.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()
    val successMessage by viewModel.successMessage.collectAsState()
    
    // Estados del formulario
    var nombre by remember { mutableStateOf("") }
    var descripcion by remember { mutableStateOf("") }
    var precio by remember { mutableStateOf("") }
    var stock by remember { mutableStateOf("") }
    var categoriaSeleccionada by remember { mutableStateOf<Categoria?>(null) }
    var activo by remember { mutableStateOf(true) } // Boolean para la UI
    var imagen by remember { mutableStateOf("") }
    
    // Dropdown estado
    var expanded by remember { mutableStateOf(false) }
    
    // SnackBar
    val snackbarHostState = remember { SnackbarHostState() }
    
    // Efectos para mostrar mensajes
    LaunchedEffect(errorMessage) {
        errorMessage?.let {
            snackbarHostState.showSnackbar(
                message = it,
                actionLabel = "Cerrar"
            )
        }
    }
    
    LaunchedEffect(successMessage) {
        successMessage?.let {
            snackbarHostState.showSnackbar(
                message = it,
                actionLabel = "OK"
            )
            // Navegar de vuelta después de éxito
            onNavigateBack()
        }
    }
    
    // Cargar datos si es edición
    LaunchedEffect(productoId) {
        viewModel.cargarCategorias()
        productoId?.let {
            viewModel.cargarProducto(it) { producto ->
                nombre = producto.nombre
                descripcion = producto.descripcion
                precio = producto.precio.toString()
                stock = producto.stock.toString()
//                categoriaSeleccionada = producto.categoria
                activo = producto.isActivo // Usar la propiedad de conveniencia
                imagen = producto.imagen ?: ""
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { 
                    Text(
                        text = if (productoId == null) "Crear Producto" else "Editar Producto",
                        fontWeight = FontWeight.Bold
                    ) 
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Volver"
                        )
                    }
                },
                actions = {
                    IconButton(
                        onClick = {
                            val precioDouble = precio.toDoubleOrNull()
                            val stockInt = stock.toIntOrNull()
                            
                            if (nombre.isBlank()) {
                                viewModel.setError("El nombre es requerido")
                                return@IconButton
                            }
                            if (precioDouble == null || precioDouble <= 0) {
                                viewModel.setError("El precio debe ser un número válido mayor a 0")
                                return@IconButton
                            }
                            if (stockInt == null || stockInt < 0) {
                                viewModel.setError("El stock debe ser un número válido mayor o igual a 0")
                                return@IconButton
                            }
                            
                            if (productoId == null) {
                                viewModel.crearProducto(
                                    nombre = nombre,
                                    descripcion = descripcion,
                                    precio = precioDouble,
                                    stock = stockInt,
                                    activo = activo,
                                    categoriaId = categoriaSeleccionada?.id
                                )
                            } else {
                                viewModel.actualizarProducto(
                                    id = productoId,
                                    nombre = nombre,
                                    descripcion = descripcion,
                                    precio = precioDouble,
                                    stock = stockInt,
                                    activo = activo,
                                    categoriaId = categoriaSeleccionada?.id
                                )
                            }
                        },
                        enabled = !isLoading
                    ) {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = "Guardar"
                        )
                    }
                }
            )
        },
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) }
    ) { paddingValues ->
        
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            
            // Nombre del producto
            OutlinedTextField(
                value = nombre,
                onValueChange = { nombre = it },
                label = { Text("Nombre del producto *") },
                modifier = Modifier.fillMaxWidth(),
                enabled = !isLoading,
                singleLine = true
            )
            
            // Descripción
            OutlinedTextField(
                value = descripcion,
                onValueChange = { descripcion = it },
                label = { Text("Descripción") },
                modifier = Modifier.fillMaxWidth(),
                enabled = !isLoading,
                minLines = 3,
                maxLines = 5
            )
            
            // Precio
            OutlinedTextField(
                value = precio,
                onValueChange = { precio = it },
                label = { Text("Precio *") },
                modifier = Modifier.fillMaxWidth(),
                enabled = !isLoading,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                singleLine = true,
                prefix = { Text("$") }
            )
            
            // Stock
            OutlinedTextField(
                value = stock,
                onValueChange = { stock = it },
                label = { Text("Stock *") },
                modifier = Modifier.fillMaxWidth(),
                enabled = !isLoading,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                singleLine = true
            )
            
            // Categoría
            ExposedDropdownMenuBox(
                expanded = expanded,
                onExpandedChange = { expanded = !expanded && !isLoading }
            ) {
                OutlinedTextField(
                    value = categoriaSeleccionada?.nombre ?: "",
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Categoría") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .menuAnchor(),
                    enabled = !isLoading
                )
                
                ExposedDropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false }
                ) {
                    // Opción "Sin categoría"
                    DropdownMenuItem(
                        text = { Text("Sin categoría") },
                        onClick = {
                            categoriaSeleccionada = null
                            expanded = false
                        }
                    )
                    
                    // Categorías disponibles
                    categorias.forEach { categoria ->
                        DropdownMenuItem(
                            text = { Text(categoria.nombre) },
                            onClick = {
                                categoriaSeleccionada = categoria
                                expanded = false
                            }
                        )
                    }
                }
            }
            
            // URL de imagen (opcional)
            OutlinedTextField(
                value = imagen,
                onValueChange = { imagen = it },
                label = { Text("URL de imagen (opcional)") },
                modifier = Modifier.fillMaxWidth(),
                enabled = !isLoading,
                singleLine = true
            )
            
            // Estado activo/inactivo
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Switch(
                    checked = activo,
                    onCheckedChange = { activo = it },
                    enabled = !isLoading
                )
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = if (activo) "Producto activo" else "Producto inactivo",
                    style = MaterialTheme.typography.bodyMedium
                )
            }
            
            Spacer(modifier = Modifier.height(32.dp))
            
            // Botón de acción
            Button(
                onClick = {
                    val precioDouble = precio.toDoubleOrNull()
                    val stockInt = stock.toIntOrNull()
                    
                    if (nombre.isBlank()) {
                        viewModel.setError("El nombre es requerido")
                        return@Button
                    }
                    if (precioDouble == null || precioDouble <= 0) {
                        viewModel.setError("El precio debe ser un número válido mayor a 0")
                        return@Button
                    }
                    if (stockInt == null || stockInt < 0) {
                        viewModel.setError("El stock debe ser un número válido mayor o igual a 0")
                        return@Button
                    }
                    
                    if (productoId == null) {
                        viewModel.crearProducto(
                            nombre = nombre,
                            descripcion = descripcion,
                            precio = precioDouble,
                            stock = stockInt,
                            activo = activo,
                            categoriaId = categoriaSeleccionada?.id
                        )
                    } else {
                        viewModel.actualizarProducto(
                            id = productoId,
                            nombre = nombre,
                            descripcion = descripcion,
                            precio = precioDouble,
                            stock = stockInt,
                            activo = activo,
                            categoriaId = categoriaSeleccionada?.id
                        )
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                enabled = !isLoading,
                shape = RoundedCornerShape(8.dp)
            ) {
                if (isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(20.dp),
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                } else {
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (productoId == null) "Crear Producto" else "Guardar Cambios",
                        fontWeight = FontWeight.Bold
                    )
                }
            }
            
            // Texto de ayuda
            Text(
                text = "Los campos marcados con * son obligatorios",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(top = 8.dp)
            )
        }
    }
}