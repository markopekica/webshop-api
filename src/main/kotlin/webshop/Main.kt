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

    val cassandraHost = System.getenv("CASSANDRA_HOST") ?: "127.0.0.1"
    val cassandraPort = System.getenv("CASSANDRA_PORT")?.toInt() ?: 9042
    val keyspace = System.getenv("KEYSPACE") ?: "products_ks"
    val datacenter = System.getenv("CASSANDRA_DATACENTER") ?: "datacenter1"

    val connector = CassandraConnector(cassandraHost, cassandraPort, datacenter)

    // Initialize keyspace and table if needed:
    //connector.initializeKeyspace(keyspace)
    //connector.initializeTable(keyspace)

    connector.connect(keyspace)
    val productRepository = ProductRepository(connector.session)

    embeddedServer(Netty, port = 8080) {
        module(productRepository)
    }.start(wait = true)

    // Optionally add a shutdown hook to close the session:
    Runtime.getRuntime().addShutdownHook(Thread {
        connector.close()
    })
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
        exception<Throwable> { call, cause ->
            call.respond(HttpStatusCode.InternalServerError, ErrorResponse(cause.message ?: "Server Error"))
        }
    }
    routing {
        get("/") {
            call.respondText { "Welcome to the webshop API!" }
        }
        productRoutes(repository)
        // Catch-all route for unmatched paths
        route("{...}") {
            handle {
                call.respond(HttpStatusCode.NotFound, ErrorResponse("404: Page Not Found"))
            }
        }
    }
}