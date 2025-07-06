package webshop.database

import com.datastax.oss.driver.api.core.CqlSession
import com.datastax.oss.driver.api.core.cql.SimpleStatement
import com.datastax.oss.driver.api.core.uuid.Uuids
import webshop.models.UpdateProductRequest
import java.util.*
import webshop.models.ProductEntity
import java.nio.ByteBuffer


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

    fun getProductsPaged(limit: Int, pagingState: ByteArray?): Pair<List<ProductEntity>, ByteArray?> {
        val statementBuilder = SimpleStatement.builder("SELECT * FROM product")
            .setPageSize(limit)

        if (pagingState != null) {
            statementBuilder.setPagingState(ByteBuffer.wrap(pagingState))
        }

        val resultSet = session.execute(statementBuilder.build())

        val iterator = resultSet.iterator()
        val products = mutableListOf<ProductEntity>()
        while (iterator.hasNext() && products.size < limit) {
            val row = iterator.next()
            val id = row.getUuid("id") ?: continue
            val name = row.getString("name") ?: continue
            val price = row.getDouble("price")
            products.add(ProductEntity(id, name, price))
        }

        val nextState = resultSet.executionInfo.pagingState?.array()

        return Pair(products, nextState)
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