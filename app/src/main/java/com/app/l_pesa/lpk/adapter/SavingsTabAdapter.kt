package com.app.l_pesa.lpk.adapter


import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import com.app.l_pesa.lpk.view.EarnedInterestFragment
import com.app.l_pesa.lpk.view.TokenTransferFragment
import com.app.l_pesa.lpk.view.TransferHistoryFragment

class SavingsTabAdapter(fm: FragmentManager, private var tabCount: Int): FragmentStatePagerAdapter(fm) {


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
