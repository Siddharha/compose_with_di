package com.app.l_pesa.wallet.view

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.app.AppCompatActivity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.app.l_pesa.R
import com.app.l_pesa.common.CommonMethod
import com.app.l_pesa.wallet.inter.ICallBackWallet
import com.app.l_pesa.wallet.presenter.PresenterWithdrawal
import com.google.gson.JsonObject
import kotlinx.android.synthetic.main.fragment_wallet.*

class WalletFragment :Fragment(), ICallBackWallet {


    companion object {
        fun newInstance(): Fragment {
            return WalletFragment()
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        return inflater.inflate(R.layout.fragment_wallet, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        CommonMethod.hideKeyboardView(activity as AppCompatActivity)
        initData()

    }

    private fun initData()
    {

        buttonWithdraw.setOnClickListener {

            if(etWithdrawalAmount.text.toString().isEmpty())
            {
                CommonMethod.customSnackBarError(rootLayout,activity!!,resources.getString(R.string.required_withdrawal_amount))
            }
            else
            {
                if(CommonMethod.isNetworkAvailable(activity!!))
                {
                    buttonWithdraw.isClickable=false
                    val jsonObject = JsonObject()
                    jsonObject.addProperty("amount",etWithdrawalAmount.text.toString().trim())

                    val presenterWithdrawal= PresenterWithdrawal()
                    presenterWithdrawal.doAddWithdrawal(activity!!,jsonObject,this)
                }
                else
                {
                    CommonMethod.customSnackBarError(rootLayout,activity!!,resources.getString(R.string.no_internet))
                }
            }

        }
    }

    override fun onSuccessWalletWithdrawal() {

        buttonWithdraw.isClickable=true
    }

    override fun onErrorWalletWithdrawal(message: String) {

        buttonWithdraw.isClickable=true
        CommonMethod.customSnackBarError(rootLayout,activity!!,message)
    }
}