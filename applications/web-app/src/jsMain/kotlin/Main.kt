import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import com.russhwolf.settings.Settings
import com.russhwolf.settings.StorageSettings
import com.russhwolf.settings.set
import imaginate.generation.ImageGenerator
import kotlinx.coroutines.launch
import org.jetbrains.compose.web.attributes.*
import org.jetbrains.compose.web.css.*
import org.jetbrains.compose.web.dom.*
import org.jetbrains.compose.web.renderComposable
import kotlin.io.encoding.Base64
import kotlin.io.encoding.ExperimentalEncodingApi


object MyStyleSheet : StyleSheet() {

    val logo by style {
        width(25.px)
        height(25.px)
        paddingRight(12.px)
    }

    init {
        "body" style {
            paddingLeft(25.px)
        }
        "h1" style {
        }
        "img" style {
            marginTop(25.px)
        }
    }
}


@Composable
fun App(settings: ImaginateSettings) {
    AppHeader()

    when (val apiKey = settings.apiKey) {
        null -> {
            ApiKeyPrompt(onApiKey = { settings.apiKey = it })
        }

        else -> {
            ImagePrompt(apiKey)
        }
    }
}


@Composable
fun AppHeader() {
    H1 {
        Img(src = "icon.jpg", attrs = { classes(MyStyleSheet.logo) })
        Text("imaginate")
    }
}


@Composable
fun ApiKeyPrompt(onApiKey: (String) -> Unit) {
    val (apiKey, setApiKey) = remember { mutableStateOf("") }
    TextInput {
        placeholder("Type your API key")
        value(apiKey)
        onInput { event -> setApiKey(event.value) }
    }
    Button({ onClick { onApiKey(apiKey) } }) {
        Text("Accept")
    }
}


@Composable
fun ImagePrompt(apiKey: String) {
    val (prompt, setPrompt) = remember { mutableStateOf("") }
    val (imageSrc, setImageSrc) = remember { mutableStateOf<String?>(null) }
    val imageGenerator = remember { ImageGenerator(apiKey) }

    val coroutineScope = rememberCoroutineScope()

    @OptIn(ExperimentalEncodingApi::class)
    fun loadNewImage() = coroutineScope.launch {
        setImageSrc("data:image/jpeg;charset=utf-8;base64,${
            Base64.encode(imageGenerator.generate(prompt))
        }")
    }

    TextInput {
        placeholder("Type your prompt")
        value(prompt)
        onInput { event -> setPrompt(event.value) }
    }
    Button({ onClick { loadNewImage() } }) {
        Text("Generate new image!")
    }
    if (imageSrc != null) {
        Div {
            Img(imageSrc)
        }
    }
}

class ImaginateSettings(
    private val settings: Settings
) {

    private
    val apiKeyState = mutableStateOf(settings.getStringOrNull("api-key"))

    var apiKey: String?
        get() = apiKeyState.value
        set(value) {
            settings["api-key"] = value
            apiKeyState.value = value
        }
}


fun main() {
    renderComposable(rootElementId = "root") {
        Style(MyStyleSheet)
        App(ImaginateSettings(StorageSettings()))
    }
}
