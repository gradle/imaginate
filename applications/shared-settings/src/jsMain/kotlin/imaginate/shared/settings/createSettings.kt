package imaginate.shared.settings

import com.russhwolf.settings.StorageSettings


fun createImaginateSettings(): ImaginateSettings = DefaultImaginateSettings(
    StorageSettings()
)
