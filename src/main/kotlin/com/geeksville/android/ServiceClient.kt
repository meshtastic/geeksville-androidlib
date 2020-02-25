package com.geeksville.android

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.IBinder
import android.os.IInterface
import com.geeksville.util.exceptionReporter
import java.io.Closeable

/**
 * A wrapper that cleans up the service binding process
 */
open class ServiceClient<T : IInterface>(private val stubFactory: (IBinder) -> T) : Closeable,
    Logging {

    var serviceP: T? = null

    /// A getter that returns the bound service or throws if not bound
    val service get() = serviceP ?: throw Exception("Service not bound")

    private var context: Context? = null

    fun connect(c: Context, intent: Intent, flags: Int) {
        context = c
        logAssert(c.bindService(intent, connection, flags))
    }

    override fun close() {
        context?.unbindService(connection)
        serviceP = null
        context = null
    }

    /// Called when we become connected
    open fun onConnected(service: T) {
    }

    /// called on loss of connection
    open fun onDisconnected() {
    }

    private val connection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName, binder: IBinder) = exceptionReporter {
            val s = stubFactory(binder)
            serviceP = s
            onConnected(s)
        }

        override fun onServiceDisconnected(name: ComponentName?) = exceptionReporter {
            serviceP = null
            onDisconnected()
        }
    }
}