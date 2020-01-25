package com.geeksville.util

import mu.KotlinLogging
import java.util.prefs.BackingStoreException
import java.util.prefs.Preferences
import kotlin.reflect.KProperty

private val logger = KotlinLogging.logger {}

/**
 * Expose java preference settings via Kotlin delegates.  Usage:
 * var mine: String by Preference("default")
 */
open class Preference(private val defaultVal: String) {
    operator fun getValue(thisRef: Any?, property: KProperty<*>): String {

        val preferences = Preferences.userNodeForPackage(thisRef!!::class.java)

        val v = preferences.get(property.name, defaultVal)
        logger.debug { "Read $preferences.${property.name} as $v" }
        return v
    }

    operator fun setValue(thisRef: Any?, property: KProperty<*>, value: String) {
        val preferences = Preferences.userNodeForPackage(thisRef!!::class.java)

        // We try to avoid generating unnecessary writes
        if (value != preferences.get(property.name, null)) {
            logger.debug { "Write $preferences.${property.name} as $value" }
            preferences.put(property.name, value)
            flushPreferences(preferences)
        } else
            logger.debug { "Ignoring unchanged $preferences.${property.name} as $value" }
    }

    /**
     * Write our changes to disk (advanced subclasses can override to do things like make filesystems writable etc...
     */
    protected open fun flushPreferences(preferences: Preferences) {
        preferences.flush()
    }
}

/**
 * A sort of nasty class that temporarily remounts a specified filesystem as rw while storing preferences, then changes
 * back to ro after writing.  Used only for (usually) readonly raspberry pi filesystems.
 */
class RemountRWPreference(defaultVal: String) : Preference(defaultVal) {
    val filesystem = "/"
    val shouldRestoreRO = false // Some file systems (such as root, have open files so can become rw but can't change back to ro

    private fun makeProcess(option: String) = Runtime.getRuntime().exec(arrayOf("/bin/mount", "-o", "remount,$option", filesystem))

    /// We try to avoid remounting, we only do it if we fail to write the first time
    var needsRemounting = false

    override fun flushPreferences(preferences: Preferences) {
        if (!needsRemounting) {
            try {
                preferences.flush()
            } catch (ex: BackingStoreException) {
                logger.warn { "Failed writing preferences, will try to remount rw" }

                if (shouldRestoreRO)
                    needsRemounting = true // Use the full unmount/remount code from now on
                else {
                    // Just slam our filesystem over to rw once and for all and try again
                    makeProcess("rw").waitFor()
                    preferences.flush()
                }
            }
        }

        if (needsRemounting) {
            // Note: we ignore the exit code for these processes - because unless running as root this remount will probably not succeed
            makeProcess("rw").waitFor()
            preferences.flush()
            makeProcess("ro").waitFor()
        }
    }
}