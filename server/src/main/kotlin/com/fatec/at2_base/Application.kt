package com.fatec.at2_base

import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.serialization.Serializable

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

val jogos = mutableListOf(
    Jogo(1, "Minecraft", "Sandbox", "Construção e sobrevivência em mundo aberto"),
    Jogo(2, "Valorant", "FPS", "Jogo competitivo de tiro tático"),
    Jogo(3, "EA FC 26", "Esportes", "Simulador de futebol")
)

var proximoId = 4

fun main() {
    embeddedServer(
        Netty,
        port = 8080,
        host = "0.0.0.0",
        module = Application::module
    ).start(wait = true)
}

fun Application.module() {

    install(ContentNegotiation) {
        json()
    }

    routing {

        get("/jogos") {
            call.respond(jogos)
        }

        post("/jogos") {

            val novo = call.receive<NovoJogo>()

            val jogo = Jogo(
                id = proximoId++,
                titulo = novo.titulo,
                genero = novo.genero,
                descricao = novo.descricao
            )

            jogos.add(jogo)

            call.respond(
                HttpStatusCode.Created,
                jogo
            )
        }
    }
}