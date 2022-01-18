package com.geeksville.android

import android.app.Activity
import android.os.Bundle
import android.content.IntentSender
import android.content.Intent
import android.util.Log


interface PlayClientCallbacks /* : Activity */ {
    /**
     * Called to tell activity we've lost connection to play
     */
    fun onPlayConnectionSuspended() :Unit

    /**
     * Called to tell activity we are now connected to play
     * Do remaining init here
     */
    fun onPlayConnected() : Unit

    /**
     * Called when this machine does not have a valid form of play.
     */
    fun onPlayUnavailable() : Unit

}

/**
 * Created by kevinh on 1/5/15.
 */

public class PlayClient(val context: Activity, val playCallbacks: PlayClientCallbacks) : Logging {

    private fun showErrorDialog(code: Int) {
    }

    fun hasPlayServices(): Boolean {
		return false;
    }

    /**
     * Must be called from onActivityResult
     * @return true if we handled this
     */
    fun playOnActivityResult(requestCode: Int, resultCode: Int, data: Intent?): Boolean = false;

    fun playOnStart() {

    }

    fun playOnStop() {
    }

    fun playSaveInstanceState(outState: Bundle) {
    }
}
