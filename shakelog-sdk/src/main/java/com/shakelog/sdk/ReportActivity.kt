package com.shakelog.sdk

import android.app.AlertDialog
import android.graphics.Bitmap
import android.graphics.Canvas
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
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
import com.shakelog.sdk.utils.DeviceCollector

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

        val input = EditText(this)
        input.hint = "Describe the bug..."
        builder.setView(input)

        builder.setPositiveButton("Send") { _, _ ->
            val description = input.text.toString()
            saveAndSendReport(finalBitmap, description)
        }

        builder.setNegativeButton("Cancel") { dialog, _ ->
            dialog.cancel()
        }

        builder.show()
    }

    private fun saveAndSendReport(bitmap: Bitmap, description: String) {
        try {
            val file = File(cacheDir, "bug_report_${System.currentTimeMillis()}.png")
            val outputStream = FileOutputStream(file)

            val deviceData = DeviceCollector.getDeviceData(this)
            Log.d("ShakeLog", "Device Info: $deviceData")

            bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream)
            outputStream.flush()
            outputStream.close()

            Log.d("ShakeLog", "Report Saved! Path: ${file.absolutePath}")
            Log.d("ShakeLog", "Description: $description")

            Toast.makeText(this, "Report sent successfully", Toast.LENGTH_LONG).show()

            finish()

        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(this, "Unable to send the report", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        if(isFinishing) {
            ShakeLog.pendingScreenshot = null
        }
    }
}