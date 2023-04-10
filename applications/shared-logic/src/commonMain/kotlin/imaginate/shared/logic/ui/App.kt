package imaginate.shared.logic.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.unit.dp
import imaginate.generation.ImageGenerator
import kotlinx.coroutines.launch

@Composable
fun App() {
    val (apiKey, setApiKey) = remember { mutableStateOf<String?>(null) }

    MaterialTheme {
        Column(Modifier.fillMaxSize(), Arrangement.spacedBy(8.dp), Alignment.CenterHorizontally) {

            when (apiKey) {
                null -> {
                    ApiKeyPrompt(onApiKey = setApiKey)
                }

                else -> {
                    ImagePrompt(apiKey)
                }
            }

        }
    }
}

@Composable
fun ApiKeyPrompt(onApiKey: (String) -> Unit) {
    val (apiKey, setApiKey) = remember { mutableStateOf("") }
    TextField(
        apiKey,
        onValueChange = setApiKey,
        placeholder = { Text("Type your API key") }
    )
    Button(onClick = { onApiKey(apiKey) }) {
        Text("Accept")
    }
}

@Composable
fun ImagePrompt(apiKey: String) {
    val prompt = remember { mutableStateOf("") }
    val image = remember { mutableStateOf<ImageBitmap?>(null) }
    val imageGenerator = remember { ImageGenerator(apiKey) }

    val coroutineScope = rememberCoroutineScope()

    fun loadNewImage() = coroutineScope.launch {
        image.value = imageBitmapFromBytes(imageGenerator.generate(prompt.value))
    }

    TextField(
        prompt.value,
        onValueChange = { value -> prompt.value = value },
        placeholder = { Text("Type your prompt") }
    )
    Button(onClick = ::loadNewImage) {
        Text("Generate new image!")
    }
    if (image.value != null) {
        Image(image.value!!, prompt.value)
    }
}
