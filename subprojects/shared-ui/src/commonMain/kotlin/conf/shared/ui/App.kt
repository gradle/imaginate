package conf.shared.ui

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
import conf.domain.ImageGenerator
import kotlinx.coroutines.launch

@Composable
fun App() {

    val prompt = remember { mutableStateOf("") }
    val image = remember { mutableStateOf<ImageBitmap?>(null) }
    val imageGenerator = remember { ImageGenerator() }

    val coroutineScope = rememberCoroutineScope()

    fun loadNewImage() = coroutineScope.launch {
        image.value = imageBitmapFromBytes(imageGenerator.generate(prompt.value))
    }

    MaterialTheme {
        Column(Modifier.fillMaxSize(), Arrangement.spacedBy(8.dp), Alignment.CenterHorizontally) {
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
    }
}
