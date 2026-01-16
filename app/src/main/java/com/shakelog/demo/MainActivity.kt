package com.shakelog.demo

import android.os.Bundle
import android.util.Log
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.shakelog.sdk.ShakeLog
import com.shakelog.sdk.network.ShakeLogNetworkInterceptor
import okhttp3.OkHttpClient
import okhttp3.Request
import kotlin.concurrent.thread

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        ShakeLog.init(application, "sk_01bebaa9398244d2850dd0d24616f2d4")

        val btnNetworkTest = findViewById<Button>(R.id.button) // נשתמש בקיים לצורך הדגמה

        btnNetworkTest.setOnClickListener {
            makeTestNetworkCall()
        }
    }

    private fun makeTestNetworkCall() {
        // 1. יצירת קליינט עם ה-Interceptor שלך
        val client = OkHttpClient.Builder()
            .addInterceptor(ShakeLogNetworkInterceptor()) // <--- הנה הקסם!
            .build()

        // 2. ביצוע בקשה ברקע (סתם בשביל לייצר תעבורה)
        thread {
            try {
                val request = Request.Builder()
                    .url("https://www.google.com")
                    .build()

                val response = client.newCall(request).execute()
                Log.d("DemoApp", "Response code: ${response.code}")
                // ה-SDK אמור לתפוס את זה לבד עכשיו!

            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}