package com.geeksville.android

import android.util.Log

/**
 * Created by kevinh on 12/24/14.
 */

interface Logging {

    companion object {
        /// if false NO logs will be shown, set this in the application based on BuildConfig.DEBUG
        var showLogs = true

        /// If false debug logs will not be shown (but others might)
        var showDebug = true
    }

    private fun tag(): String = this.javaClass.getName()

    fun info(msg: String) {
        if (showLogs)
            Log.i(tag(), msg)
    }

    fun verbose(msg: String) {
        if (showDebug && showLogs)
            Log.v(tag(), msg)
    }

    fun debug(msg: String) {
        if (showDebug && showLogs)
            Log.d(tag(), msg)
    }

    fun warn(msg: String) = Log.w(tag(), msg)

    /**
     * Log an error message, note - we call this errormsg rather than error because error() is
     * a stdlib function in kotlin in the global namespace and we don't want users to accidentally call that.
     */
    fun errormsg(msg: String, ex: Throwable? = null) = Log.e(tag(), msg, ex)

    /// Kotlin assertions are disabled on android, so instead we use this assert helper
    fun logAssert(f: Boolean) {
        if (!f) {
            val ex = AssertionError("Assertion failed")

            // if(!Debug.isDebuggerConnected())
            throw ex
        }
    }

    /// Report an error (including messaging our crash reporter service if allowed
    fun reportError(s: String) {
        errormsg(s) // FIXME also report
    }
}