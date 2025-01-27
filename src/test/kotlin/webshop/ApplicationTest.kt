package webshop

import io.ktor.server.testing.*
import kotlin.test.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.mockk.mockk
import webshop.database.ProductRepository


class ApplicationTest {

    private val mockRepository = mockk<ProductRepository>(relaxed = true)

    @Test
    fun sampleTest() {
        assertTrue(true)
    }

    @Test
    fun testRoot() = testApplication {
        application {
            module(mockRepository)
        }
        val response = client.get("/")
        assertEquals(HttpStatusCode.OK, response.status)
        assertEquals("Welcome to the webshop API!", response.bodyAsText())
    }

}