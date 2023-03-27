plugins {
    `kotlin-dsl`
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(libs.versions.jvm.get().toInt()))
    }
}

dependencies {
    implementation("conf:domain-library")
}
