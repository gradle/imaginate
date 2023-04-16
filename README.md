![CI](https://github.com/gradle/gradle-kotlinconf-2023-app/actions/workflows/ci.yml/badge.svg?branch=main)

# Kotlin, one language to build them all!

This repository contains the [applications](applications) and [slides](slides) used in the talk named _Kotlin, one language to build them all!_. 

The slides are available at https://gradle.github.io/imaginate

The video recording is available at https://youtu.be/88FJwx8Yf3o?t=18545

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

## Slides

Build "htdocs" in [slides/build/docs/asciidocRevealJs](slides/build/docs/asciidocRevealJs)
```shell
./gradlew :slides:asciidoctorRevealJs
```

Builds "htdocs" continuously, opens a browser
```shell
./gradlew :slides:asciidoctorRevealJs --continuous
```
