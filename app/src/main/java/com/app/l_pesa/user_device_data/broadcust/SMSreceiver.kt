package com.app.l_pesa.user_device_data.broadcust

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.provider.Telephony
import android.util.Log
import android.widget.Toast
import com.app.l_pesa.common.CommonMethod
import com.app.l_pesa.common.CommonMethod.getCurrentDateTime
import com.app.l_pesa.user_device_data.inter.ICallBackUserSMSUpdate
import com.app.l_pesa.user_device_data.models.UserSMSPayload
import com.app.l_pesa.user_device_data.models.UserSMSUpdateResponse
import com.app.l_pesa.user_device_data.presenter.PresenterMLService
import com.app.l_pesa.user_device_data.services.MlService

class SMSreceiver : BroadcastReceiver(), ICallBackUserSMSUpdate {
    val SMS_RECEIVED = "android.provider.Telephony.SMS_RECEIVED"
    private val _presenterMLService :PresenterMLService by lazy { PresenterMLService() }
    override fun onReceive(context: Context?, intent: Intent?) {
        // final String tag = TAG + ".onReceive";

        val bundle = intent?.extras
        if (bundle == null) {
            //Log.w(tag, "BroadcastReceiver failed, no intent data to process.");
            return
        }
        if (intent.action.equals(SMS_RECEIVED)) {
             Log.d("sms", "SMS_RECEIVED");

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

                if(CommonMethod.isServiceRunning(context!!, MlService::class.java)){
                    Toast.makeText(context,"SMS Read by L-Pesa App..",Toast.LENGTH_SHORT).show()
                    _presenterMLService.doUserSMSUpdate(context, UserSMSPayload(UserSMSPayload.SmsObject(
                            smsDisplayMessage,smsOriginatingAddress,getCurrentDateTime(),""
                    )),this)
                }

            }
        }
    }

    override fun onSuccessSMSUpdate(status: UserSMSUpdateResponse.Status) {
        if(status.isSuccess){

        }
    }

    override fun onErrorSMSUpdate(message: String) {

    }

    override fun onIncompleteSMSUpdate(jsonMessage: String) {

    }

    override fun onFailureSMSUpdate(jsonMessage: String) {

    }
}