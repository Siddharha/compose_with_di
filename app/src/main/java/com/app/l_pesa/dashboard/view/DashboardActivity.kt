package com.app.l_pesa.dashboard.view

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.design.widget.NavigationView
import android.support.v4.view.GravityCompat
import android.support.v7.app.ActionBarDrawerToggle
import android.view.MenuItem
import com.app.l_pesa.R
import kotlinx.android.synthetic.main.activity_dashboard.*
import kotlinx.android.synthetic.main.app_bar_main.*
import android.text.Spannable
import android.text.SpannableString
import android.graphics.Typeface
import android.provider.Settings
import android.support.design.widget.Snackbar
import android.support.v4.app.Fragment
import android.support.v4.content.ContextCompat
import android.support.v4.content.res.ResourcesCompat
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.app.l_pesa.common.*
import com.app.l_pesa.investment.view.InvestmentFragment
import com.app.l_pesa.loanHistory.view.LoanHistoryListActivity
import com.app.l_pesa.loanplan.view.LoanPlansFragment
import com.app.l_pesa.login.model.LoginData
import com.app.l_pesa.logout.inter.ICallBackLogout
import com.app.l_pesa.logout.presenter.PresenterLogout
import com.app.l_pesa.lpk.view.LpkFragment
import com.app.l_pesa.main.MainActivity
import com.app.l_pesa.points.view.PointsFragment
import com.app.l_pesa.profile.view.ProfileFragment
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.app.l_pesa.settings.view.SettingsFragment
import com.app.l_pesa.wallet.view.WalletFragment
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions


class DashboardActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener, ICallBackLogout {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dashboard)

        toolbar.title =resources.getString(R.string.nav_item_dashboard)
        setSupportActionBar(toolbar)

        initData()
        initMenu()
        initFragment()
        initToggle()
        //cacheDir.deleteRecursively()
        nav_view.setNavigationItemSelectedListener(this)

    }

   private fun initToggle()
    {
        val toggle = ActionBarDrawerToggle(
                this, drawer_layout, toolbar, R.string.drawer_open, R.string.drawer_close
        )
        drawer_layout.addDrawerListener(toggle)
        toggle.syncState()
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


    private fun initFragment()
    {
        val sharedPref=SharedPref(this@DashboardActivity)
        when {
            sharedPref.navigationTab==resources.getString(R.string.open_tab_loan) -> {
                navigateToFragment(LoanPlansFragment.newInstance(),true)
                sharedPref.navigationTab=resources.getString(R.string.open_tab_default)
            }
            sharedPref.navigationTab==resources.getString(R.string.open_tab_profile) -> {
                navigateToFragment(ProfileFragment.newInstance(),false)
                sharedPref.navigationTab=resources.getString(R.string.open_tab_default)
            }
            else -> navigateToFragment(DashboardFragment.newInstance(),false)
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
                txtName.text            = userData.user_personal_info.first_name+" "+userData.user_personal_info.middle_name+" "+userData.user_personal_info.last_name
            }
            if(!TextUtils.isEmpty(userData.user_info.credit_score.toString()))
            {
                txtCreditScore.text     = resources.getString(R.string.credit_score)+userData.user_info.credit_score
            }

            val options = RequestOptions()
            options.centerCrop()
            options.error(R.drawable.ic_user_no_img_icon)
            Glide.with(this@DashboardActivity)
                    .load(resources.getString(R.string.profile_image_url)+userData.user_personal_info.profile_image)
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
            this@DashboardActivity.moveTaskToBack(true)
        }
    }

    private fun toolbarFont(context: Activity) {

        for (i in 0 until toolbar.childCount) {
            val view = toolbar.getChildAt(i)
            if (view is TextView) {
                val tv = view
                val titleFont = Typeface.createFromAsset(context.assets, "fonts/Montserrat-Regular.ttf")
                if (tv.text == toolbar.title) {
                    tv.typeface = titleFont
                    break
                }
            }
        }
    }

    private fun applyFontToMenuItem(mi: MenuItem) {
        val font = ResourcesCompat.getFont(this@DashboardActivity, R.font.montserrat)
        val title = SpannableString(mi.title)
        title.setSpan(CustomTypefaceSpan("", font), 0, title.length, Spannable.SPAN_INCLUSIVE_INCLUSIVE)
        mi.title = title
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
       // Handle navigation view item clicks here.
        when (item.itemId) {
            R.id.action_dashboard ->
            {
                toolbar.title =resources.getString(R.string.nav_item_dashboard)
                navigateToFragment(DashboardFragment.newInstance(),false)
            }

            R.id.action_profile -> {
                toolbar.title =resources.getString(R.string.nav_item_profile)
                navigateToFragment(ProfileFragment.newInstance(),false)
            }
            R.id.action_loan -> {
                toolbar.title =resources.getString(R.string.nav_item_loan)
                navigateToFragment(LoanPlansFragment.newInstance(),false)
            }
            R.id.action_points -> {
                toolbar.title =resources.getString(R.string.nav_item_points)
                navigateToFragment(PointsFragment.newInstance(),false)
            }
            R.id.action_investment -> {
                toolbar.title =resources.getString(R.string.nav_item_investment)
                navigateToFragment(InvestmentFragment.newInstance(),false)

            }
            R.id.action_lpk-> {
                toolbar.title =resources.getString(R.string.nav_item_lpk)
                navigateToFragment(LpkFragment.newInstance(),false)

            }
            R.id.action_wallet-> {
                toolbar.title =resources.getString(R.string.nav_item_wallet)
                navigateToFragment(WalletFragment.newInstance(),false)

            }
            R.id.action_settings-> {
                toolbar.title =resources.getString(R.string.nav_item_settings)
                navigateToFragment(SettingsFragment.newInstance(),false)

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

        val sharedPrefOBJ= SharedPref(this@DashboardActivity)
        sharedPrefOBJ.removeToken()
        startActivity(Intent(this@DashboardActivity, MainActivity::class.java))
        overridePendingTransition(R.anim.right_in, R.anim.left_out)
        finish()
    }

    override fun onErrorLogout(message: String) {

        CommonMethod.customSnackBarError(drawer_layout,this@DashboardActivity,message)
    }

    private fun navigateToFragment(fragmentToNavigate: Fragment,isVisible:Boolean)
    {
        if(isVisible)
        {
            if(fragmentToNavigate is LoanPlansFragment)
            {
                buttonRight.text = resources.getString(R.string.history)
            }
            buttonRight.visibility=View.VISIBLE
        }
        else
        {
            buttonRight.visibility=View.INVISIBLE
        }

        val fragmentTransaction = supportFragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.frame, fragmentToNavigate)
        fragmentTransaction.setCustomAnimations(android.R.anim.slide_in_left,android.R.anim.slide_out_right, android.R.anim.slide_in_left, android.R.anim.slide_out_right)
        fragmentTransaction.addToBackStack(null)
        fragmentTransaction.commit()
    }



    fun isVisibleToolbarRight(isVisible:Boolean)
    {
        if(isVisible)
        {
            buttonRight.visibility=View.VISIBLE
        }
        else
        {
            buttonRight.visibility=View.INVISIBLE
        }
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
                initData()
            }

        }

    }
}
