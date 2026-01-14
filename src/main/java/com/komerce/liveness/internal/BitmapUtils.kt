package com.komerce.liveness.internal

import android.graphics.Bitmap
import android.graphics.Matrix
import androidx.camera.core.ImageProxy

/**
 * Extension functions for Bitmap and ImageProxy processing.
 */

internal fun ImageProxy.toRotatedBitmap(): Bitmap {
    val bitmap = this.toBitmap()
    val rotationDegrees = this.imageInfo.rotationDegrees
    
    val matrix = Matrix()
    // Rotate to match device orientation
    matrix.postRotate(rotationDegrees.toFloat())
    
    // Front camera is usually mirrored, but CameraX toBitmap handles some aspects.
    // If we want to mirror it back to look like a selfie:
    // matrix.postScale(-1f, 1f, bitmap.width / 2f, bitmap.height / 2f)
    
    return Bitmap.createBitmap(
        bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true
    )
}
