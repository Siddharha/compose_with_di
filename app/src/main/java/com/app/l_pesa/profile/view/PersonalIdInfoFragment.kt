package com.app.l_pesa.profile.view

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.app.l_pesa.R
import com.app.l_pesa.common.SharedPref
import com.app.l_pesa.loanplan.adapter.PersonalIdAdapter
import com.app.l_pesa.profile.inter.ICallBackClickPersonalId
import com.app.l_pesa.profile.model.ResUserInfo
import com.google.gson.Gson
import kotlinx.android.synthetic.main.fragment_personal_id_layout.*
import com.app.l_pesa.common.ActionItem
import com.app.l_pesa.common.QuickAction



class PersonalIdInfoFragment : Fragment(), ICallBackClickPersonalId {

    private val TYPE_VIEW   = 1
    private val TYPE_DELETE = 2





    companion object {
        fun newInstance(): Fragment {
            return PersonalIdInfoFragment()
        }
    }
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        return inflater.inflate(R.layout.fragment_personal_id_layout, container,false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initData()

    }

    private fun initData()
    {
        val sharedPrefOBJ= SharedPref(activity!!)
        val profileInfo  = Gson().fromJson<ResUserInfo.Data>(sharedPrefOBJ.profileInfo, ResUserInfo.Data::class.java)

        if(profileInfo.userIdsPersonalInfo!!.size>0)
        {
            val currentLoanAdapter                = PersonalIdAdapter(activity!!,profileInfo.userIdsPersonalInfo!!,this)
            rvPersonalId.layoutManager            = LinearLayoutManager(activity!!, LinearLayoutManager.VERTICAL, false)
            rvPersonalId.adapter                  = currentLoanAdapter
        }


    }

    override fun onClickIdList(userIdsPersonalInfo: ResUserInfo.UserIdsPersonalInfo, position: Int, it: View) {

        val nextItem = ActionItem(TYPE_VIEW, "Next", resources.getDrawable(R.drawable.ic_due_icon))
        val prevItem = ActionItem(TYPE_DELETE, "Prev", resources.getDrawable(R.drawable.ic_id_no_icon))
        val quickAction = QuickAction(activity!!, QuickAction.VERTICAL)
        quickAction.addActionItem(nextItem)
        quickAction.addActionItem(prevItem)
        quickAction.show(it)
    }
}