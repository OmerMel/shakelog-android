package com.shakelog.sdk

import android.annotation.SuppressLint
import android.app.Activity
import android.app.Application
import android.content.Intent
import android.content.Intent.FLAG_ACTIVITY_NEW_TASK
import android.graphics.Bitmap
import android.os.Bundle
import android.util.Log
import com.shakelog.sdk.data.BreadcrumbManager
import com.shakelog.sdk.data.BreadcrumbType
import com.shakelog.sdk.utils.ScreenshotHelper
import java.lang.ref.WeakReference

object ShakeLog : Application.ActivityLifecycleCallbacks {

    @SuppressLint("StaticFieldLeak")
    private var shakeDetector: ShakeDetector? = null

    public var pendingScreenshot: Bitmap? = null

    private var currentActivityRef: WeakReference<Activity?> = WeakReference(null)

    /// API Key set by the developer
    var apiKey: String = ""
        private set

    // Reporter user ID
    var userId: String? = null
        private set

    val userMetadata = mutableMapOf<String, String>()

    fun init(application: Application, apiKey: String) {
        if(shakeDetector != null)
            return

        this.apiKey = apiKey
        Log.d("ShakeLogSDK", "Initializing SDK with API Key: $apiKey")

        application.registerActivityLifecycleCallbacks(this)

        shakeDetector = ShakeDetector(application.applicationContext)

        shakeDetector?.setOnShakeListener {
            val currentActivity = currentActivityRef.get()
            if (currentActivity != null) {
                Log.d("ShakeLogSDK", "Shake detected, taking screenshot...")
                ScreenshotHelper.takeScreenshot(currentActivity) { bitmap ->
                    if (bitmap != null) {
                        Log.d("ShakeLogSDK", "Screenshot taken successfully.")
                        pendingScreenshot = bitmap

                        val intent = Intent(currentActivity, ReportActivity::class.java)
                        intent.addFlags(FLAG_ACTIVITY_NEW_TASK)
                        currentActivity.startActivity(intent)
                    } else {
                        Log.d("ShakeLogSDK", "Failed to take screenshot.")
                    }
                }
            } else {
                Log.d("ShakeLogSDK", "No current activity found to take screenshot.")
            }
        }

        shakeDetector?.start()
    }

    /**
     *Add ability for developer to log manual logs
     */
    fun log(message: String) {
        BreadcrumbManager.add(BreadcrumbType.USER, message)
    }

    /**
     * Setting a unique identifier for the reporting user
     */
    fun setUserIdentifier(id: String) {
        this.userId = id
        BreadcrumbManager.add(BreadcrumbType.USER, "User ID set to: $id")
    }

    /**
     * Adding custom metadata for the reporting user
     */
    fun setMetadata(key: String, value: String) {
        userMetadata[key] = value
    }

    /**
     * Clearing user information upon logout
     */
    fun clearUser() {
        this.userId = null
        userMetadata.clear()
        BreadcrumbManager.add(BreadcrumbType.USER, "User logged out / cleared")
    }

    // --- ActivityLifecycleCallbacks Implementation ---
    // These methods help keep track of the current activity
    override fun onActivityResumed(activity: Activity) {
        currentActivityRef = WeakReference(activity)

        BreadcrumbManager.add(
            type = BreadcrumbType.LIFECYCLE,
            message = "Screen Viewed: ${activity.javaClass.simpleName}"
        )
    }

    override fun onActivityPaused(activity: Activity) {
        if (currentActivityRef.get() == activity) {
            currentActivityRef = WeakReference(null)
        }
    }

    override fun onActivityStopped(activity: Activity) {
        BreadcrumbManager.add(
            type = BreadcrumbType.LIFECYCLE,
            message = "Screen Background: ${activity.javaClass.simpleName}"
        )
    }

    // Unused lifecycle methods
    override fun onActivitySaveInstanceState(p0: Activity, p1: Bundle) { }
    override fun onActivityStarted(p0: Activity) { }
    override fun onActivityCreated(p0: Activity, p1: Bundle?) { }
    override fun onActivityDestroyed(p0: Activity) { }
}