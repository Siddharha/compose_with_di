package com.app.l_pesa.dashboard.view

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.Gravity
import android.view.View
import com.app.l_pesa.R
import com.app.l_pesa.common.BaseActivity
import kotlinx.android.synthetic.main.activity_dashboard.*

class DashboardActivity : BaseActivity() {

    override fun initDrawerMenuView() {

        if (drawer_layout != null && drawer_layout!!.isDrawerOpen(Gravity.START)) {
            drawer_layout!!.closeDrawer(Gravity.START)

        }


    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dashboard)
    }

    override fun onClick(p0: View?) {


    }

    override fun onDrawerItemSelected(view: View?, position: Int) {

        when (position) {
            0 -> {

               // startFragment(container, DashboardFragment(), true)
            }
            1 -> {

               // startFragment(container, ViewPlans(), true)
            }

            2 -> {

                //profilePresenter?.onProfileOptionClicked(this@LandingActivityNew , MyProfile())

            }
            3 ->{
                //startFragment(container, LoanHistoryFragment(), true)
            }

            4 ->{
               // startFragment(container, BuyPoint(), true)
            }

            5 ->
            {
                //startFragment(container, ReferFriend(), true)
            }


            6 -> {
               // startFragment(container, HelpAndSupport(), true)

            }

            7 ->
            {
              //  startFragment(container, SettingsScreen(), true)

            }

            8 ->  {
               // landingPresenter?.onLogoutClicked(this@LandingActivityNew,getDeviceInfo().device_id)
            }


        }
    }



}
