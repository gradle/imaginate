plugins {
    id("base")
    alias(libs.plugins.kotlin.mpp) apply false
    alias(libs.plugins.kotlin.android) apply false
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.compose) apply false
}
