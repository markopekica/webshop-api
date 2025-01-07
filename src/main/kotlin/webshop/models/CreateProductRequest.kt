package webshop.models

import kotlinx.serialization.Serializable

@Serializable
data class CreateProductRequest(
    val name: String,
    val price: Double
)