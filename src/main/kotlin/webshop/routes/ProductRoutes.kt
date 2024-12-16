package webshop.routes

import io.ktor.server.routing.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import webshop.models.Product
import io.ktor.http.HttpStatusCode

fun Route.productRoutes() {
    get("products") {
        val products = listOf(
            Product(1, "Laptop", 533.0),
            Product(2, "Headphones", 60.0),
            Product(3, "Lunch", 15.0)
        )
        call.respond(products)
    }

    get("/products/{id}") {
        val products = listOf(
            Product(1, "Laptop", 533.0),
            Product(2, "Headphones", 60.0),
            Product(3, "Lunch", 15.0)
        )
        val id = call.parameters["id"]?.toIntOrNull()
        if (id == null) {
            call.respond(HttpStatusCode.BadRequest, "Invalid product > ID: must be a number.")
            return@get
        }

        val product = products.find { it.id == id }
        if ( product == null ) {
            call.respond(HttpStatusCode.BadRequest, "Product with ID $id not found.")
            return@get
        } else {
            call.respond(product)
        }
    }

}