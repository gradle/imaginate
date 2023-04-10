package imaginate.android

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import imaginate.shared.logic.ui.App
import imaginate.shared.settings.ImaginateSettings

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            App(ImaginateSettings(this))
        }
    }
}
