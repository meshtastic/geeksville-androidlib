package com.geeksville.android

import android.util.Log
import com.geeksville.util.Exceptions

/**
 * Created by kevinh on 12/24/14.
 */

typealias LogPrinter = (Int, String, String) -> Unit

interface Logging {

    companion object {
        /// if false NO logs will be shown, set this in the application based on BuildConfig.DEBUG
        var showLogs = true

        /// If false debug logs will not be shown (but others might)
        var showDebug = true

        /**
         * By default all logs are printed using the standard android Log class.  But clients
         * can change printlog to a different implementation (for logging to files or via
         * google crashlytics)
         */
        var printlog: LogPrinter = { level, tag, message ->
            if (showLogs) {
                if (showDebug || level > Log.DEBUG)
                    Log.println(level, tag, message)
            }
        }
    }

    private fun tag(): String = this.javaClass.getName()

    fun info(msg: String) = printlog(Log.INFO, tag(), msg)
    fun verbose(msg: String) = printlog(Log.VERBOSE, tag(), msg)
    fun debug(msg: String) = printlog(Log.DEBUG, tag(), msg)
    fun warn(msg: String) = printlog(Log.WARN, tag(), msg)

    /**
     * Log an error message, note - we call this errormsg rather than error because error() is
     * a stdlib function in kotlin in the global namespace and we don't want users to accidentally call that.
     */
    fun errormsg(msg: String, ex: Throwable? = null) =
        printlog(Log.ERROR, tag(), "$msg (exception ${ex?.message ?: "none"}")

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
        Exceptions.report(Exception("logging reportError: $s"), s)
    }
}