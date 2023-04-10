![CI](https://github.com/gradle/gradle-kotlinconf-2023-app/actions/workflows/ci.yml/badge.svg?branch=main)

# Kotlin, one language to build them all!

This repository contains the [applications](applications) and [slides](slides) used in the talk named _Kotlin, one language to build them all!_. 

## Browser Application

Run application in a browser
```shell
./gradlew :web-app:jsBrowserRun 
```

Run application, rebuild on source code changes with live-reload
```shell
./gradlew :web-app:jsBrowserRun --continuous
```

Build "htdocs" in [applications/web-app/build/distributions](applications/web-app/build/distributions)
```shell
./gradlew :web-app:jsBrowserProductionWebpack
```

## Desktop Application

Run application
```shell
./gradlew :desktop-app:run
```

Run application, rebuild on source code changes, close the app to trigger rebuild
```shell
./gradlew :desktop-app:run --continuous
```

## Android Application

Run the application on a device or emulator

* Open the project in Android Studio >= Giraffe
* Select the `android-app` run target and a device or emulator
* Click the run button

Build the APK in [](applications/android-app/build/outputs/apk/debug/android-app-debug.apk)
```shell
./gradlew :android-app:assembleDebug
```

## Slides

Build "htdocs" in [slides/build/docs/asciidocRevealJs](slides/build/docs/asciidocRevealJs)
```shell
./gradlew :slides:asciidoctorRevealJs
```

Builds "htdocs" continuously, opens a browser
```shell
./gradlew :slides:asciidoctorRevealJs --continuous
```

## Notes

* 45 minutes slot
  * 35 minutes effectively
* demo a build for a multi-platform application
  * code shared between build and application
  * library that does an API request to generate an image from an prompt
    * code would be used in Java for build, Android for the App and Javascript for a web
    * initially the library could be a stub
* presentation
  * show the final result first then describe the build
  * navigate code in the IDE
  * we should have another highlight in the middle
    * add a new platform?
      * need to make sure it is a simple change
* Gradle features to highlight during the presentation
  * Kotlin DSL
    * Assignment operator
  * configuration cache
    * with / without difference
  * dependency resolution
    * version catalogs
    * dependency tree
* tasks
  * 3 builds
    * core library
    * build logic
      * reuses the core library to generate assets
        * api key https://github.com/etiennestuder/gradle-credentials-plugin
        * splash screen?
        * icon?
        * cacheable task that does a remote call
          * input to the task would be the prompts for the assets
            * src/prompts/icon.txt
            * src/prompts/splash.txt
    * app
      * reuses the library
      * asks for the api key and store it https://github.com/russhwolf/multiplatform-settings
    * :shared-logic subproject
      * with the app model
      * state machine like for no-key / key
  * move root build subprojects to a subfolder
  * multi-platform app that consumes the library
    * compose?
    * short feedback loops for development?
      * compose for desktop
