package com.geeksville.util

import android.os.RemoteException
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

/// Convert any exceptions in this service call into a RemoteException that the client can
/// then handle
fun <T> toRemoteExceptions(inner: () -> T): T = try {
    inner()
} catch (ex: Throwable) {
    Log.e("toRemoteExceptions", "Uncaught exception, returning to remote client", ex)
    throw RemoteException(ex.message)
}