package webshop

import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.server.application.*
import io.ktor.server.routing.*
import io.ktor.server.response.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.serialization.kotlinx.json.*
import webshop.routes.productRoutes

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
        productRoutes()
    }
}