package com.komerce.liveness.internal

import android.app.ActivityManager
import android.content.Context
import android.os.Build

internal object DeviceUtils {
    
    fun isLowEndDevice(context: Context): Boolean {
        val activityManager = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        
        // Check if device is low RAM
        val isLowRam = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            activityManager.isLowRamDevice
        } else {
            false
        }
        
        // Check RAM size (less than 2GB is considered low end for ML Kit)
        val memInfo = ActivityManager.MemoryInfo()
        activityManager.getMemoryInfo(memInfo)
        val totalRamGb = memInfo.totalMem / (1024 * 1024 * 1024.0)
        
        return isLowRam || totalRamGb < 2.0
    }
}
