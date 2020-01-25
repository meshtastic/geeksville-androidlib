package com.geeksville.util

import io.sentry.Sentry
import mu.KotlinLogging
import java.io.ByteArrayOutputStream
import java.io.PrintStream

private val logger = KotlinLogging.logger {}

/**
 * A helper class to make it easy to print to strings.
 * Usage:
 * val asStr = StringOutputStream()
ex.printStackTrace(asStr.input)
val result = asStr().toString()
 */
class StringOutputStream : ByteArrayOutputStream() {
    val input = PrintStream(this)
}


fun reportException(ex: Throwable, t: Thread = Thread.currentThread()) {
    logger.error { "Uncaught exception: $ex in $t" }

    val asStr = StringOutputStream()
    ex.printStackTrace(asStr.input)
    logger.error { "Stack trace: $asStr" }
    Sentry.capture(ex)
}

fun initExceptionReporter() {
    Sentry.init()

    java.lang.Thread.setDefaultUncaughtExceptionHandler { t, ex ->
        reportException(ex, t)
    }
}


/**
 * This wraps (and discards) exceptions, but first it reports them to our bug tracking system and prints
 * a message to the log.
 */
fun exceptionReporter(inner: () -> Unit) = try {
    inner()
} catch (ex: Throwable) {
    reportException(ex)
}
