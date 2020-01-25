package com.geeksville.util

// import jdk.nashorn.internal.runtime.ECMAErrors.getMessage
import com.google.gson.JsonObject
import com.pubnub.api.PNConfiguration
import com.pubnub.api.PubNub
import com.pubnub.api.PubNubException
import com.pubnub.api.callbacks.SubscribeCallback
import com.pubnub.api.enums.PNOperationType
import com.pubnub.api.enums.PNReconnectionPolicy
import com.pubnub.api.enums.PNStatusCategory
import com.pubnub.api.models.consumer.PNStatus
import com.pubnub.api.models.consumer.pubsub.PNMessageResult
import com.pubnub.api.models.consumer.pubsub.PNPresenceEventResult
import mu.KotlinLogging
import java.io.Closeable
import java.util.*

private val logger = KotlinLogging.logger {}


class PubnubClient(myUUID: String) : Closeable {

    private val pnConfiguration = PNConfiguration().apply {
        // Note: These two keys are not super secret, if someone found them they would merely be able to publish/subscribe
        // to my channel (under my account).   FIXME - I think this is okay? confirm before ship.
        subscribeKey = "sub-c-00b8b6b8-e206-11e8-a197-eed1401c575e"
        publishKey = "pub-c-cd3c6220-1eda-4a3c-aa92-71448d5d255e"
        uuid = myUUID // use different id for the JS viewer
        isSecure = true // false for debugging
        reconnectionPolicy = PNReconnectionPolicy.EXPONENTIAL // We want auto reconnection attempts
    }

    private val pubnub: PubNub by lazy { PubNub(pnConfiguration) }

    // data class SubscribeHandler(val onMsg: (JsonObject) -> Unit = { }, val onConnected: () -> Unit = { })

    private val subscribers = MultiMap<String, (JsonObject) -> Unit>()
    private val connectionListeners = mutableListOf<() -> Unit>()

    init {
        val callback = object : SubscribeCallback() {
            override fun status(pubnub: PubNub, status: PNStatus) {

                //
                // Handle category style statuses
                //
                if (status.category == PNStatusCategory.PNUnexpectedDisconnectCategory) {
                    // This event happens when radio / connectivity is lost
                } else if (status.category == PNStatusCategory.PNConnectedCategory) {

                    // Connect event. You can do stuff like publish, and know you'll get it.
                    // Or just use the connected event to confirm you are subscribed for
                    // UI / internal notifications, etc

                    connectionListeners.forEach { it() }

                } else if (status.category == PNStatusCategory.PNReconnectedCategory) {

                    // Happens as part of our regular operation. This event happens when
                    // radio / connectivity is lost, then regained.
                } else if (status.category == PNStatusCategory.PNDecryptionErrorCategory) {

                    // Handle messsage decryption error. Probably client configured to
                    // encrypt messages and on live data feed it received plain text.
                }

                //
                // Handle subscription operations
                //
                if (status.operation != null) {
                    logger.debug { "Received operation: ${status.operation}" }
                    when (status.operation) {
                        PNOperationType.PNSubscribeOperation ->
                            logger.debug { "Ignoring subscription" }
                        PNOperationType.PNUnsubscribeOperation ->
                            logger.debug { "Ignoring unsubscription" }
                        PNOperationType.PNHeartbeatOperation ->
                            logger.debug { "Ignoring heartbeat" }
                        else ->
                            logger.debug { "Unexpected status ${status.operation}" }
                    }
                }
            }

            override fun message(pubnub: PubNub, message: PNMessageResult) {
                // Handle new message stored in message.message
                val channel = if (message.channel != null) {
                    // Message has been received on channel group stored in
                    message.channel
                } else {
                    // Message has been received on channel stored in
                    message.subscription
                }

                val receivers = subscribers.get(channel)

                val receivedMessageObject = message.message.asJsonObject
                logger.debug { "Received $receivedMessageObject on $channel" }

                /*
                log the following items with your favorite logger
                    - message.getMessage()
                    - message.getSubscription()
                    - message.getTimetoken()

                if using coroutines for parallelism possibly do this
                if(receivers != null)
                    runBlocking(Dispatchers.Default) {
                        receivers.forEach { async { it(receivedMessageObject)} }
                    }
                */

                if (receivers != null)
                    receivers.forEach { it(receivedMessageObject) }
            }

            override fun presence(pubnub: PubNub, presence: PNPresenceEventResult) {
                logger.debug { "ignoring presence change" }
            }
        }
        pubnub.addListener(callback)
        logger.debug { "pubnub created" }
    }

    override fun close() {
        pubnub.unsubscribeAll()
        pubnub.disconnect()
        pubnub.forceDestroy()
    }


    fun publish(channelName: String, msg: JsonObject) {
        // FIXME - call async version and make this a suspend function?
        try {
            val result = pubnub.publish().channel(channelName).message(msg).sync()
            logger.debug { "Published $msg, result $result" }
        } catch (ex: PubNubException) {
            // Probably java.net.UnknownHostException: ps.pndsn.com: Name or service not known
            if (ex.errormsg.startsWith("java.net.UnknownHostException"))
                logger.error { "Ignoring unknown pubnub host: ${ex.errormsg}, while publishing $msg" }
            else
                throw ex
        }


        /*
        pubnub.publish().channel(channelName).message(messageJsonObject).async(object : PNCallback<PNPublishResult>() {
                            override fun onResponse(result: PNPublishResult, status: PNStatus) {
                                // Check whether request successfully completed or not.
                                if (!status.isError) {

                                    // Message successfully published to specified channel.
                                } else {

                                    // Handle message publish error. Check 'category' property to find out possible issue
                                    // because of which request did fail.
                                    //
                                    // Request can be resent using: [status retry];
                                }// Request processing failed.
                            }
                        })
         */
    }

    /**
     * Subscribe to a channel
     */
    fun subscribe(channelName: String, onMsg: (JsonObject) -> Unit) {
        subscribers.add(channelName, onMsg)
        pubnub.subscribe().channels(Arrays.asList(channelName)).execute()
    }

    fun addConnectionListener(l: () -> Unit) {
        connectionListeners.add(l)
    }

}