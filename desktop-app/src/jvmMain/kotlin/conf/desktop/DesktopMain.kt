package conf.desktop

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import conf.common.commonLogic

@Composable
@Preview
fun DesktopApp() {
    var text by remember { mutableStateOf("Hello, world!") }

    MaterialTheme {
        Button(onClick = {
            text = commonLogic("Hello, Desktop!")
        }) {
            Text(text)
        }
    }
}

fun main() = application {
    Window(onCloseRequest = ::exitApplication) {
        DesktopApp()
    }
}
