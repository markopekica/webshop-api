package webshop.routes

import io.ktor.http.*
import io.ktor.server.routing.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import webshop.models.Product
import io.ktor.server.plugins.*
import io.ktor.server.request.*
import webshop.models.CreateProductRequest
import webshop.models.ErrorResponse
import webshop.models.UpdateProductRequest


val products = mutableListOf(
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
            ?: throw BadRequestException("ID must be an integer")

        val product = products.find { it.id == productId }
            ?: throw NotFoundException("Product with ID $productId not found")

        call.respond(product)
    }

    post("/products") {
        val request = call.receive<CreateProductRequest>()
        val errors = request.validate()

        if (errors.isNotEmpty()) {
            call.respond(HttpStatusCode.BadRequest, ErrorResponse(errors.joinToString(", ")))
            return@post
        }
        val newProduct = Product(
            id = products.size + 1,
            name = request.name,
            price = request.price
        )
        products.add(newProduct)
        call.respond(HttpStatusCode.Created, newProduct)
    }

    put("/products/{id}") {
        // review and refactor
        val productId = call.parameters["id"]?.toIntOrNull()
            ?: throw BadRequestException("Invalid product ID")

        val updateRequest = call.receive<UpdateProductRequest>()
        val errors = updateRequest.validate()
        if (errors.isNotEmpty()) {
            call.respond(HttpStatusCode.BadRequest, ErrorResponse(errors.joinToString { ", " }))
            return@put
        }

        val product = products.find { it.id == productId }
        //?: throw NotFoundException("Product with ID $productId not found")
        if (product == null) {
            call.respond(HttpStatusCode.NotFound, ErrorResponse("Product with ID $productId not found"))
            return@put
        }

        updateRequest.name?.let { product.name = it }
        updateRequest.price?.let { product.price = it }

        call.respond(HttpStatusCode.OK, product)
    }

}

fun CreateProductRequest.validate(): List<String> {
    val errors = mutableListOf<String>()
    if (this.name.isBlank()) {
        errors.add("Product name can not be empty")
    }
    if (this.price <= 0) {
        errors.add("Price must be greater than 0")
    }
    return errors
}

fun UpdateProductRequest.validate(): List<String> {
    val errors = mutableListOf<String>()
    if (this.name != null && this.name.isBlank()) {
        errors.add("Product name cannot be empty")
    }
    if (this.price != null && this.price <= 0) {
        errors.add("Price must be greater than 0")
    }
    return errors
}