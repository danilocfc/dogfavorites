package com.example.dogfavorites.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.*
import androidx.compose.runtime.Composable

@Composable
fun DogAppTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) {
        darkColorScheme() // Cores para o Tema Escuro
    } else {
        lightColorScheme() // Cores para o Tema Claro
    }

    MaterialTheme(
        colorScheme = colorScheme,
        content = content
    )
}