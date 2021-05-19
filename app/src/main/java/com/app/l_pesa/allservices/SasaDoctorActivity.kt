package com.app.l_pesa.allservices

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import com.app.l_pesa.R
import com.app.l_pesa.allservices.inter.ICallBackSasaUser
import com.app.l_pesa.allservices.models.SasaUserInfoResponse
import com.app.l_pesa.allservices.presenter.PresenterSasaDoctor
import com.app.l_pesa.profile.inter.ICallBackUserInfo
import com.app.l_pesa.profile.model.ResUserInfo
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.activity_sasa_doctor.*

class SasaDoctorActivity : AppCompatActivity(), ICallBackSasaUser {
    private lateinit var presenterSasaDoctor: PresenterSasaDoctor
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sasa_doctor)
        initialize()
        loadUserInfo()
    }

    private fun initialize(){
        presenterSasaDoctor = PresenterSasaDoctor()
    }
    private fun loadUserInfo(){
        presenterSasaDoctor.getUserInfo(this,this)
    }

    override fun onSuccessUserInfo(data: SasaUserInfoResponse.Data) {
        Glide.with(this).load(intent.getStringExtra("logo")).into(imgSasaLogo)
        data.userInfo.apply {
            tvEmail.text = emailAddress
            tvNumber.text = phoneNumber
            if(emailAddressVerify ==1){
                btnLink.setEnable(true)
            }else{
                btnLink.setEnable(false)
            }
        }


    }

    override fun onSessionTimeOut(jsonMessage: String) {

    }

    override fun onErrorUserInfo(jsonMessage: String) {
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
