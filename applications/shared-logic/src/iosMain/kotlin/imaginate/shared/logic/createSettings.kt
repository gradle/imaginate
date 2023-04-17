package imaginate.shared.logic

import com.russhwolf.settings.NSUserDefaultsSettings
import imaginate.shared.logic.DefaultImaginateSettings
import imaginate.shared.logic.ImaginateSettings

fun createImaginateSettings(): ImaginateSettings = DefaultImaginateSettings(
    NSUserDefaultsSettings.Factory().create("imaginate")
)
