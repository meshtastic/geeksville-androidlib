package com.geeksville.util

fun <K, V> kotlin.collections.Map<K, V?>.dump() {
    this.forEach { (k, v) ->
        println("$k -> $v")
    }
}