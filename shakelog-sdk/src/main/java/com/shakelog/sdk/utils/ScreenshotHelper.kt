package com.shakelog.sdk.utils

import android.app.Activity
import android.graphics.Bitmap
import android.graphics.Rect
import android.os.Handler
import android.os.Looper
import android.view.PixelCopy
import androidx.core.graphics.createBitmap

object ScreenshotHelper {

    fun takeScreenshot(activity: Activity, callback: (Bitmap?) -> Unit) {
        val view = activity.window.decorView

        if(view.width == 0 || view.height == 0) {
            callback(null)
            return
        }

        val bitmap = try {
            createBitmap(view.width, view.height, Bitmap.Config.ARGB_8888)
        } catch (e: Exception) {
            e.printStackTrace()
            callback(null)
            return
        }

        val rect = Rect(0, 0, view.width, view.height)
        val handler = Handler(Looper.getMainLooper())

        PixelCopy.request(activity.window , rect, bitmap, { copyResult ->
            if (copyResult == PixelCopy.SUCCESS) {
                callback(bitmap)
            } else {
                callback(null)
            }
        }, handler)
    }
}