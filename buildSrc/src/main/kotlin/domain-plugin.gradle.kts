import confbuild.ImageGeneratorTask

tasks.register<ImageGeneratorTask>("generateIcon") {
    image.set(layout.buildDirectory.file("generated-icon.jpg"))
}
