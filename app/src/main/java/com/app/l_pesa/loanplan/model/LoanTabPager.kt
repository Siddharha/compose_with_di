package com.app.l_pesa.loanplan.model

import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentStatePagerAdapter

import com.app.l_pesa.loanplan.view.BusinessLoan
import com.app.l_pesa.loanplan.view.CurrentLoan

/**
 * Created by Intellij Amiya on 21/2/19.
 * Who Am I- https://stackoverflow.com/users/3395198/
 * A good programmer is someone who looks both ways before crossing a One-way street.
 * Kindly follow https://source.android.com/setup/code-style
 */
class LoanTabPager//Constructor to the class
(fm: FragmentManager, //integer to count number of tabs
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
