package com.geeksville.analytics

import android.content.Context
import android.os.Bundle
import com.geeksville.android.AppPrefs
import com.geeksville.android.Logging

/**
 * Implement our analytics API using firebase analtics
 */
class GoogleAnalytics(context: Context): AnalyticsProvider, Logging {

    val t = com.google.firebase.analytics.FirebaseAnalytics.getInstance(context)

    init {

        val pref = AppPrefs(context)
        t.setUserId(pref.getInstallId())
    }

    override fun endSession() {
        track("End Session")
        // Mint.flush() // Send results now
    }

    override fun trackLowValue(event: String, vararg properties: DataPair) {
        track(event, *properties)
    }

    override fun track(event: String, vararg properties: DataPair) {
        /*
        val bundle = Bundle()
        bundle.putString(FirebaseAnalytics.Param.ITEM_ID, id)
        bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, name)
        bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, "image")
        t.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle)


        // We only support one property
        val prop = properties.firstOrNull()
        if(prop != null) {
            // We just pass the value, throw away label
            n.setLabel(prop.value.toString())
        }

         */

        debug("Analytics: track $event")
    }

    override fun startSession() {
        debug("Analytics: start session")
        // automatic with firebase
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
        // automatic with firebase
    }

    override fun endScreenView() {
        debug("Analytics: end screen")
    }
}