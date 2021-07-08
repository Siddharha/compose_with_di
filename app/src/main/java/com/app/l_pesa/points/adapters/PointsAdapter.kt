package com.app.l_pesa.points.adapters

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.app.l_pesa.points.view.HowItWorksFragment
import com.app.l_pesa.points.view.PlansFragment
import com.app.l_pesa.points.view.PurchaseHistoryFragment

private const val NUM_TABS = 3
class PointsAdapter(fragment: Fragment) :
        FragmentStateAdapter(fragment) {
    override fun getItemCount(): Int {
        return NUM_TABS
    }

    override fun createFragment(position: Int): Fragment {
        when (position) {
            0 -> return HowItWorksFragment()
            1 -> return PlansFragment()
        }
        return PurchaseHistoryFragment()
    }
}