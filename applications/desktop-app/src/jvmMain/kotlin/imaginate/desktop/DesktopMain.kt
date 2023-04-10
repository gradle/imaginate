package imaginate.desktop

import androidx.compose.ui.res.painterResource
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import imaginate.shared.logic.ui.App
import imaginate.shared.settings.ImaginateSettings

fun main() = application {
    Window(
        title = "imaginate",
        icon = painterResource("icon.jpg"),
        onCloseRequest = ::exitApplication,
    ) {
        App(ImaginateSettings())
    }
}
