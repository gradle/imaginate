package imaginate.generation

import io.ktor.client.HttpClient
import io.ktor.client.HttpClientConfig
import io.ktor.client.call.body
import io.ktor.client.engine.HttpClientEngine
import io.ktor.client.request.get
import io.ktor.client.request.headers
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.bodyAsText
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

class ImageGenerator private constructor(
    private val client: HttpClient,
    private val apiKey: String?,
) {

    sealed interface Result {
        class Success(val image: ByteArray) : Result
        class Failure(val reason: String) : Result
    }

    constructor(apiKey: String? = null) : this(HttpClient(httpClientConfig), apiKey)

    internal
    constructor(client: HttpClientEngine, apiKey: String? = null) : this(HttpClient(client, httpClientConfig), apiKey)

    suspend fun generate(
        prompt: String,
        width: Int = 512,
        height: Int = width,
    ): Result {
        val response = try {
            httpResponseFor(prompt, width, height)
        } catch (e: Exception) {
            return Result.Failure(e.message ?: e.toString())
        }
        return if (response.status.value in 200..299) {
            Result.Success(response.body())
        } else {
            Result.Failure("Failed to generate image '${extractMessageFrom(response.bodyAsText())}'")
        }
    }

    private
    suspend fun httpResponseFor(prompt: String, width: Int, height: Int): HttpResponse =
        when (apiKey) {
            null -> {
                // apiKey is not set, return picsum image
                client.get("https://picsum.photos/$width/$height.jpg")
            }

            else -> {
                validate(width, height)
                client.post(textToImage) {
                    headers {
                        append("Authorization", "Bearer $apiKey")
                        append("Content-Type", "application/json")
                        append("Accept", "image/png")
                    }
                    setBody(
                        Json.encodeToString(
                            RequestParameters(
                                width = width,
                                height = height,
                                prompts = listOf(RequestPrompt(prompt))
                            )
                        )
                    )
                }
            }
        }

    /**
     * [width] and [height] must be multiples of 64 and greater than or equal 128.
     * 262,144 <= [width] * [height] <= 1,048,576
     * See [endpoint documentation](https://platform.stability.ai/rest-api#tag/v1generation/operation/textToImage).
     */
    private
    fun validate(width: Int, height: Int) {
        fun check(what: String, value: Int) {
            require(value % 64 == 0) {
                "$what must be a multiple of 64"
            }
            require(value >= 128) {
                "$what must be greater than or equal 128"
            }
        }
        check("width", width)
        check("height", width)
        require((height * width) in 262_144..1_048_576) {
            "262,144 <= width*height <= 1,048,576 where width=$width, height=$height and width*height=${width * height}"
        }
    }

    private
    companion object {

        val httpClientConfig: HttpClientConfig<*>.() -> Unit = {}

        const val engineId = "stable-diffusion-512-v2-1"
        const val textToImage = "https://api.stability.ai/v1/generation/$engineId/text-to-image"
    }
}

@Serializable
internal
data class RequestParameters(
    val width: Int,
    val height: Int,
    @SerialName("text_prompts")
    val prompts: List<RequestPrompt>
)

@Serializable
internal
data class RequestPrompt(
    val text: String
)

@Serializable
internal
data class FailureResponse(
    val message: String
)

private
val partialJson = Json { ignoreUnknownKeys = true }

internal
fun extractMessageFrom(response: String): String =
    try {
        partialJson.decodeFromString<FailureResponse>(response).message
    } catch (e: Exception) {
        e.printStackTrace()
        response
    }