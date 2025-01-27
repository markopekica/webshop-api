package webshop

import io.mockk.every
import io.mockk.mockk
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.AfterEach
import org.testcontainers.containers.CassandraContainer
import webshop.database.CassandraConnector
import webshop.database.ProductEntity
import webshop.database.ProductRepository
import java.util.*
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals

class CassandraIntegrationTest {

    private val cassandra = CassandraContainer<Nothing>("cassandra:4.0")
    private lateinit var repository: ProductRepository
    private var mockRepository: ProductRepository = mockk(relaxed = true)

    @BeforeTest
    fun setupMocks() {
        every { mockRepository.getAllProducts() } returns listOf(
            ProductEntity(id = UUID.randomUUID(), name = "Mock ProductEntity", price = 99.99)
        )
    }

    @BeforeEach
    fun setUp() {
        cassandra.start()

        val connector = CassandraConnector(
            cassandra.host,
            cassandra.firstMappedPort
        )
        connector.connect("system")

        connector.session.execute(
            """
        CREATE KEYSPACE IF NOT EXISTS products_ks
        WITH replication = {'class': 'SimpleStrategy', 'replication_factor': 1}
        """.trimIndent()
        )

        connector.connect("products_ks")

        connector.session.execute(
            """
        CREATE TABLE IF NOT EXISTS product (
            id UUID PRIMARY KEY,
            name TEXT,
            price DOUBLE
        )
        """.trimIndent()
        )

        repository = ProductRepository(connector.session)
    }

    @AfterEach
    fun tearDown() {
        repository.session.execute("TRUNCATE TABLE products_ks.product")
        cassandra.stop()
    }

    @Test
    fun `test adding a product`() {
        val product = repository.addProduct("Test Product", 10.99)

        val fetchedProduct = repository.getProductById(product.id)
        assertEquals(product, fetchedProduct)
    }

}
