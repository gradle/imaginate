package conf.domain

import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.respond
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.http.headersOf
import io.ktor.utils.io.ByteReadChannel
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Test
import kotlin.test.assertContentEquals
import kotlin.test.assertEquals

class ImageGeneratorTest {

    @Test
    fun `requests image with the correct width and height`() {
        val width = 1
        val height = 2
        val responseBody = ByteArray(width * height) { it.toByte() }
        runBlocking {
            val subject = ImageGenerator(MockEngine { request ->
                assertEquals(
                    "https://picsum.photos/$width/$height.jpg",
                    request.url.toString()
                )
                respond(
                    content = ByteReadChannel(responseBody),
                    status = HttpStatusCode.OK,
                    headers = headersOf(HttpHeaders.ContentType, "image/jpg")
                )
            })
            assertContentEquals(
                responseBody,
                subject.generate("a purple elephant", width = width, height = height)
            )
        }
    }
}