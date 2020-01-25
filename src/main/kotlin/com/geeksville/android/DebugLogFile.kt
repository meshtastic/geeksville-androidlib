package com.geeksville.android

import android.content.Context
import java.io.File
import java.io.FileOutputStream
import java.io.PrintWriter

/**
 * Create a debug log on the SD card (if needed and allowed and app is configured for debugging (FIXME)
 * FIXME, make a directory based on appname
 *
 * write strings to that file
 */
class DebugLogFile(context: Context, name: String) {
    val stream = FileOutputStream(File(context.getExternalFilesDir(null), name), true)
    val file = PrintWriter(stream)

    fun close() {
        file.close()
    }

    fun log(s: String) {
        file.println(s) // FIXME, optionally include timestamps
        file.flush() // for debugging
    }
}