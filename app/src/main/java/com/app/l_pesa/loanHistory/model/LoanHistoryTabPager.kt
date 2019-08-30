package com.app.l_pesa.loanHistory.model

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import com.app.l_pesa.loanHistory.view.BusinessLoanHistory
import com.app.l_pesa.loanHistory.view.CurrentLoanHistory


class LoanHistoryTabPager(fm: FragmentManager,private var tabCount: Int)//Initializing tab count
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
