package imaginate.desktop

import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import imaginate.shared.logic.ui.App

fun main() = application {
    Window(
        title = "Imaginate",
        onCloseRequest = ::exitApplication,
    ) {
        App()
    }
}
