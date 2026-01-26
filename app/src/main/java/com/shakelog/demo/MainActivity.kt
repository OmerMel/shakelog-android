package com.shakelog.demo

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.shakelog.sdk.ShakeLog
import com.shakelog.sdk.network.ShakeLogNetworkInterceptor
import okhttp3.*
import java.io.IOException

class MainActivity : AppCompatActivity() {

    // מפתח ה-API מהפורטל שלך
    private val MY_API_KEY = "sk_01bebaa9398244d2850dd0d24616f2d4"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // 1. אתחול ה-SDK
        ShakeLog.init(application, MY_API_KEY)

        // הגדרת משתמש "מזוייף" לדמו
        ShakeLog.setUserIdentifier("student_demo@college.edu")
        ShakeLog.setMetadata("plan", "Premium")

        val btnPlay = findViewById<Button>(R.id.btn_play_hit)
        val tvTitle = findViewById<TextView>(R.id.tv_song_title)
        val tvArtist = findViewById<TextView>(R.id.tv_artist)
        val tvStatus = findViewById<TextView>(R.id.tv_status)

        btnPlay.setOnClickListener {
            // עדכון ה-UI למצב "טוען"
            tvTitle.text = "Loading..."
            tvArtist.text = "Fetching data..."
            btnPlay.isEnabled = false // מונעים לחיצה כפולה

            // לוג של ה-SDK
            ShakeLog.log("User clicked Play on Top Hit")

            // 2. יצירת בקשת רשת עם הבאג
            val client = OkHttpClient.Builder()
                .addInterceptor(ShakeLogNetworkInterceptor()) // החיבור ל-SDK
                .build()

            // כתובת שבורה בכוונה (מחזירה 500)
            val request = Request.Builder()
                .url("https://httpstat.us/500?sleep=2000") // השהייה של 2 שניות
                .build()

            client.newCall(request).enqueue(object : Callback {
                override fun onFailure(call: Call, e: IOException) {
                    // שגיאת רשת אמיתית (אין אינטרנט וכו')
                    runOnUiThread {
                        tvStatus.text = "Connection Error"
                        btnPlay.isEnabled = true
                    }
                }

                override fun onResponse(call: Call, response: Response) {
                    // --- הבאג ---
                    // השרת מחזיר 500, אבל אנחנו לא מטפלים בזה ב-UI!
                    // המשתמש נשאר תקוע על "Loading..."
                    // אבל ה-SDK יקליט את השגיאה האדומה ברקע.

                    if (response.isSuccessful) {
                        runOnUiThread {
                            tvTitle.text = "Shape of You"
                            tvArtist.text = "Ed Sheeran"
                            btnPlay.isEnabled = true
                        }
                    } else {
                        // אנחנו מדפיסים לוג אבל לא מעדכנים את המסך למשתמש!
                        // זה הבאג שהם יצטרכו לדווח עליו.
                        ShakeLog.log("Server Error: ${response.code}")
                    }
                }
            })
        }
    }
}