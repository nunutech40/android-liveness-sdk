# 📸 Android Liveness Detection SDK

<p align="center">
  <img src="https://raw.githubusercontent.com/nunutech40/android-liveness-sdk/main/docs/demo.gif" width="300" alt="Liveness SDK Demo" />
</p>

**Simple, Configurable, and Lightweight Liveness Detection for Android**  
Built on top of **Google ML Kit** and **CameraX**, designed for real-world identity verification.

[![Kotlin](https://img.shields.io/badge/language-Kotlin-purple)](https://kotlinlang.org/)
![MinSDK](https://img.shields.io/badge/minSdk-24-orange)
![Java](https://img.shields.io/badge/Java-11%2B-blue)
![Platform](https://img.shields.io/badge/platform-Android-green)
[![License](https://img.shields.io/badge/license-Apache%202.0-blue.svg)](LICENSE)

---

## ✨ Features

- **✅ Multi-Step Verification** - Customizable flow: Look Left, Look Right, Smile, Blink.
- **📱 Portrait Optimized** - High-quality portrait output images, automatically rotated and corrected.
- **⚡ Low-End Performance** - Intelligent hardware detection with auto-tuning for budget devices.
- **🔒 Privacy First** - 100% On-device processing. No cloud, no biometric data ever leaves the phone.
- **📸 Audit Mode** - Capture evidence bitmaps for every individual step or just the final selfie.

---

## 📋 System Requirements

- **Minimum SDK**: API Level 24 (Android 7.0)
- **Compile SDK**: API Level 34+
- **Java Version**: Java 11 or 17
- **Kotlin Version**: 1.7.0+

---

## 🛠️ Installation

### 1. Repository Setup
Add the JitPack repository to your `settings.gradle` (using the modern `dependencyResolutionManagement` style):

```kotlin
dependencyResolutionManagement {
    repositories {
        google()
        mavenCentral()
        maven { url = uri("https://jitpack.io") }
    }
}
```

### 2. Add Dependency
Add the following to your app module's `build.gradle`:

```kotlin
dependencies {
    implementation("com.github.nunutech40:android-liveness-sdk:1.0.4")
}
```

---

## 🚀 Quick Start

### 1. Permissions
Add Camera permission in your `AndroidManifest.xml`:

```xml
<uses-permission android:name="android.permission.CAMERA" />
```

### 2. Layout
Add `PreviewView` to your XML layout:

```xml
<androidx.camera.view.PreviewView
    android:id="@+id/cameraPreview"
    android:layout_width="match_parent"
    android:layout_height="match_parent" />
```

### 3. Implementation
The following is a basic implementation in an Activity or Fragment:

```kotlin
import com.komerce.liveness.LivenessFactory
import com.komerce.liveness.api.*

// 1. Create Configuration
val config = LivenessConfig(
    steps = listOf(
        LivenessStep.LOOK_LEFT,
        LivenessStep.LOOK_RIGHT,
        LivenessStep.BLINK,
        LivenessStep.SMILE
    ),
    isAuditMode = true // Optional: capture bitmap for every step
)

// 2. Create Detector
val detector = LivenessFactory.create(this)

// 3. Bind to Lifecycle & PreviewView
detector.bind(lifecycleOwner = this, previewView = binding.cameraPreview)

// 4. Start Session
detector.startDetection(
    config = config,
    onStepSuccess = { step ->
        // Trigger UI feedback (e.g., Update instruction text)
        statusTextView.text = "Success: ${step.name}. Next step..."
    },
    onStepError = { error ->
        // Handle common issues (e.g., No face detected)
        errorTextView.text = when(error) {
            LivenessError.NO_FACE_DETECTED -> "Please show your face"
            LivenessError.MULTIPLE_FACES -> "Multiple faces detected!"
            else -> "Align your face to the center"
        }
    },
    onComplete = { result ->
        if (result.isSuccess) {
            val mainSelfie = result.totalBitmap // The final high-res selfie
            val stepPhotos = result.stepEvidence // Map of Step -> Bitmap (if isAuditMode=true)
            
            // Proceed with your business logic (e.g. Upload to server)
        }
    }
)
```

---

## ⚙️ API Reference

### `LivenessStep`
Defines the required user actions:
- `LOOK_LEFT` / `LOOK_RIGHT`: Head rotation detection.
- `SMILE`: Smile probability detection.
- `BLINK`: Eye blink detection.

### `LivenessError`
Real-time feedback during detection:
- `NO_FACE_DETECTED`: No face in frame.
- `MULTIPLE_FACES`: Anti-spoofing - prevents multiple people in frame.
- `FACE_TOO_FAR` / `TOO_CLOSE`: Distance guidance.
- `NOT_CENTERED`: Ensures face is within the optimal area.

---

## 🛡️ ProGuard / R8
The SDK provides its own ProGuard rules. You don't need to add any special configuration to your `proguard-rules.pro`. Our library includes `consumer-rules.pro` which automatically handles the necessary obfuscation exclusions for ML Kit and Internal APIs.

---

## ☕ Support the Developer

Hi! I'm **Nunu Nugraha**, an independent developer passionate about mobile AI. If this SDK helps your project, consider supporting its maintenance!

- **Saweria (IDR):** [saweria.co/nunugraha17](https://saweria.co/nunugraha17)
- **Buy Me a Coffee:** [buymeacoffee.com/nunutech401](https://www.buymeacoffee.com/nunutech401)

---

## 📄 License

Copyright 2025 Nunu Nugraha.  
Licensed under the Apache License, Version 2.0. See [LICENSE](LICENSE) for details.
