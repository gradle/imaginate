import imaginate.libs
import imaginate.composeCompiler

plugins {
    id("org.jetbrains.compose")
}

compose {
    // See https://androidx.dev/storage/compose-compiler/repository
    kotlinCompilerPlugin = "androidx.compose.compiler:compiler:${libs.composeCompiler}"
}
