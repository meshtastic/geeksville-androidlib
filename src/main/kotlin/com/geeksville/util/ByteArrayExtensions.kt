package com.geeksville.util

fun ByteArray.toHexString() = joinToString("") { "%02x".format(it) }