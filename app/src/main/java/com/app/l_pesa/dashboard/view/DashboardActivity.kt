package com.app.l_pesa.dashboard.view

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
import com.app.l_pesa.profile.view.ProfileFragment


class DashboardActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dashboard)
        setSupportActionBar(toolbar)

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
                supportFragmentManager.beginTransaction()
                .replace(R.id.frame, DashboardFragment()).commit()
            }

            R.id.action_profile -> {
                supportFragmentManager.beginTransaction()
                .replace(R.id.frame, ProfileFragment()).commit()
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
}
