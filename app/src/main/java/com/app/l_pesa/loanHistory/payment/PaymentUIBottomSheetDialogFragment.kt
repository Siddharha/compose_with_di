package com.app.l_pesa.loanHistory.payment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.app.l_pesa.R
import com.app.l_pesa.loanHistory.inter.ICallBackPaymentPayout
import com.app.l_pesa.loanHistory.model.ResPaybackSchedule
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.payment_ui_dialog_bottom_sheet.view.*

class PaymentUIBottomSheetDialogFragment(loanInfo: ResPaybackSchedule.LoanInfo) : BottomSheetDialogFragment(),ICallBackPaymentPayout {
   // private val schedule = sch
    private val loanInfo = loanInfo
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

            if(v.etAmount1.isChecked) {
                PayUtil.payNow(requireContext(),loanInfo,this)
            }else {
               Toast.makeText(requireContext(),"Please select amount to be paid.",Toast.LENGTH_SHORT).show()
            }
        }

        v.btnCancel.setOnClickListener {
            dismiss()
        }
        return v
    }

    private fun loadData(v: View) {
        v.etAmount1.text = loanInfo.payfullamount?.loanAmount.toString()
       // v.tvHistoryID.setText(schedule.loanHistoryId)
    }

    override fun onSuccessLoanPayment() {
        Toast.makeText(requireContext(),"payment successful",Toast.LENGTH_SHORT).show()
        dismiss()
    }

    override fun onSessionTimeOut(jsonMessage: String) {

    }

    override fun onErrorLoanPayment(message: String) {
        Toast.makeText(requireContext(),message,Toast.LENGTH_SHORT).show()
    }
}