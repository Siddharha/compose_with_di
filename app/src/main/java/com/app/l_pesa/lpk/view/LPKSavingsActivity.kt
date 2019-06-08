package com.app.l_pesa.lpk.view

import android.app.Activity
import android.graphics.Typeface
import android.os.Bundle
import com.google.android.material.tabs.TabLayout
import androidx.fragment.app.Fragment
import androidx.viewpager.widget.ViewPager
import androidx.appcompat.app.AppCompatActivity
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.app.l_pesa.R
import com.app.l_pesa.common.CommonMethod
import com.app.l_pesa.lpk.adapter.SavingsTabAdapter
import kotlinx.android.synthetic.main.activity_lpk_savings.*



class LPKSavingsActivity : AppCompatActivity(), TabLayout.OnTabSelectedListener {

    private var tabLayout: TabLayout? = null
    private var viewPager: androidx.viewpager.widget.ViewPager? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_lpk_savings)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        toolbarFont(this@LPKSavingsActivity)

        initUI()
    }

    private fun initUI()
    {
        tabLayout=findViewById(R.id.tabLayout)
        viewPager=findViewById(R.id.viewPager)
        tabLayout!!.addTab(tabLayout!!.newTab().setText(resources.getString(R.string.token_transfer)))
        tabLayout!!.addTab(tabLayout!!.newTab().setText(resources.getString(R.string.transfer_history)))
        tabLayout!!.addTab(tabLayout!!.newTab().setText(resources.getString(R.string.earned_interest)))
        tabLayout!!.tabGravity = TabLayout.GRAVITY_FILL

        val adapter = SavingsTabAdapter(supportFragmentManager, tabLayout!!.tabCount)
        viewPager!!.adapter = adapter
        changeTabsFont()
        tabLayout!!.addOnTabSelectedListener(this)

        viewPager!!.offscreenPageLimit = 3

        imgFilter.setOnClickListener {

            if(viewPager!!.currentItem==1)
            {
                val fragment = adapter.instantiateItem(viewPager!!, 1) as androidx.fragment.app.Fragment
                if (fragment is TransferHistoryFragment) {
                    fragment.doFilter()

                }
            }

            else if(viewPager!!.currentItem==2)
            {
                val fragment = adapter.instantiateItem(viewPager!!, 2) as androidx.fragment.app.Fragment
                if (fragment is EarnedInterestFragment) {
                    fragment.doFilter()

                }
            }
        }

    }

    override fun onTabReselected(p0: TabLayout.Tab?) {

    }

    override fun onTabUnselected(p0: TabLayout.Tab?) {

    }

    override fun onTabSelected(p0: TabLayout.Tab?) {
        viewPager!!.currentItem = p0!!.position

        if(p0.position==0)
        {
            imgFilter.visibility= View.INVISIBLE
        }
        else
        {
            imgFilter.visibility= View.VISIBLE
            CommonMethod.hideKeyboardView(this@LPKSavingsActivity)
        }

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
                CommonMethod.hideKeyboardView(this@LPKSavingsActivity)
                onBackPressed()
                overridePendingTransition(R.anim.left_in, R.anim.right_out)
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
