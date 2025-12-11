package com.komerce.liveness.api

/**
 * Konfigurasi untuk sesi Liveness.
 * @param steps Daftar tantangan (urutan berpengaruh).
 * @param isAuditMode Jika true, simpan foto tiap step. Jika false, cuma simpan foto akhir.
 */
data class LivenessConfig(
    val steps: List<LivenessStep>,
    val isAuditMode: Boolean = false
)