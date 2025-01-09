package webshop

import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.testing.*
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource
import webshop.models.CreateProductRequest
import kotlin.test.Test
import kotlin.test.assertContains
import kotlin.test.assertEquals


class ProductRoutesTest {

    @ParameterizedTest
    @ValueSource(strings = ["1", "abc", "999"])
    fun productRoutesTest(id: String) = testApplication {
        application {
            module()
        }
        val client = createClient {
            this@testApplication.install(ContentNegotiation) {
                json()
            }
        }
        val response = client.get("/products/$id")
        assertEquals(
            when (id) {
                "1" -> HttpStatusCode.OK
                "abc" -> HttpStatusCode.BadRequest
                else -> HttpStatusCode.NotFound
            }, response.status
        )
    }

    @Test
    fun testCreateProduct() = testApplication {
        application {
            module()
        }
        val client = createClient {
            this@testApplication.install(ContentNegotiation) {
                json()
            }
        }
        val response = client.post("/products") {
            contentType(ContentType.Application.Json)
            setBody(Json.encodeToString(CreateProductRequest.serializer(), CreateProductRequest("Smartwatch", 299.99)))
        }
        assertEquals(HttpStatusCode.Created, response.status)
        assertContains(response.bodyAsText(), "Smartwatch")
    }

    @Test
    fun testCreateInvalidProduct() = testApplication {
        application {
            module()
        }
        val client = createClient {
            this@testApplication.install(ContentNegotiation) {
                json()
            }
        }
        val response = client.post("/products") {
            contentType(ContentType.Application.Json)
            setBody(Json.encodeToString(CreateProductRequest("", -10.0)))
        }
        assertEquals(HttpStatusCode.BadRequest, response.status)
        assertContains(response.bodyAsText(), "Product name can not be empty")
        assertContains(response.bodyAsText(), "Price must be greater than 0")
    }

}