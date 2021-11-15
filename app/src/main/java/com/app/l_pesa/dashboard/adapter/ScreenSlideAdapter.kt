package com.app.l_pesa.dashboard.adapter

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.app.l_pesa.dashboard.view.ScreenSlidePageAdFragment

class ScreenSlidePagerAdapter(fa: Fragment) : FragmentStateAdapter(fa) {
    override fun getItemCount(): Int = 2

    override fun createFragment(position: Int): Fragment {
        return if(position == 0){
            ScreenSlidePageAdFragment.defiInstance()

        }else{
            ScreenSlidePageAdFragment.loanInstance()
        }
    }
}