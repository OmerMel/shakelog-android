package com.shakelog.sdk.utils

import java.io.BufferedReader
import java.io.File
import java.io.FileWriter
import java.io.InputStreamReader

object LogcatHelper {

    fun saveLogcatToFile(outputFile: File): Boolean {
        return try {
            val process = Runtime.getRuntime().exec("logcat -d -t 500 -v threadtime")
            val reader = BufferedReader(InputStreamReader(process.inputStream))
            val writer = FileWriter(outputFile)

            var line: String?
            while (reader.readLine().also { line = it } != null) {
                writer.write(line + "\n")
            }

            writer.flush()
            writer.close()
            reader.close()
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }
}