package com.komerce.liveness.api

import  android.graphics.Bitmap

// Tahapan Liveness
enum class LivenessStep {
    IDLE,       // Belum mulai
    LOOK_LEFT,  // "Silakan Tengok Kiri"
    LOOK_RIGHT, // "Silakan Tengok Kanan"
    SMILE,      // "Silakan Senyum"
    BLINK,
    DONE        // Selesai
}

// Jenis Error yang mungkin terjadi
enum class LivenessError {
    NO_FACE_DETECTED,   // Muka gak ada
    MULTIPLE_FACES,     // Ada 2 orang (Bahaya joki)
    FACE_TOO_FAR,       // Muka kekecilan
    FACE_TOO_CLOSE,     // Muka kegedean
    NOT_CENTERED        // Muka gak di tengah
}

data class LivenessResult(
    val isSuccess: Boolean,
    val totalBitmap: Bitmap? = null,
    val stepEvidence: Map<LivenessStep, Bitmap> = emptyMap(),
    val error: LivenessError? = null
)