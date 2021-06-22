package com.app.l_pesa.allservices.views

import android.app.Dialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.app.l_pesa.R
import com.app.l_pesa.allservices.inter.ICallBackSasaPayment
import com.app.l_pesa.allservices.inter.ICallBackSasaUser
import com.app.l_pesa.allservices.models.SasaPaymentResponse
import com.app.l_pesa.allservices.models.SasaUserInfoResponse
import com.app.l_pesa.allservices.presenter.PresenterSasaDoctor
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.activity_sasa_doctor.*
import kotlinx.android.synthetic.main.sasa_link_status_dialog.*

class SasaDoctorActivity : AppCompatActivity(), ICallBackSasaUser, ICallBackSasaPayment {
    private lateinit var presenterSasaDoctor: PresenterSasaDoctor
    private lateinit var sasaLinkStatus:SasaUserInfoResponse.Data.ButtonInfo
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sasa_doctor)
        initialize()
        loadUserInfo()
        onActionPerform()
    }

    private fun onActionPerform() {
        srDetails.setOnRefreshListener {
            reloadPage()
        }
        btnLink.setOnClickListener {
            if(sasaLinkStatus.link != "detail") {
                presenterSasaDoctor.doPayment(this, this)
            }else{
                showUserLinkForSasaDialog(sasaLinkStatus.data)
            }
        }

        imgClose.setOnClickListener {
            onBackPressed()
        }
    }

    private fun initialize(){
        presenterSasaDoctor = PresenterSasaDoctor()
    }
    private fun loadUserInfo(){
        presenterSasaDoctor.getUserInfo(this,this)
    }

    override fun onSuccessUserInfo(data: SasaUserInfoResponse.Data) {
        clSasaLink.visibility = View.VISIBLE
        pbLoader.visibility = View.GONE
        Glide.with(this).load(resources.getString(R.string.sasa_logo_url)).into(imgSasaLogo)
        data.userInfo.apply {
            tvEmail.text = emailAddress
            tvNumber.text = phoneNumber
            tvNameValue.text = "${firstName} ${middleName} ${lastName}"
            tvDobValue.text = dob
            tvMeritalValue.text = meritalStatus
            tvSexValue.text = sex
            if(emailAddressVerify ==1){
                btnLink.setEnable(true)
            }else{
                btnLink.setEnable(false)
            }
        }

        data.buttonInfo.apply {
            btnLink.text = text
            sasaLinkStatus = this
        }


    }

    override fun onSessionTimeOut(jsonMessage: String) {
        Toast.makeText(this,jsonMessage,Toast.LENGTH_SHORT).show()
    }

    override fun onErrorUserInfo(jsonMessage: String) {
       Toast.makeText(this,jsonMessage,Toast.LENGTH_SHORT).show()
    }

    override fun onSuccessPayment(data: SasaPaymentResponse.Data) {
       //
       showPaymentForSasaDialog(data)
    }

    private fun showPaymentForSasaDialog(data:SasaPaymentResponse.Data) {
        val alertDialog = AlertDialog.Builder(this)
        alertDialog.apply {
            setCancelable(false)
           /* setMessage("You will receive SMS with payments details. This is your serial number - ${serialNumber}." +
                    " \nThis number will be valid after your successful payment.\nCopy the serial number for farther use.")*/
            setMessage("You will receive SMSs with payments details. " +
                    "\nTo make payment, use pay-bill number: ${data.payBill}, account number: ${data.refNo}. This is your serial number ${data.serialNumber}. " +
                    "\nThis serial number will be valid after your successful payment.")
            setNegativeButton("Dismiss") {d,_->
                reloadPage()
                d.dismiss()
                    }
            create()
            show()
        }
    }

    private fun reloadPage() {
        clSasaLink.visibility = View.GONE
        pbLoader.visibility = View.VISIBLE

        if(srDetails.isRefreshing){
            srDetails.isRefreshing = false
        }
        loadUserInfo()
    }

    private fun showUserLinkForSasaDialog(data:SasaUserInfoResponse.Data.ButtonInfo.Data) {
        val alertDialog = Dialog(this,R.style.MyAlertDialogTheme)
        alertDialog.apply {
            setCancelable(false)

            /* setMessage("You will receive SMS with payments details. This is your serial number - ${serialNumber}." +
                     " \nThis number will be valid after your successful payment.\nCopy the serial number for farther use.")*/
      /*      var message =  "\nSerial ID: ${data.serialNumber}\n"+
                    "\nRef. No: ${data.identityNumber}\n"+
                    "\nPayment Status: ${data.paymentStatus}\n"+
                    "\nActual amount: ${data.actualAmount}\n" +
                    if(data.status != "pending"){"\nValid from: ${data.validFrom}  upto: ${data.validUpto}"}else{""}
            message = message.replace("null","- - -")
            setMessage(message)*/
            setContentView(R.layout.sasa_link_status_dialog)
            tvSlId.text = data.serialNumber
            tvRefNo.text = data.identityNumber
            tvPaymentStatus.text = data.paymentStatus
            tvActualAmount.text = data.actualAmount
            tvValidThrough.text = "from ${data.validFrom} to ${data.validUpto}"

            if(data.status != "pending"){
                tvValidThrough.visibility = View.VISIBLE
                ValidThroughKey.visibility = View.VISIBLE
            }else{
                tvValidThrough.visibility = View.GONE
                ValidThroughKey.visibility = View.GONE
            }
            btnDismiss.setOnClickListener {
                dismiss()
            }

//            setNegativeButton("Dismiss") {d,_->
//                d.dismiss()
//            }
            create()
            show()
        }
    }
    override fun onSessionPaymentTimeOut(jsonMessage: String) {
        //
    }

    override fun onErrorUserPayment(jsonMessage: String) {
       //
    }


}

private fun Button.setEnable(b: Boolean) {
    apply {
      if (!b) {
         alpha = 0.5f
          isEnabled = false
      } else {
          alpha = 1f
          isEnabled = true
      }

    }
}
