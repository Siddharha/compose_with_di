package com.app.l_pesa.lpk.adapter


import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import com.app.l_pesa.lpk.view.EarnedInterestFragment
import com.app.l_pesa.lpk.view.TokenTransferFragment
import com.app.l_pesa.lpk.view.TransferHistoryFragment

class SavingsTabAdapter(fm: FragmentManager, private var tabCount: Int): FragmentStatePagerAdapter(fm) {


    override fun getItem(position: Int): Fragment {
        //Returning the current tabs
        return when (position) {
            0 -> {
                  TokenTransferFragment()
            }
            1 -> {
                  TransferHistoryFragment()
            }
            else -> {
                  EarnedInterestFragment()
            }
        }
    }


    override fun getCount(): Int {
        return tabCount
    }
}
