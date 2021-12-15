package com.app.l_pesa.user_device_data.broadcust

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.telephony.TelephonyManager
import android.widget.Toast
import com.app.l_pesa.common.CommonMethod.getCurrentDateTime
import com.app.l_pesa.user_device_data.inter.ICallBackUserCallLogUpdate
import com.app.l_pesa.user_device_data.models.UserCallLogPayload
import com.app.l_pesa.user_device_data.models.UserCallLogUpdateResponse
import com.app.l_pesa.user_device_data.presenter.PresenterMLService
import java.lang.Exception

class CallLogReceiver : BroadcastReceiver() {

    private var caller_number = ""
   // val CAL_LOG_RECEIVED = TelephonyManager.ACTION_PHONE_STATE_CHANGED
    private val _presenterMLService :PresenterMLService by lazy { PresenterMLService() }

    override fun onReceive(context: Context?, intent: Intent?) {
       try {
           println("Receiver start")
           val state = intent!!.getStringExtra(TelephonyManager.EXTRA_STATE)
           var incomingNumber = intent.getStringExtra(TelephonyManager.EXTRA_INCOMING_NUMBER)

           if(incomingNumber == null){
//               val telephony =
//                   context!!.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
//               telephony.listen(object : PhoneStateListener() {
//                   override fun onCallStateChanged(state: Int, incNumber: String) {
//                       super.onCallStateChanged(state, incNumber)
//                      // println("incomingNumber : $incomingNumber")
//                      // incomingNumber = incNumber
//                       //Log.e("Incoming Number", "Number is ,$incNumber")
//                       //Log.e("State", "State is ,$state")
//                   }
//               }, PhoneStateListener.LISTEN_CALL_STATE)
           }else{
               //Log.e("Incoming Number", "Number is ,$incomingNumber")
              // Log.e("State", "State is ,$state")
           }

           if (state == TelephonyManager.EXTRA_STATE_RINGING) {
              // Toast.makeText(context, "Incoming Call State", Toast.LENGTH_SHORT).show()
//               Toast.makeText(
//                   context,
//                   "Ringing State Number is -$incomingNumber",
//                   Toast.LENGTH_SHORT
//               ).show()

               if(incomingNumber !=null){

                   if(caller_number ==""){
                       caller_number = incomingNumber
                   }
                   _presenterMLService.doUserCallLogUpdate(context!!, UserCallLogPayload(
                       contactName = "",
                       number = caller_number,
                       timeStamp = getCurrentDateTime(),
                       type = ""),object :ICallBackUserCallLogUpdate{
                       override fun onSuccessCalLogUpdate(s: UserCallLogUpdateResponse) {
                           //
                           if (s.status.isSuccess){
                               Toast.makeText(context,s.status.message,Toast.LENGTH_SHORT).show()
                           }
                       }

                       override fun onErrorCalLogUpdate(message: String) {
                           //
                       }

                       override fun onIncompleteCalLogUpdate(jsonMessage: String) {
                           //
                       }

                       override fun onFailureCalLogUpdate(jsonMessage: String) {
                           //
                       }

                   })
               }
           }
           if (state == TelephonyManager.EXTRA_STATE_OFFHOOK) {
               caller_number = ""
               //Toast.makeText(context, "Call Received State", Toast.LENGTH_SHORT).show()
//               Toast.makeText(
//                   context,
//                   "Recived  Number is -$incomingNumber",
//                   Toast.LENGTH_SHORT
//               ).show()
           }
           if (state == TelephonyManager.EXTRA_STATE_IDLE) {
               caller_number = ""
               //Toast.makeText(context, "Call Idle State", Toast.LENGTH_SHORT).show()
           }
       } catch (e: Exception) {
           e.printStackTrace()
       }
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