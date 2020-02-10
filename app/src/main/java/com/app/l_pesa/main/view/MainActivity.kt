package com.app.l_pesa.main.view

import android.app.Activity
import android.content.Intent
import android.content.IntentSender
import android.os.Bundle
import android.os.Handler
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.app.l_pesa.R
import com.app.l_pesa.analytics.MyApplication
import com.app.l_pesa.common.CommonMethod.openPrivacyUrl
import com.app.l_pesa.common.CommonMethod.openTermCondition
import com.app.l_pesa.common.SharedPref
import com.app.l_pesa.common.richText
import com.app.l_pesa.login.view.LoginActivity
import com.app.l_pesa.registration.view.RegistrationStepOneActivity
import com.google.android.material.snackbar.Snackbar
import com.google.android.play.core.appupdate.AppUpdateInfo
import com.google.android.play.core.appupdate.AppUpdateManager
import com.google.android.play.core.appupdate.AppUpdateManagerFactory
import com.google.android.play.core.install.InstallStateUpdatedListener
import com.google.android.play.core.install.model.AppUpdateType
import com.google.android.play.core.install.model.InstallStatus
import com.google.android.play.core.install.model.UpdateAvailability
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity() {


    private var appUpdateManager: AppUpdateManager? = null
    private var installStateUpdatedListener: InstallStateUpdatedListener? = null

  /*  private val appUpdateManager: AppUpdateManager by lazy { AppUpdateManagerFactory.create(this) }
    private val appUpdatedListener: InstallStateUpdatedListener by lazy {
        object : InstallStateUpdatedListener {
            override fun onStateUpdate(installState: InstallState) {
                when {
                    installState.installStatus() == InstallStatus.DOWNLOADED -> popupSnackbarForCompleteUpdate()
                    installState.installStatus() == InstallStatus.INSTALLED -> appUpdateManager.unregisterListener(this)

                }
            }
        }
    }*/

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        initUI()
        /*AppUpdater(this@MainActivity)
                .setUpdateFrom(UpdateFrom.GOOGLE_PLAY)
                .setDisplay(Display.DIALOG)
                .showAppUpdated(true)
                .setCancelable(true)
                .start()*/

        checkForAppUpdate()

        onClickTermsPolicy()

    }


    private fun onClickTermsPolicy(){
        val sharedPref = SharedPref(this@MainActivity)
        tvMainTermsCondition.richText(getString(R.string.privacy_term_condition)){
            spannables = listOf(
                    41..61 to { openTermCondition(this@MainActivity, sharedPref.countryCode) },
                    66..80 to { openPrivacyUrl(this@MainActivity, sharedPref.countryCode) }
            )
        }

    }



    private fun initUI()
    {

        buttonLogin.setOnClickListener {

            buttonLogin.isClickable       = false
            val intent = Intent(this@MainActivity, LoginActivity::class.java)
            startActivity(intent)
            overridePendingTransition(R.anim.right_in, R.anim.left_out)

            Handler().postDelayed({
                buttonLogin.isClickable   = true
            }, 1000)


        }

        buttonSignUp.setOnClickListener {

            buttonSignUp.isClickable       = false
            startActivity(Intent(this@MainActivity, RegistrationStepOneActivity::class.java))
            overridePendingTransition(R.anim.right_in, R.anim.left_out)
            Handler().postDelayed({
                buttonSignUp.isClickable   = true
            }, 1000)


        }
    }

    private fun checkForAppUpdate() {
        // Creates instance of the manager.
        appUpdateManager = AppUpdateManagerFactory.create(this)

        // Returns an intent object that you use to check for an update.
        val appUpdateInfoTask = appUpdateManager!!.appUpdateInfo

        // Create a listener to track request state updates.
        installStateUpdatedListener = InstallStateUpdatedListener { installState ->
            // Show module progress, log state, or install the update.
            if (installState.installStatus() == InstallStatus.DOWNLOADED)
            // After the update is downloaded, show a notification
            // and request user confirmation to restart the app.
                popupSnackbarForCompleteUpdateAndUnregister()
        }

        // Checks that the platform will allow the specified type of update.
        appUpdateInfoTask.addOnSuccessListener { appUpdateInfo ->
            if (appUpdateInfo.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE) {
                // Request the update.
                if (appUpdateInfo.isUpdateTypeAllowed(AppUpdateType.FLEXIBLE)) {

                    // Before starting an update, register a listener for updates.
                    appUpdateManager!!.registerListener(installStateUpdatedListener)
                    // Start an update.
                    startAppUpdateFlexible(appUpdateInfo)
                } else if (appUpdateInfo.isUpdateTypeAllowed(AppUpdateType.IMMEDIATE)) {
                    // Start an update.
                    startAppUpdateImmediate(appUpdateInfo)
                }
            }
        }
    }

    private fun startAppUpdateImmediate(appUpdateInfo: AppUpdateInfo) {
        try {
            appUpdateManager!!.startUpdateFlowForResult(
                    appUpdateInfo,
                    AppUpdateType.IMMEDIATE,
                    // The current activity making the update request.
                    this,
                    // Include a request code to later monitor this update request.
                    APP_UPDATE_REQUEST_CODE)
        } catch (e: IntentSender.SendIntentException) {
            e.printStackTrace()
        }

    }

    private fun startAppUpdateFlexible(appUpdateInfo: AppUpdateInfo) {
        try {
            appUpdateManager!!.startUpdateFlowForResult(
                    appUpdateInfo,
                    AppUpdateType.FLEXIBLE,
                    // The current activity making the update request.
                    this,
                    // Include a request code to later monitor this update request.
                    APP_UPDATE_REQUEST_CODE)
        } catch (e: IntentSender.SendIntentException) {
            e.printStackTrace()
            unregisterInstallStateUpdListener()
        }

    }

    public override fun onActivityResult(requestCode: Int, resultCode: Int, intent: Intent?) {
        super.onActivityResult(requestCode, resultCode, intent)

        when (requestCode) {

            APP_UPDATE_REQUEST_CODE -> if (resultCode != Activity.RESULT_OK) { //RESULT_OK / RESULT_CANCELED / RESULT_IN_APP_UPDATE_FAILED
                unregisterInstallStateUpdListener()
            }
        }
    }

    override fun onDestroy() {
        unregisterInstallStateUpdListener()
        super.onDestroy()
    }


    /*private fun checkForAppUpdate() {
        // Returns an intent object that you use to check for an update.
        val appUpdateInfoTask = appUpdateManager.appUpdateInfo

        // Checks that the platform will allow the specified type of update.
        appUpdateInfoTask.addOnSuccessListener { appUpdateInfo ->
            if (appUpdateInfo.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE) {
                // Request the update.
                try {
                    val installType = when {
                        appUpdateInfo.isUpdateTypeAllowed(AppUpdateType.FLEXIBLE) -> AppUpdateType.FLEXIBLE
                        appUpdateInfo.isUpdateTypeAllowed(AppUpdateType.IMMEDIATE) -> AppUpdateType.IMMEDIATE
                        else -> null
                    }
                    if (installType == AppUpdateType.FLEXIBLE) appUpdateManager.registerListener(appUpdatedListener)

                    appUpdateManager.startUpdateFlowForResult(
                            appUpdateInfo,
                            installType!!,
                            this,
                            APP_UPDATE_REQUEST_CODE)
                } catch (e: IntentSender.SendIntentException) {
                    e.printStackTrace()
                }
            }
        }
    }
*/

   /* override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == APP_UPDATE_REQUEST_CODE) {
            if (resultCode != Activity.RESULT_OK) {
                Toast.makeText(this@MainActivity,
                        "App Update failed, please try again on the next app launch.",
                        Toast.LENGTH_SHORT)
                        .show()
            }
        }
    }

    private fun popupSnackbarForCompleteUpdate() {
        val snackbar = Snackbar.make(
                findViewById(R.id.drawer_layout),
                "An update has just been downloaded.",
                Snackbar.LENGTH_INDEFINITE)
        snackbar.setAction("RESTART") { appUpdateManager.completeUpdate() }
        snackbar.setActionTextColor(ContextCompat.getColor(this@MainActivity, R.color.colorApp))
        snackbar.show()
    }*/


    /**
     * Displays the snackbar notification and call to action.
     * Needed only for Flexible app update
     */
    private fun popupSnackbarForCompleteUpdateAndUnregister() {
        val snackbar = Snackbar.make(
                findViewById(R.id.drawer_layout),
                "An update has just been downloaded.",
                Snackbar.LENGTH_INDEFINITE)
        snackbar.setAction("RESTART"){ appUpdateManager!!.completeUpdate() }
        snackbar.setActionTextColor(ContextCompat.getColor(this@MainActivity, R.color.colorApp))
        snackbar.show()
        unregisterInstallStateUpdListener()
    }
    private fun checkNewAppVersionState() {
        appUpdateManager!!
                .appUpdateInfo
                .addOnSuccessListener { appUpdateInfo ->
                    //FLEXIBLE:
                    // If the update is downloaded but not installed,
                    // notify the user to complete the update.
                    if (appUpdateInfo.installStatus() == InstallStatus.DOWNLOADED) {
                        popupSnackbarForCompleteUpdateAndUnregister()
                    }

                    //IMMEDIATE:
                    if (appUpdateInfo.updateAvailability() == UpdateAvailability.DEVELOPER_TRIGGERED_UPDATE_IN_PROGRESS) {
                        // If an in-app update is already running, resume the update.
                        startAppUpdateImmediate(appUpdateInfo)
                    }
                }

    }

    /**
     * Needed only for FLEXIBLE update
     */
    private fun unregisterInstallStateUpdListener() {
        if (appUpdateManager != null && installStateUpdatedListener != null)
            appUpdateManager!!.unregisterListener(installStateUpdatedListener)
    }

    override fun onResume() {
        super.onResume()
       /* appUpdateManager
                .appUpdateInfo
                .addOnSuccessListener { appUpdateInfo ->

                    // If the update is downloaded but not installed,
                    // notify the user to complete the update.
                    if (appUpdateInfo.installStatus() == InstallStatus.DOWNLOADED) {
                        popupSnackbarForCompleteUpdate()
                    }

                    //Check if Immediate update is required
                    try {
                        if (appUpdateInfo.updateAvailability() == UpdateAvailability.DEVELOPER_TRIGGERED_UPDATE_IN_PROGRESS) {
                            // If an in-app update is already running, resume the update.
                            appUpdateManager.startUpdateFlowForResult(
                                    appUpdateInfo,
                                    AppUpdateType.IMMEDIATE,
                                    this,
                                    APP_UPDATE_REQUEST_CODE)
                        }
                    } catch (e: IntentSender.SendIntentException) {
                        e.printStackTrace()
                    }
                }*/

        checkNewAppVersionState()
        MyApplication.getInstance().trackScreenView(this@MainActivity::class.java.simpleName)


    }

    companion object {
        private const val APP_UPDATE_REQUEST_CODE = 530
    }

    override fun onBackPressed() {
        val homeIntent = Intent(Intent.ACTION_MAIN)
        homeIntent.addCategory(Intent.CATEGORY_HOME)
        homeIntent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
        startActivity(homeIntent)
    }


}
