package com.geeksville.util

/// A toString that makes sure all newlines are removed (for nice logging).
fun Any.toOneLineString() = this.toString().replace('\n', ' ')
