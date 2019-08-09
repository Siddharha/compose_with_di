package com.app.l_pesa.loanHistory.model

import androidx.fragment.app.FragmentManager
import com.app.l_pesa.loanHistory.view.BusinessLoanHistory
import com.app.l_pesa.loanHistory.view.CurrentLoanHistory


class LoanHistoryTabPager(fm: FragmentManager, //integer to count number of tabs
                          private var tabCount: Int)//Initializing tab count
    : androidx.fragment.app.FragmentStatePagerAdapter(fm) {

    //Overriding method getItem
    override fun getItem(position: Int): androidx.fragment.app.Fragment? {
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
