package com.app.l_pesa.profile.view


import android.app.Activity
import android.content.Intent
import android.graphics.Typeface
import android.os.Bundle
import android.view.MenuItem
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager.widget.ViewPager
import com.app.l_pesa.R
import com.app.l_pesa.analytics.MyApplication
import com.app.l_pesa.common.CommonMethod
import com.app.l_pesa.common.SharedPref
import com.app.l_pesa.dashboard.view.DashboardActivity
import com.app.l_pesa.profile.model.IdInformationTabPager
import com.google.android.material.tabs.TabLayout
import kotlinx.android.synthetic.main.activity_profile_edit_id_info.*

class ProfileEditIdInfoActivity : AppCompatActivity(), TabLayout.OnTabSelectedListener {

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
                val titleFont = Typeface.createFromAsset(context.assets, "fonts/Montserrat-Regular.ttf")
                if (view.text == toolbar.title) {
                    view.typeface = titleFont
                    break
                }
            }
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {

                val sharedPref= SharedPref(this@ProfileEditIdInfoActivity)
                if(sharedPref.profileUpdate==resources.getString(R.string.status_true))
                {
                    sharedPref.navigationTab=resources.getString(R.string.open_tab_profile)
                    val intent = Intent(this@ProfileEditIdInfoActivity, DashboardActivity::class.java)
                    startActivity(intent)
                    overridePendingTransition(R.anim.left_in, R.anim.right_out)

                }
                else
                {
                    CommonMethod.hideKeyboardView(this@ProfileEditIdInfoActivity)
                    onBackPressed()
                    overridePendingTransition(R.anim.left_in, R.anim.right_out)
                }

                true
            }

            else -> super.onOptionsItemSelected(item)
        }
    }


    override fun onBackPressed() {
        val sharedPref= SharedPref(this@ProfileEditIdInfoActivity)
        if(sharedPref.profileUpdate==resources.getString(R.string.status_true))
        {
            sharedPref.navigationTab=resources.getString(R.string.open_tab_profile)
            val intent = Intent(this@ProfileEditIdInfoActivity, DashboardActivity::class.java)
            startActivity(intent)
            overridePendingTransition(R.anim.left_in, R.anim.right_out)
        }
        else
        {
            super.onBackPressed()
            overridePendingTransition(R.anim.left_in, R.anim.right_out)
        }
    }

    public override fun onResume() {
        super.onResume()
        MyApplication.getInstance().trackScreenView(this@ProfileEditIdInfoActivity::class.java.simpleName)

    }

}
