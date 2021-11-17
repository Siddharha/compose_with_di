package com.app.l_pesa.firebase

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.media.RingtoneManager
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import com.app.l_pesa.R
import com.app.l_pesa.notification.view.NotificationActivity
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import java.util.*


class FirebaseMessagingIdService : FirebaseMessagingService() {

    private lateinit var notificationManager: NotificationManager
    private val CHANNELID = "l_pesa"

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)
        remoteMessage.let { message ->

            notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                setupNotificationChannels()
            }
                val notificationId = Random().nextInt(60000)
                val intent=Intent(this, NotificationActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                val pendingIntent = PendingIntent.getActivity(this, 0, intent,
                        PendingIntent.FLAG_ONE_SHOT)

                val soundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
                val notificationBuilder = NotificationCompat.Builder(this,CHANNELID)
                        .setSmallIcon(R.drawable.ic_notification)
                        .setContentTitle(message.data["title"])
                        .setContentText(message.data["body"])
                        .setAutoCancel(true)
                        .setSound(soundUri)
                        .setContentIntent(pendingIntent)


                notificationManager.notify(notificationId, notificationBuilder.build())
            }


    }

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        Log.e("FCM token", "onNewToken: $token", )
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private fun setupNotificationChannels() {
        val adminChannelName = getString(R.string.app_name)
        val adminChannelDescription = getString(R.string.app_name)

        val adminChannel: NotificationChannel
        adminChannel = NotificationChannel(CHANNELID, adminChannelName, NotificationManager.IMPORTANCE_HIGH)
        adminChannel.description = adminChannelDescription
        adminChannel.enableLights(true)
        adminChannel.lightColor = Color.RED
        adminChannel.enableVibration(true)
        notificationManager.createNotificationChannel(adminChannel)
    }
}
