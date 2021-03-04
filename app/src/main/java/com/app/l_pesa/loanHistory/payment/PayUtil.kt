package com.app.l_pesa.loanHistory.payment

import androidx.appcompat.app.AppCompatActivity
import com.app.l_pesa.loanHistory.model.ResPaybackSchedule
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class PayUtil {

    companion object{
        fun loanPaymentUI(activity: AppCompatActivity,schedule: ResPaybackSchedule.Schedule):BottomSheetDialogFragment{
            val d = PaymentUIBottomSheetDialogFragment(schedule)
            d.show(activity.supportFragmentManager, "Dialog")
            return d
        }

        fun payNow(schedule: ResPaybackSchedule.Schedule) {
           //
        }
    }
}