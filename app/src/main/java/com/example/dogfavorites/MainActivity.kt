package com.example.dogfavorites

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.example.dogfavorites.data.local.DogDatabase
import com.example.dogfavorites.data.remote.DogApiService
import com.example.dogfavorites.ui.AboutScreen
import com.example.dogfavorites.ui.FavoritesScreen
import com.example.dogfavorites.ui.HomeScreen
import com.example.dogfavorites.ui.theme.DogAppTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //  Inicializa a API
        val api = DogApiService.create()

        // Inicializa o Banco de Dados
        val db = DogDatabase.getDatabase(this)
        val dao = db.dogDao()

        setContent {
            DogAppTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    var currentScreen by remember { mutableStateOf("home") }

                    when (currentScreen) {
                        "home" -> HomeScreen(
                            api = api,
                            dao = dao,
                            onNavigateToFav = { currentScreen = "fav" },
                            onNavigateToAbout = { currentScreen = "about" }
                        )
                        "fav" -> FavoritesScreen(
                            dao = dao,
                            onBack = { currentScreen = "home" }
                        )
                        "about" -> AboutScreen(
                            onBack = { currentScreen = "home" }
                        )
                    }
                }
            }
        }
    }
}