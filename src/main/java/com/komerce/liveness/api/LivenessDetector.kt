package com.komerce.liveness.api

import androidx.camera.view.PreviewView
import androidx.lifecycle.LifecycleOwner

/**
 * LivenessDetector (Public Interface)
 *
 * Ini adalah "Remote Control" utama untuk menggunakan Liveness SDK.
 * Aplikasi Utama (Activity/Fragment) cukup berkomunikasi lewat interface ini
 * tanpa perlu tahu keruwetan CameraX atau ML Kit di belakang layar.
 */
interface LivenessDetector {

    /**
     * Langkah 1: Persiapan (Binding).
     * Menghubungkan SDK dengan Siklus Hidup (Lifecycle) Activity dan Tampilan Kamera.
     * Wajib dipanggil paling awal (biasanya di `onCreate` atau `onViewCreated`).
     *
     * @param lifecycleOwner Activity atau Fragment (`this`).
     * Penting agar kamera otomatis mati (release) saat user keluar aplikasi,
     * jadi baterai tidak boros.
     * @param previewView    Komponen UI (`<androidx.camera.view.PreviewView>`) di XML layout kamu
     * tempat kamera akan ditampilkan.
     */
    fun bind(lifecycleOwner: LifecycleOwner, previewView: PreviewView)

    /**
     * Langkah 2: Eksekusi (Mulai Deteksi).
     * Memulai kamera dan algoritma analisis wajah.
     *
     * @param challenges    Daftar urutan tantangan yang harus dilakukan user.
     * Contoh: `listOf(LivenessStep.LOOK_LEFT, LivenessStep.SMILE)`.
     * SDK akan mengeksekusi ini secara berurutan (Playlist).
     *
     * @param onStepSuccess Callback yang dipanggil SETIAP KALI user lolos satu tantangan.
     * Param `step` berisi tantangan yang barusan selesai.
     * Gunakan ini untuk update UI (Misal ganti teks dari "Tengok Kiri" ke "Sekarang Senyum").
     *
     * @param onStepError   Callback yang dipanggil jika ada masalah pada frame kamera saat ini.
     * Contoh: `NO_FACE_DETECTED` (Wajah hilang), `MULTIPLE_FACES` (Ada 2 orang).
     * Gunakan ini untuk memberi peringatan teks merah ke user.
     * Note: Ini tidak menghentikan proses, hanya info error per frame.
     *
     * @param onComplete    Callback FINAL yang dipanggil ketika SEMUA tantangan selesai.
     * Mengembalikan `LivenessResult` yang berisi:
     * - `isSuccess`: Boolean
     * - `evidencePhotos`: Map<Step, Bitmap> (Foto bukti tiap step)
     */
    fun startDetection(
        challenges: List<LivenessStep>,
        onStepSuccess: (LivenessStep) -> Unit,
        onStepError: (LivenessError) -> Unit,
        onComplete: (LivenessResult) -> Unit
    )

    /**
     * Opsional: Mematikan kamera secara manual.
     *
     * Berguna jika user menekan tombol "Batalkan" atau "Back" sebelum proses selesai.
     * Jika aplikasi ditutup (Destroy), fungsi ini otomatis dipanggil oleh LifecycleOwner,
     * jadi tidak wajib dipanggil manual di `onDestroy`.
     */
    fun stopDetection()
}