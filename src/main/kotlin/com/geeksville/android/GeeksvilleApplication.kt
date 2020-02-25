package com.geeksville.android

import android.app.Application
import android.content.Context
import android.net.ConnectivityManager
import com.geeksville.analytics.AnalyticsProvider
import com.geeksville.analytics.MixpanelAnalytics
import com.geeksville.analytics.TeeAnalytics

/**
 * Created by kevinh on 1/4/15.
 */

open class GeeksvilleApplication(
    val splunkKey: String?,
    val mixpanelKey: String?,
    val pushKey: String? = null
) : Application(), Logging {

    companion object {
        lateinit var analytics: AnalyticsProvider
    }

    var splunk: AnalyticsProvider? = null
    var mixAnalytics: MixpanelAnalytics? = null

    override fun onCreate() {
        super<Application>.onCreate()

        /*
        if(splunkKey != null)
            splunk = SplunkAnalytics(this, splunkKey) // Only used for crash reports
        */

        val googleAnalytics = com.geeksville.analytics.GoogleAnalytics(this)
        if (mixpanelKey != null) {
            val mix = com.geeksville.analytics.MixpanelAnalytics(this, mixpanelKey, pushKey)
            mixAnalytics = mix

            analytics = TeeAnalytics(googleAnalytics, mix)
        } else
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

    fun getAnalytics() = GeeksvilleApplication.analytics

}