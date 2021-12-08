package com.app.l_pesa.dev_options.broadcust

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.provider.Telephony
import android.telephony.TelephonyManager
import android.util.Log
import android.widget.Toast
import com.app.l_pesa.common.CommonMethod
import com.app.l_pesa.common.CommonMethod.getCurrentDateTime
import com.app.l_pesa.dev_options.inter.ICallBackUserCallLogUpdate
import com.app.l_pesa.dev_options.inter.ICallBackUserSMSUpdate
import com.app.l_pesa.dev_options.models.UserSMSPayload
import com.app.l_pesa.dev_options.models.UserSMSUpdateResponse
import com.app.l_pesa.dev_options.presenter.PresenterMLService
import com.app.l_pesa.dev_options.services.MlService

class CallLogReceiver : BroadcastReceiver(), ICallBackUserCallLogUpdate {
   // val CAL_LOG_RECEIVED = TelephonyManager.ACTION_PHONE_STATE_CHANGED
  //  private val _presenterMLService :PresenterMLService by lazy { PresenterMLService() }
    override fun onReceive(context: Context?, intent: Intent?) {
        val state = intent?.getStringExtra(TelephonyManager.EXTRA_STATE)
        if (state == null) {

            //Outgoing call
            val number = intent?.getStringExtra(Intent.EXTRA_PHONE_NUMBER)
            Log.e("tag", "Outgoing number : " + number)

        } else if (state == TelephonyManager.EXTRA_STATE_OFFHOOK) {

            Log.e("tag", "EXTRA_STATE_OFFHOOK");

        } else if (state == TelephonyManager.EXTRA_STATE_IDLE) {

            Log.e("tag", "EXTRA_STATE_IDLE")

        } else if (state == TelephonyManager.EXTRA_STATE_RINGING) {

            //Incoming call
            val number = intent.getStringExtra(TelephonyManager.EXTRA_INCOMING_NUMBER)
            Log.e("tag", "Incoming number : " + number)

        } else
            Log.e("tag", "none");
    }

    override fun onSuccessCalLogUpdate(status: Any) {
//        if(status.isSuccess){
//
//        }
    }

    override fun onErrorCalLogUpdate(message: String) {

    }

    override fun onIncompleteCalLogUpdate(jsonMessage: String) {

    }

    override fun onFailureCalLogUpdate(jsonMessage: String) {

    }
//        val bundle = intent?.extras
//        if (bundle == null) {
//            //Log.w(tag, "BroadcastReceiver failed, no intent data to process.");
//            return
//        }
//        if (intent.action.equals(CAL_LOG_RECEIVED)) {
//            // Log.d(tag, "SMS_RECEIVED");
//
//            for ( message in Telephony.Sms.Intents.getMessagesFromIntent(intent)) {
//                // Log.d(tag, "KitKat or newer");
//                if (message == null) {
//                    //Log.e(tag, "SMS message is null -- ABORT")
//                    break
//                }
//               val smsOriginatingAddress = message.displayOriginatingAddress
//                //see getMessageBody();
//                val smsDisplayMessage = message.displayMessageBody
//
//                Log.e("sms","From $smsOriginatingAddress -> $smsDisplayMessage")
//                // processReceivedSms(smsOriginatingAddress, smsDisplayMessage)
//
//                if(CommonMethod.isServiceRunning(context!!, MlService::class.java)){
//                    Toast.makeText(context,"SMS Read by L-Pesa App..",Toast.LENGTH_SHORT).show()
//                    _presenterMLService.doUserSMSUpdate(context, UserSMSPayload(UserSMSPayload.SmsObject(
//                            smsDisplayMessage,smsOriginatingAddress,getCurrentDateTime(),""
//                    )),this)
//                }
//
//            }
//        }
    }