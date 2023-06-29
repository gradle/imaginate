package imaginate.android

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import imaginate.shared.ui.CommonUi
import imaginate.shared.logic.createImaginateSettings

class AndroidMainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            CommonUi(createImaginateSettings(this))
        }
    }
}
