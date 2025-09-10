package webshop.models
import kotlinx.serialization.Serializable

@Serializable
data class PagedProductsResponse(
    val products: List<ProductEntity>,
    val nextPagingState: String? = null
)