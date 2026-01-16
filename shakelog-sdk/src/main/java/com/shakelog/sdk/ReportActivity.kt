package com.shakelog.sdk

import android.app.AlertDialog
import android.graphics.Bitmap
import android.graphics.Canvas
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatImageView
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import com.shakelog.sdk.ui.DrawingView
import java.io.File
import java.io.FileOutputStream
import androidx.core.graphics.createBitmap
import com.shakelog.sdk.data.BreadcrumbData
import com.shakelog.sdk.data.BreadcrumbManager
import com.shakelog.sdk.network.NetworkManager
import com.shakelog.sdk.network.model.DeviceInfoData
import com.shakelog.sdk.network.model.ReportRequest
import com.shakelog.sdk.utils.DeviceCollector
import java.time.Instant
import java.util.UUID

class ReportActivity : AppCompatActivity() {

    private lateinit var imgScreenshot: AppCompatImageView
    private lateinit var drawingView: DrawingView
    private lateinit var btnClear: Button
    private lateinit var btnNext: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_report)

        hideSystemUI()
        findViews()
        setupListeners()

        val bitmap = ShakeLog.pendingScreenshot
        if(bitmap != null) {
            imgScreenshot.setImageBitmap(bitmap)
        } else {
            Toast.makeText(this, "No screenshot available", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

    }

    private fun hideSystemUI() {
        val windowInsetsController =
            WindowCompat.getInsetsController(window, window.decorView)

        windowInsetsController.systemBarsBehavior =
            WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE

        windowInsetsController.hide(WindowInsetsCompat.Type.systemBars())

    }

    private fun findViews() {
        imgScreenshot = findViewById(R.id.img_screenshot)
        drawingView = findViewById(R.id.drawing_canvas)
        btnClear = findViewById(R.id.btn_clear)
        btnNext = findViewById(R.id.btn_next)
    }

    private fun setupListeners() {
        btnClear.setOnClickListener {
            drawingView.clearCanvas()
        }

        btnNext.setOnClickListener {
            handleNextStep()
        }
    }

    private fun handleNextStep() {
        val finalBitmap = combineImages()

        if (finalBitmap != null) {
            showReportDialog(finalBitmap)
        } else {
            Toast.makeText(this, "Error", Toast.LENGTH_SHORT).show()
        }
    }

    private fun combineImages(): Bitmap? {
        return try {
            val combinedBitmap = createBitmap(imgScreenshot.width, imgScreenshot.height)

            val canvas = Canvas(combinedBitmap)

            imgScreenshot.draw(canvas)

            drawingView.draw(canvas)

            combinedBitmap
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    private fun showReportDialog(finalBitmap: Bitmap) {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Report Bug")

        val container = LinearLayout(this)
        container.orientation = LinearLayout.VERTICAL
        container.setPadding(50, 40, 50, 10)

        val inputName = EditText(this)
        inputName.hint = "Name"
        container.addView(inputName)

        val inputEmail = EditText(this)
        inputEmail.hint = "Email"
        inputEmail.inputType = android.text.InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS
        container.addView(inputEmail)

        val inputDesc = EditText(this)
        inputDesc.hint = "Description of the issue"
        inputDesc.minLines = 3
        inputDesc.gravity = android.view.Gravity.TOP or android.view.Gravity.START
        container.addView(inputDesc)

        builder.setView(container)

        builder.setPositiveButton("Send", null)

        val dialog = builder.create()
        dialog.show()

        // override the positive button to prevent auto-dismiss
        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener {
            val name = inputName.text.toString().trim()
            val email = inputEmail.text.toString().trim()
            val description = inputDesc.text.toString().trim()

            var isValid = true

            if (name.isEmpty()) {
                inputName.error = "Please enter your name"
                isValid = false
            }

            if (email.isEmpty()) {
                inputEmail.error = "Please enter your email"
                isValid = false
            }

            if (isValid) {
                dialog.dismiss()
                saveAndSendReport(finalBitmap, description, name, email)
            }
        }

        builder.setNegativeButton("Cancel") { dialog, _ ->
            dialog.cancel()
        }
    }

    private fun saveAndSendReport(bitmap: Bitmap, description: String, name: String, email: String) {
        try {
            val file = File(cacheDir, "bug_report_${System.currentTimeMillis()}.png")
            val outputStream = FileOutputStream(file)
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream)
            outputStream.flush()
            outputStream.close()

            NetworkManager.uploadImage(file) { imageUrl ->
                if (imageUrl != null) {
                    Log.d("ShakeLog", "Image uploaded: $imageUrl")
                    sendJsonReport(imageUrl, description, name, email)
                } else {
                    Toast.makeText(this, "Upload image failed", Toast.LENGTH_LONG).show()
                }
            }

        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(this, "Unable to send the report", Toast.LENGTH_SHORT).show()
        }
    }

    private fun sendJsonReport(imageUrl: String, description: String, name: String, email: String) {
        val request = createRequest(imageUrl, description, name, email)

        NetworkManager.sendReport(request) { success ->
            if (success) {
                runOnUiThread {
                    Toast.makeText(this, "Report sent successfully ", Toast.LENGTH_LONG).show()
                    finish()
                }
            } else {
                runOnUiThread {
                    Toast.makeText(this, "Unable to send the report", Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    private fun createRequest(imageUrl: String, description: String, name: String, email: String): ReportRequest {
        val deviceMap = DeviceCollector.getDeviceData(this)

        val deviceInfo = DeviceInfoData(
            manufacturer = deviceMap["manufacturer"] ?: "Unknown",
            model = deviceMap["model"] ?: "Unknown",
            osVersion = deviceMap["os_version"] ?: "Unknown",
            sdkVersion = deviceMap["sdk_version"] ?: "Unknown",
            batteryLevel = deviceMap["battery_level"] ?: "Unknown",
            screenSize = deviceMap["screen_resolution"] ?: "Unknown"
        )

        val logs = BreadcrumbManager.getLogs().map { breadcrumb ->
            BreadcrumbData(
                timestamp = breadcrumb.timestamp,
                type = breadcrumb.type,
                message = breadcrumb.message,
                data = breadcrumb.data
            )
        }

        // Copy existing user metadata and add reporter name
        val userMeta = HashMap(ShakeLog.userMetadata)
        userMeta["reporter_name"] = name

        return ReportRequest(
            apiKey = ShakeLog.apiKey,
            reportId = UUID.randomUUID().toString(),
            timestamp = Instant.now().toString(),
            userDescription = description,
            userIdentifier = email, // reporter email as identifier
            userMetadata = userMeta, // additional reporter metadata
            device = deviceInfo,
            screenshotUrl = imageUrl,
            breadcrumbs = logs
        )
    }

    override fun onDestroy() {
        super.onDestroy()
        if(isFinishing) {
            ShakeLog.pendingScreenshot = null
        }
    }
}