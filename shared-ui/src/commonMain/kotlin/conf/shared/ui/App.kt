package conf.shared.ui

import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import conf.shared.logic.sharedLogic

@Composable
fun App() {
    MaterialTheme {
        var text by remember { mutableStateOf("Hello, World!") }

        Button(onClick = {
            text = sharedLogic("Hello, ${getPlatformName()}")
        }) {
            Text(text)
        }
    }
}
