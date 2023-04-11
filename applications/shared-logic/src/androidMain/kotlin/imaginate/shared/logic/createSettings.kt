package imaginate.shared.logic

import android.content.Context
import com.russhwolf.settings.SharedPreferencesSettings

fun createImaginateSettings(context: Context): ImaginateSettings = DefaultImaginateSettings(
    SharedPreferencesSettings.Factory(context).create()
)