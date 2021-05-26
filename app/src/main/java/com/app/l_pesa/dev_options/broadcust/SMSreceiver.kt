package com.app.l_pesa.dev_options.broadcust

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.provider.Telephony
import android.util.Log

class SMSreceiver : BroadcastReceiver() {
    val SMS_RECEIVED = "android.provider.Telephony.SMS_RECEIVED"
    override fun onReceive(context: Context?, intent: Intent?) {
        // final String tag = TAG + ".onReceive";
        val bundle = intent?.extras
        if (bundle == null) {
            //Log.w(tag, "BroadcastReceiver failed, no intent data to process.");
            return
        }
        if (intent.action.equals(SMS_RECEIVED)) {
            // Log.d(tag, "SMS_RECEIVED");

            for ( message in Telephony.Sms.Intents.getMessagesFromIntent(intent)) {
                // Log.d(tag, "KitKat or newer");
                if (message == null) {
                    //Log.e(tag, "SMS message is null -- ABORT")
                    break
                }
               val smsOriginatingAddress = message.displayOriginatingAddress
                //see getMessageBody();
                val smsDisplayMessage = message.displayMessageBody

                Log.e("sms","From $smsOriginatingAddress -> $smsDisplayMessage")
               // processReceivedSms(smsOriginatingAddress, smsDisplayMessage)
            }
        }
    }
}