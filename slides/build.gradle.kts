import io.freefair.gradle.plugins.sass.SassCompile
import org.asciidoctor.gradle.jvm.slides.RevealJSOptions
import sass.embedded_protocol.EmbeddedSass

plugins {
    id("java-base")
    id("org.asciidoctor.jvm.revealjs") version "4.0.0-alpha.1"
    id("io.freefair.sass-base") version "8.0.1"
    id("org.ajoberstar.git-publish") version "4.1.1"
}

repositories {
    mavenCentral()
    maven(url = "https://jcenter.bintray.com/") {
        content {
            includeModule("me.champeau.deck2pdf", "deck2pdf")
        }
    }
    ruby {
        gems()
    }
}

asciidoctorj {
    setVersion("2.5.3")
    fatalWarnings(missingIncludes())
    modules {
        diagram.use()
        diagram.version("2.2.1")
    }
}

revealjs {
    version = "3.1.0"
    templateGitHub {
        setOrganisation("hakimel")
        setRepository("reveal.js")
        setTag("3.9.1")
    }
}

sass {
    omitSourceMapUrl = true
    sourceMapContents = false
    sourceMapEmbed = false
    sourceMapEnabled = false
    outputStyle = EmbeddedSass.OutputStyle.EXPANDED
}

val pdfConfiguration: Configuration by configurations.creating
dependencies {
    pdfConfiguration("me.champeau.deck2pdf:deck2pdf:0.3.0")
}

@CacheableTask
abstract class Deck2Pdf : DefaultTask() {

    @get:Classpath
    abstract val deck2pdfClasspath: ConfigurableFileCollection

    @get:InputFile
    @get:PathSensitive(PathSensitivity.RELATIVE)
    abstract val inputFile: RegularFileProperty

    @get:OutputFile
    abstract val destinationFile: RegularFileProperty

    @get:InputDirectory
    @get:PathSensitive(PathSensitivity.RELATIVE)
    protected
    abstract val workingDirectory: DirectoryProperty

    @get:Inject
    protected
    abstract val execution: ExecOperations

    init {
        workingDirectory.fileProvider(inputFile.map { it.asFile.parentFile })
    }

    @TaskAction
    fun action() {
        execution.javaexec {
            classpath = deck2pdfClasspath
            mainClass = "me.champeau.deck2pdf.Main"
            jvmArgs = listOf(
                "--add-opens", "java.base/java.util=ALL-UNNAMED",
                "--add-opens", "java.base/java.lang=ALL-UNNAMED",
            )
            workingDir(inputFile.get().asFile.parentFile)
            args = listOf(
                inputFile.get().asFile.relativeTo(workingDir).path,
                destinationFile.get().asFile.absolutePath,
                "--profile=revealjs"
            )
        }
    }
}

tasks {
    withType(Copy::class).configureEach {
        duplicatesStrategy = DuplicatesStrategy.WARN
    }
    val sharedResources = register("copySharedResources", Sync::class) {
        from(layout.projectDirectory.dir("../applications/shared-resources/src/images"))
        into(layout.buildDirectory.dir("shared-resources"))
    }
    val sass = register("sassCompile", SassCompile::class) {
        source(layout.projectDirectory.dir("src/style"))
        destinationDir = layout.buildDirectory.dir("style")
    }
    asciidoctorGemsPrepare {
        outputs.cacheIf { false }
    }
    asciidoctorRevealJs {
        dependsOn(sass)
        dependsOn(sharedResources)
        baseDirFollowsSourceDir()
        inputs.dir(layout.projectDirectory.dir("src/docs/asciidoc"))
        sourceDirProperty = layout.projectDirectory.dir("src/docs/asciidoc")
        resources {
            from("${sourceDir}/images") {
                into("images")
            }
            from(sharedResources.map { it.destinationDir }) {
                into("images/shared")
            }
            from(sass.map { it.destinationDir }) {
                into("style")
            }
        }
        theme = "simple"
        revealjsOptions {
            setControls(true)
            setSlideNumber("c/t")
            setProgressBar(true)
            setPushToHistory(true)
            setOverviewMode(true)
            setKeyboardShortcuts(true)
            setTouchMode(true)
            setTransition(RevealJSOptions.Transition.SLIDE)
            setTransitionSpeed(RevealJSOptions.TransitionSpeed.DEFAULT)
            setBackgroundTransition(RevealJSOptions.Transition.FADE)
        }
        attributes(
            mapOf(
                "imagesdir" to "./images",
                "icons" to "font",
                "idprefix" to "slide-",
                "docinfo" to "private",
                // Configurations not available via the `revealjsOptions` block - See https://revealjs.com/config/
                "revealjs_disableLayout" to "true", // Disables the default reveal.js slide layout (scaling and centering) so that you can use custom CSS layout
                "revealjs_controlsLayout" to "edges", // Determines where controls appear, "edges" or "bottom-right"
                "revealjs_autoPlayMedia" to false   // Auto-playing embedded media (video/audio/iframe) - true/false or null: Media will only autoplay if data-autoplay is present
            )
        )
        resources {
            from(layout.projectDirectory.dir("src/docs/resources"))
        }
    }
    val pdfTasks = listOf(
        "kotlinConfPdf" to "2023-kotlinconf",
        "sunnyTechPdf" to "2023-sunnytech",
    ).map { (taskName, htmlFilename) ->
        register(taskName, Deck2Pdf::class) {
            dependsOn(asciidoctorRevealJs)

            deck2pdfClasspath.from(pdfConfiguration)
            inputFile.fileProvider(asciidoctorRevealJs.map { it.outputDir.resolve("$htmlFilename.html") })
            destinationFile.set(layout.buildDirectory.file("pdf/$htmlFilename-gradle-imaginate-slides.pdf"))

            doFirst {
                val requiredJavaVersion = JavaVersion.toVersion(libs.versions.jvm.get().toInt())
                val wrongJavaVersionMessage =
                    "This build must be run with a JavaFX enabled JDK version $requiredJavaVersion."
                try {
                    Class.forName("javafx.application.Application")
                    if (JavaVersion.current() != requiredJavaVersion) throw Exception(
                        wrongJavaVersionMessage
                    )
                } catch (ignore: Exception) {
                    throw Exception(wrongJavaVersionMessage)
                }
            }
        }
    }
    val pdf = register("exportPdfs") {
        dependsOn(pdfTasks)
    }
    val html = register("zipHtml", Zip::class) {
        archiveBaseName = "gradle-imaginate-slides"
        from(asciidoctorRevealJs.map { it.outputDir })
        into("slides")
    }
    assemble {
        dependsOn(asciidoctorRevealJs, html, pdf)
    }
}

gitPublish {
    repoUri = "git@github.com:gradle/imaginate.git"
    branch = "gh-pages"
    contents {
        from(tasks.asciidoctorRevealJs)
        from(files(layout.buildDirectory.dir("pdf")) {
            builtBy(tasks.named("exportPdfs"))
        })
    }
    commitMessage = when (val sha = System.getenv("GITHUB_SHA")) {
        null -> "Publish slides"
        else -> "Publish slides\n\nFrom $sha"
    }
    sign = false
}

tasks.named("gitPublishCopy", Copy::class) {
    exclude(".asciidoctor")
}

// Configuration cache incompatibilities
mapOf(
    "asciidoctor" to listOf(
        org.asciidoctor.gradle.jvm.slides.AsciidoctorJRevealJSTask::class,
        org.asciidoctor.gradle.jvm.gems.AsciidoctorGemPrepare::class,
    ),
    "sass" to listOf(
        io.freefair.gradle.plugins.sass.SassCompile::class,
    )
).forEach { (plugin, taskTypes) ->
    taskTypes.forEach { taskType ->
        tasks.withType(taskType).configureEach {
            notCompatibleWithConfigurationCache("$plugin plugin")
        }
    }
}
mapOf(
    "git-publish" to listOf("gitPublishCopy"),
).forEach { (plugin, taskNames) ->
    taskNames.forEach { taskName ->
        tasks.named(taskName) {
            notCompatibleWithConfigurationCache("$plugin plugin")
        }
    }
}
