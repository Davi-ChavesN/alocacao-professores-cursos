package com.alocacaprofs.view.components

import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable

@Composable
fun Botao(texto: String, onClick: () -> Unit) {
    Button(onClick = onClick) {
        Text(text = texto)
    }
}