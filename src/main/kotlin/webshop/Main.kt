package webshop

import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.server.application.*
import io.ktor.server.routing.*
import io.ktor.server.response.*
import webshop.models.Product
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.serialization.kotlinx.json.*

fun main() {
    embeddedServer(Netty, port = 8080) {    // start Ktor server using Netty engine
        install(ContentNegotiation) {
            json()  // adds plugin to handle JSON data > serialize objects to JSON
        }
        module()
    }.start(wait = true)
}

fun Application.module() {
    routing {   // defines API endpoints
        get("/") {
            call.respondText { "Welcome to the webshop API!" }
        }
        get("/products") {
            val products = listOf(
                Product(1, "Laptop", 533.0),
                Product(2, "Headphones", 60.0),
                Product(3, "Lunch", 15.0)
            )
            call.respond(products)
        }
    }
}