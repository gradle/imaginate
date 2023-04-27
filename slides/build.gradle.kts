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
    val pdf = register("exportPdf", JavaExec::class) {
        dependsOn(asciidoctorRevealJs)

        classpath = pdfConfiguration
        mainClass = "me.champeau.deck2pdf.Main"

        workingDir(asciidoctorRevealJs.map { it.outputDir })
        val outDirPath = "../../pdf"
        args = listOf("index.html", "$outDirPath/gradle-imaginate-slides.pdf", "--profile=revealjs")

        inputs.dir(workingDir)
        outputs.dir(workingDir.resolve(outDirPath))

        doFirst {
            val requiredJavaVersion = JavaVersion.VERSION_11
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
        from(files(layout.buildDirectory.file("pdf/gradle-imaginate-slides.pdf")) {
            builtBy(tasks.named("exportPdf"))
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
