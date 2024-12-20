package webshop.routes

import io.ktor.server.routing.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import webshop.models.Product

fun Route.productRoutes() {
    get("products") {
        val products = listOf(
            Product(1, "Laptop", 533.0),
            Product(2, "Headphones", 60.0),
            Product(3, "Lunch", 15.0)
        )
        call.respond(products)
    }
}