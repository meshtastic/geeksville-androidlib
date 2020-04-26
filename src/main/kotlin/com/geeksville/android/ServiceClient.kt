package com.geeksville.android

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.IBinder
import android.os.IInterface
import com.geeksville.util.exceptionReporter
import java.io.Closeable

class BindFailedException : Exception("bindService failed")

/**
 * A wrapper that cleans up the service binding process
 */
open class ServiceClient<T : IInterface>(private val stubFactory: (IBinder) -> T) : Closeable,
    Logging {

    var serviceP: T? = null

    /// A getter that returns the bound service or throws if not bound
    val service get() = serviceP ?: throw Exception("Service not bound")

    private var context: Context? = null

    private var isClosed = true

    fun connect(c: Context, intent: Intent, flags: Int) {
        context = c
        if (isClosed) {
            isClosed = false
            if (!c.bindService(intent, connection, flags)) {

                // Some phones seem to ahve a race where if you unbind and quickly rebind bindService returns false.  Try
                // a short sleep to see if that helps
                reportError("Needed to use the second bind attempt hack")
                Thread.sleep(200)
                if (!c.bindService(intent, connection, flags)) {
                    throw BindFailedException()
                }
            }
        } else {
            warn("Ignoring rebind attempt for service")
        }
    }

    override fun close() {
        isClosed = true
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
            if (!isClosed) {
                val s = stubFactory(binder)
                serviceP = s
                onConnected(s)
            } else {
                // If we start to close a service, it seems that there is a possibility a onServiceConnected event is the queue
                // for us.  Be careful not to process that stale event
                warn("A service connected while we were closing it, ignoring")
            }
        }

        override fun onServiceDisconnected(name: ComponentName?) = exceptionReporter {
            serviceP = null
            onDisconnected()
        }
    }
}