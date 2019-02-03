package com.app.l_pesa.dashboard.view


import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.app.l_pesa.R
import android.support.v4.widget.SwipeRefreshLayout



class DashboardFragment: Fragment() {

    lateinit var swipeRefreshLayoutOBJ :SwipeRefreshLayout

    override fun onCreateView(inflater: LayoutInflater,container: ViewGroup?, savedInstanceState: Bundle?): View? {

        val view = inflater.inflate(R.layout.dashboard_layout, container,false)
        initUI(view)
        return view
    }

    private fun initUI(view: View)
    {
        swipeRefreshLayoutOBJ=view.findViewById(R.id.swipeRefreshLayout)
        swipeRefreshLayoutOBJ.setColorSchemeResources(R.color.colorAccent)
        swipeRefreshLayoutOBJ.setOnRefreshListener {

          //  swipeRefreshLayoutOBJ.isRefreshing = false
        }
    }
}

