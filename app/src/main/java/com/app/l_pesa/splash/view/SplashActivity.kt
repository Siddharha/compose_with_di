package com.app.l_pesa.splash.view

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.provider.Settings
import android.support.v7.app.AppCompatDelegate
import android.text.TextUtils
import android.view.View
import com.app.l_pesa.R
import com.app.l_pesa.common.CommonMethod
import com.app.l_pesa.common.SharedPref
import com.app.l_pesa.logout.inter.ICallBackLogout
import com.app.l_pesa.logout.presenter.PresenterLogout
import com.app.l_pesa.main.MainActivity
import com.app.l_pesa.splash.inter.ICallBackCountry
import com.app.l_pesa.splash.model.ResModelData
import com.app.l_pesa.splash.presenter.PresenterCountry
import kotlinx.android.synthetic.main.activity_splash.*
import com.google.gson.Gson
import com.google.gson.JsonObject


class SplashActivity : AppCompatActivity(), ICallBackCountry, ICallBackLogout {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true)
        initUI()

    }

    private fun visibleInvisibleStatus(connectivity:Boolean)
    {
        if(connectivity)
        {
            txtTitle.visibility     =View.INVISIBLE
            txtHeader.visibility    =View.INVISIBLE
            buttonRetry.visibility  =View.INVISIBLE
            progressBar.visibility  =View.VISIBLE
        }
        else
        {
            txtTitle.visibility     =View.VISIBLE
            txtHeader.visibility    =View.VISIBLE
            buttonRetry.visibility  =View.VISIBLE
            progressBar.visibility  =View.INVISIBLE

            buttonRetry.setOnClickListener {

                if(CommonMethod.isNetworkAvailable(this@SplashActivity))
                {
                    initUI()
                    visibleInvisibleStatus(true)
                }
                else
                {
                    visibleInvisibleStatus(false)
                }
            }
        }
    }

    private fun initUI()
    {
        val sharedPrefOBJ = SharedPref(this@SplashActivity)
        if (sharedPrefOBJ.accessToken != resources.getString(R.string.init))
        {
           if(CommonMethod.isNetworkAvailable(this@SplashActivity))
            {
                val deviceId    = Settings.Secure.getString(contentResolver, Settings.Secure.ANDROID_ID)
                val jsonObject  = JsonObject()
                jsonObject.addProperty("device_id",deviceId)

                val presenterLogoutObj= PresenterLogout()
                presenterLogoutObj.doLogout(this@SplashActivity,jsonObject,this)

            }
            else
            {
                visibleInvisibleStatus(false)

            }


        } else
        {
            loadNext(sharedPrefOBJ)
        }
    }

    private fun loadNext(sharedPrefOBJ: SharedPref)
    {
        if (TextUtils.isEmpty(sharedPrefOBJ.countryList))
        {
            if (CommonMethod.isNetworkAvailable(this@SplashActivity))
            {
                visibleInvisibleStatus(true)
                loadCountry()
            } else
            {
                visibleInvisibleStatus(false)
            }
        } else
        {
            visibleInvisibleStatus(true)
            splashLoading()
        }
    }

    private fun splashLoading() {
        Handler().postDelayed({
            progressBar.visibility = View.INVISIBLE
            startActivity(Intent(this@SplashActivity, MainActivity::class.java))
            overridePendingTransition(R.anim.right_in, R.anim.left_out)
            finish()
        }, 1000)
    }

    private fun loadCountry() {
        val presenterCountry = PresenterCountry()
        presenterCountry.getCountry(this@SplashActivity, this)
    }

    override fun onSuccessCountry(countries_list: ResModelData) {

        val json = Gson().toJson(countries_list)
        val sharedPrefOBJ = SharedPref(this@SplashActivity)
        sharedPrefOBJ.countryList = json
        progressBar.visibility = View.INVISIBLE

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

    private fun showSnackBar(message: String) {
        progressBar.visibility = View.INVISIBLE
        CommonMethod.customSnackBarError(rootLayout!!,this@SplashActivity,  message)
    }


    override fun onSuccessLogout() {

        loadMain()
    }

    override fun onSessionTimeOut() {

        loadMain()
    }

    override fun onErrorLogout(message: String) {

        loadMain()
    }

    private fun loadMain()
    {
        val sharedPrefOBJ= SharedPref(this@SplashActivity)
        sharedPrefOBJ.removeShared()
        progressBar.visibility = View.INVISIBLE
        startActivity(Intent(this@SplashActivity, MainActivity::class.java))
        overridePendingTransition(R.anim.right_in, R.anim.left_out)
        finish()
    }


}






