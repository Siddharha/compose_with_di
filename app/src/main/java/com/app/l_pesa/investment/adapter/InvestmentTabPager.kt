package com.app.l_pesa.investment.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import com.app.l_pesa.investment.view.InvestmentHistory
import com.app.l_pesa.investment.view.InvestmentPlan


class InvestmentTabPager(fm: FragmentManager, private var tabCount: Int): FragmentStatePagerAdapter(fm) {

    //Overriding method getItem
    override fun getItem(position: Int): Fragment? {
        //Returning the current tabs
        return when (position) {
            0 -> {
                InvestmentPlan()
            }
            1 -> {
                InvestmentHistory()
            }

            else -> null
        }
    }


    override fun getCount(): Int {
        return tabCount
    }
}
