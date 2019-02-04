package com.app.l_pesa.password.view

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.app.l_pesa.R
import com.app.l_pesa.common.CommonMethod
import com.app.l_pesa.dashboard.view.DashboardActivity
import com.app.l_pesa.settings.view.SettingsFragment
import kotlinx.android.synthetic.main.app_bar_main.*


/**
 * Created by Intellij Amiya on 04-02-2019.
 * A good programmer is someone who looks both ways before crossing a One-way street.
 * Kindly follow https://source.android.com/setup/code-style
 */
class ChangePasswordFragment : Fragment() {

    companion object {
        fun newInstance(): Fragment {
            return ChangePasswordFragment()
        }
    }


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        val view= inflater.inflate(R.layout.fragment_change_password, container,false)


        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


      //  (activity as DashboardActivity).supportActionBar?.title = getString(R.string.app_name)
      // (activity as DashboardActivity).supportActionBar?.setDisplayHomeAsUpEnabled(true)
        //(activity as DashboardActivity).supportActionBar?.setDisplayShowHomeEnabled(true)

       /* (activity as DashboardActivity).toolbar.setNavigationOnClickListener {

          //  (activity as DashboardActivity).initToggle()
            //(activity as DashboardActivity).supportFragmentManager.popBackStack()
             (activity as DashboardActivity).supportFragmentManager.popBackStack()

           // CommonMethod.startFragment(activity!!,R.id.frame,SettingsFragment(),true)
        }*/
    }
}