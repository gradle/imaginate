package imaginate.shared.ui

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
import imaginate.shared.logic.ImaginateSettings
import kotlinx.coroutines.launch

@Composable
fun App(settings: ImaginateSettings) {
    MaterialTheme {
        Column(Modifier.fillMaxSize(), Arrangement.spacedBy(8.dp), Alignment.CenterHorizontally) {

            when (val apiKey = settings.apiKey) {
                null -> {
                    ApiKeyPrompt(onApiKey = { settings.apiKey = it })
                }

                else -> {
                    ImagePrompt(apiKey, onClearApiKey = { settings.apiKey = null })
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
    Button(onClick = { onApiKey(apiKey) }, enabled = apiKey.isNotBlank()) {
        Text("Accept")
    }
}

@Composable
fun ImagePrompt(apiKey: String, onClearApiKey: () -> Unit) {
    val (prompt, setPrompt) = remember { mutableStateOf("") }
    val (image, setImage) = remember { mutableStateOf<ImageBitmap?>(null) }
    val imageGenerator = remember { ImageGenerator(apiKey) }

    val coroutineScope = rememberCoroutineScope()

    fun loadNewImage() = coroutineScope.launch {
        setImage(imageBitmapFromBytes(imageGenerator.generate(prompt)))
    }

    TextField(
        prompt,
        onValueChange = setPrompt,
        placeholder = { Text("Type your prompt") }
    )
    Button(onClick = ::loadNewImage, enabled = prompt.isNotBlank()) {
        Text("Generate new image!")
    }
    if (image != null) {
        Image(image, prompt)
    }
    Button(onClick = { onClearApiKey() }) {
        Text("Clear API key")
    }
}
