package com.app.l_pesa.splash.view

import android.content.Intent
import android.content.res.Resources
import android.os.Bundle
import android.os.Handler
import android.provider.Settings
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import android.view.View
import com.app.l_pesa.R
import com.app.l_pesa.common.CommonMethod
import com.app.l_pesa.common.SharedPref
import com.app.l_pesa.logout.inter.ICallBackLogout
import com.app.l_pesa.logout.presenter.PresenterLogout
import com.app.l_pesa.main.view.MainActivity
import com.app.l_pesa.splash.inter.ICallBackCountry
import com.app.l_pesa.splash.model.ResModelData
import com.app.l_pesa.splash.presenter.PresenterCountry
import com.google.gson.Gson
import com.google.gson.JsonObject
import kotlinx.android.synthetic.main.activity_splash.*
import android.view.animation.AnimationSet
import android.view.animation.AnimationUtils
import android.view.animation.TranslateAnimation
import android.view.animation.ScaleAnimation



class SplashActivity : AppCompatActivity(), ICallBackCountry, ICallBackLogout {


    private lateinit  var animationSet: AnimationSet

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true)

        val viewWidth = view_progress.getWidth()
        val move = TranslateAnimation((-(getScreenWidth() / 2) + viewWidth / 2).toFloat(), (getScreenWidth() / 2 + viewWidth / 2 + viewWidth).toFloat(), 0f, 0f)
        move.setDuration(1000);
        val move1 = TranslateAnimation((-viewWidth).toFloat(), 0f, 0f, 0f)
        move1.setDuration(500);
        val laftOut = ScaleAnimation(0f, 1f, 1f, 1f)
        laftOut.duration = 500


        animationSet = AnimationSet(true);
        animationSet.addAnimation(move);
        animationSet.addAnimation(move1);
        animationSet.addAnimation(laftOut);
        animationSet.addAnimation(AnimationUtils.loadAnimation(getApplicationContext(), R.anim.slideout));

        startAnimation()
        initUI()

    }

    private fun startAnimation() {
        view_progress.startAnimation(animationSet)
        android.os.Handler().postDelayed({ startAnimation() }, 1000)
    }

    fun getScreenWidth(): Int {
        return Resources.getSystem().getDisplayMetrics().widthPixels
    }


    private fun initUI()
    {
        val sharedPrefOBJ = SharedPref(this@SplashActivity)
        if (sharedPrefOBJ.accessToken != resources.getString(com.app.l_pesa.R.string.init))
        {
            if(CommonMethod.isNetworkAvailable(this@SplashActivity))
            {
               frameProgress.visibility = View.VISIBLE
               logoutProcess()

            }
            else {
                buttonRetry.visibility = View.VISIBLE
                frameProgress.visibility = View.INVISIBLE

                buttonRetry.setOnClickListener {

                    if (CommonMethod.isNetworkAvailable(this@SplashActivity))
                    {
                        logoutProcess()
                    } else
                    {
                        CommonMethod.customSnackBarError(rootLayout!!,this@SplashActivity,  resources.getString(com.app.l_pesa.R.string.no_internet))
                    }

                }
            }

        }
        else
        {
            loadNext()
        }


    }

    private fun logoutProcess()
    {
        buttonRetry.visibility = View.INVISIBLE
        val deviceId    = Settings.Secure.getString(contentResolver, Settings.Secure.ANDROID_ID)
        val jsonObject  = JsonObject()
        jsonObject.addProperty("device_id",deviceId)

        val presenterLogoutObj= PresenterLogout()
        presenterLogoutObj.doLogout(this@SplashActivity,jsonObject,this)
    }

    private fun loadNext()
    {

        val sharedPrefOBJ = SharedPref(this@SplashActivity)
        if (sharedPrefOBJ.countryList==resources.getString(com.app.l_pesa.R.string.init))
        {
            if(CommonMethod.isNetworkAvailable(this@SplashActivity))
            {
                loadCountry()
            }
            else {
                txtTitle.visibility = View.VISIBLE
                txtHeader.visibility = View.VISIBLE
                buttonRetry.visibility = View.VISIBLE
                frameProgress.visibility = View.INVISIBLE

                buttonRetry.setOnClickListener {

                    if (CommonMethod.isNetworkAvailable(this@SplashActivity))
                    {
                        loadCountry()
                    } else
                    {
                        CommonMethod.customSnackBarError(rootLayout!!,this@SplashActivity,  resources.getString(com.app.l_pesa.R.string.no_internet))
                    }

                }
            }

        }
        else
        {
            frameProgress.visibility = View.VISIBLE
            splashLoading()
        }


    }

    private fun splashLoading() {
        Handler().postDelayed({
            startActivity(Intent(this@SplashActivity, MainActivity::class.java))
            overridePendingTransition(com.app.l_pesa.R.anim.right_in, com.app.l_pesa.R.anim.left_out)
            finish()
        }, 2000)
    }

    private fun loadCountry() {

        txtTitle.visibility = View.INVISIBLE
        txtHeader.visibility = View.INVISIBLE
        buttonRetry.visibility = View.INVISIBLE
        frameProgress.visibility = View.VISIBLE
        val presenterCountry = PresenterCountry()
        presenterCountry.getCountry(this@SplashActivity, this)
    }

    override fun onSuccessCountry(countries_list: ResModelData) {

        val json = Gson().toJson(countries_list)
        val sharedPrefOBJ = SharedPref(this@SplashActivity)
        sharedPrefOBJ.countryList = json
        frameProgress.visibility = View.INVISIBLE

        startActivity(Intent(this@SplashActivity, MainActivity::class.java))
        overridePendingTransition(com.app.l_pesa.R.anim.right_in, com.app.l_pesa.R.anim.left_out)
        finish()
    }

    override fun onEmptyCountry() {
        showSnackBar(resources.getString(com.app.l_pesa.R.string.no_country))
        buttonRetry.visibility  =View.VISIBLE
        frameProgress.visibility  =View.INVISIBLE
    }

    override fun onFailureCountry(jsonMessage: String) {
        showSnackBar(jsonMessage)
        buttonRetry.visibility  =View.VISIBLE
        frameProgress.visibility  =View.INVISIBLE
        buttonRetry.setOnClickListener {

            if (CommonMethod.isNetworkAvailable(this@SplashActivity))
            {
                loadCountry()
            } else
            {
                CommonMethod.customSnackBarError(rootLayout!!,this@SplashActivity,  resources.getString(com.app.l_pesa.R.string.no_internet))
            }

        }

    }

    private fun showSnackBar(message: String) {
        frameProgress.visibility = View.INVISIBLE
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
        frameProgress.visibility = View.INVISIBLE
        startActivity(Intent(this@SplashActivity, MainActivity::class.java))
        overridePendingTransition(com.app.l_pesa.R.anim.right_in, com.app.l_pesa.R.anim.left_out)
        finish()
    }


}






