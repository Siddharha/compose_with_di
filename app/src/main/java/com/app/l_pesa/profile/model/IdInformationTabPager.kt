package com.app.l_pesa.profile.model

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import com.app.l_pesa.profile.view.BusinessIdInfoFragment
import com.app.l_pesa.profile.view.PersonalIdInfoFragment


class IdInformationTabPager(fm: FragmentManager, private var tabCount: Int): FragmentStatePagerAdapter(fm) {

    //Overriding method getItem
    override fun getItem(position: Int): Fragment {
        //Returning the current tabs
        return when (position) {
            0 -> {
                PersonalIdInfoFragment()
            }
            else -> {
                BusinessIdInfoFragment()
            }

        }
    }


    override fun getCount(): Int {
        return tabCount
    }
}
