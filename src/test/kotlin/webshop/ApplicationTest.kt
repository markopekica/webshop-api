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
        assertEquals(HttpStatusCode.OK, response.status)
        assertEquals("Welcome to the webshop API!", response.bodyAsText())
    }

}