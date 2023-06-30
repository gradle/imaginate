import androidx.compose.ui.window.ComposeUIViewController

import imaginate.shared.ui.CommonUi
import imaginate.shared.logic.createImaginateSettings

fun MainViewController() =
    ComposeUIViewController {
        CommonUi(createImaginateSettings())
    }
