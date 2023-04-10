package imaginate.shared.settings

import com.russhwolf.settings.PreferencesSettings

fun ImaginateSettings(): ImaginateSettings = DefaultImaginateSettings(
    PreferencesSettings.Factory().create()
)