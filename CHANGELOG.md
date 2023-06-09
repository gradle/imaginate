# Changelog

All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [1.1.0] - 2023-06-30 - SunnyTech 2023

### Added

- An `:ios-app` that uses `:shared-ui` and `:shared-logic`.
- Applications use Material 3 theme across platforms, except for the DOM based `:web-app`.
- GitHub [Workflows](.github/workflows) and [Actions](.github/actions) that package applications for
  all supported platforms.

### Fixed

- `GenerateImages` task outputs are now cacheable. Only `:shared-resources` disables tracking the
  task state because its configured outputs are committed to git.

### Changed

- The build now requires Java 17.
- On macos the build now requires cocoapods.

<!-- ### Removed -->

## [1.0.0] - 2023-04-14 - KotlinConf 2023

First public release.

[unreleased]: https://github.com/gradle/imaginate/compare/v1.1.0...HEAD
[1.1.0]: https://github.com/gradle/imaginate/compare/v1.0.0...v1.1.0
[1.0.0]: https://github.com/gradle/imaginate/releases/tag/v1.0.0
