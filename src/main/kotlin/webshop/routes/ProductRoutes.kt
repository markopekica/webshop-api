package webshop.routes

import io.ktor.server.routing.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import webshop.models.Product
import io.ktor.server.plugins.*


val products = listOf(
    Product(1, "Laptop", 533.0),
    Product(2, "Headphones", 60.0),
    Product(3, "Lunch", 15.0)
)

fun Route.productRoutes() {
    get("/products") {
        call.respond(products)
    }

    get("/products/{id}") {
        val productId = call.parameters["id"]?.toIntOrNull()
            ?: throw BadRequestException("ID must be a number")

        val product = products.find { it.id == productId }
            ?: throw NotFoundException("Product with ID $productId not found")

        call.respond(product)
    }

}