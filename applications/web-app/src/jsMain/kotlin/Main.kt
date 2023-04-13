import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import imaginate.generation.ImageGenerator
import imaginate.shared.logic.ImaginateSettings
import imaginate.shared.logic.createImaginateSettings
import kotlinx.coroutines.launch
import org.jetbrains.compose.web.attributes.*
import org.jetbrains.compose.web.css.*
import org.jetbrains.compose.web.css.Color.red
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

    val error by style {
        color(red)
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
            ImagePrompt(apiKey, onClearApiKey = { settings.apiKey = null })
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
    Button({
        onClick { onApiKey(apiKey) }
        if (apiKey.isBlank()) {
            disabled()
        }
    }) {
        Text("Accept")
    }
}


@Composable
fun ImagePrompt(apiKey: String, onClearApiKey: () -> Unit) {
    val (prompt, setPrompt) = remember { mutableStateOf("") }
    val (imageSrc, setImageSrc) = remember { mutableStateOf<String?>(null) }
    val (failure, setFailure) = remember { mutableStateOf<String?>(null) }
    val imageGenerator = remember { ImageGenerator(apiKey) }

    val coroutineScope = rememberCoroutineScope()

    @OptIn(ExperimentalEncodingApi::class)
    fun loadNewImage() = coroutineScope.launch {
        when (val result = imageGenerator.generate(prompt)) {
            is ImageGenerator.Result.Failure -> {
                setFailure(result.reason)
                setImageSrc(null)
            }

            is ImageGenerator.Result.Success -> {
                setImageSrc(
                    "data:image/jpeg;charset=utf-8;base64,${
                        Base64.encode(result.image)
                    }"
                )
                setFailure(null)
            }
        }
    }

    TextInput {
        placeholder("Type your prompt")
        value(prompt)
        onInput { event -> setPrompt(event.value) }
    }
    Button({
        onClick { loadNewImage() }
        if (prompt.isBlank()) {
            disabled()
        }
    }) {
        Text("Generate new image!")
    }
    if (imageSrc != null) {
        Div {
            Img(imageSrc)
        }
    }
    if (failure != null) {
        Div({ classes(MyStyleSheet.error) }) {
            Text(failure)
        }
    }
    Br()
    Button({ onClick { onClearApiKey() } }) {
        Text("Clear API key")
    }
}

fun main() {
    renderComposable(rootElementId = "root") {
        Style(MyStyleSheet)
        App(createImaginateSettings())
    }
}
