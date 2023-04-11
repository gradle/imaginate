package imaginate.shared.logic

import com.russhwolf.settings.PreferencesSettings

fun createImaginateSettings(): ImaginateSettings = DefaultImaginateSettings(
    PreferencesSettings.Factory().create()
)