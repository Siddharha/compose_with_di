package com.app.l_pesa.sms_retrieval

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import com.google.android.gms.common.api.Status
import com.google.android.gms.auth.api.phone.SmsRetriever
import com.google.android.gms.common.api.CommonStatusCodes

class SMSBroadcastReceiver : BroadcastReceiver() {

    private var otpReceiver:OTPReceiveListener? =null
    fun smsInti(receiver: OTPReceiveListener){
        otpReceiver = receiver
    }


    override fun onReceive(context: Context, intent: Intent) {

        if (SmsRetriever.SMS_RETRIEVED_ACTION == intent.action) {
            val extras = intent.extras
            val status = extras!!.get(SmsRetriever.EXTRA_STATUS) as Status

            when (status.statusCode) {
                CommonStatusCodes.SUCCESS -> {
                    // Get SMS message contents
                    var otp: String = extras.get(SmsRetriever.EXTRA_SMS_MESSAGE) as String
                    Log.d("OTP_Message", otp)
                    // Extract one-time code from the message and complete verification
                    // by sending the code back to your server for SMS authenticity.
                    // But here we are just passing it to MainActivity

                    //otp = /*otp.trim().replace("<#>yourL-PesaVerification Codeis:", "").split("ID: ".toRegex())*//*.dropLastWhile { it.isEmpty() }.toTypedArray()*//*[0]*/
                    otp = otp.trim().split(":")/*.dropLastWhile { it.isEmpty() }.toTypedArray()*/[1].split(".")[0].trim()
                            otpReceiver?.onOTPReceived(otp)


                }

                CommonStatusCodes.TIMEOUT ->
                    // Waiting for SMS timed out (5 minutes)
                    // Handle the error ...

                    otpReceiver?.onOTPTimeOut()

            }
        }
    }




}
