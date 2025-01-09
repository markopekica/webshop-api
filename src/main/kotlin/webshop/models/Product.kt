package webshop.models

import kotlinx.serialization.Serializable

@Serializable
data class Product(
    val id: Int,
    var name: String,   // should be refactored back to val
    var price: Double
)