package com.app.l_pesa.dev_options.broadcust

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import com.app.l_pesa.common.SharedPref
import com.app.l_pesa.dev_options.services.MlService

class ObserverRestarter : BroadcastReceiver() {

    override fun onReceive(context: Context?, intent: Intent?) {

        val serviceIntent = Intent(context, MlService::class.java)

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                context?.startForegroundService( serviceIntent)
            } else {
                context?.startService(serviceIntent)
            }


    }
}