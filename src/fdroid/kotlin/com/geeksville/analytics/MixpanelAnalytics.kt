package com.geeksville.analytics


import android.content.Context
import com.geeksville.android.AppPrefs
import com.geeksville.android.Logging
import org.json.JSONObject


class MixpanelAnalytics(context: Context, apiToken: String, pushToken: String? = null) :
    AnalyticsProvider, Logging {

    init {
    }

    override fun trackLowValue(event: String, vararg properties: DataPair) {
    }

    override fun setEnabled(on: Boolean) {
    }

    override fun track(event: String, vararg properties: DataPair) {
    }

    override fun endSession() {
    }

    override fun startSession() {
    }

    override fun setUserInfo(vararg p: DataPair) {
    }

    override fun increment(name: String, amount: Double) {
    }

    override fun sendScreenView(name: String) {
    }

    override fun endScreenView() {}
}

