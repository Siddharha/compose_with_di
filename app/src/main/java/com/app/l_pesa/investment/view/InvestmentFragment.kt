package com.app.l_pesa.investment.view

import android.graphics.Typeface
import android.os.Bundle
import com.google.android.material.tabs.TabLayout
import androidx.fragment.app.Fragment
import androidx.viewpager.widget.ViewPager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.app.l_pesa.R
import com.app.l_pesa.dashboard.view.DashboardActivity
import com.app.l_pesa.investment.adapter.InvestmentTabPager
import kotlinx.android.synthetic.main.app_bar_main.*


class InvestmentFragment : Fragment(), TabLayout.OnTabSelectedListener {


    private var tabLayout: TabLayout? = null
    private var viewPager: ViewPager? = null

    companion object {
        fun newInstance(): Fragment {
            return InvestmentFragment()
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
        (activity as DashboardActivity).visibleFilter(false)
        (activity as DashboardActivity).visibleButton(false)

        tabLayout=activity!!.findViewById(R.id.tabLayout)
        viewPager=activity!!.findViewById(R.id.viewPager)
        tabLayout!!.addTab(tabLayout!!.newTab().setText(resources.getString(R.string.investment_plan)))
        tabLayout!!.addTab(tabLayout!!.newTab().setText(resources.getString(R.string.investment_history)))
        tabLayout!!.tabGravity = TabLayout.GRAVITY_FILL
        tabLayout!!.tabMode = TabLayout.MODE_FIXED

        val adapter = InvestmentTabPager(childFragmentManager, tabLayout!!.tabCount)
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

        if(viewPager!!.currentItem==1)
        {
            (activity as DashboardActivity).visibleFilter(true)
            (activity as DashboardActivity).imgFilter.setOnClickListener {

                val fragment = viewPager!!.adapter!!.instantiateItem(viewPager!!, 1) as androidx.fragment.app.Fragment
                if (fragment is InvestmentHistory) {
                    fragment.doFilter()

                }

            }
        }
        else
        {
            (activity as DashboardActivity).visibleFilter(false)
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
                    val face = Typeface.createFromAsset(activity!!.assets, "fonts/Montserrat-Regular.ttf")
                    tabViewChild.typeface = face
                }
            }
        }
    }
}