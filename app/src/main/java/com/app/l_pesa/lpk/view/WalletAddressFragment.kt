package com.app.l_pesa.lpk.view

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.app.AppCompatActivity
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.app.l_pesa.R
import com.app.l_pesa.common.CommonMethod
import com.app.l_pesa.common.SharedPref
import com.app.l_pesa.dashboard.model.ResDashboard
import com.app.l_pesa.lpk.inter.ICallBackWalletAddress
import com.app.l_pesa.lpk.presenter.PresenterWalletAddress
import com.google.gson.Gson
import com.google.gson.JsonObject
import kotlinx.android.synthetic.main.fragment_wallet_address.*
import java.util.HashMap

class WalletAddressFragment : Fragment(), ICallBackWalletAddress {


    companion object {
        fun newInstance(): Fragment {
            return WalletAddressFragment()
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        return inflater.inflate(R.layout.fragment_wallet_address, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        swipeRefresh()
        initData()

    }

    private fun swipeRefresh()
    {
        swipeRefreshLayout.setColorSchemeResources(R.color.colorAccent)
        swipeRefreshLayout.setOnRefreshListener {
            swipeRefreshLayout.isRefreshing=false
        }
    }

    private fun initData()
    {
        val sharedPrefOBJ=SharedPref(activity!!)
        val userDashBoard  = Gson().fromJson<ResDashboard.Data>(sharedPrefOBJ.userDashBoard, ResDashboard.Data::class.java)

        val hashMapOLD = HashMap<String, String>()
        hashMapOLD["wallet"]     = ""+userDashBoard.walletAddress

        if(!TextUtils.isEmpty(userDashBoard.walletAddress))
        {
            etWalletAddress.setText(userDashBoard.walletAddress)
        }

        buttonWalletAddress.setOnClickListener {

            CommonMethod.hideKeyboardView(activity as AppCompatActivity)

            val hashMapNew = HashMap<String, String>()
            hashMapNew["wallet"]     = etWalletAddress.text.toString().trim()

            when {
                hashMapOLD==hashMapNew -> CommonMethod.customSnackBarError(rootLayout,activity!!,resources.getString(R.string.change_one_info))
                TextUtils.isEmpty(etWalletAddress.text.toString()) -> CommonMethod.customSnackBarError(rootLayout,activity!!,resources.getString(R.string.required_wallet_address))
                else -> {
                    buttonWalletAddress.isClickable=false
                    swipeRefreshLayout.setColorSchemeResources(R.color.colorAccent)
                    swipeRefreshLayout.isRefreshing=true

                    val presenterWalletAddress= PresenterWalletAddress()
                    val jsonObject = JsonObject()
                    jsonObject.addProperty("ether_address",etWalletAddress.text.toString().trim())

                    presenterWalletAddress.doWalletAddress(activity!!,jsonObject,this)


                }
            }
        }
    }


    override fun onSuccessWalletAddress() {

        buttonWalletAddress.isClickable=true
        swipeRefreshLayout.isRefreshing=false
        CommonMethod.customSnackBarSuccess(rootLayout,activity!!,resources.getString(R.string.token_withdrawal_successfully))
    }

    override fun onErrorWalletAddress(message: String) {

        CommonMethod.customSnackBarError(rootLayout,activity!!,message)
        buttonWalletAddress.isClickable=true
        swipeRefreshLayout.isRefreshing=false
    }


}