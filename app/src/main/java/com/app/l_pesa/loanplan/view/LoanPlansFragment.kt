package com.app.l_pesa.loanplan.view

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.app.l_pesa.R
import android.support.v4.view.ViewPager
import android.support.design.widget.TabLayout
import com.app.l_pesa.loanplan.model.LoanTabPager
import android.graphics.Typeface
import android.widget.TextView
import com.app.l_pesa.common.SharedPref


/**
 * Created by Intellij Amiya on 21/2/19.
 *  Who Am I- https://stackoverflow.com/users/3395198/
 * A good programmer is someone who looks both ways before crossing a One-way street.
 * Kindly follow https://source.android.com/setup/code-style
 */
class LoanPlansFragment : Fragment(),TabLayout.OnTabSelectedListener {


    private var tabLayout: TabLayout? = null
    private var viewPager: ViewPager? = null

    companion object {
        fun newInstance(): Fragment {
            return LoanPlansFragment()
        }
    }
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        return inflater.inflate(R.layout.layout_tab_common, container,false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

       initUI()

    }

    private fun initUI()
    {
        tabLayout=activity!!.findViewById(R.id.tabLayout)
        viewPager=activity!!.findViewById(R.id.viewPager)
        tabLayout!!.addTab(tabLayout!!.newTab().setText(resources.getString(R.string.current_loan_plans)))
        tabLayout!!.addTab(tabLayout!!.newTab().setText(resources.getString(R.string.business_loan_plans)))
        tabLayout!!.tabGravity = TabLayout.GRAVITY_FILL

        val adapter = LoanTabPager(childFragmentManager, tabLayout!!.tabCount)
        viewPager!!.adapter = adapter
        changeTabsFont()
        tabLayout!!.addOnTabSelectedListener(this)

        val sharedPref= SharedPref(activity!!)
        if(sharedPref.openTabLoan=="CURRENT")
        {
            tabLayout!!.setScrollPosition(0,0f,true)
            viewPager!!.currentItem = 0
        }
        else
        {
            tabLayout!!.setScrollPosition(1,0f,true)
            viewPager!!.currentItem = 1
            sharedPref.openTabLoan="CURRENT"
        }
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
                    val face = Typeface.createFromAsset(activity!!.assets, "fonts/Montserrat-Regular.ttf")
                    tabViewChild.typeface = face
                }
            }
        }
    }
}