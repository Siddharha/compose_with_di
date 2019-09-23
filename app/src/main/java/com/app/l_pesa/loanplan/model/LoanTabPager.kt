package com.app.l_pesa.loanplan.model

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import com.app.l_pesa.loanplan.view.BusinessLoan
import com.app.l_pesa.loanplan.view.CurrentLoan

class LoanTabPager(fm: FragmentManager, private var tabCount: Int): FragmentStatePagerAdapter(fm) {

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
