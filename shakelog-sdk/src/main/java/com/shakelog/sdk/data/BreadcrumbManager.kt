package com.shakelog.sdk.data

import java.util.LinkedList

object BreadcrumbManager {

    private const val MAX_BREADCRUMBS = 50

    private val breadcrumbs = LinkedList<BreadcrumbData>()

    fun add(type: BreadcrumbType, message: String, data: Map<String, String>? = null) {
        synchronized(breadcrumbs) {
            if (breadcrumbs.size >= MAX_BREADCRUMBS) {
                breadcrumbs.removeFirst() // Remove oldest breadcrumb
            }
            breadcrumbs.add(BreadcrumbData(type = type, message = message, data = data))
        }
    }

    fun getLogs(): List<BreadcrumbData> {
        synchronized(breadcrumbs) {
            // Return a copy to prevent external modification
            return ArrayList(breadcrumbs)
        }
    }

    fun clear() {
        breadcrumbs.clear()
    }
}