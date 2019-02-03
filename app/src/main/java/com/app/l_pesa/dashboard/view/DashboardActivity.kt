package com.app.l_pesa.dashboard.view

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.design.widget.NavigationView
import android.support.v4.view.GravityCompat
import android.support.v7.app.ActionBarDrawerToggle
import android.view.Menu
import android.view.MenuItem
import com.app.l_pesa.R
import com.app.l_pesa.dashboard.view.DashboardFragment
import kotlinx.android.synthetic.main.activity_dashboard.*
import kotlinx.android.synthetic.main.app_bar_main.*
import android.text.Spannable
import com.app.l_pesa.common.CustomTypefaceSpan
import android.text.SpannableString
import android.graphics.Typeface
import android.support.design.internal.NavigationMenuView
import android.support.v7.widget.DividerItemDecoration
import android.view.SubMenu
import android.graphics.drawable.InsetDrawable
import android.graphics.drawable.Drawable
import android.content.res.TypedArray








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
        val attr = intArrayOf(android.R.attr.listDivider)

        val a = obtainStyledAttributes(attr)
        val divider = a.getDrawable(0)
        val inset = resources.getDimensionPixelSize(R.dimen._10sdp)
        val insetDivider = InsetDrawable(divider, inset, 0, 0, 0)
        a.recycle()

        val navMenuView = nav_view.getChildAt(0) as NavigationMenuView
        val itemDecoration = DividerItemDecoration(this@DashboardActivity, DividerItemDecoration.VERTICAL)
        itemDecoration.setDrawable(insetDivider)
        navMenuView.addItemDecoration(itemDecoration)

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
        val mNewTitle = SpannableString(mi.title)
        mNewTitle.setSpan(CustomTypefaceSpan("", font), 0, mNewTitle.length, Spannable.SPAN_INCLUSIVE_INCLUSIVE)
        mi.title = mNewTitle
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
       // Handle navigation view item clicks here.
        when (item.groupId) {
            R.id.action_dashboard-> {
                val fManager = supportFragmentManager
                val tx = fManager.beginTransaction()
                tx.replace(R.id.frame, DashboardFragment())
                tx.commit()
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
