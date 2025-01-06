package webshop

import io.ktor.server.testing.*
import kotlin.test.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.plugins.contentnegotiation.*


class ApplicationTest {

    @Test
    fun sampleTest() {
        assertTrue(true)
    }

    @Test
    fun testRoot() = testApplication {
        application {
            module()
        }
        val response = client.get("/")
        println("\n****\nresponse - $response")
        println("Response body: ${response.bodyAsText()}")
        assertEquals(HttpStatusCode.OK, response.status)
        assertEquals("Welcome to the webshop API!", response.bodyAsText())
    }

    /*
    @Test
    fun validProductIdReturnsProduct() = testApplication {
        application {
            module()
        }
        val client = createClient {
            this@testApplication.install(ContentNegotiation) {
                json()
            }
        }
        val response = client.get("/products/1")
        assertEquals(HttpStatusCode.OK, response.status)
    }

    @Test
    fun invalidProductIdReturns404() = testApplication {
        application {
            module()
        }
        val response = client.get("/products/-1")
        assertEquals(HttpStatusCode.NotFound, response.status)
        println("\n****\nresponse.toString(): ${response.status}")
        assertContains(response.toString(), "404 Not Found")
        assertEquals(response.status.toString(), "404 Not Found")
    }

    @Test
    fun nonNumericIdReturns400() = testApplication {
        application {
            module()
        }
        val response = client.get("/products/abc")
        assertEquals(HttpStatusCode.BadRequest, response.status)
    }
*/

}