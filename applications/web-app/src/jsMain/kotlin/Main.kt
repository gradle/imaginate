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

    val prompt = remember { mutableStateOf("") }
    val imageSrc = remember { mutableStateOf<String?>(null) }
    val imageGenerator = remember { ImageGenerator() }

    val coroutineScope = rememberCoroutineScope()

    @OptIn(ExperimentalEncodingApi::class)
    fun loadNewImage() = coroutineScope.launch {
        imageSrc.value = "data:image/jpeg;charset=utf-8;base64,${
            Base64.encode(imageGenerator.generate(prompt.value))
        }"
    }

    H1 {
        Img(src = "icon.jpg", attrs = { classes(MyStyleSheet.logo)})
        Text("imaginate")
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
