import io.freefair.gradle.plugins.sass.SassCompile
import org.asciidoctor.gradle.jvm.slides.RevealJSOptions
import sass.embedded_protocol.EmbeddedSass

plugins {
    id("java-base")
    id("org.asciidoctor.jvm.revealjs") version "4.0.0-alpha.1"
    id("io.freefair.sass-base") version "8.0.1"
    id("org.ajoberstar.git-publish") version "3.0.1"
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
    withType<Copy>().configureEach {
        duplicatesStrategy = DuplicatesStrategy.WARN
    }
    val sassCompile by registering(SassCompile::class) {
        source(layout.projectDirectory.dir("src/style"))
        destinationDir = layout.buildDirectory.dir("style")
    }
    asciidoctorGemsPrepare {
        outputs.cacheIf { false }
    }
    asciidoctorRevealJs {
        dependsOn(sassCompile)
        baseDirFollowsSourceDir()
        inputs.dir(layout.projectDirectory.dir("src/docs/asciidoc"))
        sourceDirProperty = layout.projectDirectory.dir("src/docs/asciidoc")
        resources {
            from("${sourceDir}/images") {
                include("**")
                into("images")
            }
            from(sassCompile.map { it.destinationDir }) {
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
                "docinfo" to "shared",
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
    val exportPdf by registering(JavaExec::class) {
        dependsOn(asciidoctorRevealJs)

        classpath = pdfConfiguration
        mainClass = "me.champeau.deck2pdf.Main"

        workingDir(asciidoctorRevealJs.map { it.outputDir })
        val outDirPath = "../../pdf"
        args = listOf("index.html", "$outDirPath/slides.pdf", "--profile=revealjs")

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
    val zipHtml by registering(Zip::class) {
        archiveBaseName = "slides"
        from(asciidoctorRevealJs.map { it.outputDir })
        into("slides")
    }
    assemble {
        dependsOn(asciidoctorRevealJs, zipHtml, exportPdf)
    }
}

gitPublish {
    repoUri = "git@github.com:gradle/gradle-kotlinconf-2023-app.git"
    branch = "gh-pages"
    contents {
        from(tasks.asciidoctorRevealJs)
    }
    commitMessage = "Publish slides"
}
