package com.shakelog.sdk.data

data class BreadcrumbData(
    val timestamp: Long = System.currentTimeMillis(),
    val type: BreadcrumbType,
    val message: String,
    val data: Map<String, String>? = null
)

enum class BreadcrumbType {
    LIFECYCLE, // Screen/activity/fragment lifecycle events
    SYSTEM,    // System events
    NETWORK,   // API calls and network events
    USER,      // User interactions
    ERROR      // Errors and exceptions
}
