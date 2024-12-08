package webshop.models

import kotlinx.serialization.Serializable


@Serializable   // allows the class to automatically be converted to/from JSON by Kotlinx Serialization
data class Product(
    val id: Int,
    val name: String,
    val price: Double
)
