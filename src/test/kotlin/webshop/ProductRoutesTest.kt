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