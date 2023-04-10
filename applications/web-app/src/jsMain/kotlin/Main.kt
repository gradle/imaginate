import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
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
fun App() {
    AppHeader()

    val (apiKey, setApiKey) = remember { mutableStateOf<String?>(null) }
    when (apiKey) {
        null -> {
            ApiKeyPrompt(onApiKey = setApiKey)
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
    val prompt = remember { mutableStateOf("") }
    val imageSrc = remember { mutableStateOf<String?>(null) }
    val imageGenerator = remember { ImageGenerator(apiKey) }

    val coroutineScope = rememberCoroutineScope()

    @OptIn(ExperimentalEncodingApi::class)
    fun loadNewImage() = coroutineScope.launch {
        imageSrc.value = "data:image/jpeg;charset=utf-8;base64,${
            Base64.encode(imageGenerator.generate(prompt.value))
        }"
    }

    TextInput {
        placeholder("Type your prompt")
        value(prompt.value)
        onInput { event -> prompt.value = event.value }
    }
    Button({ onClick { loadNewImage() } }) {
        Text("Generate new image!")
    }
    if (imageSrc.value != null) {
        Div {
            Img(imageSrc.value!!)
        }
    }
}


fun main() {
    renderComposable(rootElementId = "root") {
        Style(MyStyleSheet)
        App()
    }
}
