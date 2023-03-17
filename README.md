# KotlinConf

* 45 minutes slot
* demo a build for a multi-platform application
  * code shared between build and application
  * library that does an API request to generate an image from an prompt
    * code would be used in Java for build, Android for the App and Javascript for a web
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
        * splash screen?
        * icon?
        * cacheable task that does a remote call
          * input to the task would be the prompts for the assets
            * src/prompts/icon.txt
            * src/prompts/splash.txt
    * app
      * reuses the library
  * core library that accepts a prompt and returns a image
    * initially the library could be a stub
  * multi-platform app that consumes the library
    * compose?
    * short feedback loops for development?
      * compose for desktop
