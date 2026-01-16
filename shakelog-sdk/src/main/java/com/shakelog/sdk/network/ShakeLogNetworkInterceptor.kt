package com.shakelog.sdk.network

import com.shakelog.sdk.data.BreadcrumbManager
import com.shakelog.sdk.data.BreadcrumbType
import okhttp3.Interceptor
import okhttp3.Response
import java.io.IOException

class ShakeLogNetworkInterceptor : Interceptor {

    @Throws(IOException::class)
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        val startNs = System.nanoTime()

        val response: Response
        try {
            response = chain.proceed(request)
        } catch (e: Exception) {
            // if there was an error during the network call, log it as an error breadcrumb
            BreadcrumbManager.add(
                type = BreadcrumbType.ERROR,
                message = "Network Error: ${request.method} ${request.url}",
                data = mapOf(
                    "url" to request.url.toString(),
                    "error" to (e.message ?: "Unknown Error")
                )
            )
            throw e
        }

        // Calculate the duration of the request
        val tookMs = java.util.concurrent.TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - startNs)

        // Add a breadcrumb for the network request
        BreadcrumbManager.add(
            type = BreadcrumbType.NETWORK,
            message = "${request.method} ${response.code}",
            data = mapOf(
                "url" to request.url.toString(),
                "method" to request.method,
                "status_code" to response.code.toString(),
                "duration_ms" to tookMs.toString(),
                "response_size" to (response.body?.contentLength()?.toString() ?: "unknown")
            )
        )

        return response
    }
}