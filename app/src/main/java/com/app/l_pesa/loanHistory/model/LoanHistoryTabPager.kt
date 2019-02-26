package com.app.l_pesa.loanHistory.model

import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentStatePagerAdapter
import com.app.l_pesa.loanHistory.view.BusinessLoanHistory
import com.app.l_pesa.loanHistory.view.CurrentLoanHistory


class LoanHistoryTabPager(fm: FragmentManager, //integer to count number of tabs
    private var tabCount: Int)//Initializing tab count
    : FragmentStatePagerAdapter(fm) {

    //Overriding method getItem
    override fun getItem(position: Int): Fragment? {
        //Returning the current tabs
        return when (position) {
            0 -> {
                CurrentLoanHistory()
            }
            1 -> {
                BusinessLoanHistory()
            }

            else -> null
        }
    }


    override fun getCount(): Int {
        return tabCount
    }
}
