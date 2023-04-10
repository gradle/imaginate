package imaginate.shared.settings

import android.content.Context
import com.russhwolf.settings.SharedPreferencesSettings

fun ImaginateSettings(context: Context): ImaginateSettings = DefaultImaginateSettings(
    SharedPreferencesSettings.Factory(context).create()
)