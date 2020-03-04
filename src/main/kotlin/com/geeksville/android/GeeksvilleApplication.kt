package com.geeksville.android

import android.app.Activity
import android.app.Application
import android.content.Context
import android.net.ConnectivityManager
import android.os.Bundle
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
        var currentActivity: Activity? = null
    }

    var splunk: AnalyticsProvider? = null
    var mixAnalytics: MixpanelAnalytics? = null

    private val lifecycleCallbacks = object : ActivityLifecycleCallbacks {
        override fun onActivityPaused(activity: Activity) {
        }

        override fun onActivityStarted(activity: Activity) {
        }

        override fun onActivityDestroyed(activity: Activity) {
            currentActivity = null
        }

        override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {
        }

        override fun onActivityStopped(activity: Activity) {
        }

        override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {
            currentActivity = activity
        }

        override fun onActivityResumed(activity: Activity) {
        }
    }

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

        registerActivityLifecycleCallbacks(lifecycleCallbacks)
    }

    fun isInternetConnected(): Boolean {
        val cm = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

        val activeNetwork = cm.getActiveNetworkInfo();
        val isConnected = activeNetwork != null &&
                activeNetwork.isConnectedOrConnecting();

        return isConnected
    }

}


fun geeksvilleApp(context: Context) = context.applicationContext as GeeksvilleApplication


interface GeeksvilleApplicationClient {

    fun getAnalytics() = GeeksvilleApplication.analytics

}