package com.app.l_pesa.loanHistory.payment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.app.l_pesa.R
import com.app.l_pesa.loanHistory.model.ResPaybackSchedule
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import kotlinx.android.synthetic.main.payment_ui_dialog_bottom_sheet.view.*

class PaymentUIBottomSheetDialogFragment(sch: ResPaybackSchedule.Schedule) : BottomSheetDialogFragment() {
    private val schedule = sch
  /*  @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.content_dialog_bottom_sheet, container, false);
        Button btn1 = (Button)v.findViewById(R.id.btn1);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(),YourActivity.class));
            }
        });
        return v;
    }*/

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val v = inflater.inflate(R.layout.payment_ui_dialog_bottom_sheet, container, false)
        loadData(v)
        v.btnPay.setOnClickListener {
            PayUtil.payNow(schedule)
        }
        return v
    }

    private fun loadData(v: View) {
        v.etAmount.setText(schedule.paidAmount.toString())
       // v.tvHistoryID.setText(schedule.loanHistoryId)
    }
}