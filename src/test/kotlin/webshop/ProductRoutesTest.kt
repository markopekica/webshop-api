package webshop

import io.ktor.server.testing.*
import webshop.database.ProductEntity
import webshop.database.ProductRepository
import java.util.UUID
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.mockk.every
import io.mockk.mockk
import kotlinx.serialization.json.Json
import webshop.models.CreateProductRequest
import kotlin.test.*

class ProductRoutesTest {

    @Test
    fun `test GET all products`() = testApplication {
        val mockRepository = mockk<ProductRepository>(relaxed = true)

        every { mockRepository.getAllProducts() } returns listOf(
            ProductEntity(UUID.randomUUID(), "Product A", 99.99),
            ProductEntity(UUID.randomUUID(), "Product B", 199.99)
        )

        application {
            module(mockRepository)
        }

        val response = client.get("/products")

        assertEquals(HttpStatusCode.OK, response.status)

        val responseBody = response.bodyAsText()
        assertContains(responseBody, "Product A")
        assertContains(responseBody, "Product B")
    }

    @Test
    fun `test GET product by ID`() = testApplication {
        val mockRepository = mockk<ProductRepository>(relaxed = true)

        val sampleProductId = UUID.randomUUID()
        val sampleProduct = ProductEntity(sampleProductId, "Sample Product", 50.0)

        every { mockRepository.getProductById(sampleProductId) } returns sampleProduct

        application {
            module(mockRepository)
        }

        val response = client.get("/products/$sampleProductId")

        assertEquals(HttpStatusCode.OK, response.status)

        val responseBody = response.bodyAsText()
        assertContains(responseBody, "Sample Product")
        assertContains(responseBody, "50.0")
    }

    @Test
    fun `test GET product by invalid ID`() = testApplication {
        val mockRepository = mockk<ProductRepository>(relaxed = true)

        every { mockRepository.getProductById(any()) } returns null

        application {
            module(mockRepository)
        }

        val nonExistentId = UUID.randomUUID()

        val response = client.get("/products/$nonExistentId")

        assertEquals(HttpStatusCode.NotFound, response.status)

        val responseBody = response.bodyAsText()
        assertContains(responseBody, "404: Page Not Found")
    }

    @Test
    fun `test POST products with valid data`() = testApplication {
        val mockRepository = mockk<ProductRepository>(relaxed = true)

        every { mockRepository.addProduct("Smartwatch", 299.99) } returns ProductEntity(
            UUID.randomUUID(),
            "Smartwatch",
            299.99
        )

        application {
            module(mockRepository)
        }

        val requestBody = Json.encodeToString(
            CreateProductRequest.serializer(),
            CreateProductRequest("Smartwatch", 299.99)
        )

        val response = client.post("/products") {
            contentType(ContentType.Application.Json)
            setBody(requestBody)
        }

        assertEquals(HttpStatusCode.Created, response.status)
        assertContains(response.bodyAsText(), "Smartwatch")
    }

}