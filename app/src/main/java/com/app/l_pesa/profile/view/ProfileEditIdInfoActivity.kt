package com.app.l_pesa.profile.view

import android.app.Activity
import android.graphics.Typeface
import android.os.Bundle
import android.support.design.widget.TabLayout
import android.support.v4.view.ViewPager
import android.support.v7.app.AppCompatActivity
import android.view.MenuItem
import android.view.ViewGroup
import android.widget.TextView
import com.app.l_pesa.R
import com.app.l_pesa.profile.model.IdInformationTabPager


import kotlinx.android.synthetic.main.activity_profile_edit_id_info.*

class ProfileEditIdInfoActivity : AppCompatActivity(),TabLayout.OnTabSelectedListener {

    private var tabLayout: TabLayout? = null
    private var viewPager: ViewPager? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile_edit_id_info)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        toolbarFont(this@ProfileEditIdInfoActivity)

        initData()


    }

    private fun initData()
    {

        tabLayout=findViewById(R.id.tabLayout)
        viewPager=findViewById(R.id.viewPager)
        tabLayout!!.addTab(tabLayout!!.newTab().setText(resources.getString(R.string.personal_id_information)))
        tabLayout!!.addTab(tabLayout!!.newTab().setText(resources.getString(R.string.business_id_information)))
        tabLayout!!.tabGravity = TabLayout.GRAVITY_FILL

        val adapter = IdInformationTabPager(supportFragmentManager, tabLayout!!.tabCount)
        viewPager!!.adapter = adapter
        changeTabsFont()
        tabLayout!!.addOnTabSelectedListener(this)

    }

    override fun onTabReselected(p0: TabLayout.Tab?) {

    }

    override fun onTabUnselected(p0: TabLayout.Tab?) {

    }

    override fun onTabSelected(p0: TabLayout.Tab?) {
        viewPager!!.currentItem = p0!!.position

    }

    private fun changeTabsFont() {
        val vg = tabLayout!!.getChildAt(0) as ViewGroup
        val tabsCount = vg.childCount
        for (j in 0 until tabsCount) {
            val vgTab = vg.getChildAt(j) as ViewGroup
            val tabChildsCount = vgTab.childCount
            for (i in 0 until tabChildsCount) {
                val tabViewChild = vgTab.getChildAt(i)
                if (tabViewChild is TextView) {
                    val face = Typeface.createFromAsset(assets, "fonts/Montserrat-Regular.ttf")
                    tabViewChild.typeface = face
                }
            }
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




    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {

                /*if(swipeRefreshLayout.isRefreshing)
                {
                    CommonMethod.customSnackBarError(llRoot,this@ProfileEditBusinessInfoActivity,resources.getString(R.string.please_wait))
                }
                else
                {*/
                    onBackPressed()
                    overridePendingTransition(R.anim.left_in, R.anim.right_out)
               // }
                true
            }

            else -> super.onOptionsItemSelected(item)
        }
    }



    override fun onBackPressed() {
        super.onBackPressed()
        overridePendingTransition(R.anim.left_in, R.anim.right_out)
    }


}
