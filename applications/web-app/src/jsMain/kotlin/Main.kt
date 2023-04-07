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


object MyStyleSheet : StyleSheet()


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

    Div({ style { padding(25.px) } }) {
        TextInput {
            placeholder("Type your prompt")
            value(prompt.value)
            onInput { event -> prompt.value = event.value }
        }
        Button({ onClick { loadNewImage() } }) {
            Text("Generate new image!")
        }
    }
    if (imageSrc.value != null) {
        Div({ style { padding(25.px) } }) {
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
