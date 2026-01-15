# Changelog

All notable changes to the Android Liveness SDK will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [1.0.5] - 2025-01-15

### Added
- **Android 15+ 16KB Page Alignment Support** - Full compatibility with Google Play's 16KB page size requirement (mandatory from November 2025)
- Comprehensive documentation for 16KB page alignment in `docs/16KB_PAGE_ALIGNMENT.md`
- Android 15+ compatibility section in README

### Changed
- **Target SDK updated to 35** (Android 15) - from API 34
- **Compile SDK updated to 35** (Android 15) - from API 34
- **Updated CameraX to 1.4.1** (from 1.3.0) - Includes properly aligned native libraries for 16KB page size support
  - `androidx.camera:camera-core:1.4.1`
  - `androidx.camera:camera-camera2:1.4.1`
  - `androidx.camera:camera-lifecycle:1.4.1`
  - `androidx.camera:camera-view:1.4.1`
- **Updated AndroidX dependencies** for Android 15 compatibility:
  - `androidx.core:core-ktx:1.12.0` (from 1.10.1)
  - `androidx.appcompat:appcompat:1.7.0` (from 1.6.1)
  - `com.google.android.material:material:1.12.0` (from 1.9.0)
- **Updated ML Kit Face Detection to 16.1.7** (from 16.1.5) - Latest version with improvements
- **Updated Kotlin Coroutines to 1.7.3** (from 1.6.4) - Better performance and stability
- **Updated test dependencies**:
  - `androidx.test.ext:junit:1.2.1` (from 1.1.5)
- Added explicit packaging configuration for native libraries (`useLegacyPackaging = false`)

### Fixed
- Native library alignment issues (`libface_detector_v2_jni.so` and `libimage_processing_util_jni.so`) that caused compatibility problems with 16KB page size devices

### Technical Details
- CameraX 1.4.1 provides 16KB-aligned version of `libimage_processing_util_jni.so` which overrides the misaligned library from ML Kit
- APKs built with this version will pass Google Play Console's 16KB validation checks
- Full Android 15 (API 35) support with latest stable dependencies
- No breaking changes - fully backward compatible with existing implementations

---

## [1.0.4] - 2025-01-14

### Added
- JitPack publishing support
- Maven publication configuration
- Sources JAR generation for better IDE support

### Changed
- Improved documentation in README.md
- Added installation guide for JitPack integration

---

## [1.0.3] - 2025-01-13

### Added
- Low-end device optimization with automatic performance mode detection
- Device tier detection using `ActivityManager.isLowRamDevice()`

### Changed
- ML Kit Face Detection now uses adaptive performance mode based on device capabilities
- Optimized frame processing for budget devices

---

## [1.0.2] - 2025-01-12

### Added
- Portrait image output with automatic rotation correction
- Bitmap orientation handling based on device sensor orientation

### Fixed
- Output images appearing in landscape/native sensor orientation
- Front camera mirror effect on captured selfies

---

## [1.0.1] - 2025-01-11

### Added
- ProGuard/R8 consumer rules for automatic obfuscation handling
- Comprehensive API documentation

### Changed
- Improved error handling and feedback messages
- Enhanced state machine enforcement

---

## [1.0.0] - 2025-01-10

### Added
- Initial release of Android Liveness SDK
- Multi-step verification support (Look Left, Look Right, Smile, Blink)
- Audit mode for capturing proof-of-work bitmaps
- Privacy-first on-device processing
- CameraX integration for camera handling
- Google ML Kit Face Detection integration
- State machine enforcement for detection steps
- Comprehensive error handling with `LivenessError` enum
- Lifecycle-aware components

### Technical Stack
- Kotlin
- Minimum SDK: API 24 (Android 7.0)
- Target SDK: API 34
- Java 11 compatibility
- Gradle 8.5
- CameraX 1.3.0
- ML Kit Face Detection 16.1.5

---

## Version Numbering

This project follows [Semantic Versioning](https://semver.org/):
- **MAJOR** version for incompatible API changes
- **MINOR** version for new functionality in a backward compatible manner
- **PATCH** version for backward compatible bug fixes

---

## Upgrade Guide

### From 1.0.4 to 1.0.5

**No code changes required!** This is a dependency update for Android 15+ compatibility.

Simply update your dependency:
```gradle
dependencies {
    implementation 'com.github.nunutech40:android-liveness-sdk:1.0.5'
}
```

Then clean and rebuild:
```bash
./gradlew clean
./gradlew build
```

**What you get:**
- ✅ Automatic 16KB page alignment support
- ✅ Google Play validation compliance
- ✅ Future-proof for Android 15+ devices
- ✅ No breaking changes

---

## Support

For issues, questions, or feature requests, please:
- Open an issue on [GitHub](https://github.com/nunutech40/android-liveness-sdk/issues)
- Check the [documentation](https://github.com/nunutech40/android-liveness-sdk/tree/main/docs)
- Contact: [Your contact information]

---

## License

Copyright 2025 Nunu Nugraha.  
Licensed under the Apache License, Version 2.0.
