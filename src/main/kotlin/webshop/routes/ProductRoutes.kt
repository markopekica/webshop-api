package webshop.routes

import io.ktor.http.*
import io.ktor.server.routing.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.request.*
import webshop.database.ProductRepository
import webshop.models.*
import webshop.utils.ValidationUtils
import java.util.*


fun Route.productRoutes(repository: ProductRepository) {
    /*get("/products") {
        val products = repository.getAllProducts()
        call.respond(products)
    }

     */

    get("/products") {
        val limit = call.request.queryParameters["limit"]?.toIntOrNull() ?: 20
        val pagingStateParam = call.request.queryParameters["pagingState"]
        val pagingStateBytes = pagingStateParam?.let { Base64.getDecoder().decode(it) }

        val (products, nextPagingState) = repository.getProductsPaged(limit, pagingStateBytes)

        println("[GET] /products -> Returned ${products.size} product(s), limit=$limit, pagingState=${pagingStateParam ?: "none"}")

        call.respond(
            PagedProductsResponse(
                products = products,
                nextPagingState = nextPagingState?.let { Base64.getEncoder().encodeToString(it) }
            )
        )
    }

    get("/products/{id}") {
        val id = call.parameters["id"]?.let(UUID::fromString)
        if (id == null) {
            println("[GET] /products/$id -> Invalid UUID format")
            call.respond(HttpStatusCode.BadRequest, "Invalid ID format")
            return@get
        }

        val product = repository.getProductById(id)
        if (product == null) {
            println("[GET] /products/$id -> Product not found")
            call.respond(HttpStatusCode.NotFound, "Product not found")
        } else {
            println("[GET] /products/$id -> Found product `${product.name}`")
            call.respond(HttpStatusCode.OK, product)
        }
    }

    post("/products") {
        val request = call.receive<CreateProductRequest>()

        val errors = ValidationUtils.validateCreateRequest(request)
        if (errors.isNotEmpty()) {
            println("[POST] /products -> Validation failed: ${errors.joinToString {"; "}}")
            call.respond(HttpStatusCode.BadRequest, ErrorResponse(errors.joinToString(", ")))
            return@post
        }

        val newProduct = repository.addProduct(request.name, request.price)
        println("[POST] /products -> Created product `${newProduct.name}` with ID ${newProduct.id}")
        call.respond(HttpStatusCode.Created, newProduct)
    }

    put("/products/{id}") {
        val productId = call.parameters["id"]?.let { UUID.fromString(it) }
            ?: return@put call.respond(HttpStatusCode.BadRequest, ErrorResponse("Invalid product ID format"))

        val request = call.receive<UpdateProductRequest>()
        val errors = ValidationUtils.validateUpdateRequest(request)
        if (errors.isNotEmpty()) {
            println("[PUT] /products/$productId -> Validation failed: ${errors.joinToString("; ")}")
            return@put call.respond(HttpStatusCode.BadRequest, ErrorResponse(errors.joinToString { ", " }))
        }

        if (repository.getProductById(productId) == null) {
            println("[PUT] /product/$productId -> Product not found")
            return@put call.respond(HttpStatusCode.NotFound, ErrorResponse("Product with ID $productId not found"))
        }

        //val existingProduct = repository.getProductById(productId)
        //    ?: return@put call.respond(HttpStatusCode.NotFound, ErrorResponse("Product with ID $productId not found"))

        val updatedProduct = repository.updateProduct(productId, request)
        println("[PUT] /product/$productId -> Product updated")
        call.respond(HttpStatusCode.OK, updatedProduct)
    }

    delete("/products/{id}") {
        val productId = call.parameters["id"]
        if (productId == null) {
            println("[DELETE] /products -> Missing product ID")
            call.respond(HttpStatusCode.BadRequest, ErrorResponse("Product ID is required"))
            return@delete
        }

        try {
            val uuid = UUID.fromString(productId)
            if (repository.deleteProductById(uuid)) {
                val successMessage = "Product with ID $productId was successfully deleted."
                println("[DELETE] /products/$productId -> Success")
                call.respond(HttpStatusCode.OK, ErrorResponse(successMessage))
            } else {
                println("[DELETE] /products/$productId -> Not found")
                call.respond(HttpStatusCode.NotFound, ErrorResponse("Product with ID $productId not found"))
            }
        } catch (e: IllegalArgumentException) {
            println("[DELETE] /products/$productId -> Invalid ID format")
            call.respond(HttpStatusCode.BadRequest, ErrorResponse("Invalid Product ID format"))
        }
    }

}

fun validateName(name: String?, allowNull: Boolean = false): String? {
    if (name.isNullOrBlank()) return "Product name can not be empty"
    return null
}

fun validatePrice(price: Double?, allowNull: Boolean = false): String? {
    if (price != null && price <= 0) return "Price must be greater than 0"
    return null
}
/*
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

 */