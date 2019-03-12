package com.app.l_pesa.profile.view

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.app.l_pesa.R
import com.app.l_pesa.common.SharedPref
import com.app.l_pesa.profile.model.ResUserInfo
import com.google.gson.Gson

class PersonalIdInfoFragment : Fragment() {

    companion object {
        fun newInstance(): Fragment {
            return PersonalIdInfoFragment()
        }
    }
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        return inflater.inflate(R.layout.fragment_loan_plan_list, container,false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initData()

    }

    private fun initData()
    {
        val sharedPrefOBJ= SharedPref(activity!!)
        val gson         = Gson()
        val profileInfo  = Gson().fromJson<ResUserInfo.Data>(sharedPrefOBJ.profileInfo, ResUserInfo.Data::class.java)
    }
}