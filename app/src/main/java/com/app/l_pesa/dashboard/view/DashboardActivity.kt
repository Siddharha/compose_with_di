package com.app.l_pesa.dashboard.view

import android.annotation.SuppressLint
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.design.widget.NavigationView
import android.support.v4.view.GravityCompat
import android.support.v7.app.ActionBarDrawerToggle
import android.view.Menu
import android.view.MenuItem
import com.app.l_pesa.R
import kotlinx.android.synthetic.main.activity_dashboard.*
import kotlinx.android.synthetic.main.app_bar_main.*
import android.text.Spannable
import com.app.l_pesa.common.CustomTypefaceSpan
import android.text.SpannableString
import android.graphics.Typeface
import android.provider.Settings
import android.text.TextUtils
import com.app.l_pesa.common.CommonMethod
import com.app.l_pesa.common.CommonTextRegular
import com.app.l_pesa.common.SharedPref
import com.app.l_pesa.login.model.LoginData
import com.app.l_pesa.logout.inter.ICallBackLogout
import com.app.l_pesa.profile.view.ProfileFragment
import com.google.gson.Gson
import com.google.gson.JsonObject


class DashboardActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener, ICallBackLogout {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dashboard)

        toolbar.title =resources.getString(R.string.nav_item_dashboard)
        setSupportActionBar(toolbar)

        initData()
        initMenu()
        initFragment()

        val toggle = ActionBarDrawerToggle(
                this, drawer_layout, toolbar, R.string.drawer_open, R.string.drawer_close
        )
        drawer_layout.addDrawerListener(toggle)
        toggle.syncState()


        nav_view.setNavigationItemSelectedListener(this)
    }

    private fun  initMenu()
    {
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

        val fManager = supportFragmentManager
        val tx = fManager.beginTransaction()
        tx.add(R.id.frame,DashboardFragment())
        tx.commit()

    }

    @SuppressLint("SetTextI18n")
    private fun initData()
    {
        val navigationView  = findViewById<NavigationView>(R.id.nav_view)
        val header          = navigationView.getHeaderView(0)
        val txtName         = header.findViewById<CommonTextRegular>(R.id.txtName)
        val txtCreditScore  = header.findViewById<CommonTextRegular>(R.id.txtCreditScore)
        val sharedPrefOBJ= SharedPref(this@DashboardActivity)
        val userData = Gson().fromJson<LoginData>(sharedPrefOBJ.userInfo, LoginData::class.java)
        if(userData!=null)
        {

            if(!TextUtils.isEmpty(userData.user_personal_info.first_name))
            {
                txtName.text            = userData.user_personal_info.first_name+" "+userData.user_personal_info.last_name
            }
            if(!TextUtils.isEmpty(userData.user_info.credit_score))
            {
                txtCreditScore.text     = resources.getString(R.string.credit_score)+userData.user_info.credit_score
            }

        }

    }

    override fun onBackPressed() {
        if (drawer_layout.isDrawerOpen(GravityCompat.START)) {
            drawer_layout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
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
                supportFragmentManager.beginTransaction()
                .replace(R.id.frame, DashboardFragment()).commit()
            }

            R.id.action_profile -> {
                toolbar.title =resources.getString(R.string.nav_item_profile)
                supportFragmentManager.beginTransaction()
                .replace(R.id.frame, ProfileFragment()).commit()
            }

            R.id.action_logout -> {
                doLogout()
            }
            /* "Courses" -> {
                 var fManager = supportFragmentManager
                 var tx = fManager.beginTransaction()
                 tx.replace(R.id.frame1,CoursesFrag())
                 tx.commit()
             }
             "Projects" -> {
                 var fManager = supportFragmentManager
                 var tx = fManager.beginTransaction()
                 tx.replace(R.id.frame1,ProjectsFrag())
                 tx.commit()
             }
             "Materials" -> {
                 var fManager = supportFragmentManager
                 var tx = fManager.beginTransaction()
                 tx.replace(R.id.frame1,MaterialsFrag())
                 tx.commit()
             }
             "ContactUs" -> {
                 var fManager = supportFragmentManager
                 var tx = fManager.beginTransaction()
                 tx.replace(R.id.frame1,ContactUsFrag())
                 tx.commit()
             }*/
        }

        drawer_layout.closeDrawer(GravityCompat.START)
        return true
    }

    private fun doLogout()
    {
        if(CommonMethod.isNetworkAvailable(this@DashboardActivity))
        {
            val deviceId= Settings.Secure.getString(contentResolver, Settings.Secure.ANDROID_ID)
            val jsonObject = JsonObject()
            jsonObject.addProperty("device_id",deviceId)
        }
        else
        {
            CommonMethod.setSnackBar(this@DashboardActivity,drawer_layout,resources.getString(R.string.no_internet))
        }

    }

    override fun onSuccessLogout() {

    }

    override fun onErrorLogout() {

    }
}
