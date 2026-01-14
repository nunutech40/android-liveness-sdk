# ProGuard rules for android-liveness-sdk

# Keep the public API
-keep class com.komerce.liveness.LivenessFactory { *; }
-keep interface com.komerce.liveness.api.** { *; }
-keep class com.komerce.liveness.api.** { *; }

# ML Kit Face Detection
-keep class com.google.mlkit.** { *; }
-keep interface com.google.mlkit.** { *; }

# CameraX
-keep class androidx.camera.** { *; }
-keep interface androidx.camera.** { *; }