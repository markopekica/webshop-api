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

suspend fun ApplicationCall.respondValidationErrors(errors: List<String>): Boolean {
    if (errors.isNotEmpty()) {
        respond(HttpStatusCode.BadRequest, ErrorResponse(errors.joinToString(", ")))
        return true
    }
    return false
}

private suspend fun parseProductId(call: ApplicationCall): Int? {
    val productId = call.parameters["id"]?.toIntOrNull()
    if (productId == null) {
        call.respond(HttpStatusCode.BadRequest, "Invalid product ID")
    }
    return productId
}

private suspend fun findProductById(call: ApplicationCall, productId: Int?): Product? {
    val product = products.find { it.id == productId }
    if (product == null) {
        call.respond(HttpStatusCode.NotFound, ErrorResponse("Product with ID $productId not found"))
    }
    return product
}

private fun applyUpdates(product: Product, updateRequest: UpdateProductRequest) {
    updateRequest.name?.let { product.name = it }
    updateRequest.price?.let { product.price = it }
}

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
        if (call.respondValidationErrors(request.validate())) {
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
        val productId = parseProductId(call) ?: return@put
        val updateRequest = call.receive<UpdateProductRequest>()

        if (call.respondValidationErrors(updateRequest.validate())) {
            return@put
        }

        val product = findProductById(call, productId) ?: return@put
        applyUpdates(product, updateRequest)
        call.respond(HttpStatusCode.OK, product)
    }

    delete("/products/{id}") {
        //val productId = call.parameters["id"]?.toIntOrNull()
        val productId = parseProductId(call) ?: return@delete
        //val product = products.find { it.id == productId }
        val product = findProductById(call, productId) ?: return@delete

        products.remove(product)
        call.respond(HttpStatusCode.NoContent)
    }

}

fun validateName(name: String?, allowNull: Boolean = false): String? {
    if (allowNull && name == null) return null
    if (name.isNullOrBlank()) return "Product name can not be empty"
    return null
}

fun validatePrice(price: Double?, allowNull: Boolean = false): String? {
    if (allowNull && price == null) return null
    if (price != null && price <= 0) return "Price must be greater than 0"
    return null
}

fun CreateProductRequest.validate(): List<String> {
    return listOfNotNull(
        validateName(this.name),
        validatePrice(this.price)
    )
}

fun UpdateProductRequest.validate(): List<String> {
    return listOfNotNull(
        validateName(this.name, allowNull = true),
        validatePrice(this.price, allowNull = true)
    )
}