package com.geeksville.util

/**
 * A MultiMap implementation for kotlin
 */
class MultiMap<KeyType, ValType> : HashMap<KeyType, MutableSet<ValType>>() {

    fun add(key: KeyType, v: ValType) {
        getOrPut(key) { -> mutableSetOf() }.add(v)
    }

}