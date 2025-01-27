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
import io.ktor.server.plugins.*
import webshop.database.CassandraConnector
import webshop.database.ProductRepository
import webshop.models.ErrorResponse

fun main() {
    val connector = CassandraConnector("127.0.0.1", 9042)
    connector.connect("products_ks")
    val productRepository = ProductRepository(connector.session)

    embeddedServer(Netty, port = 8080) {
        module(productRepository)
    }.start(wait = true)
}

fun Application.module(repository: ProductRepository) {
    install(ContentNegotiation) {
        json()
    }
    install(StatusPages) {
        exception<NotFoundException> { call, cause ->
            call.respond(HttpStatusCode.NotFound, ErrorResponse(cause.message ?: "Not Found"))
        }
        exception<BadRequestException> { call, cause ->
            call.respond(HttpStatusCode.BadRequest, ErrorResponse(cause.message ?: "Bad Request"))
        }
        status(HttpStatusCode.NotFound) { call, status ->
            call.respondText("404: Page Not Found", status = status)
        }
        exception<Throwable> { call, cause ->
            call.respond(HttpStatusCode.InternalServerError, ErrorResponse(cause.message ?: "Server Error"))
        }
    }
    routing {
        get("/") {
            call.respondText { "Welcome to the webshop API!" }
        }
        productRoutes(repository)
    }
}