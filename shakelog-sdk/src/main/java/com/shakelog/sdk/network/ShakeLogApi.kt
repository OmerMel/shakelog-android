package com.shakelog.sdk.network

import com.shakelog.sdk.network.model.ReportRequest
import com.shakelog.sdk.network.model.ReportResponse
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

interface ShakeLogApi {

    @POST("api/report")
    fun createReport(@Body report: ReportRequest): Call<ReportResponse>
}