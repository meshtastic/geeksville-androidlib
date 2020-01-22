package com.geeksville.android

import android.app.Application
import com.google.android.gms.analytics.Tracker
import kotlin.properties.Delegates
import android.content.Context
import android.net.ConnectivityManager
import com.geeksville.analytics.*
import com.google.android.gms.analytics.HitBuilders

/**
 * Created by kevinh on 1/4/15.
 */

open public class GeeksvilleApplication(val googleAnalyticsRes: Int, val splunkKey: String?, val mixpanelKey: String?, val pushKey: String?): Application(), Logging {

    var analytics: AnalyticsProvider? = null
    var splunk: AnalyticsProvider? = null
    var mixAnalytics: MixpanelAnalytics? = null

    override fun onCreate() {
        super<Application>.onCreate()

        if(splunkKey != null)
            splunk = SplunkAnalytics(this, splunkKey) // Only used for crash reports

        val googleAnalytics = GoogleAnalytics(this, googleAnalyticsRes)
        if(mixpanelKey != null) {
            val mix = com.geeksville.analytics.MixpanelAnalytics(this, mixpanelKey, pushKey)
            mixAnalytics = mix

            analytics = TeeAnalytics(googleAnalytics, mix)
        }
        else
            analytics = googleAnalytics
    }

    fun isInternetConnected(): Boolean {
        val cm = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

        val activeNetwork = cm.getActiveNetworkInfo();
        val isConnected = activeNetwork != null &&
                activeNetwork.isConnectedOrConnecting();

        return isConnected
    }

}

interface GeeksvilleApplicationClient {
    fun getApplication(): Application

    fun geeksvilleApp() = getApplication() as GeeksvilleApplication

    fun getAnalytics() = geeksvilleApp().analytics!!

}