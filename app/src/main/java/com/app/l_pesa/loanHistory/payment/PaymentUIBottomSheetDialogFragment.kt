package com.app.l_pesa.loanHistory.payment

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.app.l_pesa.R
import com.app.l_pesa.loanHistory.inter.ICallBackPaymentPayout
import com.app.l_pesa.loanHistory.model.ResLoanPayment
import com.app.l_pesa.loanHistory.model.ResPaybackSchedule
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.payment_ui_dialog_bottom_sheet.view.*

class PaymentUIBottomSheetDialogFragment(loanAmount: Double,
                                         loanRefNo:String,
loanCurrencyCode:String,paymentType:String) : BottomSheetDialogFragment(),ICallBackPaymentPayout {
   // private val schedule = sch
    private val loanAmount = loanAmount
    private val loanRefNo = loanRefNo
    private val loanCurrencyCode = loanCurrencyCode
    private val paymentType = paymentType
    private  lateinit var v:View
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
        v = inflater.inflate(R.layout.payment_ui_dialog_bottom_sheet, container, false)
        loadData(v)
        v.btnPay.setOnClickListener {

            AlertDialog.Builder(requireContext())
                    .setMessage("Do you want to pay suggested amount?")
                .setPositiveButton("Pay"){d,_->
                    v.llLoader.visibility = View.VISIBLE
                    PayUtil.payNow(requireContext(),loanAmount,loanRefNo,this)
                    d.dismiss()
                }
                    .setNegativeButton("dismiss"){d,_->
                        d.dismiss()
                    }
                    .create().show()
          //  if(v.etAmount1.isChecked) {

//            }else {
//                AlertDialog.Builder(requireContext())
//                    .setMessage("Please select amount to be paid.")
//                    .setNegativeButton("dismiss"){d,_->
//                        d.dismiss()
//                    }
//                    .create().show()
//              // Toast.makeText(requireContext(),"Please select amount to be paid.",Toast.LENGTH_SHORT).show()
//            }
        }

        v.btnCancel.setOnClickListener {
            dismiss()
        }
        return v
    }

    private fun loadData(v: View) {

        v.tvSc.text = paymentType
        v.etAmount1.text = "$loanCurrencyCode $loanAmount"
       // v.tvHistoryID.setText(schedule.loanHistoryId)
    }

    override fun onSuccessLoanPayment(loanPaymentData: ResLoanPayment.Data) {
        v.llLoader.visibility = View.GONE
        if(loanPaymentData.responseCode =="0") {
            Toast.makeText(requireContext(), loanPaymentData.customerMessage, Toast.LENGTH_SHORT)
                .show()
        }else{
            Toast.makeText(requireContext(), loanPaymentData.customerMessage, Toast.LENGTH_SHORT)
                .show()
        }
        dismiss()
    }

//    override fun onSuccessLoanPayment() {
//        Toast.makeText(requireContext(),"payment successful",Toast.LENGTH_SHORT).show()
//        dismiss()
//    }

    override fun onSessionTimeOut(jsonMessage: String) {
        v.llLoader.visibility = View.GONE
    }

    override fun onErrorLoanPayment(message: String) {
        v.llLoader.visibility = View.GONE
        Toast.makeText(requireContext(),message,Toast.LENGTH_SHORT).show()
    }
}