# Technical Requirements Document (TRD) - Android Liveness SDK

## 1. Technical Stack
- **Languages:** Kotlin
- **Minimum SDK:** 24 (Android 7.0)
- **Core Libraries:**
    - CameraX (1.3.0)
    - Google ML Kit Face Detection (16.1.6)
    - Kotlin Coroutines

## 2. Architecture Enhancements

### 2.1. Orientation Correction Logic
Normally, `imageProxy.toBitmap()` in CameraX 1.3.0+ returns a Bitmap in the sensor's native orientation. To fix the "landscape" issue:
1.  Read `imageProxy.imageInfo.rotationDegrees`.
2.  Use a `Matrix` to rotate the resulting `Bitmap`.
3.  Implement a helper function `rotateBitmap(bitmap: Bitmap, degrees: Int): Bitmap`.

### 2.2. Low-end Device Optimization
We will implement a `DeviceTier` detection system:
- **Detection Method:** Use `ActivityManager.isLowRamDevice()` and check available processor cores/RAM.
- **ML Kit Optimization:**
    - **High-end:** `FaceDetectorOptions.PERFORMANCE_MODE_ACCURATE`
    - **Low-end:** `FaceDetectorOptions.PERFORMANCE_MODE_FAST`
- **Resolution Optimization:** 
    - Adjust `ImageAnalysis.Builder().setTargetResolution(Size(480, 640))` specifically for low-end devices to reduce pixel processing overhead.

### 2.3. Maven Publishing Configuration
Transform the project into a distributable library:
1.  **Plugins:** Add `id 'maven-publish'` to `build.gradle`.
2.  **Publication Block:** 
    ```groovy
    publishing {
        publications {
            release(MavenPublication) {
                groupId = 'com.github.nunutech40'
                artifactId = 'liveness-sdk'
                version = '1.0.0'
                afterEvaluate {
                    from components.release
                }
            }
        }
    }
    ```
3.  **JitPack Support:** Ensure `jitpack.yml` is present if specific Java versions are needed for the build.

## 3. Class Design Changes
- **`DeviceUtils`:** New utility class for hardware capability checks.
- **`FaceAnalyzer`:** Updated to accept a `performanceMode` parameter determined at runtime during initialization.
- **`BitmapExtensions`:** Utility for rotating and flipping images (since front camera images are often mirrored).

## 4. Performance Targets
- **Inference Time:** < 100ms per frame on low-end devices in FAST mode.
- **Memory Footprint:** < 50MB peak heap usage during analysis.
