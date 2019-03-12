package com.app.l_pesa.profile.view

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.content.ContextCompat
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
import java.util.ArrayList
import android.widget.ArrayAdapter





class PersonalIdInfoFragment : Fragment(), ICallBackClickPersonalId {

    private val TYPE_VIEW   = 1
    private val TYPE_DELETE = 2
    var listPersonalId      : ArrayList<ResUserInfo.UserIdsPersonalInfo>? = null
    var personalIdAdapter   : PersonalIdAdapter? = null

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
        listPersonalId= ArrayList()
        val sharedPrefOBJ= SharedPref(activity!!)
        val profileInfo  = Gson().fromJson<ResUserInfo.Data>(sharedPrefOBJ.profileInfo, ResUserInfo.Data::class.java)
        listPersonalId!!.clear()

        if(profileInfo.userIdsPersonalInfo!!.size>0)
        {
            listPersonalId!!.addAll(profileInfo.userIdsPersonalInfo!!)
            personalIdAdapter                    = PersonalIdAdapter(activity!!,listPersonalId!!,this)
            rvPersonalId.layoutManager            = LinearLayoutManager(activity!!, LinearLayoutManager.VERTICAL, false)
            rvPersonalId.adapter                  = personalIdAdapter
        }


    }

    override fun onClickIdList(userIdsPersonalInfo: ResUserInfo.UserIdsPersonalInfo, position: Int, it: View) {

        val nextItem = ActionItem(TYPE_VIEW, activity!!.resources.getString(R.string.view_file), ContextCompat.getDrawable(activity!!,R.drawable.ic_view_file))
        val prevItem = ActionItem(TYPE_DELETE, activity!!.resources.getString(R.string.delete), ContextCompat.getDrawable(activity!!,R.drawable.ic_delete))
        val quickAction = QuickAction(activity!!, QuickAction.VERTICAL)
        quickAction.addActionItem(nextItem)
        quickAction.addActionItem(prevItem)
        quickAction.show(it)

        quickAction.setOnActionItemClickListener(object : QuickAction.OnActionItemClickListener {
            override fun onItemClick(source: QuickAction, pos: Int, actionId: Int) {

                //val actionItem = quickAction.getActionItem(pos)
                if(pos==1)
                {
                    listPersonalId!!.removeAt(position)
                    personalIdAdapter!!.notifyDataSetChanged()
                }
                else
                {

                }


            }
        })
    }
}