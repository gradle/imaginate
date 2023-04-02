package conf.domain

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get

class ImageGenerator {

    private
    val client = HttpClient()

    suspend fun generate(prompt: String): ByteArray {
        val response = client.get("https://picsum.photos/320.jpg")
        if (response.status.value in 200..299) {
            return response.body()
        } else {
            throw Exception("Failed to generate image")
        }
    }
}
