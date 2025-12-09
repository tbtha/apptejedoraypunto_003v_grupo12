package com.vivitasol.carcasamvvm.views

import androidx.compose.animation.core.*
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.*
import kotlinx.coroutines.launch
import com.vivitasol.carcasamvvm.navigation.Route
import com.vivitasol.carcasamvvm.viewmodels.ThemeViewModel
import com.vivitasol.carcasamvvm.views.ProductoFormView

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MenuShellView() {
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    val innerNavController = rememberNavController()
    val themeViewModel: ThemeViewModel = viewModel()
    val isDarkTheme by themeViewModel.isDarkTheme.collectAsState()

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet {
                Text(
                    text = "Menú",
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier.padding(16.dp)
                )
                
                // Switch para cambiar tema
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Tema oscuro", style = MaterialTheme.typography.bodyMedium)
                    Spacer(modifier = Modifier.width(8.dp))
                    
                    // Animación de escala y rotación al cambiar tema
                    var isAnimating by remember { mutableStateOf(false) }
                    val scale by animateFloatAsState(
                        targetValue = if (isAnimating) 1.2f else 1f,
                        animationSpec = spring(
                            dampingRatio = Spring.DampingRatioMediumBouncy,
                            stiffness = Spring.StiffnessLow
                        ),
                        finishedListener = { isAnimating = false },
                        label = "scale"
                    )
                    val rotation by animateFloatAsState(
                        targetValue = if (isAnimating) 360f else 0f,
                        animationSpec = tween(400),
                        label = "rotation"
                    )
                    
                    Switch(
                        checked = isDarkTheme,
                        onCheckedChange = { 
                            isAnimating = true
                            themeViewModel.toggleTheme()
                        },
                        modifier = Modifier
                            .scale(scale)
                            .graphicsLayer { rotationZ = rotation }
                    )
                }
                
                HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
                
//                NavigationDrawerItem(
//                    label = { Text("2.1.3Componentes") },
//                    selected = currentInnerRoute(innerNavController) == Route.Option1.route,
//                    onClick = {
//                        innerNavController.navigate(Route.Option1.route) {
//                            popUpTo(Route.Option1.route) { inclusive = false }
//                            launchSingleTop = true
//                        }
//                        scope.launch { drawerState.close() }
//                    }
//                )
                NavigationDrawerItem(
                    label = { Text("Productos") },
                    selected = currentInnerRoute(innerNavController) == Route.Option2.route,
                    onClick = {
                        innerNavController.navigate(Route.Option2.route) {
                            popUpTo(Route.Option1.route) { inclusive = false }
                            launchSingleTop = true
                        }
                        scope.launch { drawerState.close() }
                    }
                )
//                NavigationDrawerItem(
//                    label = { Text("2.3.3form") },
//                    selected = currentInnerRoute(innerNavController) == Route.Option3.route,
//                    onClick = {
//                        innerNavController.navigate(Route.Option3.route) {
//                            popUpTo(Route.Option1.route) { inclusive = false }
//                            launchSingleTop = true
//                        }
//                        scope.launch { drawerState.close() }
//                    }
//                )

//                NavigationDrawerItem(
//                    label = { Text("2.4.2Persistencia y Animaciones") },
//                    selected = currentInnerRoute(innerNavController) == Route.Option4.route,
//                    onClick = {
//                        innerNavController.navigate(Route.Option4.route) {
//                            popUpTo(Route.Option1.route) { inclusive = false }
//                            launchSingleTop = true
//                        }
//                        scope.launch { drawerState.close() }
//                    }
//                )

//                NavigationDrawerItem(
//                    label = { Text("2.4.4FuncionNativa(Camara)") },
//                    selected = currentInnerRoute(innerNavController) == Route.Option5.route,
//                    onClick = {
//                        innerNavController.navigate(Route.Option5.route) {
//                            popUpTo(Route.Option1.route) { inclusive = false }
//                            launchSingleTop = true
//                        }
//                        scope.launch { drawerState.close() }
//                    }
//                )

                NavigationDrawerItem(
                    label = { Text("Inventario") },
                    selected = currentInnerRoute(innerNavController) == Route.Inventario.route,
                    onClick = {
                        innerNavController.navigate(Route.Inventario.route) {
                            popUpTo(Route.Option1.route) { inclusive = false }
                            launchSingleTop = true
                        }
                        scope.launch { drawerState.close() }
                    }
                )
            }
        }
    ) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("tejedoraypunto") },
                    navigationIcon = {
                        IconButton(onClick = {
                            scope.launch {
                                if (drawerState.isClosed) drawerState.open() else drawerState.close()
                            }
                        }) {
                            Icon(Icons.Default.Menu, contentDescription = "Menú")
                        }
                    }
                )
            }
        ) { innerPadding ->
            // NavHost interno para las opciones del menú
            NavHost(
                navController = innerNavController,
                startDestination = Route.Option1.route,
                modifier = Modifier.padding(innerPadding)
            ) {
                composable(Route.Option1.route) { Option1View() }
                composable(Route.Option2.route) { Option2View(navController = innerNavController) } // <--recibe nav
                composable(Route.Option3.route) { Option3View() }
                //pantalla de detalle para la clase 2(con nav)
                composable(Route.Option2Detail.route) { backStack ->
                    val id = backStack.arguments?.getString("id") ?: "sin-id"
                    Option2DetailView(
                        id = id,
                        onBack = { innerNavController.navigateUp() }
                    )
                }
                composable(Route.Option4.route) { Option4View() }
                composable(Route.Option5.route) { Option5CameraView() }
                composable(Route.Inventario.route) { InventarioView(navController = innerNavController) }
                
                // Formulario para crear producto
                composable(Route.ProductoForm.route) { 
                    ProductoFormView(
                        onNavigateBack = { innerNavController.navigateUp() }
                    )
                }
                
                // Formulario para editar producto
                composable(Route.ProductoEdit.route) { backStack ->
                    val id = backStack.arguments?.getString("id")?.toIntOrNull()
                    ProductoFormView(
                        onNavigateBack = { innerNavController.navigateUp() },
                        productoId = id
                    )
                }
            }
        }
    }
}

@Composable
private fun currentInnerRoute(navController: NavHostController): String? {
    val entry by navController.currentBackStackEntryAsState()
    return entry?.destination?.route
}
