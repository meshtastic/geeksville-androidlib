package com.geeksville.android

import android.content.Context
import android.content.pm.ApplicationInfo
import android.os.Build
import android.util.Log
import com.geeksville.andlib.BuildConfig

/**
 * Created by kevinh on 12/24/14.
 */

interface Logging {

    companion object {
        var showDebug = true
    }
    private fun tag(): String = this.javaClass.getName()

    fun info(msg: String) = Log.i(tag(), msg)
    fun verbose(msg: String) = Log.v(tag(), msg)
    fun debug(msg: String) {
       if(showDebug)
           Log.d(tag(), msg)
    }
    fun warn(msg: String) = Log.w(tag(), msg)
    fun error(msg: String) = Log.e(tag(), msg)
}