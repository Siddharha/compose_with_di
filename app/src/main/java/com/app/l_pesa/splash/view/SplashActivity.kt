package com.app.l_pesa.splash.view

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.text.TextUtils
import android.view.View
import com.app.l_pesa.R
import com.app.l_pesa.common.CommonMethod
import com.app.l_pesa.common.SharedPref
import com.app.l_pesa.main.MainActivity
import com.app.l_pesa.splash.inter.ICallBackCountry
import com.app.l_pesa.splash.model.ResModelData
import com.app.l_pesa.splash.presenter.PresenterCountry
import kotlinx.android.synthetic.main.activity_splash.*
import com.google.gson.Gson


class SplashActivity : AppCompatActivity(), ICallBackCountry {


    override fun onCreate(savedInstanceState: Bundle?) {
     super.onCreate(savedInstanceState)
     setContentView(R.layout.activity_splash)

        val sharedPrefOBJ=SharedPref(this@SplashActivity)
        if(TextUtils.isEmpty(sharedPrefOBJ.countryList))
        {
            if(CommonMethod.isNetworkAvailable(this@SplashActivity))
            {
                loadCountry()
            }
            else
            {
                showSnackBar(resources.getString(R.string.no_internet))
            }
        }
        else
        {
            splashLoading()
        }

    }

    private fun splashLoading()
    {
        Handler().postDelayed({
            progressBar.visibility          = View.INVISIBLE
            startActivity(Intent(this@SplashActivity, MainActivity::class.java))
            overridePendingTransition(R.anim.right_in, R.anim.left_out)
            finish()
        }, 1000)
    }

    private fun loadCountry()
    {
        val presenterCountry= PresenterCountry()
        presenterCountry.getCountry(this@SplashActivity,this)
    }

    override fun onSuccessCountry(countries_list: ResModelData) {

        val gsonObj         =   Gson()
        val json            =   gsonObj.toJson(countries_list)
        val sharedPrefOBJ   =   SharedPref(this@SplashActivity)
        sharedPrefOBJ.countryList   = json
        progressBar.visibility      = View.INVISIBLE

        startActivity(Intent(this@SplashActivity, MainActivity::class.java))
        overridePendingTransition(R.anim.right_in, R.anim.left_out)
        finish()
    }

    override fun onEmptyCountry() {
        showSnackBar(resources.getString(R.string.no_country))
    }

    override fun onFailureCountry(jsonMessage: String) {
        showSnackBar(jsonMessage)

    }

    private fun showSnackBar(message:String)
    {
        progressBar.visibility=View.INVISIBLE
        CommonMethod.setSnackBar(this@SplashActivity,rootLayout,message)
    }

}
