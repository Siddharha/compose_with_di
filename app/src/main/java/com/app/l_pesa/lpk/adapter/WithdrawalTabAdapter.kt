package com.app.l_pesa.lpk.adapter

import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentStatePagerAdapter
import com.app.l_pesa.lpk.view.TokenWithdrawalFragment
import com.app.l_pesa.lpk.view.WalletAddressFragment
import com.app.l_pesa.lpk.view.WithdrawalHistoryFragment

class WithdrawalTabAdapter (fm: FragmentManager, private var tabCount: Int): FragmentStatePagerAdapter(fm) {


    override fun getItem(position: Int): Fragment? {
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