# Product Requirements Document (PRD) - Android Liveness SDK

## 1. Overview
The **Android Liveness SDK** is a lightweight, offline-first library designed to provide facial liveness detection for identity verification. It leverages Google’s ML Kit and Android’s CameraX to ensure high performance and privacy by processing everything on-device.

## 2. Target Audience
- Developers building Fintech, E-commerce, or Security applications that require "Selfie with Liveness" verification.
- Applications targeting a wide range of Android devices, including low-end models.

## 3. Core Features (Current)
- **Multi-Step Verification:** Supports sequential challenges (Look Left, Look Right, Smile, Blink).
- **Audit Mode:** Capability to capture proof-of-work Bitmaps for each verification step.
- **Privacy-First:** Fully offline on-device processing.
- **State Machine Enforcement:** Prevents skipping or reordering of detection steps.

## 4. New Requirements & Enhancements
### 4.1. Portrait Image Output
- **Problem:** Currently, the output Bitmaps (selfie results) are often returned in landscape/native sensor orientation, making them appear "lying down" when displayed.
- **Solution:** The SDK must automatically rotate the captured Bitmaps based on the device's orientation so that the final result is always in **Portrait (Upright)** mode.

### 4.2. Adaptive Performance (Low-end Device Support)
- **Problem:** Real-time face analysis (Accurate mode) can be heavy for low-end Android devices, causing lag or crashes.
- **Solution:** 
    - Auto-detect device performance tier (Low RAM detection).
    - Provide a "Fast Mode" configuration for ML Kit on these devices.
    - Optimize frame processing frequency if necessary.

### 4.3. Standard Library Distribution (Maven)
- **Problem:** Currently distributed via manual cloning or git submodules, which is tedious to maintain.
- **Solution:** Package the SDK as a standard Maven dependency (via JitPack or Maven Central) for easy installation in Kotlin/Java projects.

## 5. User Stories
- **As a User**, I want my selfie to look straight and upright after I finish the liveness check.
- **As a Developer**, I want to install this SDK easily using a single line in my `build.gradle`.
- **As a User with a cheap phone**, I want the liveness check to remain smooth and responsive without lagging.

## 6. Success Metrics
- 100% of output images are in portrait orientation.
- SDK can be installed via `implementation 'com.github.user:repo:version'`.
- Zero crashes/significant lag reported on devices with < 2GB RAM.
