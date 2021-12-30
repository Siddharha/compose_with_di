package com.app.l_pesa.splash.view

import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.provider.Settings
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import com.app.l_pesa.R
import com.app.l_pesa.common.CommonMethod
import com.app.l_pesa.common.SharedPref
import com.app.l_pesa.common.isGooglePlayServicesAvailable
import com.app.l_pesa.common.toast
import com.app.l_pesa.logout.inter.ICallBackLogout
import com.app.l_pesa.logout.presenter.PresenterLogout
import com.app.l_pesa.main.view.MainActivity
import com.app.l_pesa.splash.inter.ICallBackCountry
import com.app.l_pesa.splash.inter.ICallBackVersion
import com.app.l_pesa.splash.model.ResModelData
import com.app.l_pesa.splash.presenter.PresenterCountry
import com.google.gson.Gson
import com.google.gson.JsonObject
import kotlinx.android.synthetic.main.activity_login.*
import kotlinx.android.synthetic.main.activity_splash.*
import kotlinx.android.synthetic.main.activity_splash.rootLayout
import kotlinx.android.synthetic.main.activity_splash.txtHeader
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread


class SplashActivity : AppCompatActivity(), ICallBackCountry, ICallBackLogout {


    lateinit var sharedPrefOBJ:SharedPref
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true)
        initialize()
        //initUI()


    }

    private fun initialize(){
        sharedPrefOBJ = SharedPref(this)
    }

    override fun onResume() {
        super.onResume()
        if(isGooglePlayServicesAvailable(this)){
            checkVersion()
        }
    }



    private fun initUI() {
       // val sharedPrefOBJ = SharedPref(this@SplashActivity)

        if (sharedPrefOBJ.accessToken != resources.getString(R.string.init)) {
            if (CommonMethod.isNetworkAvailable(this@SplashActivity)) {
                progressBar.visibility = View.VISIBLE
                logoutProcess()
            } else {
                llAlert.visibility = View.VISIBLE
                buttonRetry.visibility = View.VISIBLE
                progressBar.visibility = View.INVISIBLE

                buttonRetry.setOnClickListener {
                    if (CommonMethod.isNetworkAvailable(this@SplashActivity)) {
                        logoutProcess()
                    } else {
                        CommonMethod.customSnackBarError(rootLayout!!, this@SplashActivity, resources.getString(R.string.no_internet))
                    }
                }
            }

        } else {
            loadNext()
        }


    }

    private fun logoutProcess() {
        buttonRetry.visibility = View.INVISIBLE
        llAlert.visibility = View.INVISIBLE
        //val deviceId = Settings.Secure.getString(contentResolver, Settings.Secure.ANDROID_ID)

        var deviceId = ""
        if (sharedPrefOBJ.uuid.isNotEmpty()) {
            deviceId = sharedPrefOBJ.uuid
            val jsonObject = JsonObject()
            jsonObject.addProperty("device_id", deviceId)

            val presenterLogoutObj = PresenterLogout()
            presenterLogoutObj.doLogout(this@SplashActivity, jsonObject, this)

        }else{
            loadMain()
        }

    }

    private fun loadNext() {

     //   val sharedPrefOBJ = SharedPref(this@SplashActivity)
        if (sharedPrefOBJ.countryList == resources.getString(R.string.init) || sharedPrefOBJ.countryList.isEmpty()) {
            if (CommonMethod.isNetworkAvailable(this@SplashActivity)) {
                loadCountry()
            } else {
                txtTitle.visibility = View.VISIBLE
                txtHeader.visibility = View.VISIBLE
                buttonRetry.visibility = View.VISIBLE
                llAlert.visibility = View.VISIBLE
                progressBar.visibility = View.INVISIBLE

                buttonRetry.setOnClickListener {

                    if (CommonMethod.isNetworkAvailable(this@SplashActivity)) {
                        loadCountry()
                    } else {
                        CommonMethod.customSnackBarError(rootLayout!!, this@SplashActivity, resources.getString(R.string.no_internet))
                    }

                }
            }

        } else {
            progressBar.visibility = View.VISIBLE
            splashLoading()
        }


    }

    private fun splashLoading() {
        Handler(Looper.myLooper()!!).postDelayed({
            startActivity(Intent(this@SplashActivity, MainActivity::class.java))
            overridePendingTransition(R.anim.fade_in, R.anim.fade_out)
            finish()
        }, CommonMethod.splashTime())
    }

    private fun loadCountry() {

        txtTitle.visibility = View.INVISIBLE
        txtHeader.visibility = View.INVISIBLE
        buttonRetry.visibility = View.INVISIBLE
        llAlert.visibility = View.INVISIBLE
        progressBar.visibility = View.VISIBLE
        val presenterCountry = PresenterCountry()
            presenterCountry.getCountry(this@SplashActivity, this@SplashActivity)




    }

    override fun onSuccessCountry(countries_list: ResModelData) {
        val json = Gson().toJson(countries_list)
    //    val sharedPrefOBJ = SharedPref(this@SplashActivity)
        //sharedPrefOBJ.countryList = ""    //TEST CLEAR
        sharedPrefOBJ.countryList = json
        progressBar.visibility = View.INVISIBLE

        startActivity(Intent(this@SplashActivity, MainActivity::class.java))
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out)
        finish()
    }

    override fun onEmptyCountry() {
        showSnackBar(resources.getString(R.string.no_country))
        buttonRetry.visibility = View.VISIBLE
        llAlert.visibility = View.VISIBLE
        progressBar.visibility = View.INVISIBLE
    }

    override fun onFailureCountry(jsonMessage: String) {
        showSnackBar(jsonMessage)
        buttonRetry.visibility = View.VISIBLE
        llAlert.visibility = View.VISIBLE
        progressBar.visibility = View.INVISIBLE
        buttonRetry.setOnClickListener {

            if (CommonMethod.isNetworkAvailable(this@SplashActivity)) {
                loadCountry()
            } else {
                CommonMethod.customSnackBarError(rootLayout!!, this@SplashActivity, resources.getString(R.string.no_internet))
            }

        }

    }

    private fun showSnackBar(message: String) {
        progressBar.visibility = View.INVISIBLE
        CommonMethod.customSnackBarError(rootLayout!!, this@SplashActivity, message)
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

    private fun loadMain() {
        sharedPrefOBJ.removeShared()
        progressBar.visibility = View.INVISIBLE
        startActivity(Intent(this@SplashActivity, MainActivity::class.java))
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out)
        finish()
    }

    private fun checkVersion() {
        //
        if (CommonMethod.isNetworkAvailable(this@SplashActivity)) {
            checkingVersion()
        } else {
            txtTitle.visibility = View.VISIBLE
            txtHeader.visibility = View.VISIBLE
            buttonRetry.visibility = View.VISIBLE
            llAlert.visibility = View.VISIBLE
            progressBar.visibility = View.INVISIBLE

            buttonRetry.setOnClickListener {
                if (CommonMethod.isNetworkAvailable(this@SplashActivity)) {
                    checkingVersion()
                } else {
                    CommonMethod.customSnackBarError(rootLayout!!, this@SplashActivity, resources.getString(R.string.no_internet))
                }

            }
        }


    }

    private fun checkingVersion(){
        //
        txtTitle.visibility = View.INVISIBLE
        txtHeader.visibility = View.INVISIBLE
        buttonRetry.visibility = View.INVISIBLE
        llAlert.visibility = View.INVISIBLE
        //
        var version = ""
        try {
            val pInfo = this.packageManager.getPackageInfo(this.packageName, 0)
            version = pInfo.versionName.replace(".","")
        } catch ( e: PackageManager.NameNotFoundException) {
            e.printStackTrace()
        }
        val jsonObject = JsonObject()
        jsonObject.addProperty("version", version)
        progressBar.visibility = View.VISIBLE
        PresenterCountry().checkVersion(this@SplashActivity, jsonObject, object : ICallBackVersion {
            override fun onResponse(status: Boolean) {
                progressBar.visibility = View.INVISIBLE
                if (status) {
                    //"$status".toast(this@SplashActivity)

                    sharedPrefOBJ.countryList = resources.getString(R.string.init) //removing country list --->Test
                    initUI()
                } else {
                    // "$status".toast(this@SplashActivity)
                    val dialog = AlertDialog.Builder(this@SplashActivity, R.style.MyAlertDialogTheme)
                    dialog.setCancelable(false)
                    dialog.setTitle("Update Available")
                    dialog.setMessage("New Version available in Play Store.")
                            .setPositiveButton("Update Now") { _, _ ->
                                goToPlayStore()
                            }
                            .setNegativeButton("Cancel") { _, _ -> finish() }
                    dialog.show()
                }
            }

            override fun onFailureCountry(jsonMessage: String) {
                progressBar.visibility = View.INVISIBLE
                CommonMethod.customSnackBarError(rootLayout!!, this@SplashActivity, jsonMessage)
            }

        })
    }

    private fun goToPlayStore() {
        val i = Intent(Intent.ACTION_VIEW)
        i.data = Uri.parse("https://play.google.com/store/apps/details?id=com.app.l_pesa")
        startActivity(i)
        finish()
    }


}






