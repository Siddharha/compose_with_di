package com.app.l_pesa.dashboard.adapter

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.app.l_pesa.dashboard.model.ResDashboard
import com.app.l_pesa.dashboard.view.ScreenSlidePageAdFragment

class ScreenSlidePagerAdapter(fa: Fragment, private val bannerList: List<ResDashboard.Banner>?) : FragmentStateAdapter(fa) {
    override fun getItemCount(): Int = bannerList?.size ?: 0

    override fun createFragment(position: Int): ScreenSlidePageAdFragment {
        // val bannerItem = bannerList?.get(position)

        return ScreenSlidePageAdFragment.newInstance(bannerList!![position])

    }
}
//        return if(position == 0){
//            ScreenSlidePageAdFragment.instance(bannerItem)
//
//        }else{
//            ScreenSlidePageAdFragment.loanInstance(bannerItem)
//        }
