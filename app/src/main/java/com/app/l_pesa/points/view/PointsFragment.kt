package com.app.l_pesa.points.view

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.app.l_pesa.R

class PointsFragment:Fragment() {

    companion object {
        fun newInstance(): Fragment {
            return PointsFragment()
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        return inflater.inflate(R.layout.fragment_points, container, false)
    }

}
