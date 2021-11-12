package com.app.l_pesa.loanHistory.payment

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import com.app.l_pesa.common.SharedPref
import com.app.l_pesa.loanHistory.model.PayoutPayload
import com.app.l_pesa.loanHistory.model.ResPaybackSchedule
import com.app.l_pesa.loanHistory.presenter.PresenterPayout
import com.app.l_pesa.otpview.model.PinData
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.gson.Gson

class PayUtil {

    companion object{
        fun loanPaymentUI(activity: AppCompatActivity,loanInfo: ResPaybackSchedule.LoanInfo):BottomSheetDialogFragment{
            val d = PaymentUIBottomSheetDialogFragment(loanInfo)
            d.show(activity.supportFragmentManager, "Dialog")
            return d
        }

        fun payNow(context: Context, loanInfo: ResPaybackSchedule.LoanInfo, frg: PaymentUIBottomSheetDialogFragment) {
            //schedule.paidAmount
            val presenterPayout= PresenterPayout()
            val sharedPrefOBJ= SharedPref(context)
            val modelDevice = Gson().fromJson<PinData>(sharedPrefOBJ.deviceInfo, PinData::class.java)
            //"Amount to pay is: "+dataOBJ.loanInfo!!.currencyCode+" "+dataOBJ.loanInfo!!.payfullamount!!.loanAmount.toString()+"\n"+
            //                "Reference number is: "+dataOBJ.loanInfo!!.identityNumber+"\n"+
            //                "L-Pesa Short code is: "+dataOBJ.loanInfo!!.merchantCode.toString()
            val payoutPayload = PayoutPayload(PayoutPayload.PaymentDetails(
                    loanInfo.payfullamount?.loanAmount!!,"${loanInfo.identityNumber}",modelDevice.post_data.phone_no
            ))
            presenterPayout.doPayout(context,payoutPayload,frg)
           //
        }

    }
}