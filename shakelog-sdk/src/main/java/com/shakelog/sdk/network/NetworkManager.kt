package com.shakelog.sdk.network

import android.net.Uri
import android.util.Log
import com.google.firebase.storage.FirebaseStorage
import com.shakelog.sdk.network.model.ReportRequest
import com.shakelog.sdk.network.model.ReportResponse
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Call
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.Callback
import retrofit2.converter.gson.GsonConverterFactory
import java.io.File
import java.util.concurrent.TimeUnit

object NetworkManager {

    private const val TAG = "ShakeLogNetwork"

    // server local address
    // if real device: your computer's IP address on the local network
    // if emulator: use "http://10.0.2.2:8080/"
    private const val BASE_URL = "http://192.168.1.247:8080/"

    private val api: ShakeLogApi by lazy {
        val logging = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }

        val client = OkHttpClient.Builder()
            .addInterceptor(logging)
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .build()

        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .client(client)
            .build()
            .create(ShakeLogApi::class.java)
    }

    fun uploadImage(file: File, callback: (String?) -> Unit) {
        val storageRef = FirebaseStorage.getInstance().reference

        val imageRef = storageRef.child("screenshots/${file.name}")
        val fileUri = Uri.fromFile(file)

        Log.d(TAG, "Starting upload to Firebase: ${file.name}")

        imageRef.putFile(fileUri)
            .addOnSuccessListener {
                // Upload succeeded, now get the download URL
                imageRef.downloadUrl.addOnSuccessListener { uri ->
                    Log.d(TAG, "Upload success! URL: $uri")
                    callback(uri.toString())
                }
            }
            .addOnFailureListener { exception ->
                Log.e(TAG, "Upload failed: ${exception.message}")
                callback(null)
            }
    }

    fun sendReport(reportRequest: ReportRequest, callback: (Boolean) -> Unit) {
        Log.d(TAG, "Sending report to server: $reportRequest")

        api.createReport(reportRequest).enqueue(object : Callback<ReportResponse> {
            override fun onResponse(call: Call<ReportResponse>, response: Response<ReportResponse>) {
                if (response.isSuccessful) {
                    Log.d(TAG, "Report sent successfully! ID: ${response.body()?.reportId}")
                    callback(true)
                } else {
                    Log.e(TAG, "Server error: ${response.code()} - ${response.errorBody()?.string()}")
                    callback(false)
                }
            }

            override fun onFailure(call: Call<ReportResponse>, t: Throwable) {
                Log.e(TAG, "Network failure: ${t.message}")
                callback(false)
            }
        })
    }
}
