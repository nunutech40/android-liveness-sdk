# 📸 Android Liveness Detection SDK

**Simple, Configurable, and Lightweight Liveness Detection for Android.**  
Built on top of **Google ML Kit** and **CameraX**.

![Kotlin](https://img.shields.io/badge/language-Kotlin-purple)
![MinSDK](https://img.shields.io/badge/minSdk-24-orange)
![Platform](https://img.shields.io/badge/platform-Android-green)

---

## ✨ Features

- **Configurable Steps**  
  Define your own verification flow (e.g. Look Left → Look Right → Smile).

- **Audit Mode**  
  Save evidence for every step or only the final selfie.

- **State Machine Logic**  
  Prevents users from skipping verification steps.

- **Privacy First (Offline)**  
  All processing runs on-device. No data is sent outside by the SDK.

- **Clean API**  
  Simple usage via `LivenessFactory` and `LivenessConfig`.

---

## 🛠 Installation

### Option 1: Git Submodule (Recommended for Private Projects)

1. Add SDK as submodule:
```bash
git submodule add https://github.com/YOUR_USERNAME/android-liveness-sdk.git liveness-sdk
```

2. Register module in `settings.gradle`:
```groovy
include ':liveness-sdk'
```

3. Add dependency in app `build.gradle`:
```groovy
dependencies {
    implementation project(':liveness-sdk')
    implementation "androidx.camera:camera-view:1.3.0"
}
```

---

## 🚀 Quick Start

### 1. Permission Setup

Add permission and camera requirement to `AndroidManifest.xml`:

```xml
<uses-permission android:name="android.permission.CAMERA" />
<uses-feature
    android:name="android.hardware.camera"
    android:required="true" />
```

---

### 2. Layout Setup

Add `PreviewView` to your layout:

```xml
<androidx.camera.view.PreviewView
    android:id="@+id/cameraPreview"
    android:layout_width="match_parent"
    android:layout_height="match_parent" />
```

---

### 3. Basic Usage

```kotlin
// 1. Create Config
val livenessConfig = LivenessConfig(
    steps = listOf(
        LivenessStep.LOOK_LEFT,
        LivenessStep.LOOK_RIGHT,
        LivenessStep.SMILE
    ),
    isAuditMode = false
)

// 2. Create Detector
val detector = LivenessFactory.create(context)

// 3. Bind Camera to Lifecycle
detector.bind(
    lifecycleOwner = this,
    previewView = cameraPreview
)

// 4. Start Detection
detector.startDetection(
    config = livenessConfig,
    onStepSuccess = { step ->
        // Update UI for next instruction
    },
    onStepError = { error ->
        // Handle error (e.g. Face not detected)
    },
    onComplete = { result ->
        if (result.isSuccess) {
            val selfie = result.totalBitmap
            // Upload or process final photo
        }
    }
)
```

---

## ⚙️ Configuration

### `LivenessConfig`

| Parameter | Type | Description |
|--------|------|-------------|
| `steps` | `List<LivenessStep>` | Ordered verification steps |
| `isAuditMode` | `Boolean` | `false`: only final photo<br>`true`: save photo for every step |

---

### `LivenessStep`

| Step | Description |
|-----|------------|
| `LOOK_LEFT` | Head rotation > 35° left |
| `LOOK_RIGHT` | Head rotation > 35° right |
| `SMILE` | Smile probability > 0.7 |
| `BLINK` | Both eyes blink detected |

---

### `LivenessResult`

| Property | Type | Description |
|--------|------|-------------|
| `isSuccess` | `Boolean` | All steps passed |
| `totalBitmap` | `Bitmap?` | Final selfie (available on success) |
| `stepEvidence` | `Map<LivenessStep, Bitmap>` | Step photos (audit mode only) |
| `error` | `LivenessError?` | Failure reason |

---

## 📱 Requirements

- **Min SDK**: 24 (Android 7.0)
- **Java Version**: 11
- **Camera Permission** granted

---

## 🤝 Contributing

Contributions are welcome.

- Core logic: `internal/FaceAnalyzer.kt`
- Public APIs: `api` package

Submit changes via Pull Request.

---

## 📄 License

```text
Copyright 2025 Komerce

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0
```
