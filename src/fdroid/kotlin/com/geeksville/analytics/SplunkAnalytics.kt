package com.geeksville.analytics

import android.content.Context
import com.geeksville.android.Logging


/**
 * Created by kevinh on 12/24/14.
 */

class SplunkAnalytics(val context: Context, apiToken: String) : AnalyticsProvider, Logging {

    init {
    }

    override fun endSession() {
    }

    override fun trackLowValue(event: String, vararg properties: DataPair) {
    }

    override fun setEnabled(on: Boolean) {
    }

    override fun track(event: String, vararg properties: DataPair) {
    }

    override fun startSession() {
    }

    override fun setUserInfo(vararg p: DataPair) {
    }

    override fun increment(name: String, amount: Double) {
    }

    override fun sendScreenView(name: String) {
    }

    override fun endScreenView() {
    }
}

