package com.app.l_pesa.points.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.app.l_pesa.R
import com.app.l_pesa.dashboard.view.DashboardActivity

class PointsFragment: Fragment() {

    companion object {
        fun newInstance(): Fragment {
            return PointsFragment()
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        return inflater.inflate(R.layout.fragment_points, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        (activity as DashboardActivity).visibleFilter(false)
        (activity as DashboardActivity).visibleButton(false)
    }

}
