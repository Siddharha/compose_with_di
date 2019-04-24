package com.app.l_pesa.lpk.view

import android.annotation.SuppressLint
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
import com.app.l_pesa.lpk.inter.ICallBackTokenTransfer
import com.app.l_pesa.lpk.model.ResInfoLPK
import com.app.l_pesa.lpk.presenter.PresenterTokenTransfer
import com.google.gson.Gson
import com.google.gson.JsonObject
import kotlinx.android.synthetic.main.fragment_token_transfer.*
import java.text.DecimalFormat

class TokenTransferFragment : Fragment(), ICallBackTokenTransfer {


    companion object {
        fun newInstance(): Fragment {
            return TokenTransferFragment()
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        return inflater.inflate(R.layout.fragment_token_transfer, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initData()
        swipeRefresh()

    }

    private fun swipeRefresh()
    {
        swipeRefreshLayout.setColorSchemeResources(R.color.colorAccent)
        swipeRefreshLayout.setOnRefreshListener {
            swipeRefreshLayout.isRefreshing=false
        }
    }

    @SuppressLint("SetTextI18n")
    private fun initData()
    {

        val sharedPrefOBJ= SharedPref(activity!!)
        val infoLPK = Gson().fromJson<ResInfoLPK.Data>(sharedPrefOBJ.lpkInfo, ResInfoLPK.Data::class.java)

        val format = DecimalFormat()
        format.isDecimalSeparatorAlwaysShown = false

        txtLPK.text         = format.format(infoLPK.lpkValue)+" LPK"
        txtLockedVal.text   = format.format(infoLPK.lpkLockedValue)+" LPK is in Deposit"
        txtSavings.text     = infoLPK.lpkSavingsText

        buttonTransfer.setOnClickListener {

            if(TextUtils.isEmpty(etToken.text.toString()))
            {
                CommonMethod.hideKeyboardView(activity!! as AppCompatActivity)
                CommonMethod.customSnackBarError(rootLayout,activity!!,resources.getString(R.string.required_token_amount))
            }
            else
            {
                if(CommonMethod.isNetworkAvailable(activity!!))
                {
                    buttonTransfer.isClickable=false
                    swipeRefreshLayout.isRefreshing=true
                    CommonMethod.hideKeyboardView(activity!! as AppCompatActivity)
                    val jsonObject = JsonObject()
                    jsonObject.addProperty("token_value",etToken.text.toString())

                    val presenterTokenTransfer= PresenterTokenTransfer()
                    presenterTokenTransfer.doTokenTransfer(activity!!,jsonObject,this)
                }
                else
                {
                    CommonMethod.customSnackBarError(rootLayout,activity!!,resources.getString(R.string.no_internet))
                }
            }

        }
    }

    override fun onSuccessTokenTransfer() {

        buttonTransfer.isClickable=true
        swipeRefreshLayout.isRefreshing=false
        CommonMethod.customSnackBarSuccess(rootLayout,activity!!,resources.getString(R.string.token_transfer_successfully))
    }

    override fun onErrorTokenTransfer(message: String) {

        buttonTransfer.isClickable=true
        swipeRefreshLayout.isRefreshing=false
        CommonMethod.customSnackBarError(rootLayout,activity!!,message)
    }
}