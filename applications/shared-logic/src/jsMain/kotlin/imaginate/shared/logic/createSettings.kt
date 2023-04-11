package imaginate.shared.logic

import com.russhwolf.settings.StorageSettings

fun createImaginateSettings(): ImaginateSettings = DefaultImaginateSettings(
    StorageSettings()
)
