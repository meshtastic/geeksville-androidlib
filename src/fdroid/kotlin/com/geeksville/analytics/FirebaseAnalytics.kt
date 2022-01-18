package com.geeksville.analytics

import android.content.Context
import android.os.Bundle
import com.geeksville.android.AppPrefs
import com.geeksville.android.GeeksvilleApplication
import com.geeksville.android.Logging

/**
 * Implement our analytics API using firebase analtics
 */
class GoogleAnalytics(context: Context) : AnalyticsProvider, Logging {

    init {
    }

    override fun setEnabled(on: Boolean) {
    }

    override fun endSession() {
    }

    override fun trackLowValue(event: String, vararg properties: DataPair) {
    }

    override fun track(event: String, vararg properties: DataPair) {
    }

    override fun startSession() {
    }

    override fun setUserInfo(vararg p: DataPair) {
    }

    override fun increment(name: String, amount: Double) {
    }

    /**
     * Send a google analyics screen view event
     */
    override fun sendScreenView(name: String) {
    }

    override fun endScreenView() {
    }
}
