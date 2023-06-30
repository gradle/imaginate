package imaginate.shared.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement.spacedBy
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color.Companion.Red
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.unit.dp
import imaginate.generation.ImageGenerator
import imaginate.shared.logic.ImaginateSettings
import kotlinx.coroutines.launch

@Composable
fun CommonUi(settings: ImaginateSettings) {
    MaterialTheme {
        Column(Modifier.fillMaxSize(), spacedBy(8.dp), CenterHorizontally) {

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
@OptIn(ExperimentalMaterial3Api::class)
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
@OptIn(ExperimentalMaterial3Api::class)
fun ImagePrompt(apiKey: String, onClearApiKey: () -> Unit) {
    val (prompt, setPrompt) = remember { mutableStateOf("") }
    val (image, setImage) = remember { mutableStateOf<ImageBitmap?>(null) }
    val (failure, setFailure) = remember { mutableStateOf<String?>(null) }
    val (processing, setProcessing) = remember { mutableStateOf(false) }
    val imageGenerator = remember { ImageGenerator(apiKey) }

    val coroutineScope = rememberCoroutineScope()

    fun loadNewImage(prompt: String) = coroutineScope.launch {
        setProcessing(true)
        when (val result = imageGenerator.generate(prompt)) {
            is ImageGenerator.Result.Failure -> {
                setFailure(result.reason)
                setImage(null)
            }

            is ImageGenerator.Result.Success -> {
                setImage(imageBitmapFromBytes(result.image))
                setFailure(null)
            }
        }
        setProcessing(false)
    }

    TextField(
        prompt,
        onValueChange = setPrompt,
        placeholder = { Text("Type your prompt") }
    )
    Button(
        onClick = { loadNewImage(prompt) },
        enabled = prompt.isNotBlank() && !processing
    ) {
        Text("Generate new image!")
    }
    if (image != null) {
        Image(image, prompt)
    }
    if (failure != null) {
        Text(failure, color = Red)
    }
    Button(
        onClick = { onClearApiKey() },
        enabled = !processing
    ) {
        Text("Clear API key")
    }
}
