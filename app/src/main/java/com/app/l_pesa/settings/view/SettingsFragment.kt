package com.app.l_pesa.settings.view

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.app.l_pesa.R
import com.app.l_pesa.R.array.settings_item_icon
import com.app.l_pesa.R.array.settings_item_name
import com.app.l_pesa.common.CommonMethod
import com.app.l_pesa.dashboard.view.DashboardActivity
import com.app.l_pesa.password.view.ChangePasswordFragment
import com.app.l_pesa.profile.view.ProfileFragment
import com.app.l_pesa.settings.adapter.RecyclerViewAdapter
import com.app.l_pesa.settings.inter.ICallBackListClick
import com.app.l_pesa.settings.model.SettingsItem
import kotlinx.android.synthetic.main.activity_dashboard.*
import kotlinx.android.synthetic.main.fragment_settings.*


/**
 * Created by Intellij Amiya on 04-02-2019.
 * A good programmer is someone who looks both ways before crossing a One-way street.
 * Kindly follow https://source.android.com/setup/code-style
 */
class SettingsFragment : Fragment(), ICallBackListClick {

    companion object {
        fun newInstance(): Fragment {
            return SettingsFragment()
        }
    }

    private var settingsList: MutableList<SettingsItem> = mutableListOf()
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        val view= inflater.inflate(R.layout.fragment_settings, container,false)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        initData()

        rv_list.layoutManager = LinearLayoutManager(activity)
        rv_list.adapter = RecyclerViewAdapter(activity!!, settingsList,this)
    }

    private fun initData()
    {
        val settingsName = resources.getStringArray(settings_item_name)
        val settingsIcon   = resources.obtainTypedArray(settings_item_icon)
        settingsList.clear()
        for (i in settingsName.indices){
            settingsList.add(SettingsItem(settingsName[i],
                    settingsIcon.getResourceId(i, 0)))
        }

        settingsIcon.recycle()
    }

    override fun onClickListItem(position: Int?) {

        if(position==0)
        {
           CommonMethod.startFragment(activity!!,R.id.frame,ChangePasswordFragment.newInstance())
        }

    }
}