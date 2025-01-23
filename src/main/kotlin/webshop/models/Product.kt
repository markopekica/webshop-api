package webshop.models


import kotlinx.serialization.Serializable
import java.util.UUID

@Serializable
data class ProductEntity(
    @Serializable(with = UUIDSerializer::class) val id: UUID,
    val name: String,
    val price: Double
)

@Serializable
data class Product(
    val id: Int,
    var name: String,   // should be refactored back to val
    var price: Double
)