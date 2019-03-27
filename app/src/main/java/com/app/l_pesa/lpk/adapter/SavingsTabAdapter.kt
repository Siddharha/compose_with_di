package com.app.l_pesa.lpk.adapter

import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentStatePagerAdapter
import com.app.l_pesa.lpk.view.InterestHistoryFragment
import com.app.l_pesa.lpk.view.TokenTransferFragment
import com.app.l_pesa.lpk.view.TransferHistoryFragment

class SavingsTabAdapter//Constructor to the class
(fm: FragmentManager, //integer to count number of tabs
 private var tabCount: Int)//Initializing tab count
    : FragmentStatePagerAdapter(fm) {

    //Overriding method getItem
    override fun getItem(position: Int): Fragment? {
        //Returning the current tabs
        return when (position) {
            0 -> {
                  TokenTransferFragment()
            }
            1 -> {
                  TransferHistoryFragment()
            }
            2 -> {
                  InterestHistoryFragment()
                 }

            else -> null
        }
    }


    override fun getCount(): Int {
        return tabCount
    }
}
