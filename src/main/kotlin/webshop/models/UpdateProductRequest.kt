package webshop.models

import kotlinx.serialization.Serializable

@Serializable
data class UpdateProductRequest(
    val name: String,
    val price: Double
)