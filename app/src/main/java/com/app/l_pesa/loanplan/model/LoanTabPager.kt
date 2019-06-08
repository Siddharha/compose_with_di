package com.app.l_pesa.loanplan.model

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter

import com.app.l_pesa.loanplan.view.BusinessLoan
import com.app.l_pesa.loanplan.view.CurrentLoan

/**
 * Created by Intellij Amiya on 21/2/19.
 * Who Am I- https://stackoverflow.com/users/3395198/
 * A good programmer is someone who looks both ways before crossing a One-way street.
 * Kindly follow https://source.android.com/setup/code-style
 */
class LoanTabPager(fm: androidx.fragment.app.FragmentManager, private var tabCount: Int): androidx.fragment.app.FragmentStatePagerAdapter(fm) {

    //Overriding method getItem
    override fun getItem(position: Int): androidx.fragment.app.Fragment? {
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
