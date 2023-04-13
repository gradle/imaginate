import imaginate.ImageFormat.Companion.DRAWABLE
import imaginate.ImageFormat.Companion.BITMAP
import imaginate.registerIncomingImages

val sharedBitmaps = configurations.registerIncomingImages(
    "sharedBitmaps",
    objects.named(BITMAP)
)
tasks.register("sharedBitmaps", Sync::class) {
    from(sharedBitmaps)
    into(layout.buildDirectory.dir("shared-bitmaps"))
}

val sharedDrawables = configurations.registerIncomingImages(
    "sharedDrawables",
    objects.named(DRAWABLE)
)
tasks.register("sharedDrawables", Sync::class) {
    from(sharedDrawables)
    into(layout.buildDirectory.dir("shared-drawables"))
}
