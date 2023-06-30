![CI](https://github.com/gradle/imaginate/actions/workflows/ci.yml/badge.svg?branch=main)

# Kotlin, one language to build them all!

This repository contains the [applications](applications) and [slides](slides) used in the talk named _Kotlin, one language to build them all!_. 

* The slides are available at https://gradle.github.io/imaginate
* The video recording is available at https://youtu.be/88FJwx8Yf3o?t=18545

All notable changes to this project will be documented in the [Changelog](CHANGELOG.md) .

### Set up the environment

You need the following on all platforms:

* `JAVA_HOME` environment variable pointing to Java >= 17
* [Android Studio](https://developer.android.com/studio) or [IntelliJ IDEA](https://www.jetbrains.com/idea/)

> **Warning**
> You need macos to write and run ios-specific code on simulated or real devices.
> This is an Apple requirement.
> On non-macos systems building ios-specific code is automatically disabled.

On macos you need:

* [CocoaPods](https://kotlinlang.org/docs/native-cocoapods.html)
* [Xcode](https://apps.apple.com/us/app/xcode/id497799835)
* [Android Studio KMM plugin](https://plugins.jetbrains.com/plugin/14936-kotlin-multiplatform-mobile)

In case of trouble you can use [KDoctor](https://github.com/Kotlin/kdoctor).

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

Build the APK in [applications/android-app/build/outputs/apk/debug](applications/android-app/build/outputs/apk/debug)
```shell
./gradlew :android-app:assembleDebug
```

## ios Application

Run the application on a device or simulator

* Run `./gradlew :ios-app:podInstall`
* Open `applications/ios-app/iosApp/iosApp.xcworkspace` in xcode
* Select a simulator or device
* Click the run button

## Slides

Build "htdocs" in [slides/build/docs/asciidocRevealJs](slides/build/docs/asciidocRevealJs)
```shell
./gradlew :slides:asciidoctorRevealJs
```

Builds "htdocs" continuously, opens a browser
```shell
./gradlew :slides:asciidoctorRevealJs --continuous
```

Builds PDF
```shell
./gradlew :slides:exportPdf
```

Note that building the slides PDF requires a JVM with JavaFX bundled.
