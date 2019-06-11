package com.app.l_pesa.settings.view

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.app.l_pesa.R
import com.app.l_pesa.R.array.*
import com.app.l_pesa.common.SharedPref
import com.app.l_pesa.dashboard.view.DashboardActivity
import com.app.l_pesa.help.view.HelpActivity
import com.app.l_pesa.notification.view.NotificationActivity
import com.app.l_pesa.pin.view.ChangeLoginPinActivity
import com.app.l_pesa.pin.view.ChangePinActivity
import com.app.l_pesa.pin.view.SetUpPinActivity
import com.app.l_pesa.pinview.model.LoginData
import com.app.l_pesa.settings.adapter.SettingsAdapter
import com.app.l_pesa.settings.inter.ICallBackListClick
import com.app.l_pesa.settings.model.SettingsItem
import com.google.gson.Gson
import kotlinx.android.synthetic.main.fragment_settings.*


/**
 * Created by Intellij Amiya on 04-02-2019.
 * A good programmer is someone who looks both ways before crossing a One-way street.
 * Kindly follow https://source.android.com/setup/code-style
 */
class SettingsFragment :Fragment(), ICallBackListClick {

    companion object {
        fun newInstance(): Fragment {
            return SettingsFragment()
        }
    }

    private var settingsList: MutableList<SettingsItem> = mutableListOf()
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        return inflater.inflate(R.layout.fragment_settings, container,false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initData()

        rv_list.layoutManager = LinearLayoutManager(activity)
        rv_list.adapter = SettingsAdapter(activity!!, settingsList,this)
    }

    private fun initData()
    {
       (activity as DashboardActivity).visibleFilter(false)
       (activity as DashboardActivity).visibleButton(false)
       val sharedPrefOBJ= SharedPref(activity!!)
       val userData = Gson().fromJson<LoginData>(sharedPrefOBJ.userInfo, LoginData::class.java)

        if(userData.user_info.mpin_password)
        {
            val settingsName = resources.getStringArray(settings_item_name_pin)
            val settingsIcon   = resources.obtainTypedArray(settings_item_icon_pin)

            settingsList.clear()
            for (i in settingsName.indices){
                settingsList.add(SettingsItem(settingsName[i],
                        settingsIcon.getResourceId(i, 0)))
            }

            settingsIcon.recycle()
        }
        else
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


    }

    override fun onClickListItem(position: Int?) {

        val sharedPrefOBJ= SharedPref(activity!!)
        val userData = Gson().fromJson<LoginData>(sharedPrefOBJ.userInfo, LoginData::class.java)

            if(position==0)
            {
                startActivity(Intent(activity, ChangeLoginPinActivity::class.java))
                activity!!.overridePendingTransition(R.anim.right_in, R.anim.left_out)
            }

            else if(position==1) {
                if (userData.user_info.mpin_password) {

                    startActivity(Intent(activity, ChangePinActivity::class.java))
                    activity!!.overridePendingTransition(R.anim.right_in, R.anim.left_out)
                } else {
                    startActivity(Intent(activity, SetUpPinActivity::class.java))
                    activity!!.overridePendingTransition(R.anim.right_in, R.anim.left_out)
                }
            }
            else if(position==3)
            {
                startActivity(Intent(activity, NotificationActivity::class.java))
                activity!!.overridePendingTransition(R.anim.right_in, R.anim.left_out)
            }
            else if(position==4)
            {
                 startActivity(Intent(activity, HelpActivity::class.java))
                 activity!!.overridePendingTransition(R.anim.right_in, R.anim.left_out)
            }
          else{

                Toast.makeText(activity,resources.getString(R.string.coming_soon),Toast.LENGTH_SHORT).show()
            }
        }

}