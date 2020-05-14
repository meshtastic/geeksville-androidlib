package com.geeksville.concurrent

import com.geeksville.util.Exceptions
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.launch
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext

private val errorHandler =
    CoroutineExceptionHandler { _, exception ->
        Exceptions.report(
            exception,
            "MeshService-coroutine",
            "coroutine-exception"
        )
    }

/// Wrap launch with an exception handler, FIXME, move into a utility lib
fun CoroutineScope.handledLaunch(
    context: CoroutineContext = EmptyCoroutineContext,
    start: CoroutineStart = CoroutineStart.DEFAULT,
    block: suspend CoroutineScope.() -> Unit
) = this.launch(
    context = context + com.geeksville.concurrent.errorHandler,
    start = start,
    block = block
)