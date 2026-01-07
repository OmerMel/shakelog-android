package com.shakelog.sdk.network.model

import com.shakelog.sdk.data.BreadcrumbData

data class ReportRequest(
    val reportId: String,
    val timestamp: String,
    val userDescription: String,
    val device: DeviceInfoData,
    val screenshotUrl: String?,
    val breadcrumbs: List<BreadcrumbData>
)

data class DeviceInfoData(
    val manufacturer: String,
    val model: String,
    val osVersion: String,
    val sdkVersion: String,
    val batteryLevel: String,
    val screenSize: String
)

data class ReportResponse(
    val id: String,
    val reportId: String,
    val status: String
)
