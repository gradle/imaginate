package conf.desktop

import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import imaginate.shared.logic.ui.App

fun main() = application {
    Window(onCloseRequest = ::exitApplication) {
        App()
    }
}
