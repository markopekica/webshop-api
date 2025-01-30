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
import webshop.models.UpdateProductRequest
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
        assertContains(responseBody, "Product not found")
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
        val responseBody = response.bodyAsText()
        assertContains(responseBody, "Smartwatch")
        assertContains(responseBody, "299.99")
    }

    @Test
    fun `test POST products with invalid data`() = testApplication {
        val mockRepository = mockk<ProductRepository>(relaxed = true)

        application {
            module(mockRepository)
        }

        val requestBody = Json.encodeToString(
            CreateProductRequest.serializer(),
            CreateProductRequest("", -10.0)
        )

        val response = client.post("/products") {
            contentType(ContentType.Application.Json)
            setBody(requestBody)
        }

        assertEquals(HttpStatusCode.BadRequest, response.status)

        val responseBody = response.bodyAsText()
        assertContains(responseBody, "Name cannot be blank")
        assertContains(responseBody, "Price must be greater than 0")

    }

    @Test
    fun `test PUT products with valid data`() = testApplication {
        val mockRepository = mockk<ProductRepository>(relaxed = true)

        val productId = UUID.randomUUID()
        val originalProduct = ProductEntity(productId, "Original Name", 99.99)
        val updatedProduct = ProductEntity(productId, "Updated Name", 199.99)

        every { mockRepository.getProductById(productId) } returns originalProduct
        every { mockRepository.updateProduct(productId, any()) } returns updatedProduct

        application {
            module(mockRepository)
        }

        val requestBody = Json.encodeToString(
            UpdateProductRequest.serializer(),
            UpdateProductRequest("Updated Name", 199.99)
        )

        val response = client.put("/products/$productId") {
            contentType(ContentType.Application.Json)
            setBody(requestBody)
        }

        assertEquals(HttpStatusCode.OK, response.status)

        val responseBody = response.bodyAsText()
        assertContains(responseBody, "Updated Name")
        assertContains(responseBody, "199.99")
    }

    @Test
    fun `test PUT products with invalid data`() = testApplication {
        val mockRepository = mockk<ProductRepository>(relaxed = true)
        application {
            module(mockRepository)
        }

        val requestBody = Json.encodeToString(
            UpdateProductRequest.serializer(),
            UpdateProductRequest("", -0.99)
        )

        val productId = UUID.randomUUID()

        val response = client.put("/products/$productId") {
            contentType(ContentType.Application.Json)
            setBody(requestBody)
        }

        assertEquals(HttpStatusCode.BadRequest, response.status)

        val responseBody = response.bodyAsText()
        assertContains(responseBody, "Name cannot be blank")
        assertContains(responseBody, "Price must be greater than 0")
    }

    @Test
    fun `test PUT products for non-existent ID`() = testApplication {
        val mockRepository = mockk<ProductRepository>(relaxed = true)
        application {
            module(mockRepository)
        }

        every { mockRepository.getProductById(any()) } returns null

        val requestBody = Json.encodeToString(
            UpdateProductRequest.serializer(),
            UpdateProductRequest("Updated Name", 199.99)
        )

        val productId = UUID.randomUUID()

        val response = client.put("/products/$productId") {
            contentType(ContentType.Application.Json)
            setBody(requestBody)
        }

        assertEquals(HttpStatusCode.NotFound, response.status)

        val responseBody = response.bodyAsText()
        assertEquals("""{"message":"Product with ID $productId not found"}""", responseBody)
    }

    @Test
    fun `test DELETE existing product`() = testApplication {
        val mockRepository = mockk<ProductRepository>(relaxed = true)
        application { module(mockRepository) }

        val productId = UUID.randomUUID()

        every { mockRepository.deleteProductById(productId) } returns true

        val response = client.delete("/products/$productId")

        assertEquals(HttpStatusCode.OK, response.status)
        assertContains(response.bodyAsText(), "Product with ID $productId was successfully deleted")
    }

    @Test
    fun `test DELETE non-existent product`() = testApplication {
        val mockRepository = mockk<ProductRepository>(relaxed = true)
        application { module(mockRepository) }

        val productId = UUID.randomUUID()

        every { mockRepository.deleteProductById(productId) } returns false

        val response = client.delete("/products/$productId")

        assertEquals(HttpStatusCode.NotFound, response.status)
        assertContains(response.bodyAsText(), "Product with ID $productId not found")
    }

    @Test
    fun `test DELETE with invalid ID format`() = testApplication {
        val mockRepository = mockk<ProductRepository>(relaxed = true)
        application { module(mockRepository) }

        val response = client.delete("/products/invalid-uuid")

        assertEquals(HttpStatusCode.BadRequest, response.status)
        assertContains(response.bodyAsText(), "Invalid Product ID format")
    }

}