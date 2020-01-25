package com.geeksville.util

fun dumpThreads() {
    val group = Thread.currentThread().threadGroup

    var numThreads = group.activeCount()
    val threads = arrayOfNulls<Thread>(numThreads)

    // numThreads might shrink slightly
    numThreads = group.enumerate(threads)

    repeat(numThreads) {
        val t = threads[it]!!

        println("$it: name=${t.name}, pri=${t.priority}, daemon=${t.isDaemon}")
    }
}