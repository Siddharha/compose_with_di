package com.app.l_pesa.dev_options.services

import android.Manifest
import android.annotation.TargetApi
import android.app.*
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.graphics.Color
import android.os.Build
import android.os.IBinder
import android.os.Looper
import android.provider.Telephony
import android.telephony.TelephonyManager
import android.util.Log
import android.widget.Toast
import androidx.annotation.NonNull
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.app.l_pesa.R
import com.app.l_pesa.common.CommonMethod.getCurrentDateTime
import com.app.l_pesa.dev_options.broadcust.CallLogReceiver
import com.app.l_pesa.dev_options.broadcust.ObserverRestarter
import com.app.l_pesa.dev_options.broadcust.SMSreceiver
import com.app.l_pesa.dev_options.inter.ICallBackUserLocationUpdate
import com.app.l_pesa.dev_options.models.UserLocationPayload
import com.app.l_pesa.dev_options.models.UserLocationUpdateResponse
import com.app.l_pesa.dev_options.presenter.PresenterMLService
import com.app.l_pesa.splash.view.SplashActivity
import com.google.android.gms.location.*

class MlService : Service(), ICallBackUserLocationUpdate {
    private val NOTIFICATION_ID = 1
    private var NOTIFICATION_CHANNEL_ID = ""
    private var isRunning:Boolean = false
    private val mFusedLocationClient:FusedLocationProviderClient by lazy{ LocationServices.getFusedLocationProviderClient(this)}
    private val presenterMLService:PresenterMLService by lazy { PresenterMLService() }

    private val mSMSreceiver by lazy { SMSreceiver() }
    private val mCallLogReceiver by lazy { CallLogReceiver() }

    override fun onCreate() {
        super.onCreate()
        initialize()
        smsBroadcast()
        callLogBroadcast()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
            NOTIFICATION_CHANNEL_ID = createChannel()
        else {
            NOTIFICATION_CHANNEL_ID = ""
        }

    }

    private fun initialize(){

    }




    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        locationService()
        showServiceNotification("L-Pesa service")
        isRunning = true
        // startForeground(LOCATION_SERVICE_ID,)
        return(START_STICKY)
    }

    private fun smsBroadcast() {
        val mIntentFilter = IntentFilter(Telephony.Sms.Intents.SMS_RECEIVED_ACTION)
       // mIntentFilter.addAction("android.provider.Telephony.SMS_RECEIVED")
        registerReceiver(mSMSreceiver,mIntentFilter )
    }
    private fun callLogBroadcast() {
        registerReceiver(mCallLogReceiver, IntentFilter(TelephonyManager.ACTION_PHONE_STATE_CHANGED))
    }

    private fun locationService() {
        val locationReq = LocationRequest.create().apply {
            interval = 4000
            fastestInterval = 2000
            smallestDisplacement = 150f
            priority = LocationRequest.PRIORITY_HIGH_ACCURACY
            /*maxWaitTime= 100*/
        }
        /*LocationRequest()
        locationReq.apply {
            interval = 4000
            fastestInterval = 2000
            priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        }*/

        if (ActivityCompat.checkSelfPermission(
                        this,
                        Manifest.permission.ACCESS_FINE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                        this,
                        Manifest.permission.ACCESS_COARSE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED
        ) {

        }
        mFusedLocationClient.requestLocationUpdates(locationReq, locationCallback, Looper.getMainLooper())
    }

    private fun showServiceNotification(notificationTitle:String) {
        val i= Intent(this, SplashActivity::class.java)

        i.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP

        val pi= PendingIntent.getActivity(this, 0,
                i, 0)
          var notification: Notification?=null
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
          /*  val name = notificationTitle
            val descriptionText = "This service will pull user location, sms, emails and help user suggest better loan by analyzing these data without storing theme on server."
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val mChannel = NotificationChannel(NOTIFICATION_CHANNEL_ID, name, importance)
            mChannel.description = descriptionText
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(mChannel)*/

            var builder = NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID)
                    .setSmallIcon(R.drawable.lpesa_logo)
                    .setContentTitle(notificationTitle)
                    .setContentIntent(pi)
                    .setContentText(resources.getString(R.string.ml_service_notification_desc))
                    .setPriority(NotificationCompat.PRIORITY_DEFAULT)

            with(NotificationManagerCompat.from(this)) {
                // notificationId is a unique int for each notification that you must define
                startForeground(NOTIFICATION_ID, builder.build())
            }
        }else{
            notification = Notification.Builder(this)
                    .setAutoCancel(true)
                    .setContentTitle(notificationTitle)
                    .setContentText(resources.getString(R.string.ml_service_notification_desc))
                    .setContentIntent(pi)
                    .setSmallIcon(R.drawable.lpesa_logo)
                    .setWhen(System.currentTimeMillis())
                    .build()

            startForeground(NOTIFICATION_ID, notification)
        }


    }

    override fun onBind(intent: Intent?): IBinder? {
        //
        return null
    }


    private val locationCallback = object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult) {
            val lat = locationResult.lastLocation.latitude
            val lon = locationResult.lastLocation.longitude
            Toast.makeText(applicationContext, "lat: $lat, lon: $lon", Toast.LENGTH_SHORT).show()
              // Log.e("respo","${lat}, ${lon}")
            presenterMLService.doUserLocationUpdate(
                    this@MlService,
                    UserLocationPayload(getCurrentDateTime(),
                    lat.toString(),lon.toString()),
                    this@MlService)
            //(this as MainActivity).getLocation(locationResult)
           /* mainRepo.updateLocationData(locationResult.lastLocation)*/
            super.onLocationResult(locationResult)
        }

    }

    override fun onDestroy() {
        super.onDestroy()
            unregisterReceiver(mSMSreceiver)
        unregisterReceiver(mCallLogReceiver)

                // sendServiceBroadcast()
        mFusedLocationClient.removeLocationUpdates(locationCallback)
        stop()
    }


    private fun sendServiceBroadcast() {
        val broadcastIntent = Intent()
        broadcastIntent.action = "restartservice"
        broadcastIntent.setClass(this, ObserverRestarter::class.java)
        this.sendBroadcast(broadcastIntent)
    }

    private fun stop() {

        if (isRunning) {
            // Log.w(getClass().getName(), "Got to stop()!");
            isRunning=false

            stopForeground(true)
            stopSelf()
            //pref.setBoolSession(IS_PLAYING,false)
        }

    }

    override fun onSuccessLocationUpdate(status: UserLocationUpdateResponse.Status) {
        if(status.isSuccess){
         Log.e("respo","${status.message}")
            Toast.makeText(applicationContext, status.message, Toast.LENGTH_SHORT).show()
        }
    }

    override fun onErrorLocationUpdate(message: String) {
    }

    override fun onIncompleteLocationUpdate(jsonMessage: String) {
    }

    override fun onFailureLocationUpdate(jsonMessage: String) {
    }
    

    @NonNull
    @TargetApi(26)
    private fun createChannel() : String{
        val mNotificationManager =  this.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        val name = "snap map fake location "
        val importance = NotificationManager.IMPORTANCE_LOW

        val mChannel =  NotificationChannel("snap map channel", name, importance)

        mChannel.enableLights(true)
        mChannel.lightColor = Color.BLUE
        if (mNotificationManager != null) {
            mNotificationManager.createNotificationChannel(mChannel)
        } else {
            stopSelf()
        }
        return "snap map channel"
    }
}