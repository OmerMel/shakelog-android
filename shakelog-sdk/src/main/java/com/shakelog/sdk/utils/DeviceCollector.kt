package com.shakelog.sdk.utils

import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.BatteryManager
import android.os.Build

object DeviceCollector {

    fun getDeviceData(context: Context): Map<String, String> {
        return mapOf(
            "manufacturer" to Build.MANUFACTURER, // Samsung
            "model" to Build.MODEL,               // SM-G991B
            "device" to Build.DEVICE,             // s21ultra
            "os_version" to Build.VERSION.RELEASE,// android version
            "sdk_version" to Build.VERSION.SDK_INT.toString(), // API Level
            "battery_level" to getBatteryLevel(context),
            "screen_resolution" to getScreenResolution(context)
        )
    }

    private fun getBatteryLevel(context: Context): String {
        return try {
            val batteryStatus: Intent? = IntentFilter(Intent.ACTION_BATTERY_CHANGED).let { ifilter ->
                context.registerReceiver(null, ifilter)
            }
            val level: Int = batteryStatus?.getIntExtra(BatteryManager.EXTRA_LEVEL, -1) ?: -1
            val scale: Int = batteryStatus?.getIntExtra(BatteryManager.EXTRA_SCALE, -1) ?: -1

            if (level == -1 || scale == -1) "Unknown" else "${(level * 100 / scale.toFloat()).toInt()}%"
        } catch (e: Exception) {
            "Unknown"
        }
    }

    private fun getScreenResolution(context: Context): String {
        return try {
            val metrics = context.resources.displayMetrics
            "${metrics.heightPixels}x${metrics.widthPixels}"
        } catch (e: Exception) {
            "Unknown"
        }
    }

}