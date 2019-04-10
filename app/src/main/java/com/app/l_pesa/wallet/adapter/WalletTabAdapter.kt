package com.app.l_pesa.wallet.adapter

import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentStatePagerAdapter
import com.app.l_pesa.wallet.view.TransactionAllFragment
import com.app.l_pesa.wallet.view.TransactionCreditFragment
import com.app.l_pesa.wallet.view.TransactionDebitFragment

class WalletTabAdapter(fm: FragmentManager, private var tabCount: Int): FragmentStatePagerAdapter(fm) {

    //Overriding method getItem
    override fun getItem(position: Int): Fragment? {
        //Returning the current tabs
        return when (position) {
            0 -> {
                TransactionAllFragment()
            }
            1 -> {
                TransactionCreditFragment()
            }
            2 -> {
                TransactionDebitFragment()
            }

            else -> null
        }
    }


    override fun getCount(): Int {
        return tabCount
    }
}