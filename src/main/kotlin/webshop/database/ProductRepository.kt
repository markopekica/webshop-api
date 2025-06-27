package webshop.database

import com.datastax.oss.driver.api.core.CqlSession
import com.datastax.oss.driver.api.core.uuid.Uuids
import kotlinx.serialization.Serializable
import webshop.models.UUIDSerializer
import webshop.models.UpdateProductRequest
import java.util.*

import webshop.models.ProductEntity

/*
@Serializable
data class ProductEntity(
    @Serializable(with = UUIDSerializer::class) val id: UUID,
    val name: String,
    val price: Double
)
*/
class ProductRepository(val session: CqlSession) {

    fun addProduct(name: String, price: Double): ProductEntity {
        val id = Uuids.timeBased()
        val query = "INSERT INTO product (id, name, price) VALUES (?, ?, ?)"
        session.execute(session.prepare(query).bind(id, name, price))
        return ProductEntity(id, name, price)
    }

    fun getProductById(id: UUID): ProductEntity? {
        val query = "SELECT * FROM product WHERE id = ?"
        val resultSet = session.execute(session.prepare(query).bind(id))
        val row = resultSet.one()
        return row?.let {
            ProductEntity(
                it.getUuid("id") ?: throw IllegalArgumentException("ID can not be null"),
                it.getString("name") ?: throw IllegalArgumentException("Name can not be null"),
                it.getDouble("price") ?: throw IllegalArgumentException("Price can not be null")
            )
        }
    }

    fun deleteProductById(id: UUID): Boolean {
        val query = "DELETE FROM product WHERE id = ?"
        val statement = session.prepare(query).bind(id)
        val result = session.execute(statement)
        return result.wasApplied()
    }

    fun getAllProducts(): List<ProductEntity> {
        val query = "SELECT * FROM product"
        val resultSet = session.execute(query)
        return resultSet.mapNotNull { row ->
            row?.let {
                ProductEntity(
                    it.getUuid("id") ?: return@mapNotNull null,
                    it.getString("name") ?: return@mapNotNull null,
                    it.getDouble("price") ?: return@mapNotNull null
                )
            }
        }
    }

    fun updateProduct(id: UUID, request: UpdateProductRequest): ProductEntity {
        val query = "UPDATE product SET name = ?, price = ? WHERE id = ?"
        session.execute(session.prepare(query).bind(request.name, request.price, id))
        return ProductEntity(id, request.name, request.price)
        /*
        val existingProduct = getProductById(id) ?: return null

        val updatedName = request.name ?: existingProduct.name
        val updatedPrice = request.price ?: existingProduct.price

        val query = "UPDATE product SET name = ?, price = ? WHERE id = ?"
        session.execute(session.prepare(query).bind(updatedName, updatedPrice, id))

        return ProductEntity(id = id, name = updatedName, price = updatedPrice)
         */
    }

}