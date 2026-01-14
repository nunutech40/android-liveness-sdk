# 📸 Android Liveness Detection SDK

<p align="center">
  <img src="https://link-to-your-gif/demo.gif" width="300" />
</p>

**Simple, Configurable, and Lightweight Liveness Detection for Android**  
Built on top of **Google ML Kit** and **CameraX**, designed for real-world verification use cases.

![Kotlin](https://img.shields.io/badge/language-Kotlin-purple)
![MinSDK](https://img.shields.io/badge/minSdk-24-orange)
![Platform](https://img.shields.io/badge/platform-Android-green)

---

## 📄 Documentation
For detailed requirements and technical specifications, please refer to:
- [Product Requirements Document (PRD)](docs/PRD.md)
- [Technical Requirements Document (TRD)](docs/TRD.md)
- [Publishing Guide (JitPack)](docs/PUBLISHING.md)

---

## ✨ Features

- **Configurable Verification Steps** - Define custom liveness flow (Look Left → Look Right → Smile → Blink).
- **Portrait Output** - Captured images are automatically rotated to portrait (Upright).
- **Low-end Device Optimization** - Automatic performance adjustment for budget devices.
- **Audit Mode Support** - Capture evidence for each step or only final selfie.
- **Privacy First (Offline Processing)** - All face analysis happens on-device. No cloud dependency.

---

## 🛠 Installation

### Via Maven (JitPack)

Add this to your root `build.gradle` or `settings.gradle`:

```kotlin
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        mavenCentral()
        maven { url 'https://jitpack.io' }
    }
}
```

Add the dependency to your app `build.gradle`:

```kotlin
dependencies {
    implementation 'com.github.nunutech40:android-liveness-sdk:v1.0.0'
}
```

---

## 🚀 Quick Start

### 1. Permission Setup

```xml
<uses-permission android:name="android.permission.CAMERA" />
<uses-feature
    android:name="android.hardware.camera"
    android:required="true" />
```

### 2. Layout Setup

```xml
<androidx.camera.view.PreviewView
    android:id="@+id/cameraPreview"
    android:layout_width="match_parent"
    android:layout_height="match_parent" />
```

### 3. Basic Usage

```kotlin
val config = LivenessConfig(
    steps = listOf(
        LivenessStep.LOOK_LEFT,
        LivenessStep.LOOK_RIGHT,
        LivenessStep.SMILE
    ),
    isAuditMode = false
)

val detector = LivenessFactory.create(context)

detector.bind(
    lifecycleOwner = this,
    previewView = cameraPreview
)

detector.startDetection(
    config = config,
    onStepSuccess = { step ->
        // Update instruction UI (e.g., "Now Smile!")
    },
    onStepError = { error ->
        // Handle detection error (e.g., Face not detected)
    },
    onComplete = { result ->
        if (result.isSuccess) {
            val selfie = result.totalBitmap // Guaranteed Portrait
            // Upload or process selfie
        }
    }
)
```

---

## ⚙️ Configuration

Check the [LivenessConfig](src/main/java/com/komerce/liveness/api/LivenessConfig.kt) for full options.

---

## 🤝 Contributing

Pull Requests are welcome. Please read [TRD.md](docs/TRD.md) for architectural overview.

---

## 📄 License

Copyright 2025 Komerce
Licensed under the Apache License, Version 2.0.

