package com.komerce.liveness.internal

import android.graphics.Bitmap
import androidx.annotation.OptIn
import androidx.camera.core.ExperimentalGetImage
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.face.Face
import com.google.mlkit.vision.face.FaceDetection
import com.google.mlkit.vision.face.FaceDetectorOptions
import com.komerce.liveness.api.LivenessError
import com.komerce.liveness.api.LivenessResult
import com.komerce.liveness.api.LivenessStep

/**
 * FaceAnalyzer
 *
 * Core Engine untuk fitur Liveness Detection.
 * Class ini berfungsi sebagai "Otak" yang memproses setiap frame dari kamera secara real-time.
 *
 * Tujuannya:
 * 1. Menerima stream gambar dari CameraX (ImageProxy).
 * 2. Mengirim gambar ke Google ML Kit untuk mendeteksi wajah & pose (Euler Angles).
 * 3. Memvalidasi gerakan wajah berdasarkan urutan tantangan (steps) yang diminta.
 *
 * Cara Kerjanya:
 * - Menggunakan pola State Machine (Berurutan). User tidak bisa loncat step.
 * - Jika step 1 (misal: Nengok Kiri) selesai, sistem otomatis lanjut ke step 2.
 * - Setelah semua step selesai, sistem melakukan "Final Check" (wajah lurus) sebelum mengambil foto.
 *
 * @param steps Daftar tantangan yang harus diselesaikan user (Contoh: [LOOK_LEFT, SMILE]).
 * @param onStepSuccess Callback ketika satu tantangan berhasil dilewati.
 * @param onStepError Callback ketika terjadi error (Wajah hilang, ada 2 wajah, dll).
 * @param onComplete Callback final ketika semua tantangan selesai & foto berhasil diambil.
 */

/**
 * FaceAnalyzer
 * Otak dari Liveness Detection.
 * Mendukung mode Standard (1 Foto Akhir) dan Audit (Foto tiap step).
 */

internal class FaceAnalyzer(
    private val steps: List<LivenessStep>,
    private val isAuditMode: Boolean,
    private val isLowEnd: Boolean, // <--- Add this
    private val onStepSuccess: (LivenessStep) -> Unit,
    private val onStepError: (LivenessError) -> Unit,
    private val onComplete: (LivenessResult) -> Unit
) : ImageAnalysis.Analyzer {

    // Setup ML Kit
    private val options = FaceDetectorOptions.Builder()
        .setPerformanceMode(
            if (isLowEnd) FaceDetectorOptions.PERFORMANCE_MODE_FAST 
            else FaceDetectorOptions.PERFORMANCE_MODE_ACCURATE
        )
        .setLandmarkMode(FaceDetectorOptions.LANDMARK_MODE_ALL)
        .setClassificationMode(FaceDetectorOptions.CLASSIFICATION_MODE_ALL)
        .enableTracking()
        .build()
    private val detector = FaceDetection.getClient(options)

    // State
    private var currentStepIndex = 0
    private var isFinished = false

    // Penampung bukti (Cuma dipake kalau isAuditMode = true)
    private val evidenceMap = mutableMapOf<LivenessStep, Bitmap>()

    @OptIn(ExperimentalGetImage::class)
    override fun analyze(imageProxy: ImageProxy) {
        if (isFinished) {
            imageProxy.close()
            return
        }

        val mediaImage = imageProxy.image
        if (mediaImage != null) {
            val image = InputImage.fromMediaImage(mediaImage, imageProxy.imageInfo.rotationDegrees)
            detector.process(image)
                .addOnSuccessListener { faces ->
                    if (faces.isEmpty()) {
                        onStepError(LivenessError.NO_FACE_DETECTED)
                    } else if (faces.size > 1) {
                        onStepError(LivenessError.MULTIPLE_FACES)
                    } else {
                        processFaceLogic(faces[0], imageProxy)
                    }
                }
                .addOnFailureListener { e -> e.printStackTrace() }
                .addOnCompleteListener { imageProxy.close() }
        } else {
            imageProxy.close()
        }
    }

    private fun processFaceLogic(face: Face, imageProxy: ImageProxy) {
        // --- PHASE 1: FINAL GATE (Ambil Foto Terbaik) ---
        // Kalau semua step sudah lewat, kita cek apakah muka lurus buat foto terakhir
        if (currentStepIndex >= steps.size) {
            if (isFaceStraight(face)) {
                finishJob(imageProxy) // <--- Panggil finishJob sambil bawa gambar
            }
            return
        }

        // --- PHASE 2: CHALLENGE LOGIC ---
        val currentTargetStep = steps[currentStepIndex]
        val yaw = face.headEulerAngleY
        val smileProb = face.smilingProbability ?: 0f

        val isStepValid = when (currentTargetStep) {
            LivenessStep.LOOK_LEFT -> yaw > 35
            LivenessStep.LOOK_RIGHT -> yaw < -35
            LivenessStep.SMILE -> (yaw > -15 && yaw < 15) && smileProb > 0.7
            LivenessStep.BLINK -> (face.leftEyeOpenProbability ?: 1f) < 0.4
            else -> false
        }

        if (isStepValid) {
            // --- LOGIC CONFIGURABLE ---
            if (isAuditMode) {
                // Hanya convert ke bitmap jika mode Audit nyala (Hemat Memori)
                val bitmap = imageProxy.toRotatedBitmap()
                evidenceMap[currentTargetStep] = bitmap
            }

            onStepSuccess(currentTargetStep)
            currentStepIndex++
        }
    }

    // Helper: Pastikan muka lurus (toleransi 10 derajat)
    private fun isFaceStraight(face: Face): Boolean {
        return face.headEulerAngleY < 10 && face.headEulerAngleY > -10
    }

    private fun finishJob(imageProxy: ImageProxy) {
        if (isFinished) return
        isFinished = true

        // Ambil Foto Final (Wajib Ada)
        val finalBitmap = imageProxy.toRotatedBitmap()

        // Return Result
        onComplete(LivenessResult(
            isSuccess = true,
            totalBitmap = finalBitmap, // Foto Selfie Lurus & Tegak
            stepEvidence = if (isAuditMode) evidenceMap else emptyMap() // Bukti Step (Opsional)
        ))
    }
}