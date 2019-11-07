package com.app.l_pesa.dashboard.view

import android.annotation.SuppressLint
import android.app.Activity
import android.app.ProgressDialog
import android.content.Intent
import android.content.IntentSender
import android.graphics.Typeface
import android.os.Bundle
import android.os.CountDownTimer
import android.os.Handler
import android.provider.Settings
import android.text.Spannable
import android.text.SpannableString
import android.text.TextUtils
import android.text.style.RelativeSizeSpan
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.core.view.GravityCompat
import com.app.l_pesa.BuildConfig
import com.app.l_pesa.R
import com.app.l_pesa.analytics.MyApplication
import com.app.l_pesa.calculator.view.LoanCalculatorFragment
import com.app.l_pesa.common.*
import com.app.l_pesa.investment.view.InvestmentFragment
import com.app.l_pesa.loanHistory.view.LoanHistoryListActivity
import com.app.l_pesa.loanplan.view.LoanPlansFragment
import com.app.l_pesa.logout.inter.ICallBackLogout
import com.app.l_pesa.logout.presenter.PresenterLogout
import com.app.l_pesa.lpk.view.LpkFragment
import com.app.l_pesa.main.view.MainActivity
import com.app.l_pesa.pinview.model.LoginData
import com.app.l_pesa.points.view.PointsFragment
import com.app.l_pesa.profile.view.ProfileFragment
import com.app.l_pesa.settings.view.SettingsFragment
import com.app.l_pesa.wallet.view.WalletFragment
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.google.android.material.navigation.NavigationView
import com.google.android.material.snackbar.Snackbar
import com.google.android.play.core.appupdate.AppUpdateManager
import com.google.android.play.core.appupdate.AppUpdateManagerFactory
import com.google.android.play.core.install.InstallState
import com.google.android.play.core.install.InstallStateUpdatedListener
import com.google.android.play.core.install.model.AppUpdateType
import com.google.android.play.core.install.model.InstallStatus
import com.google.android.play.core.install.model.UpdateAvailability
import com.google.gson.Gson
import com.google.gson.JsonObject
import kotlinx.android.synthetic.main.activity_dashboard.*
import kotlinx.android.synthetic.main.app_bar_main.*


class DashboardActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener, ICallBackLogout {


    private var doubleBackStatus = false
    private lateinit var progressDialog: ProgressDialog
    private lateinit var countDownTimer: CountDownTimer

    private val appUpdateManager: AppUpdateManager by lazy { AppUpdateManagerFactory.create(this) }
    private val appUpdatedListener: InstallStateUpdatedListener by lazy {
        object : InstallStateUpdatedListener {
            override fun onStateUpdate(installState: InstallState) {
                when {
                    installState.installStatus() == InstallStatus.DOWNLOADED -> popupSnackbarForCompleteUpdate()
                    installState.installStatus() == InstallStatus.INSTALLED -> appUpdateManager.unregisterListener(this)

                }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dashboard)

        toolbar.title = resources.getString(R.string.nav_item_dashboard)
        setSupportActionBar(toolbar)

        checkForAppUpdate()
        initData()
        initMenu()
        initLoader()
        initFragment()
        initToggle()
        nav_view.setNavigationItemSelectedListener(this)
        initTimer()
    }

    private fun initTimer() {

        countDownTimer= object : CountDownTimer(CommonMethod.sessionTime().toLong(), 1000) {
            override fun onTick(millisUntilFinished: Long) {

            }
            override fun onFinish() {
                onSessionTimeOut(resources.getString(R.string.session_time_out))
                countDownTimer.cancel()

            }}
        countDownTimer.start()

    }


    override fun onUserInteraction() {
    super.onUserInteraction()

        countDownTimer.cancel()
        countDownTimer.start()
    }


    fun onSessionTimeOut(message: String) {

        val dialogBuilder = AlertDialog.Builder(this@DashboardActivity, R.style.MyAlertDialogTheme)
        dialogBuilder.setMessage(message)
                .setCancelable(false)
                .setPositiveButton("Ok") { dialog, _ ->
                    dialog.dismiss()
                    val sharedPrefOBJ= SharedPref(this@DashboardActivity)
                    sharedPrefOBJ.removeShared()
                    startActivity(Intent(this@DashboardActivity, MainActivity::class.java))
                    overridePendingTransition(R.anim.right_in, R.anim.left_out)
                    finish()
                }

        val alert = dialogBuilder.create()
        alert.setTitle(resources.getString(R.string.app_name))
        alert.show()



    }


   private fun initToggle()
    {

         val drawerToggle:ActionBarDrawerToggle = object : ActionBarDrawerToggle(
                this,
                drawer_layout,
                toolbar,
                R.string.drawer_open,
                R.string.drawer_close
        ) {
            override fun onDrawerClosed(view:View){
                CommonMethod.hideKeyboardView(this@DashboardActivity)

            }

            override fun onDrawerOpened(drawerView: View){
                CommonMethod.hideKeyboardView(this@DashboardActivity)
            }

            override fun onDrawerStateChanged(intState: Int){
                CommonMethod.hideKeyboardView(this@DashboardActivity)

            }
        }

        drawer_layout.addDrawerListener(drawerToggle)
        drawerToggle.syncState()
        toolbarFont(this@DashboardActivity)

    }

    private fun initMenu()
    {
        nav_view.itemIconTintList = null
        val sharedPref=SharedPref(this@DashboardActivity)

        when {
            sharedPref.navigationTab==resources.getString(R.string.open_tab_loan) -> nav_view.setCheckedItem(R.id.action_loan)
            sharedPref.navigationTab==resources.getString(R.string.open_tab_profile) -> nav_view.setCheckedItem(R.id.action_profile)
            else -> nav_view.setCheckedItem(R.id.action_dashboard)
        }

        nav_view.bringToFront()
        nav_view.setNavigationItemSelectedListener(this)

        val m = nav_view.menu
        for (i in 0 until m.size())
        {
            val mi = m.getItem(i)
            val subMenu = mi.subMenu
            if (subMenu != null && subMenu.size() > 0)
            {
                for (j in 0 until subMenu.size()) {
                    val subMenuItem = subMenu.getItem(j)
                    applyFontToMenuItem(subMenuItem)
                }
            }
            applyFontToMenuItem(mi)
        }

        val userData = Gson().fromJson<LoginData>(sharedPref.userInfo, LoginData::class.java)
        nav_view.menu.findItem(R.id.action_investment).isVisible = userData.menu_services.service_status.savings != 0


    }

    private fun initLoader()
    {
        progressDialog = ProgressDialog(this@DashboardActivity,R.style.MyAlertDialogStyle)
        val message=   SpannableString(resources.getString(R.string.logging_out))
        val face = Typeface.createFromAsset(assets, "fonts/Montserrat-Regular.ttf")
        message.setSpan(RelativeSizeSpan(1.0f), 0, message.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        message.setSpan(CustomTypefaceSpan("", face), 0, message.length, 0)
        progressDialog.isIndeterminate = true
        progressDialog.setMessage(message)
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER)
        progressDialog.setCancelable(false)
        progressDialog.setCanceledOnTouchOutside(false)

    }

    private fun dismiss()
    {
        if(progressDialog.isShowing)
        {
            progressDialog.dismiss()
        }
    }


    private fun initFragment()
    {
        val sharedPref=SharedPref(this@DashboardActivity)
        when {
            sharedPref.navigationTab==resources.getString(R.string.open_tab_loan) -> {
                navigateToFragment(LoanPlansFragment.newInstance())
                sharedPref.navigationTab=resources.getString(R.string.open_tab_default)
            }
            sharedPref.navigationTab==resources.getString(R.string.open_tab_profile) -> {
                navigateToFragment(ProfileFragment.newInstance())
                sharedPref.navigationTab=resources.getString(R.string.open_tab_default)
            }
            else -> navigateToFragment(DashboardFragment.newInstance())
        }


    }

    @SuppressLint("SetTextI18n")
    fun initData()
    {
        val navigationView       = findViewById<NavigationView>(R.id.nav_view)
        val header               = navigationView.getHeaderView(0)
        val txtName              = header.findViewById<CommonTextRegular>(R.id.txtName)
        val txtCreditScore       = header.findViewById<CommonTextRegular>(R.id.txtCreditScore)
        val imgProfile           = header.findViewById<CircularImageView>(R.id.imgProfile)

        val sharedPrefOBJ= SharedPref(this@DashboardActivity)
        val userData = Gson().fromJson<LoginData>(sharedPrefOBJ.userInfo, LoginData::class.java)
        if(userData!=null)
        {

            if(!TextUtils.isEmpty(userData.user_personal_info.first_name))
            {
                val firstName = userData.user_personal_info.first_name.substring(0, 1).toUpperCase() + userData.user_personal_info.first_name.substring(1)
                txtName.text            = firstName+" "+userData.user_personal_info.middle_name+" "+userData.user_personal_info.last_name
            }
            if(!TextUtils.isEmpty(userData.user_info.credit_score.toString()))
            {
                txtCreditScore.text     = resources.getString(R.string.credit_score)+" "+userData.user_info.credit_score
            }

            val options = RequestOptions()
            options.placeholder(R.drawable.ic_user)
            Glide.with(this@DashboardActivity)
                    .load(BuildConfig.PROFILE_IMAGE_URL+userData.user_personal_info.profile_image)
                    .apply(options)
                    .into(imgProfile)

        }

        openHistory()

    }

    private fun openHistory()
    {
        buttonRight.setOnClickListener {

            if(CommonMethod.isNetworkAvailable(this@DashboardActivity))
            {
                startActivity(Intent(this@DashboardActivity, LoanHistoryListActivity::class.java))
                overridePendingTransition(R.anim.right_in, R.anim.left_out)
            }
            else
            {
                customSnackBarError(drawer_layout,resources.getString(R.string.no_internet))
            }

        }
    }

    private fun customSnackBarError(view: View, message:String) {

        val snackBarOBJ = Snackbar.make(view, "", Snackbar.LENGTH_SHORT)
        snackBarOBJ.view.setBackgroundColor(ContextCompat.getColor(this@DashboardActivity,R.color.colorRed))
        (snackBarOBJ.view as ViewGroup).removeAllViews()
        val customView = LayoutInflater.from(this).inflate(R.layout.snackbar_error, null)
        (snackBarOBJ.view as ViewGroup).addView(customView)

        val txtTitle=customView.findViewById(R.id.txtTitle) as CommonTextRegular

        txtTitle.text = message

        snackBarOBJ.show()
    }

    override fun onBackPressed() {
        if (drawer_layout.isDrawerOpen(GravityCompat.START)) {
            drawer_layout.closeDrawer(GravityCompat.START)
        } else {
            if(doubleBackStatus)
            {
                this@DashboardActivity.moveTaskToBack(true)
            }
            this.doubleBackStatus = true
            CommonMethod.customSnackBarError(drawer_layout, this@DashboardActivity, resources.getString(R.string.press_again_to_exit))
            Handler().postDelayed( { doubleBackStatus = false }, 2000)

        }
    }

    private fun toolbarFont(context: Activity) {

        for (i in 0 until toolbar.childCount) {
            val view = toolbar.getChildAt(i)
            if (view is TextView) {
                val titleFont = Typeface.createFromAsset(context.assets, "fonts/Montserrat-Regular.ttf")
                if (view.text == toolbar.title) {
                    view.typeface = titleFont
                    break
                }
            }
        }
    }

    private fun applyFontToMenuItem(mi: MenuItem) {
        val font = ResourcesCompat.getFont(this@DashboardActivity, R.font.montserrat)
        val title = SpannableString(mi.title)
        title.setSpan(CustomTypefaceSpan("", font!!), 0, title.length, Spannable.SPAN_INCLUSIVE_INCLUSIVE)
        mi.title = title
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
       // Handle navigation view item clicks here.
        val currentFragment =this@DashboardActivity.supportFragmentManager.findFragmentById(R.id.frame)

        when (item.itemId) {

            R.id.action_dashboard ->
            {
                if(currentFragment is DashboardFragment)
                {
                }
                else
                {
                    toolbar.title =resources.getString(R.string.nav_item_dashboard)
                    navigateToFragment(DashboardFragment.newInstance())
                }

            }

            R.id.action_profile -> {
                if(currentFragment is ProfileFragment)
                {
                }
                else
                {
                    toolbar.title =resources.getString(R.string.nav_item_profile)
                    navigateToFragment(ProfileFragment.newInstance())
                }

            }

            R.id.action_loan_calculator -> {
                if(currentFragment is LoanCalculatorFragment)
                {
                }
                else
                {
                    toolbar.title =resources.getString(R.string.nav_item_loan_calculator)
                    navigateToFragment(LoanCalculatorFragment.newInstance())
                }

            }
            R.id.action_loan -> {
                if(currentFragment is LoanPlansFragment)
                {
                }
                else
                {
                    val sharedPref= SharedPref(this@DashboardActivity)
                    sharedPref.openTabLoan="CURRENT"
                    toolbar.title =resources.getString(R.string.nav_item_loan)
                    navigateToFragment(LoanPlansFragment.newInstance())
                }

            }
            R.id.action_points -> {
                if(currentFragment is PointsFragment)
                {
                }
                else
                {
                    toolbar.title =resources.getString(R.string.nav_item_airtime_credit)
                    navigateToFragment(PointsFragment.newInstance())
                }

            }
            R.id.action_investment -> {
                if(currentFragment is InvestmentFragment)
                {
                }
                else
                {
                    toolbar.title =resources.getString(R.string.nav_item_investment)
                    navigateToFragment(InvestmentFragment.newInstance())
                }


            }
            R.id.action_lpk-> {
                if(currentFragment is LpkFragment)
                {
                }
                else
                {
                    toolbar.title =resources.getString(R.string.nav_item_lpk)
                    navigateToFragment(LpkFragment.newInstance())
                }


            }
            R.id.action_wallet-> {
                if(currentFragment is WalletFragment)
                {
                }
                else
                {
                    toolbar.title =resources.getString(R.string.nav_item_wallet)
                    navigateToFragment(WalletFragment.newInstance())
                }


            }
            R.id.action_settings-> {
                if(currentFragment is SettingsFragment)
                {
                }
                else
                {
                    toolbar.title =resources.getString(R.string.nav_item_settings)
                    navigateToFragment(SettingsFragment.newInstance())
                }

            }

            R.id.action_logout -> {
                doLogout()
            }

        }

        drawer_layout.closeDrawer(GravityCompat.START)
        return true
    }

    fun setTitle(title:String)
    {
        toolbar.title =title
    }

    private fun doLogout()
    {
        if(CommonMethod.isNetworkAvailable(this@DashboardActivity))
        {
            progressDialog.show()
            val deviceId    = Settings.Secure.getString(contentResolver, Settings.Secure.ANDROID_ID)
            val jsonObject  = JsonObject()
            jsonObject.addProperty("device_id",deviceId)

            val presenterLogoutObj=PresenterLogout()
            presenterLogoutObj.doLogout(this@DashboardActivity,jsonObject,this)
        }
        else
        {
            CommonMethod.customSnackBarError(drawer_layout,this@DashboardActivity,resources.getString(R.string.no_internet))
        }

    }

    override fun onSuccessLogout() {

       logout()
    }

    override fun onErrorLogout(message: String) {

        dismiss()
        CommonMethod.customSnackBarError(drawer_layout,this@DashboardActivity,message)
    }

    override fun onSessionTimeOut() {
        logout()
    }

    private fun logout()
    {
        dismiss()
        val sharedPrefOBJ= SharedPref(this@DashboardActivity)
        sharedPrefOBJ.removeShared()
        startActivity(Intent(this@DashboardActivity, MainActivity::class.java))
        overridePendingTransition(R.anim.right_in, R.anim.left_out)
        finish()
    }

    private fun navigateToFragment(fragmentToNavigate: androidx.fragment.app.Fragment)
    {
        val fragmentTransaction = supportFragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.frame, fragmentToNavigate)
        fragmentTransaction.setCustomAnimations(android.R.anim.slide_in_left,android.R.anim.slide_out_right, android.R.anim.slide_in_left, android.R.anim.slide_out_right)
        fragmentTransaction.addToBackStack(null)
        fragmentTransaction.commit()
    }

    fun visibleFilter(isVisible:Boolean)
    {
        if(isVisible)
        {
            imgFilter.visibility=View.VISIBLE
        }
        else
        {
            imgFilter.visibility=View.GONE
        }

    }


    fun visibleButton(isVisible:Boolean)
    {
        if(isVisible)
        {
            buttonRight.visibility=View.VISIBLE
        }
        else
        {
            buttonRight.visibility=View.GONE
        }

    }




    fun isVisibleToolbarRight()
    {
        val sharedPref= SharedPref(this@DashboardActivity)
        if(sharedPref.currentLoanCount=="1"|| sharedPref.businessLoanCount=="1")
        {
            buttonRight.visibility=View.VISIBLE
        }
        else
        {
            buttonRight.visibility=View.INVISIBLE
        }
    }

    private fun checkForAppUpdate() {
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


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == APP_UPDATE_REQUEST_CODE) {
            if (resultCode != Activity.RESULT_OK) {
                Toast.makeText(this@DashboardActivity,
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
        snackbar.setActionTextColor(ContextCompat.getColor(this@DashboardActivity, R.color.colorApp))
        snackbar.show()
    }


    public override fun onResume()
    {
        super.onResume()
        val sharedPrefOBJ=SharedPref(this@DashboardActivity)
        val currentFragment =this@DashboardActivity.supportFragmentManager.findFragmentById(R.id.frame)
        if(currentFragment is ProfileFragment)
        {
            if(sharedPrefOBJ.profileUpdate==resources.getString(R.string.status_true))
            {
                currentFragment.loadProfileInfo(false)
                sharedPrefOBJ.profileUpdate=resources.getString(R.string.status_false)
            }

        }
        else if(currentFragment is DashboardFragment)
        {
            visibleButton(false)
            visibleFilter(false)
        }

        appUpdateManager
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
                }

            MyApplication.getInstance().trackScreenView(this@DashboardActivity::class.java.simpleName)


    }

    companion object {
        private const val APP_UPDATE_REQUEST_CODE = 1991
    }



    public override fun onDestroy() {
    super.onDestroy()
     countDownTimer.cancel()
    }


    public override fun onStop() {
        super.onStop()
        countDownTimer.cancel()

    }
}
