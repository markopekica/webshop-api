package webshop.database

import com.datastax.oss.driver.api.core.CqlSession
import com.datastax.oss.driver.api.core.uuid.Uuids
import kotlinx.serialization.Serializable
import webshop.models.UUIDSerializer
import java.util.*

@Serializable
data class ProductEntity(
    @Serializable(with = UUIDSerializer::class) val id: UUID,
    val name: String,
    val price: Double
)

class ProductRepository(private val session: CqlSession) {

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
        //session.execute(session.prepare(query).bind(id))
        //return true
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

    fun updateProduct(id: UUID, name: String?, price: Double?): Boolean {
        val updateParts = mutableListOf<String>()
        val parameters = mutableListOf<Any>()

        if (name != null) {
            updateParts.add("name = ?")
            parameters.add(name)
        }
        if (price != null) {
            updateParts.add("price = ?")
            parameters.add(price)
        }

        if (updateParts.isEmpty()) return false

        val query = "UPDATE product SET ${updateParts.joinToString(", ")} WHERE id = ?"
        parameters.add(id)

        val statement = session.prepare(query).bind(*parameters.toTypedArray())
        val result = session.execute(statement)
        return result.wasApplied()
    }

}