package imaginate.shared.settings

import com.russhwolf.settings.PreferencesSettings

fun createImaginateSettings(): ImaginateSettings = DefaultImaginateSettings(
    PreferencesSettings.Factory().create()
)