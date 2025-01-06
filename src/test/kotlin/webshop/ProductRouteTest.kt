package webshop

import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.testing.*
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource
import kotlin.test.assertEquals

class ProductRouteTest {

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
        println("\n****\nin 1 response - $response")
        println("\nin 1 id - $id")
        assertEquals(
            when (id) {
                "1" -> HttpStatusCode.OK
                "abc" -> HttpStatusCode.BadRequest
                else -> HttpStatusCode.NotFound
            }, response.status
        )
    }

}