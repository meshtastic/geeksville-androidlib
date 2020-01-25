package com.geeksville.android

import android.util.Log

/**
 * Created by kevinh on 12/24/14.
 */

interface Logging {

    companion object {
        var showDebug = true
    }

    private fun tag(): String = this.javaClass.getName()

    fun info(msg: String) = Log.i(tag(), msg)
    fun verbose(msg: String) = Log.v(tag(), msg)
    fun debug(msg: String) {
        if (showDebug)
            Log.d(tag(), msg)
    }

    fun warn(msg: String) = Log.w(tag(), msg)
    fun error(msg: String, ex: Throwable? = null) = Log.e(tag(), msg, ex)

    /// Kotlin assertions are disabled on android, so instead we use this gassert helper
    fun logAssert(f: Boolean) {
        if (!f) {
            val ex = AssertionError("Assertion failed")

            // if(!Debug.isDebuggerConnected())
            throw ex
        }
    }
}