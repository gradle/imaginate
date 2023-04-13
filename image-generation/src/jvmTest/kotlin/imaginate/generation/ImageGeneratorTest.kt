package imaginate.generation

import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.respond
import io.ktor.content.TextContent
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpMethod
import io.ktor.http.HttpStatusCode
import io.ktor.http.headersOf
import io.ktor.utils.io.ByteReadChannel
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class ImageGeneratorTest {

    @Test
    fun `requests image with the correct parameters`() {
        val apiKey = "the-api-key"
        val prompt = "a purple elephant"
        val width = 512
        val height = 512
        val engineId = "stable-diffusion-512-v2-1"
        val responseBody = ByteArray(width * height) { it.toByte() }
        runBlocking {
            val subject = ImageGenerator(
                MockEngine { request ->
                    assertEquals(
                        "https://api.stability.ai/v1/generation/$engineId/text-to-image",
                        request.url.toString()
                    )
                    assertEquals(
                        HttpMethod.Post,
                        request.method
                    )
                    request.headers.run {
                        assertEquals("Bearer $apiKey", get("Authorization"))
                        assertEquals("image/png", get("Accept"))
                    }
                    assertEquals(
                        "application/json",
                        request.body.contentType?.toString()
                    )
                    assertEquals(
                        RequestParameters(
                            width = width,
                            height = height,
                            prompts = listOf(RequestPrompt(prompt))
                        ),
                        Json.decodeFromString((request.body as TextContent).text)
                    )
                    respond(
                        content = ByteReadChannel(responseBody),
                        status = HttpStatusCode.OK,
                        headers = headersOf(HttpHeaders.ContentType, "image/png")
                    )
                },
                apiKey
            )
            val result = subject.generate(prompt, width = width, height = height)
            require(result is ImageGenerator.Result.Success)
            assertEquals(responseBody, result.image)
        }
    }
}