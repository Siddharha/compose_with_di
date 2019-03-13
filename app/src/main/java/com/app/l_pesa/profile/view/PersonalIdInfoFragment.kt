package com.app.l_pesa.profile.view

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.PopupWindow
import android.widget.Toast
import com.app.l_pesa.R
import com.app.l_pesa.common.CommonMethod
import com.app.l_pesa.common.SharedPref
import com.app.l_pesa.dashboard.model.ResDashboard
import com.app.l_pesa.loanplan.adapter.PersonalIdAdapter
import com.app.l_pesa.profile.inter.ICallBackClickPersonalId
import com.app.l_pesa.profile.model.ResUserInfo
import com.google.gson.Gson
import kotlinx.android.synthetic.main.fragment_personal_id_layout.*
import com.app.l_pesa.profile.adapter.AdapterPopupWindow
import com.app.l_pesa.profile.adapter.PersonalIdListAdapter
import com.app.l_pesa.profile.inter.ICallBackRecyclerCallbacks
import com.app.l_pesa.profile.model.ModelWindowPopUp
import java.util.ArrayList


class PersonalIdInfoFragment : Fragment(), ICallBackClickPersonalId {


    private var filterPopup : PopupWindow? = null
    private var selectedItem: Int = -1
    var listPersonalId      : ArrayList<ResUserInfo.UserIdsPersonalInfo>? = null
    var personalIdAdapter   : PersonalIdAdapter? = null
    var personalIdType=""
    var personalIdName=""
    var personalId=0

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

        swipeRefresh()
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
            personalIdAdapter                 = PersonalIdAdapter(activity!!,listPersonalId!!,this)
            rvPersonalId.layoutManager        = LinearLayoutManager(activity!!, LinearLayoutManager.VERTICAL, false)
            rvPersonalId.adapter              = personalIdAdapter
        }

        buttonSubmit.setOnClickListener {

            if(personalId==0)
            {
                CommonMethod.customSnackBarError(llRoot,activity!!,resources.getString(R.string.required_id_type))
                showDialogIdType(sharedPrefOBJ)
            }
            else if(TextUtils.isEmpty(etIdNumber.text.toString()))
            {
                CommonMethod.customSnackBarError(llRoot,activity!!,resources.getString(R.string.required_id_number))
            }
            else
            {
                if(CommonMethod.isNetworkAvailable(activity!!))
                {

                    swipeRefreshLayout.isRefreshing=true
                    buttonSubmit.isClickable=false
                }
                else
                {
                    CommonMethod.customSnackBarError(llRoot,activity!!,resources.getString(R.string.no_internet))
                }
            }
        }

        buttonCancel.setOnClickListener {

            activity!!.onBackPressed()
            activity!!.overridePendingTransition(R.anim.left_in, R.anim.right_out)
        }

        etPersonalId.isFocusable=false

        etPersonalId.setOnClickListener {
            showDialogIdType(sharedPrefOBJ)

        }


    }

    private fun swipeRefresh()
    {
        swipeRefreshLayout.setColorSchemeResources(R.color.colorAccent)
        swipeRefreshLayout.setOnRefreshListener {
        swipeRefreshLayout.isRefreshing=false
        }
    }

    private fun showDialogIdType(sharedPrefOBJ: SharedPref)
    {
        val userDashBoard  = Gson().fromJson<ResDashboard.Data>(sharedPrefOBJ.userDashBoard, ResDashboard.Data::class.java)
        if(userDashBoard.personalIdTypes!!.size>0)
        {
            val dialog= Dialog(activity!!)
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
            dialog.setContentView(R.layout.dialog_id_type)
            val recyclerView                = dialog.findViewById(R.id.recyclerView) as RecyclerView?
            val personalIdAdapter           = PersonalIdListAdapter(activity!!, userDashBoard.personalIdTypes!!,dialog,this)
            recyclerView?.layoutManager     = LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false)
            recyclerView?.adapter           = personalIdAdapter
            dialog.show()
        }
    }

    private fun dismissPopup() {
        filterPopup?.let {
            if(it.isShowing){
                it.dismiss()
            }
            filterPopup = null
        }

    }

    override fun onSelectIdType(id: Int, name: String, type: String) {

        val sharedPrefOBJ= SharedPref(activity!!)
        val profileInfo  = Gson().fromJson<ResUserInfo.Data>(sharedPrefOBJ.profileInfo, ResUserInfo.Data::class.java)
        val totalSize = 0 until profileInfo.userIdsPersonalInfo!!.size

        for(i in totalSize)
        {

            if(profileInfo.userIdsPersonalInfo!![i].verified==1 && profileInfo.userIdsPersonalInfo!![i].idTypeName==name)
            {
              Toast.makeText(activity, "Your $name is already verified",Toast.LENGTH_SHORT).show()
              break

            }
            else
            {
                personalIdType=type
                personalIdName=name
                personalId=id
                etPersonalId.setText(personalIdName)
            }
        }
    }


    override fun onClickIdList(userIdsPersonalInfo: ResUserInfo.UserIdsPersonalInfo, position: Int, it: View) {

        dismissPopup()
        filterPopup = showAlertFilter(userIdsPersonalInfo,position,it)
        filterPopup?.isOutsideTouchable = true
        filterPopup?.isFocusable = true
        filterPopup?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        filterPopup?.showAsDropDown(it)
    }

    private fun showAlertFilter(userIdsPersonalInfo: ResUserInfo.UserIdsPersonalInfo, filterposition: Int, it: View): PopupWindow {

        val filterItemList = mutableListOf<ModelWindowPopUp>()
        if(userIdsPersonalInfo.verified==1)
        {
            filterItemList.add(ModelWindowPopUp(R.drawable.ic_view_file,resources.getString(R.string.view_file)))
        }
        else
        {
            filterItemList.add(ModelWindowPopUp(R.drawable.ic_view_file,resources.getString(R.string.view_file)))
            filterItemList.add(ModelWindowPopUp(R.drawable.ic_delete,resources.getString(R.string.delete)))
        }


        val inflater = activity!!.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val view = inflater.inflate(R.layout.layout_only_recyclerview, null)
        val recyclerView = view.findViewById<RecyclerView>(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(activity)
        recyclerView.addItemDecoration(DividerItemDecoration(recyclerView.context, DividerItemDecoration.VERTICAL))

        val adapter = AdapterPopupWindow(activity!!)
        adapter.addAlertFilter(filterItemList)
        recyclerView.adapter = adapter
        adapter.selectedItem(selectedItem)

        adapter.setOnClick(object : ICallBackRecyclerCallbacks<ModelWindowPopUp>{
            override fun onItemClick(view: View, position: Int, item:ModelWindowPopUp) {
                selectedItem = position

                if(position==1)
                {
                    listPersonalId!!.removeAt(filterposition)
                    personalIdAdapter!!.notifyDataSetChanged()
                }
                else
                {
                    // View File
                }

                dismissPopup()
            }
        })

        return PopupWindow(view, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)
    }


}