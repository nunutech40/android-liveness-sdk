# 📸 Android Liveness Detection SDK

<p align="center">
  <img src="https://link-to-your-gif/demo.gif" width="300" />
</p>

**Simple, Configurable, and Lightweight Liveness Detection for Android**  
Built on top of **Google ML Kit** and **CameraX**, designed for real-world identity verification.

[![Kotlin](https://img.shields.io/badge/language-Kotlin-purple)](https://kotlinlang.org/)
![MinSDK](https://img.shields.io/badge/minSdk-24-orange)
![Platform](https://img.shields.io/badge/platform-Android-green)
[![License](https://img.shields.io/badge/license-Apache%202.0-blue.svg)](LICENSE)

---

## ✨ Features

- **✅ Multi-Step Verification** - Customizable flow: Look Left, Look Right, Smile, Blink.
- **📱 Portrait Optimized** - High-quality portrait output images, automatically rotated and corrected.
- **⚡ Low-End Performance** - Intelligent hardware detection with auto-tuning for budget devices.
- **🔒 Privacy First** - 100% On-device processing. No cloud, no biometric data ever leaves the phone.
- **📸 Audit Mode** - Capture evidence bitmpas for every individual step or just the final selfie.

---

## 🛠 Installation

### 1. Link JitPack to your project
Add the JitPack repository to your `settings.gradle` or root `build.gradle`:

```kotlin
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        mavenCentral()
        maven { url 'https://jitpack.io' }
    }
}
```

### 2. Add Dependency
Add the following to your app module's `build.gradle`:

```kotlin
dependencies {
    implementation 'com.github.nunutech40:android-liveness-sdk:v1.0.3'
}
```

---

## 🚀 Quick Start

### 1. Permissions
Ensure you have the Camera permission in your `AndroidManifest.xml`:

```xml
<uses-permission android:name="android.permission.CAMERA" />
```

### 2. Layout
Add `PreviewView` to your XML layout where the camera should appear:

```xml
<androidx.camera.view.PreviewView
    android:id="@+id/cameraPreview"
    android:layout_width="match_parent"
    android:layout_height="match_parent" />
```

### 3. Implementation
Initialize and start the liveness detection:

```kotlin
// 1. Create Configuration
val config = LivenessConfig(
    steps = listOf(
        LivenessStep.LOOK_LEFT,
        LivenessStep.LOOK_RIGHT,
        LivenessStep.SMILE
    ),
    isAuditMode = false
)

// 2. Create Detector
val detector = LivenessFactory.create(context)

// 3. Bind to Lifecycle
detector.bind(lifecycleOwner = this, previewView = binding.cameraPreview)

// 4. Start Session
detector.startDetection(
    config = config,
    onStepSuccess = { step ->
        // Trigger UI feedback (e.g., "Good! Now blink.")
    },
    onStepError = { error ->
        // Handle common errors (e.g., No face detected)
    },
    onComplete = { result ->
        if (result.isSuccess) {
            val selfie: Bitmap? = result.totalBitmap
            // Use your high-res portrait selfie!
        }
    }
)
```

---

## ⚙️ API Reference

### `LivenessStep`
Defines the required user movement:
- `LOOK_LEFT`: User must turn head to the left.
- `LOOK_RIGHT`: User must turn head to the right.
- `SMILE`: User must smile.
- `BLINK`: User must blink eyes.

### `LivenessError`
Handles failure scenarios:
- `NO_FACE_DETECTED`: No face in frame.
- `MULTIPLE_FACES`: More than one face detected (Anti-spoofing).
- `FACE_TOO_FAR` / `TOO_CLOSE`: Distance guidance.

---

## � Support the Developer

Hi! I'm **Nunu Nugraha**, the independent developer behind this Liveness SDK. 

This project was born out of my passion for high-performance mobile AI. My goal is to keep this library free, high-quality, and open-source for the developer community. If this SDK has saved you weeks of work or helped your business, consider supporting my work. Your contribution directly helps me maintain this project and build even more innovative tools!

- **Traktir di Saweria (IDR):** [saweria.co/nunugraha17](https://saweria.co/nunugraha17)
- **Buy Me a Coffee (International):** [buymeacoffee.com/nunutech401](https://www.buymeacoffee.com/nunutech401)

---

## �📄 License

Copyright 2025 Nunu Nugraha.  
Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License. See the [LICENSE](LICENSE) file for details.
