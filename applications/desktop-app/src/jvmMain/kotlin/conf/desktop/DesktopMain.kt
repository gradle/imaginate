package conf.desktop

import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import conf.shared.ui.App

fun main() = application {
    Window(onCloseRequest = ::exitApplication) {
        App()
    }
}
