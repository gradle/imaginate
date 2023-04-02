package conf.domain

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get

class ImageGenerator {

    private
    val client = HttpClient()

    suspend fun generate(
        prompt: String,
        width: Int = 320,
        height: Int = width,
    ): ByteArray {
        val response = client.get("https://picsum.photos/$width/$height.jpg")
        if (response.status.value in 200..299) {
            return response.body()
        } else {
            throw Exception("Failed to generate image")
        }
    }
}
