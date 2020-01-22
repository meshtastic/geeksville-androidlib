package com.geeksville.analytics

import android.content.Context
import org.json.JSONObject
import android.app.Application
import com.geeksville.android.Logging

/**
 * Created by kevinh on 12/24/14.
 */

data class DataPair(val name: String, val value: Any)

public interface AnalyticsProvider {
    /**
     * Store an event
     */
    fun track(event: String, vararg properties: DataPair): Unit

    /**
     * Only track this event if using a cheap provider (like google)
     */
    fun trackLowValue(event: String, vararg properties: DataPair): Unit

    fun endSession(): Unit
    fun startSession(): Unit

    /**
     * Set persistent ID info about this user, as a key value pair
     */
    fun setUserInfo(vararg p: DataPair)

    /**
     * Increment some sort of anyalytics counter
     */
    fun increment(name: String, amount: Double = 1.0)

    fun sendScreenView(name: String)
    fun endScreenView()

}


