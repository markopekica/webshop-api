package webshop

import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.server.application.*
import io.ktor.server.routing.*
import io.ktor.server.response.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.serialization.kotlinx.json.*
import webshop.routes.productRoutes
import io.ktor.server.plugins.statuspages.*
import io.ktor.http.HttpStatusCode

fun main() {
    embeddedServer(Netty, port = 8080) {
        install(ContentNegotiation) {
            json()
        }
        install(StatusPages) {
            status(HttpStatusCode.NotFound) { call, status ->
                call.respondText(text = "404: Page Not Found", status = status)
            }
            exception<Throwable> { call, cause ->
                call.respond(HttpStatusCode.InternalServerError, "500: Internal server error")
                println("Error: ${cause.localizedMessage}")
            }
        }
        module()
    }.start(wait = true)
}

fun Application.module() {
    routing {
        get("/") {
            call.respondText { "Welcome to the webshop API!" }
        }
        productRoutes()
    }
}