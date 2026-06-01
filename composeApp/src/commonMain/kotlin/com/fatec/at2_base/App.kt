package com.fatec.at2_base

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable
import androidx.compose.ui.text.style.TextAlign

@Serializable
data class Jogo(
    val id: Int,
    val titulo: String,
    val genero: String,
    val descricao: String
)

@Serializable
data class NovoJogo(
    val titulo: String,
    val genero: String,
    val descricao: String
)

val client = HttpClient {
    install(ContentNegotiation) {
        json()
    }
}

const val BASE_URL = "http://10.0.2.2:8080"

val generos = listOf(
    "FPS",
    "RPG",
    "MOBA",
    "Sandbox",
    "Corrida",
    "Esportes",
    "Estratégia",
    "Outro"
)

val cardShape = RoundedCornerShape(12.dp)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun App() {

    MaterialTheme(
        colorScheme = darkColorScheme(
            primary = Color(0xFF6C5CE7),
            secondary = Color(0xFF00CEC9),
            background = Color(0xFF0F172A),
            surface = Color(0xFF1E293B)
        )
    ) {

        var jogos by remember { mutableStateOf<List<Jogo>>(emptyList()) }

        var titulo by remember { mutableStateOf("") }
        var genero by remember { mutableStateOf(generos[0]) }
        var descricao by remember { mutableStateOf("") }

        var mensagem by remember { mutableStateOf("") }
        var mensagemErro by remember { mutableStateOf(false) }
        var expandido by remember { mutableStateOf(false) }

        val scope = rememberCoroutineScope()

        fun carregar() {
            scope.launch {
                try {
                    jogos = client.get("$BASE_URL/jogos").body()
                } catch (e: Exception) {
                    mensagem = "Erro ao carregar jogos"
                    mensagemErro = true
                }
            }
        }

        LaunchedEffect(Unit) {
            carregar()
        }

        Scaffold(
            topBar = {
                CenterAlignedTopAppBar(
                    title = {
                        Text("Game Catalog")
                    },
                    colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                        titleContentColor = Color.White
                    )
                )
            },
            containerColor = Color(0xFF0F172A)
        ) { padding ->

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(16.dp)
            ) {

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = cardShape,
                    colors = CardDefaults.cardColors(
                        containerColor = Color(0xFF1E293B)
                    )
                ) {

                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {

                        Text(
                            text = "Cadastrar Jogo",
                            style = MaterialTheme.typography.titleMedium,
                            color = Color.White,
                            modifier = Modifier.fillMaxWidth(),
                            textAlign = TextAlign.Center
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        OutlinedTextField(
                            value = titulo,
                            onValueChange = { titulo = it },
                            label = { Text("Título") },
                            modifier = Modifier.fillMaxWidth()
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        ExposedDropdownMenuBox(
                            expanded = expandido,
                            onExpandedChange = {
                                expandido = !expandido
                            }
                        ) {

                            OutlinedTextField(
                                value = genero,
                                onValueChange = {},
                                readOnly = true,
                                label = { Text("Gênero") },
                                trailingIcon = {
                                    ExposedDropdownMenuDefaults.TrailingIcon(expandido)
                                },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .menuAnchor()
                            )

                            ExposedDropdownMenu(
                                expanded = expandido,
                                onDismissRequest = {
                                    expandido = false
                                }
                            ) {
                                generos.forEach {
                                    DropdownMenuItem(
                                        text = {
                                            Text(it)
                                        },
                                        onClick = {
                                            genero = it
                                            expandido = false
                                        }
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        OutlinedTextField(
                            value = descricao,
                            onValueChange = {
                                descricao = it
                            },
                            label = { Text("Descrição") },
                            modifier = Modifier.fillMaxWidth()
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        Button(
                            onClick = {

                                scope.launch {

                                    try {

                                        client.post("$BASE_URL/jogos") {
                                            contentType(ContentType.Application.Json)
                                            setBody(
                                                NovoJogo(
                                                    titulo,
                                                    genero,
                                                    descricao
                                                )
                                            )
                                        }

                                        titulo = ""
                                        descricao = ""

                                        mensagem = "Jogo cadastrado com sucesso"
                                        mensagemErro = false

                                        carregar()

                                    } catch (e: Exception) {

                                        mensagem = "Erro ao cadastrar"
                                        mensagemErro = true
                                    }
                                }
                            },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(8.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFF00CEC9)
                            )
                        ) {
                            Text("Cadastrar")
                        }

                        if (mensagem.isNotBlank()) {

                            Spacer(modifier = Modifier.height(8.dp))

                            Text(
                                mensagem,
                                color =
                                    if (mensagemErro)
                                        MaterialTheme.colorScheme.error
                                    else
                                        Color.Green
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "Jogos cadastrados (${jogos.size})",
                    color = Color.White,
                    style = MaterialTheme.typography.titleMedium
                )

                Spacer(modifier = Modifier.height(12.dp))

                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {

                    items(jogos) { jogo ->

                        Card(
                            shape = cardShape,
                            colors = CardDefaults.cardColors(
                                containerColor = Color(0xFF1E293B)
                            )
                        ) {

                            Column(
                                modifier = Modifier.padding(12.dp)
                            ) {

                                Text(
                                    text = jogo.titulo,
                                    color = Color.White,
                                    style = MaterialTheme.typography.titleSmall
                                )

                                Spacer(modifier = Modifier.height(4.dp))

                                Text(
                                    text = jogo.genero,
                                    color = Color(0xFF00CEC9)
                                )

                                Spacer(modifier = Modifier.height(6.dp))

                                Text(
                                    text = jogo.descricao,
                                    color = Color.LightGray,
                                    style = MaterialTheme.typography.bodySmall
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}