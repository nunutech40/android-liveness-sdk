package com.komerce.liveness

import android.content.Context
import com.komerce.liveness.api.LivenessDetector
import com.komerce.liveness.internal.MLKitFaceService

/**
 * LivenessFactory
 *
 * Pintu gerbang utama (Entry Point) untuk menggunakan SDK ini.
 * Gunakan class ini untuk membuat instance dari [LivenessDetector].
 */
object LivenessFactory {

    /**
     * Membuat instance detektor liveness.
     *
     * @param context Context aplikasi (bisa Activity atau Application Context).
     * @return Object yang mengimplementasikan interface [LivenessDetector].
     */
    fun create(context: Context): LivenessDetector {
        // Di sini kita bikin instance dari implementation yang "Internal/Rahasia"
        // dan mengembalikannya sebagai Interface "Public".
        return MLKitFaceService(context)
    }
}