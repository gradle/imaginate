import androidx.compose.ui.window.ComposeUIViewController

import imaginate.shared.ui.App
import imaginate.shared.logic.createImaginateSettings

fun MainViewController() =
    ComposeUIViewController {
        App(createImaginateSettings())
    }
