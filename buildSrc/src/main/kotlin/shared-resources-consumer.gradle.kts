import imaginate.ImageFormat

val sharedBitmaps = configurations.register("sharedBitmaps") {
    attributes {
        attribute(ImageFormat.IMAGE_FORMAT_ATTRIBUTE, objects.named(ImageFormat.BITMAP))
    }
}
tasks.register("sharedBitmaps", Sync::class) {
    from(sharedBitmaps)
    into(layout.buildDirectory.dir("shared-bitmaps"))
}

val sharedDrawables = configurations.register("sharedDrawables") {
    attributes {
        attribute(ImageFormat.IMAGE_FORMAT_ATTRIBUTE, objects.named(ImageFormat.DRAWABLE))
    }
}
tasks.register("sharedDrawables", Sync::class) {
    from(sharedDrawables)
    into(layout.buildDirectory.dir("shared-drawables"))
}
