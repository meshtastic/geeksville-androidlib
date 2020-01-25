package com.geeksville.util

import android.util.Log

/**
 * This wraps (and discards) exceptions, but first it reports them to our bug tracking system and prints
 * a message to the log.
 */
fun <T> exceptionReporter(inner: () -> T): T = try {
    inner()
} catch (ex: Throwable) {
    Log.e("exceptionReporter", "Uncaught exception", ex)
    throw ex
}

/// When passing exceptions out into frameworks (AIDL) that don't understand such things, it is
/// sometimes useful to return exceptions as Strings or null for no failure

fun exceptionsToStrings(inner: () -> Unit): String? = try {
    inner()
    null
} catch (ex: Throwable) {
    Log.e("exceptionsToStrings", "Uncaught exception", ex)
    ex.message
}