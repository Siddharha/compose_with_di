package com.app.l_pesa.dashboard.view

import android.annotation.SuppressLint
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
import android.support.v4.app.Fragment
import android.text.TextUtils
import com.app.l_pesa.common.*
import com.app.l_pesa.login.model.LoginData
import com.app.l_pesa.logout.inter.ICallBackLogout
import com.app.l_pesa.logout.presenter.PresenterLogout
import com.app.l_pesa.main.MainActivity
import com.app.l_pesa.profile.view.ProfileFragment
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.app.l_pesa.settings.view.SettingsFragment
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import java.lang.Exception


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

        nav_view.setNavigationItemSelectedListener(this)
    }

   private fun initToggle()
    {
        val toggle = ActionBarDrawerToggle(
                this, drawer_layout, toolbar, R.string.drawer_open, R.string.drawer_close
        )
        drawer_layout.addDrawerListener(toggle)
        toggle.syncState()
    }

    private fun initMenu()
    {
        nav_view.itemIconTintList = null
        nav_view.setCheckedItem(R.id.action_dashboard)
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
    }



    private fun initFragment()
    {
        navigateToFragment(DashboardFragment.newInstance())

    }

    @SuppressLint("SetTextI18n")
    private fun initData()
    {
        val navigationView           = findViewById<NavigationView>(R.id.nav_view)
        val header                          = navigationView.getHeaderView(0)
        val txtName              = header.findViewById<CommonTextRegular>(R.id.txtName)
        val txtCreditScore       = header.findViewById<CommonTextRegular>(R.id.txtCreditScore)
        val imgProfile             = header.findViewById<CircularImageView>(R.id.imgProfile)

        val sharedPrefOBJ= SharedPref(this@DashboardActivity)
        val userData = Gson().fromJson<LoginData>(sharedPrefOBJ.userInfo, LoginData::class.java)
        if(userData!=null)
        {

            if(!TextUtils.isEmpty(userData.user_personal_info.first_name))
            {
                txtName.text            = userData.user_personal_info.first_name+" "+userData.user_personal_info.middle_name+" "+userData.user_personal_info.last_name
            }
            if(!TextUtils.isEmpty(userData.user_info.credit_score))
            {
                txtCreditScore.text     = resources.getString(R.string.credit_score)+userData.user_info.credit_score
            }

            try {

                val options = RequestOptions()
                options.placeholder(R.drawable.ic_profile)
                options.error(R.drawable.ic_profile)
                options.centerCrop()
                Glide.with(this@DashboardActivity)
                        .load(userData.user_personal_info.profile_image)
                        .apply(options)
                        .into(imgProfile)
            }
            catch (exception:Exception)
            {

            }


        }

    }

    override fun onBackPressed() {
        if (drawer_layout.isDrawerOpen(GravityCompat.START)) {
            drawer_layout.closeDrawer(GravityCompat.START)
        } else {
            finishAffinity()
        }
    }

    private fun applyFontToMenuItem(mi: MenuItem) {
        val font = Typeface.createFromAsset(assets, "fonts/Montserrat-Regular.ttf")
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
                navigateToFragment(DashboardFragment.newInstance())
            }

            R.id.action_profile -> {
                toolbar.title =resources.getString(R.string.nav_item_profile)
                navigateToFragment(ProfileFragment.newInstance())
            }
            R.id.action_loan -> {
                toolbar.title =resources.getString(R.string.nav_item_loan)

            }
            R.id.action_points -> {
                toolbar.title =resources.getString(R.string.nav_item_points)

            }
            R.id.action_investment -> {
                toolbar.title =resources.getString(R.string.nav_item_investment)

            }
            R.id.action_lpk-> {
                toolbar.title =resources.getString(R.string.nav_item_lpk)

            }
            R.id.action_wallet-> {
                toolbar.title =resources.getString(R.string.nav_item_wallet)

            }
            R.id.action_settings-> {
                toolbar.title =resources.getString(R.string.nav_item_settings)
                navigateToFragment(SettingsFragment.newInstance())

            }

            R.id.action_logout -> {
                doLogout()
            }

        }

        drawer_layout.closeDrawer(GravityCompat.START)
        return true
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
            CommonMethod.setSnackBar(this@DashboardActivity,drawer_layout,resources.getString(R.string.no_internet))
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

        CommonMethod.setSnackBar(this@DashboardActivity,drawer_layout,message)
    }

    private fun navigateToFragment(fragmentToNavigate: Fragment) {
        val fragmentTransaction = supportFragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.frame, fragmentToNavigate)
        fragmentTransaction.setCustomAnimations(android.R.anim.slide_in_left,android.R.anim.slide_out_right, android.R.anim.slide_in_left, android.R.anim.slide_out_right)
        fragmentTransaction.addToBackStack(null)
        fragmentTransaction.commit()
    }
}
