package com.shakelog.demo

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.shakelog.sdk.ShakeLog

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        ShakeLog.init(application)

    }
}