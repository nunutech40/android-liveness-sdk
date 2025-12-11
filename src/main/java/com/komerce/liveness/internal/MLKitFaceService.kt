package com.komerce.liveness.internal

import android.content.Context
import android.util.Log
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleOwner
import com.komerce.liveness.api.LivenessDetector
import com.komerce.liveness.api.LivenessError
import com.komerce.liveness.api.LivenessResult
import com.komerce.liveness.api.LivenessStep
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

/**
 * MLKitFaceService (Internal Implementation)
 *
 * Class ini adalah "Kuli Bangunan" yang bekerja di belakang layar.
 * Dia mengimplementasikan interface [LivenessDetector] menggunakan teknologi:
 * - CameraX (Untuk akses kamera hardware)
 * - Google ML Kit (Untuk otak deteksinya)
 *
 * TUGAS UTAMA:
 * 1. Mengelola Resource Kamera (Buka, Tutup, Bind Lifecycle).
 * 2. Menyiapkan Thread Background (Executor) agar UI tidak macet saat analisis wajah.
 * 3. Menghubungkan aliran gambar (ImageAnalysis) ke [FaceAnalyzer].
 *
 * Note: Class ini bersifat 'internal', artinya module 'app' tidak bisa melihat class ini.
 * Mereka hanya tau interface 'LivenessDetector'.
 */
internal class MLKitFaceService(
    private val context: Context
) : LivenessDetector {

    private var cameraProvider: ProcessCameraProvider? = null
    private var previewView: PreviewView? = null
    private var lifecycleOwner: LifecycleOwner? = null

    // Executor: Thread khusus untuk memproses gambar agar tidak membebani Main Thread (UI).
    // Ini penting biar tampilan kamera tetap smooth 60fps walau analisisnya berat.
    private var cameraExecutor: ExecutorService = Executors.newSingleThreadExecutor()

    // Ini "Otak" matematikanya. Disimpan di variabel biar bisa kita kontrol.
    private var faceAnalyzer: FaceAnalyzer? = null

    /**
     * Menyiapkan Provider Kamera secara Asynchronous.
     * Kita butuh Context untuk akses hardware kamera.
     */
    override fun bind(lifecycleOwner: LifecycleOwner, previewView: PreviewView) {
        this.lifecycleOwner = lifecycleOwner
        this.previewView = previewView

        val cameraProviderFuture = ProcessCameraProvider.getInstance(context)
        cameraProviderFuture.addListener({
            // Kamera siap digunakan
            cameraProvider = cameraProviderFuture.get()
        }, ContextCompat.getMainExecutor(context))
    }

    /**
     * Memulai Sesi Liveness.
     * Di sini kita merakit semua komponen: Preview + Analyzer + Lifecycle.
     */
    override fun startDetection(
        challenges: List<LivenessStep>,
        onStepSuccess: (LivenessStep) -> Unit,
        onStepError: (LivenessError) -> Unit,
        onComplete: (LivenessResult) -> Unit
    ) {
        // Validasi: Pastikan bind() sudah dipanggil dan kamera ready
        val provider = cameraProvider ?: run {
            Log.e("LivenessSDK", "CRITICAL: Camera Provider belum siap. Pastikan panggil bind() di onCreate!")
            return
        }
        val owner = lifecycleOwner ?: return
        val view = previewView ?: return

        // 1. Setup UseCase Preview (Tampilan visual di layar HP)
        val preview = Preview.Builder().build()
        preview.setSurfaceProvider(view.surfaceProvider)

        // 2. Setup Analyzer (Otak Logika)
        // Kita inject logic step dan callback dari App Utama ke dalam FaceAnalyzer
        faceAnalyzer = FaceAnalyzer(
            steps = challenges,
            onStepSuccess = onStepSuccess,
            onStepError = onStepError,
            onComplete = { result ->
                // Kalau analisis selesai (sukses/gagal), matikan kamera otomatis
                stopDetection()
                onComplete(result)
            }
        )

        // 3. Setup UseCase ImageAnalysis (Aliran data di belakang layar)
        // STRATEGY_KEEP_ONLY_LATEST: Kalau HP lemot, buang frame lama, ambil yang terbaru aja.
        // Biar analisis selalu real-time dan gak delay (lag).
        val imageAnalysis = ImageAnalysis.Builder()
            .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
            .build()
            .also {
                // Pasang analyzer ke thread background
                it.setAnalyzer(cameraExecutor, faceAnalyzer!!)
            }

        // 4. Pilih Kamera Depan (Selfie)
        val cameraSelector = CameraSelector.DEFAULT_FRONT_CAMERA

        try {
            // Unbind dulu biar bersih, barangkali ada sisa sesi sebelumnya
            provider.unbindAll()

            // BIND SEMUANYA KE LIFECYCLE
            // Artinya: Kalau Activity di-minimize (onPause), kamera otomatis mati.
            // Kalau dibuka lagi (onResume), kamera nyala lagi. Hemat baterai & kode rapi.
            provider.bindToLifecycle(
                owner,
                cameraSelector,
                preview,
                imageAnalysis
            )
        } catch (exc: Exception) {
            Log.e("LivenessSDK", "Gagal start kamera (Mungkin permission belum dikasih?)", exc)
        }
    }

    /**
     * Mematikan total kamera.
     */
    override fun stopDetection() {
        cameraProvider?.unbindAll()
    }
}