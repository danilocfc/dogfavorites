package com.example.dogfavorites.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.dogfavorites.data.local.*
import com.example.dogfavorites.data.remote.*
import kotlinx.coroutines.launch
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.draw.clip
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.rememberScrollState
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.runtime.rememberCoroutineScope

@Composable
fun HomeScreen(api: DogApiService, dao: DogDao, onNavigateToFav: () -> Unit, onNavigateToAbout: () -> Unit) {
    var dogUrl by remember { mutableStateOf("") }
    var breedName by remember { mutableStateOf("SORTEIE UM DOG") }
    val scope = rememberCoroutineScope()

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .padding(24.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {

            Text(
                text = breedName,
                style = MaterialTheme.typography.headlineMedium,
                color = MaterialTheme.colorScheme.primary,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(20.dp))

            Card(
                modifier = Modifier.size(280.dp),
                shape = RoundedCornerShape(24.dp),
                elevation = CardDefaults.cardElevation(8.dp)
            ) {
                AsyncImage(
                    model = dogUrl,
                    contentDescription = null,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            Row(
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Button(
                    onClick = {
                        scope.launch {
                            val response = api.getRandomDog()
                            dogUrl = response.message
                            breedName = extractBreed(dogUrl)
                        }
                    },
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("Sortear")
                }

                FilledTonalButton(
                    onClick = {
                        scope.launch {
                            if (dogUrl.isNotEmpty()) {
                                dao.insert(FavoriteDog(imageUrl = dogUrl, breed = breedName))
                            }
                        }
                    },
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("Favoritar ❤️")
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            OutlinedButton(
                onClick = onNavigateToFav,
                modifier = Modifier.fillMaxWidth(0.7f),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("Meus Favoritos")
            }

            TextButton(onClick = onNavigateToAbout) {
                Text("Sobre o Projeto", style = MaterialTheme.typography.labelLarge)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FavoritesScreen(dao: DogDao, onBack: () -> Unit) {
    val favorites by dao.getAll().collectAsState(initial = emptyList())
    val scope = rememberCoroutineScope()

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Meus Favoritos", style = MaterialTheme.typography.titleLarge) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "Voltar")
                    }
                }
            )
        }
    ) { padding ->
        if (favorites.isEmpty()) {
            Box(Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                Text("A lista está vazia 🐾", style = MaterialTheme.typography.bodyLarge)
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize().padding(padding),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(favorites) { dog ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        elevation = CardDefaults.cardElevation(4.dp)
                    ) {
                        Row(
                            Modifier.padding(12.dp).fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                AsyncImage(
                                    model = dog.imageUrl,
                                    contentDescription = null,
                                    modifier = Modifier.size(70.dp).clip(RoundedCornerShape(12.dp)),
                                    contentScale = ContentScale.Crop
                                )
                                Spacer(Modifier.width(16.dp))
                                Text(dog.breed, style = MaterialTheme.typography.titleMedium)
                            }

                            IconButton(onClick = {
                                scope.launch {
                                    dao.delete(dog)
                                }
                            }) {
                                Icon(
                                    imageVector = Icons.Default.Delete,
                                    contentDescription = "Eliminar",
                                    tint = MaterialTheme.colorScheme.error
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun AboutScreen(onBack: () -> Unit) {
    Column(Modifier.fillMaxSize(), Arrangement.Center, Alignment.CenterHorizontally) {
        Text("DogFavorites v1.0 - Trabalho PDM")
        Text("Utilizando DogAPI e Room Database")
        Text("Danilo Carneiro Freire de Castro - 470077")
        Button(onClick = onBack) { Text("Voltar") }
    }
}

fun extractBreed(url: String): String {
    return try {
        // A URL é: https://images.dog.ceo/breeds/nome-da-raca/foto.jpg
        val parts = url.split("/")
        val breedPart = parts[4]
        breedPart.replace("-", " ").uppercase()
    } catch (e: Exception) {
        "RAÇA DESCONHECIDA"
    }
}
