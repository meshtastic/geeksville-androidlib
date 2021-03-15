package com.geeksville.util

/// A toString that makes sure all newlines are removed (for nice logging).
fun Any.toOneLineString() = this.toString().replace('\n', ' ')

fun formatAgo(lastSeenUnix: Int): String {
    val currentTime = (System.currentTimeMillis() / 1000).toInt()
    val diffMin = (currentTime - lastSeenUnix) / 60;
    if (diffMin < 100)
        return diffMin.toString() + "m"
    if (diffMin < 6000)
        return (diffMin / 60).toString() + "h"
    return (diffMin / (60 * 24)).toString() + "d";
}
