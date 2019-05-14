package com.app.l_pesa.firebase

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.media.RingtoneManager
import android.os.Build
import android.support.annotation.RequiresApi
import android.support.v4.app.NotificationCompat
import android.support.v4.content.ContextCompat
import com.app.l_pesa.R
import com.app.l_pesa.dashboard.view.DashboardActivity
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import java.net.HttpURLConnection
import java.net.URL
import java.util.*




/**
 * Created by Intellij Amiya on 07-08-2018.
 * A good programmer is someone who looks both ways before crossing a One-way street.
 * Kindly follow https://source.android.com/setup/code-style
 */

class FirebaseMessagingIdService : FirebaseMessagingService() {

    private val NOTIFICATIONID              = "notificationId"
    private val IMAGE_URL_EXTRA             = "attachmentUrl"
    private val ADMIN_CHANNEL_ID            = "admin_channel"
    private var notificationManager: NotificationManager? = null

    override fun onNewToken(s: String?) {


    }

    override fun onMessageReceived(remoteMessage: RemoteMessage?) {


            val notificationIntent = Intent(this, DashboardActivity::class.java)
            notificationIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)

            val pendingIntent= PendingIntent.getActivity(this,0 /* Request code */, notificationIntent,PendingIntent.FLAG_ONE_SHOT)

            val notificationId       = Random().nextInt(60000)
            val bitmap           = getBitmap(remoteMessage!!.data["attachmentUrl"]!!)
            val likeIntent               = Intent(this, DashboardActivity::class.java)

            likeIntent.putExtra(NOTIFICATIONID, notificationId)
            likeIntent.putExtra(IMAGE_URL_EXTRA, remoteMessage.data["attachmentUrl"])

            val likePendingIntent                           = PendingIntent.getService(this, notificationId + 1, likeIntent, PendingIntent.FLAG_ONE_SHOT)
            val defaultSoundUri                             = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
            notificationManager                             = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                setupChannels()
            }

                val notificationBuilder = NotificationCompat.Builder(this, ADMIN_CHANNEL_ID)
                        .setLargeIcon(bitmap)
                        .setColor(ContextCompat.getColor(this,R.color.colorPrimaryDark))
                        .setSmallIcon(R.drawable.lpesa_logo)
                        .setContentTitle(remoteMessage.data["title"])
                        .setStyle(NotificationCompat.BigPictureStyle()
                                .setSummaryText(remoteMessage.data["message"])
                                .bigPicture(bitmap))/*Notification with Image*/
                        .setContentText(remoteMessage.data["message"])
                        .setAutoCancel(true)
                        .setSound(defaultSoundUri)
                        .addAction(R.mipmap.ic_launcher_foreground,
                                getString(R.string.app_name), likePendingIntent)
                        .setContentIntent(pendingIntent)


                notificationManager!!.notify(notificationId, notificationBuilder.build())


    }

    private fun getBitmap(imageUrl: String): Bitmap? {
        return try {
            val url = URL(imageUrl)
            val connection = url.openConnection() as HttpURLConnection
            connection.doInput = true
            connection.connect()
            val input = connection.inputStream
            BitmapFactory.decodeStream(input)

        } catch (e: Exception) {
            e.printStackTrace()
            null
        }

    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private fun setupChannels() {
        val adminChannelName = getString(R.string.app_name)
        val adminChannelDescription = getString(R.string.app_name)

        val adminChannel: NotificationChannel
        adminChannel = NotificationChannel(ADMIN_CHANNEL_ID, adminChannelName, NotificationManager.IMPORTANCE_LOW)
        adminChannel.description = adminChannelDescription
        adminChannel.enableLights(true)
        adminChannel.lightColor = Color.RED
        adminChannel.enableVibration(true)
        if (notificationManager != null) {
            notificationManager!!.createNotificationChannel(adminChannel)
        }
    }
}

