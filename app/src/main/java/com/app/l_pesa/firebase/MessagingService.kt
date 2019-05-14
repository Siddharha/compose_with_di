package com.app.l_pesa.firebase

import android.content.Intent
import android.support.v4.content.LocalBroadcastManager
import com.amazonaws.mobile.auth.core.internal.util.ThreadUtils.runOnUiThread
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage


/**
 * Created by Intellij Amiya on 07-08-2018.
 * A good programmer is someone who looks both ways before crossing a One-way street.
 * Kindly follow https://source.android.com/setup/code-style
 */


class MessagingService : FirebaseMessagingService() {

    private var broadcaster: LocalBroadcastManager? = null

    override fun onCreate() {
        broadcaster = LocalBroadcastManager.getInstance(this)
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage?) {

        runOnUiThread {

            println("Notification_"+remoteMessage!!.data["title"])

           /* val intent = Intent("Notification")
            intent.putExtra("title", remoteMessage.data["title"])
           // intent.putExtra("body", remoteMessage.data["body"])
            broadcaster!!.sendBroadcast(intent)*/
        }


    }
}