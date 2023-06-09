package imaginate.desktop

import androidx.compose.ui.res.painterResource
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import imaginate.shared.ui.CommonUi
import imaginate.shared.logic.createImaginateSettings

fun main() = application {
    Window(
        title = "imaginate",
        icon = painterResource("icon.jpg"),
        onCloseRequest = ::exitApplication,
    ) {
        CommonUi(createImaginateSettings())
    }
}
