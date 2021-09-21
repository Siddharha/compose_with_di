package com.app.l_pesa.points.view

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.app.l_pesa.R
import com.app.l_pesa.dashboard.view.DashboardActivity
import com.app.l_pesa.points.adapters.PointsAdapter
import com.google.android.material.tabs.TabLayoutMediator
import kotlinx.android.synthetic.main.fragment_points.view.*

class PointsFragment: Fragment() {

    private val pointsAdapter:PointsAdapter by lazy { PointsAdapter(this) }
    lateinit var rootView: View
    companion object {
        fun newInstance(): Fragment { 
            return PointsFragment()
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        rootView = inflater.inflate(R.layout.fragment_points, container, false)
        loadPage()
        return rootView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        Handler(Looper.getMainLooper()).postDelayed({
            (activity as DashboardActivity).visibleFilter(false)
            (activity as DashboardActivity).visibleButton(false)
        }, 200)

    }

    private fun loadPage(){

        rootView.apply{
            pager.adapter = pointsAdapter
            TabLayoutMediator(tabLayout,pager){tab,position->
               when(position){
                   0 -> tab.apply {
                           setIcon(R.drawable.ic_settings_icon)
                       text = "How it works"
                       }
                   1 -> tab.apply {
                       setIcon(R.drawable.ic_buy_point_icon)
                       text = "Plans"
                   }
                   2 -> tab.apply {
                       setIcon(R.drawable.ic_loan_history_img)
                       text = "Purchase history"
                   }

               }
            }.attach()
        }
    }

}
