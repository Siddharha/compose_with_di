package com.app.l_pesa.lpk.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import com.app.l_pesa.lpk.view.EarnedInterestFragment
import com.app.l_pesa.lpk.view.TokenTransferFragment
import com.app.l_pesa.lpk.view.TransferHistoryFragment

class SavingsTabAdapter(fm: androidx.fragment.app.FragmentManager, private var tabCount: Int): androidx.fragment.app.FragmentStatePagerAdapter(fm) {


    override fun getItem(position: Int): androidx.fragment.app.Fragment? {
        //Returning the current tabs
        return when (position) {
            0 -> {
                  TokenTransferFragment()
            }
            1 -> {
                  TransferHistoryFragment()
            }
            2 -> {
                  EarnedInterestFragment()
                 }

            else -> null
        }
    }


    override fun getCount(): Int {
        return tabCount
    }
}
