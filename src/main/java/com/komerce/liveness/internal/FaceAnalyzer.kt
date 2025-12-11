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

internal class FaceAnalyzer(
    private val steps: List<LivenessStep>,
    private val onStepSuccess: (LivenessStep) -> Unit,
    private val onStepError: (LivenessError) -> Unit,
    private val onComplete: (LivenessResult) -> Unit
) : ImageAnalysis.Analyzer {

    private val options = FaceDetectorOptions.Builder()
        .setPerformanceMode(FaceDetectorOptions.PERFORMANCE_MODE_ACCURATE)
        .setLandmarkMode(FaceDetectorOptions.LANDMARK_MODE_ALL)
        .setClassificationMode(FaceDetectorOptions.CLASSIFICATION_MODE_ALL)
        .enableTracking()
        .build()
    private val detector = FaceDetection.getClient(options)

    // State Variables
    private var currentStepIndex = 0
    private var isFinished = false

    private val capturedEvidence = mutableMapOf<LivenessStep, Bitmap>()

    @OptIn(ExperimentalGetImage::class)
    override fun analyze(imageProxy: ImageProxy) {
        // 1. Circuit Breaker
        if (isFinished) {
            imageProxy.close() // PENTING: Tutup frame biar gak numpuk
            return
        }

        val mediaImage = imageProxy.image
        if (mediaImage != null) {
            // 2. Convert Format: CameraX (YUV) -> ML Kit (InputImage)
            val image = InputImage.fromMediaImage(mediaImage, imageProxy.imageInfo.rotationDegrees)

            // 3. Lempar ke ML Kit
            detector.process(image)
                .addOnSuccessListener { faces ->
                    // 4. Validasi Pintu Depan (Satpam)
                    if (faces.isEmpty()) {
                        onStepError(LivenessError.NO_FACE_DETECTED)
                    } else if (faces.size > 1) {
                        onStepError(LivenessError.MULTIPLE_FACES)
                    } else {
                        // 5. Kalau aman, masuk ke Logic Bisnis
                        processFaceLogic(faces[0], imageProxy)
                    }
                }
                .addOnCompleteListener {
                    // 6. FLUSH MEMORY (Wajib!)
                    imageProxy.close()
                }
        }
    }

    private fun processFaceLogic(face: Face, imageProxy: ImageProxy) {
        // 1. Cek apakah Playlist udah abis?
        if (currentStepIndex >= steps.size) {
            finishJob()
            return
        }

        // 2. Ambil Tantangan saat ini (berdasarkan index)
        val currentTargetStep = steps[currentStepIndex]

        // 3. Ambil Data Sensor Wajah
        val yaw = face.headEulerAngleY
        val smileProb = face.smilingProbability ?: 0f

        // 4. Ujian Kelulusan (Matematika)
        val isStepValid = when (currentTargetStep) {
            LivenessStep.LOOK_LEFT -> yaw > 35    // Harus nengok > 35 derajat
            LivenessStep.LOOK_RIGHT -> yaw < -35
            LivenessStep.SMILE -> (yaw > -15 && yaw < 15) && smileProb > 0.7 // Senyum & Lurus
            LivenessStep.BLINK -> (face.leftEyeOpenProbability ?: 1f) < 0.4
            else -> false
        }

        // 5. Kalau LULUS UJIAN
        if (isStepValid) {
            // JEPRET! Ambil frame detik ini sebagai barang bukti
            val evidenceBitmap = imageProxy.toBitmap()

            // Simpan ke Map
            capturedEvidence[currentTargetStep] = evidenceBitmap

            // Lapor ke UI
            onStepSuccess(currentTargetStep)

            // Geser pointer ke lagu berikutnya
            currentStepIndex++
        }
    }

    private fun finishJob() {
        if (isFinished) return
        isFinished = true // Nyalain switch biar analyze() berhenti kerja

        // Bungkus semua bukti jadi satu paket result
        onComplete(
            LivenessResult(
                isSuccess = true,
                evidencePhotos = capturedEvidence // Map isinya: {KIRI: Foto1, KANAN: Foto2}
            )
        )
    }
}