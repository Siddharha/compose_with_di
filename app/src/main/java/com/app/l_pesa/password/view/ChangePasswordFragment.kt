package com.app.l_pesa.password.view

import android.content.Context
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
import android.support.v7.app.ActionBarDrawerToggle
import android.widget.ImageView
import com.app.l_pesa.common.CommonTextRegular
import kotlinx.android.synthetic.main.activity_dashboard.*


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

        return inflater.inflate(R.layout.fragment_change_password, container,false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        showActionBar()
    }


    private fun showActionBar() {


        val actionBar = (activity as DashboardActivity).supportActionBar
        // inflate the customized Action Bar View
        val inflater = activity!!
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val v = inflater.inflate(R.layout.fragment_actionbar, null)

        if (actionBar != null) {
            // enable the customized view and disable title
            actionBar.setDisplayShowCustomEnabled(true)
            actionBar.setDisplayShowTitleEnabled(false)

            actionBar.customView = v
            // remove Burger Icon
            (activity as DashboardActivity).toolbar.navigationIcon = null

            val imgBack=v.findViewById(R.id.back) as ImageView
            val title=v.findViewById(R.id.title) as CommonTextRegular
            title.text=resources.getString(R.string.change_password)
            // add click listener to the back arrow icon
            imgBack.setOnClickListener(View.OnClickListener {
                // reverse back the show
                actionBar.setDisplayShowCustomEnabled(false)
                actionBar.setDisplayShowTitleEnabled(true)

                val toggle = ActionBarDrawerToggle(
                        (activity as DashboardActivity), (activity as DashboardActivity).drawer_layout, (activity as DashboardActivity).toolbar, R.string.drawer_open,
                        R.string.drawer_close)
                (activity as DashboardActivity).drawer_layout.addDrawerListener(toggle)
                toggle.syncState()
                CommonMethod.startFragment(activity!!,R.id.frame,SettingsFragment.newInstance())
            })
        }
    }
}