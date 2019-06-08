package com.app.l_pesa.lpk.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import com.app.l_pesa.lpk.view.TokenWithdrawalFragment
import com.app.l_pesa.lpk.view.WalletAddressFragment
import com.app.l_pesa.lpk.view.WithdrawalHistoryFragment

class WithdrawalTabAdapter (fm: androidx.fragment.app.FragmentManager, private var tabCount: Int): androidx.fragment.app.FragmentStatePagerAdapter(fm) {


    override fun getItem(position: Int): androidx.fragment.app.Fragment? {
        //Returning the current tabs
        return when (position) {
            0 -> {
                TokenWithdrawalFragment()
            }
            1 -> {
                WithdrawalHistoryFragment()
            }
            2 -> {
                WalletAddressFragment()
            }

            else -> null
        }
    }


    override fun getCount(): Int {
        return tabCount
    }
}