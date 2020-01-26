package com.geeksville.concurrent

/**
 * This is a blocking/threaded version of coroutine Continuation
 *
 * A little bit ugly, but the coroutine version has a nasty internal bug that showed up
 * in my SyncBluetoothDevice so I needed a quick workaround.
 */
class SyncContinuation<T> {

    private val mbox = java.lang.Object()
    private var result: Result<T>? = null

    fun resume(res: T) {
        synchronized(mbox) {
            result = Result.success(res)
            mbox.notify()
        }
    }

    fun resumeWithException(ex: Throwable) {
        synchronized(mbox) {
            result = Result.failure(ex)
            mbox.notify()
        }
    }

    // Wait for the result (or throw an exception)
    fun await(timeoutMsecs: Long = -1): T {  // FIXME, support timeouts
        synchronized(mbox) {
            val startT = System.currentTimeMillis()
            while (result == null) {
                mbox.wait(timeoutMsecs)

                if (timeoutMsecs > 0 && ((System.currentTimeMillis() - startT) >= timeoutMsecs))
                    throw Exception("SyncContinuation timeout")
            }

            val r = result
            if (r != null)
                return r.getOrThrow()
            else
                throw Exception("This shouldn't happen")
        }
    }
}

/**
 * Calls an init function which is responsible for saving our continuation so that some
 * other thread can call resume or resume with exception.
 *
 * Essentially this is a blocking version of the (buggy) coroutine suspendCoroutine
 */
fun <T> suspend(timeoutMsecs: Long = -1, initfn: (SyncContinuation<T>) -> Unit): T {
    val cont = SyncContinuation<T>()

    // First call the init funct
    initfn(cont)

    // Now wait for the continuation to finish
    return cont.await(timeoutMsecs)
}
