import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import conf.domain.ImageGenerator
import io.ktor.util.encodeBase64
import kotlinx.coroutines.launch
import org.jetbrains.compose.web.attributes.*
import org.jetbrains.compose.web.css.*
import org.jetbrains.compose.web.dom.*
import org.jetbrains.compose.web.renderComposable


object MyStyleSheet : StyleSheet()


@Composable
fun App() {

    val prompt = remember { mutableStateOf("") }
    var imageSrc: String by mutableStateOf("")

    val coroutineScope = rememberCoroutineScope()
    val imageGenerator = ImageGenerator()

    fun loadNewImage() {
        coroutineScope.launch {
            val imageData = imageGenerator.generate(prompt.value).encodeBase64()
            imageSrc = "data:image/jpeg;charset=utf-8;base64,$imageData"
        }
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
    Div({ style { padding(25.px) } }) {
        Img(imageSrc)
    }
}


fun main() {
    renderComposable(rootElementId = "root") {
        Style(MyStyleSheet)
        App()
    }
}
