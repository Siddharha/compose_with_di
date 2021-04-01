package com.app.l_pesa.loanHistory.payment

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import com.app.l_pesa.loanHistory.model.PayoutPayload
import com.app.l_pesa.loanHistory.model.ResPaybackSchedule
import com.app.l_pesa.loanHistory.presenter.PresenterPayout
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class PayUtil {

    companion object{
        fun loanPaymentUI(activity: AppCompatActivity, schedule: ResPaybackSchedule.Schedule, loanInfo: ResPaybackSchedule.LoanInfo):BottomSheetDialogFragment{
            val d = PaymentUIBottomSheetDialogFragment(schedule,loanInfo)
            d.show(activity.supportFragmentManager, "Dialog")
            return d
        }

        fun payNow(context: Context, schedule: ResPaybackSchedule.Schedule, loanInfo: ResPaybackSchedule.LoanInfo, frg: PaymentUIBottomSheetDialogFragment) {
            //schedule.paidAmount
            val presenterPayout= PresenterPayout()

            val payoutPayload = PayoutPayload(PayoutPayload.PaymentDetails(
                    schedule.paidAmount,"BILL_PAID_${loanInfo.identityNumber}",loanInfo.identityNumber
            ))
            presenterPayout.doPayout(context,payoutPayload,frg)
           //
        }

    }
}