package com.app.l_pesa.loanHistory.model

import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentStatePagerAdapter
import com.app.l_pesa.loanplan.view.BusinessLoan
import com.app.l_pesa.loanplan.view.CurrentLoan

class LoanHistoryTabPager(fm: FragmentManager, //integer to count number of tabs
    private var tabCount: Int)//Initializing tab count
    : FragmentStatePagerAdapter(fm) {

    //Overriding method getItem
    override fun getItem(position: Int): Fragment? {
        //Returning the current tabs
        return when (position) {
            0 -> {
                CurrentLoan()
            }
            1 -> {
                BusinessLoan()
            }

            else -> null
        }
    }


    override fun getCount(): Int {
        return tabCount
    }
}
