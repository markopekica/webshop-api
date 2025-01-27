package webshop.models

import kotlinx.serialization.Serializable
import java.util.UUID

@Serializable
data class ProductEntity(
    @Serializable(with = UUIDSerializer::class) val id: UUID,
    val name: String,
    val price: Double
)
