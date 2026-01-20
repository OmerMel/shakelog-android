package com.shakelog.demo

import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.shakelog.sdk.ShakeLog
import com.shakelog.sdk.network.ShakeLogNetworkInterceptor
import okhttp3.*
import java.io.IOException

class MainActivity : AppCompatActivity() {

    // כאן אתה מדביק את ה-API Key שקיבלת מהפורטל שלך
    // אם עדיין אין לך, זה יעבוד אבל השרת יחזיר שגיאה בשמירה
    private val MY_API_KEY = "sk_REPLACE_WITH_YOUR_KEY"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // 1. אתחול ה-SDK בתחילת הריצה
        ShakeLog.init(application, MY_API_KEY)

        // איתור רכיבי ה-UI
        val btnLogin = findViewById<Button>(R.id.btn_login)
        val etUsername = findViewById<EditText>(R.id.et_username)
        val btnLoadDeal = findViewById<Button>(R.id.btn_load_deal)
        val tvStatus = findViewById<TextView>(R.id.tv_status)

        // 2. לוגיקה לכפתור התחברות (מדמה התחברות משתמש)
        btnLogin.setOnClickListener {
            val username = etUsername.text.toString()
            if (username.isNotEmpty()) {
                // אומרים ל-SDK מי המשתמש הנוכחי
                ShakeLog.setUserIdentifier(username)

                // מוסיפים מידע נוסף שיופיע בדיווח
                ShakeLog.setMetadata("user_rank", "Premium")
                ShakeLog.setMetadata("last_purchase_id", "ORDER-9988")

                Toast.makeText(this, "התחברת כמשתמש: $username", Toast.LENGTH_SHORT).show()

                // רישום פעולה ידנית ללוג של ה-SDK
                ShakeLog.log("User performed manual login")
            } else {
                Toast.makeText(this, "נא להזין שם משתמש", Toast.LENGTH_SHORT).show()
            }
        }

        // 3. הכפתור עם הבאג המכוון
        btnLoadDeal.setOnClickListener {
            tvStatus.text = "טוען מבצע מיוחד..."

            // יצירת HTTP Client שכולל את ה-Interceptor של ה-SDK
            // זה מה שמאפשר ל-SDK לתפוס את השגיאה ברשת אוטומטית
            val client = OkHttpClient.Builder()
                .addInterceptor(ShakeLogNetworkInterceptor())
                .build()

            // כתובת שמחזירה שגיאת 404 בכוונה
            val request = Request.Builder()
                .url("https://httpstat.us/404")
                .build()

            // שליחת הבקשה
            client.newCall(request).enqueue(object : Callback {
                override fun onFailure(call: Call, e: IOException) {
                    runOnUiThread {
                        tvStatus.text = "שגיאת חיבור: ${e.message}"
                    }
                }

                override fun onResponse(call: Call, response: Response) {
                    // --- כאן נמצא הבאג המדומה ---
                    // אנחנו בודקים אם הבקשה נכשלה, אבל לא מעדכנים את המסך!
                    // המשתמש יראה "טוען..." לנצח ויחשוב שהאפליקציה נתקעה.

                    if (!response.isSuccessful) {
                        Log.e("DemoApp", "Server returned error: ${response.code}")

                        // ה-SDK יקליט את זה אוטומטית דרך ה-Interceptor,
                        // אבל אנחנו יכולים להוסיף גם לוג ידני אם נרצה:
                        ShakeLog.log("Critical error in daily deal loading: 404")
                    } else {
                        // אם זה היה מצליח (לא יקרה בדמו הזה)
                        runOnUiThread { tvStatus.text = "המבצע נטען!" }
                    }
                }
            })
        }
    }
}