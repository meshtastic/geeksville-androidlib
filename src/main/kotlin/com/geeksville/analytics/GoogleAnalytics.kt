package com.geeksville.analytics

import android.content.Context
import com.geeksville.android.AppPrefs
import com.geeksville.android.Logging
import com.google.android.gms.analytics.HitBuilders

/**
 * Created by kevinh on 12/24/14.
 */

/**
 * Implement our analytics API using google analtics
 */
class GoogleAnalytics(context: Context, googleAnalyticsRes: Int): AnalyticsProvider, Logging {

    val analytics = com.google.android.gms.analytics.GoogleAnalytics.getInstance(context)
    val t = analytics.newTracker(googleAnalyticsRes);

    init {
        t.enableAdvertisingIdCollection(true)
        t.enableExceptionReporting(true)
        //t.enableAutoActivityTracking(true)

        // Assign a unique ID
        val pref = AppPrefs(context)
        t.set("&uid", pref.getInstallId())
    }

    override fun endSession() {
        track("End Session")
        // Mint.flush() // Send results now
    }

    override fun trackLowValue(event: String, vararg properties: DataPair) {
        track(event, *properties)
    }

    override fun track(event: String, vararg properties: DataPair) {
        val category = "default"

        val n = HitBuilders.EventBuilder()
                .setCategory(category)
                .setAction(event)

        // We only support one property
        val prop = properties.firstOrNull()
        if(prop != null) {
            // We just pass the value, throw away label
            n.setLabel(prop.value.toString())
        }

        debug("Analytics: track $event")
        t.send(n.build());
    }

    override fun startSession() {
        debug("Analytics: start session")
        t.send(HitBuilders.ScreenViewBuilder()
                .setNewSession()
                .build())
    }

    override fun setUserInfo(vararg p: DataPair) {
        // FIXME
        //p forEach { Mint.addExtraData(it.name, it.value.toString()) }
    }

    override fun increment(name: String, amount: Double) {
        //Mint.logEvent("$name increment")
    }

    /**
     * Send a google analyics screen view event
     */
    override fun sendScreenView(name: String) {
        debug("Analytics: start screen $name")
        t.setScreenName(name)
        t.send(HitBuilders.ScreenViewBuilder().build());
    }

    override fun endScreenView() {
        debug("Analytics: end screen")
        t.setScreenName(null)
    }
}