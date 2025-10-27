package com.vivitasol.carcasamvvm.views

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.vivitasol.carcasamvvm.viewmodels.WelcomeViewModel

@Composable
fun WelcomeView(
    onStartClick: () -> Unit,
    vm: WelcomeViewModel = viewModel()
) {
    val titulo = vm.titulo.collectAsState().value
    val logoResId = vm.logoResId.collectAsState().value
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            painter = painterResource(id = logoResId),
            contentDescription = "Logo de la aplicación",
            modifier = Modifier
                .size(120.dp)
                .padding(bottom = 16.dp)
        )
        Text(titulo)
        // Text("Esta es la carcasa base (Kotlin + Compose + MVVM).")
        Button(
            onClick = onStartClick,
            modifier = Modifier.padding(top = 16.dp)
        ) {
            Text("Ir al menú")
        }
    }
}
