package imaginate.shared.settings

import androidx.compose.runtime.mutableStateOf
import com.russhwolf.settings.Settings
import com.russhwolf.settings.set

interface ImaginateSettings {
    var apiKey: String?
}


internal
class DefaultImaginateSettings(
    private val settings: Settings
) : ImaginateSettings {

    private
    val apiKeyState = mutableStateOf(settings.getStringOrNull("api-key"))

    override var apiKey: String?
        get() = apiKeyState.value
        set(value) {
            settings["api-key"] = value
            apiKeyState.value = value
        }
}