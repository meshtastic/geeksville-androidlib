package com.geeksville.analytics


import android.content.Context
import com.geeksville.android.AppPrefs
import com.mixpanel.android.mpmetrics.MixpanelAPI
import org.json.JSONObject
import com.geeksville.android.Logging


class MixpanelAnalytics(context: Context, apiToken: String, pushToken: String? = null) : AnalyticsProvider, Logging {
    // Initialize the library with your
    // Mixpanel project token, MIXPANEL_TOKEN, and a reference
    // to your application context.
    // See mixpanel docs at https://mixpanel.com/help/reference/android
    val mixpanel: MixpanelAPI = MixpanelAPI.getInstance(context, apiToken)
    val people = mixpanel.getPeople()!!

    init {
        // fixupMixpanel()

        // Assign a unique ID
        val pref = AppPrefs(context)
        val id = pref.getInstallId()
        debug("Connecting to mixpanel $id")
        mixpanel.identify(id)
        people.identify(id)
        if(pushToken != null)
            people.initPushHandling(pushToken)
    }

    /**
     * Work around for mixpanel bug - no longer needed
     * https://github.com/mixpanel/mixpanel-android/issues/253
     */
    private fun fixupMixpanel() {
        try {
            val field = MixpanelAPI::class.java.getDeclaredField("mTrackingDebug")
            field.setAccessible(true)
            field.set(mixpanel, null)
        }
        catch(ex: Exception) {
            debug("Ignoring mixpanel fixup: $ex")
        }
    }

    private fun makeJSON(properties: Array<out DataPair>) =
            if (properties.isEmpty())
                null
            else {
                val r = JSONObject()
                properties.forEach { r.put(it.name, it.value) }
                r
            }

    override fun trackLowValue(event: String, vararg properties: DataPair) {
    }

    override fun track(event: String, vararg properties: DataPair) {

        debug("Tracking $event")
        val obj = makeJSON(properties)

        mixpanel.track(event, obj)
    }

    override fun endSession() {
        // track("End Session")
        mixpanel.flush()
    }

    override fun startSession() {
        track("Start Session")
    }

    override fun setUserInfo(vararg p: DataPair) {
        mixpanel.registerSuperProperties(makeJSON(p))
    }

    override fun increment(name: String, amount: Double) {
        mixpanel.getPeople().increment(name, amount)
    }

    override fun sendScreenView(name: String) {
        // too verbose for mixpanel
        track(name)
    }

    override fun endScreenView() {}
}

